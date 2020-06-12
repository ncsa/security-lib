package edu.uiuc.ncsa.security.util.terminal;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ISO 6429 (cursor addressing spec) compliant implementation of the {@link IOInterface}.
 * This has a basic command line history accessible with up and down arrows.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 6/9/20 at  7:52 AM
 */
public class ISO6429IO implements IOInterface {
    // This turns it on for everything and is ONLY a low-level debug hack
    boolean debugON = true;

    public MyLoggingFacade getLoggingFacade() {
        return loggingFacade;
    }

    public void setLoggingFacade(MyLoggingFacade loggingFacade) {

        this.loggingFacade = loggingFacade;
        terminal.loggingFacade = loggingFacade;
    }

    MyLoggingFacade loggingFacade;

    public ArrayList<String> getCommandCompletion() {
        return commandCompletion;
    }

    public void setCommandCompletion(ArrayList<String> commandCompletion) {
        this.commandCompletion = commandCompletion;
    }

    ArrayList<String> commandCompletion;

    public ISO6429IO(MyLoggingFacade loggingFacade) throws IOException {
        this.loggingFacade = loggingFacade;
        init();

    }

    public ISO6429IO() throws IOException {
        this(null);
    }

    ArrayList<StringBuilder> commandBuffer = new ArrayList<>();
    int commandBufferMaxWidth = 0;

    @Override
    public String readline() throws IOException {
        return readline(null);
    }

    @Override
    public String readline(String prompt) throws IOException {
        if (prompt == null) {
            prompt = "";
        }
        print(prompt);
        boolean keepLooping = true;
        // column where we start. It is best to ping the terminal itself for this, since some start
        // at 0 as the first column and some start at 1. Best to punt and just ask.
        int startCol = terminal.getCursorCol();
        int currentCol0 = startCol;
        int width = terminal.getScreenSize()[1];
        StringBuilder currentLine = new StringBuilder();
        commandBuffer.add(0, currentLine); // push it on the stack
        int currentBufferPosition = 0;  // start at current line
        boolean pasteModeOn = false;
        while (keepLooping) {
            KeyStroke keyStroke = terminal.getCharacter();
            debug("reading keystroke: " + keyStroke);
            switch (keyStroke.getKeyType()) {
                case Character:

                    int position = currentCol0 - startCol;
                    char character = keyStroke.getCharacter();

                    if (position < 0) {
                        position = 0;
                    }
                    if (currentLine.length() < position) {
                        position = currentLine.length() - 1;
                    }

                    currentLine.insert(position, character);
                    print(currentLine.substring(position));

                    currentCol0++; // increment the cursor position.
                    if (currentCol0 % width == 0) {
                        // insert new line so it wraps. This  moves cursor to start of line  + a line feed .
                        print("\r\n");
                    }
                    break;
                case PasteMode:
                    /*
                    Paste mode is toggled with ctrl+P. This will let you paste in huge quantities of text
                     that has embedded line feeds without
                    doing any of the cursor management which speeds things up tremendously.
                    The problem is that different terminals (read manufacturers and emulators) handle
                    pasting very differently for cursor control. Some allow queries during pasting, some don't
                    and figuring out who does what would require lists of different terminals and special cases.
                    One other way to do this in Java is by having timeouts on the input stream (like Lanterna)
                    but that means it literally will pause on every entered character for a bit, so pasting a large
                    block of text is achingly slow.
                    
                    Plan B is this: Tell it turn turn off cursor addressing and just snarf everything onto a single line.
                      
                     */
                    pasteModeOn = !pasteModeOn;
                    if (pasteModeOn) {
                        debug("paste mode ON");
                        terminal.setBold(false);
                        terminal.setColor(32);
                        println("<paste mode on>");
                    } else {
                        debug("paste mode OFF");

                        flush();
                        println("\n<paste mode off>");
                        terminal.setBold(false);
                        terminal.setColor(defaultColor);
                    }
                    break;
                case ArrowUp:
                    if (!commandBuffer.isEmpty()) {
                        terminal.clearLine(startCol);
                        currentBufferPosition = Math.min(++currentBufferPosition, commandBuffer.size() - 1);
                        currentLine = new StringBuilder(commandBuffer.get(currentBufferPosition));
                        print(currentLine);
                        currentCol0 = terminal.getCursorCol();
                    }
                    break;
                case ArrowDown:
                    if (!commandBuffer.isEmpty()) {
                        terminal.clearLine(startCol);
                        currentBufferPosition = Math.max(--currentBufferPosition, 0);
                        currentLine = new StringBuilder(commandBuffer.get(currentBufferPosition));
                        print(currentLine);
                        currentCol0 = terminal.getCursorCol();
                    }
                    break;

                case ArrowLeft:
                    // overrunning the end of the line is forbidden
                    currentCol0 = Math.max(0, terminal.getCursorCol() - 1);
                    terminal.setCursorCol(currentCol0);
                    break;
                case ArrowRight:
                    currentCol0 = Math.min(startCol + currentLine.length(), terminal.getCursorCol() + 1);

                    terminal.setCursorCol(currentCol0);
                    break;
                case Enter:
                    if (pasteModeOn) {

                        currentLine.append("\n");
                        print("\r\n");// move cursor down 1 row and set it at the beginning
                        currentCol0++; // increment the cursor position.
                        break;
                    }
                    commandBufferMaxWidth = Math.max(commandBufferMaxWidth, currentLine.length());
                    if (currentBufferPosition == 0) {
                        if (1 < commandBuffer.size() && !StringUtils.equals(currentLine.toString(), commandBuffer.get(1).toString())) {
                            commandBuffer.set(0, currentLine);
                        }
                    } else {
                        if (1 < commandBuffer.size()) {
                            if (StringUtils.equals(currentLine.toString(), commandBuffer.get(1).toString())) {
                                // don't add just take away unused buffer.
                                commandBuffer.remove(0);
                            } else {
                                commandBuffer.set(0, currentLine);
                            }
                        }
                    }
                    println("");

                    return currentLine.toString();
                case Backspace:
                    // delete character to LEFT of cursor and redraw
                    currentCol0 = terminal.getCursorCol() - startCol;
                    if (0 < currentCol0 && 0 < currentLine.length()) {
                        currentCol0 = Math.max(0, currentCol0 - 1);
                        currentLine = currentLine.deleteCharAt(currentCol0);
                        terminal.setCursorCol(startCol + currentCol0);
                        print(currentLine.substring(currentCol0) + " "); // blanks out last char
                        currentCol0 = startCol + currentCol0;
                        terminal.setCursorCol(currentCol0);
                    }
                    break;
                case Delete:
                    currentCol0 = terminal.getCursorCol() - startCol;

                    if (0 < currentCol0 && currentCol0 < currentLine.length()) {
                        currentCol0 = Math.min(startCol + currentLine.length() - 1, currentCol0);

                        currentLine = currentLine.deleteCharAt(currentCol0);
                        terminal.setCursorCol(startCol + currentCol0);
                        print(currentLine.substring(currentCol0) + " "); // blanks out last char
                        currentCol0 = startCol + currentCol0;
                        terminal.setCursorCol(currentCol0);
                    }
                case End:
                    currentCol0 = startCol + currentLine.length();
                    terminal.setCursorCol(currentCol0);
                    break;
                case Home:
                    currentCol0 = startCol;
                    terminal.setCursorCol(currentCol0);
                    break;
                case Tab:
                    // if the user hits a tab, take the line so far and do a look up
                    ArrayList<String> p = new ArrayList<>();

                    currentCol0 = terminal.getCursorCol() - startCol;
                    int boundaryIndex = findPreviousBreak(currentLine, currentCol0);

                    String currentSnippet = currentLine.substring(boundaryIndex + 1, currentCol0);
                    for (String x : commandCompletion) {
                        if (x.startsWith(currentSnippet)) {
                            p.add(x);
                        }
                    }
                    if (p.isEmpty()) {
                    } else {

                        if (p.size() == 1) {
                            // there is a unique one found.
                            currentLine.append(p.get(0).substring(currentSnippet.length()));
                            terminal.clearLine(startCol);
                            terminal.setCursorCol(startCol); // put the cursor at the end of the line
                            print(currentLine);
                            currentCol0 = startCol + currentLine.length();
                            terminal.setCursorCol(currentCol0); // put the cursor at the end of the line
                            break;
                        } else {
                            println("");
                            for (String pp : p) {
                                println(pp);
                            }
                            print(prompt + currentLine);
                            currentCol0 = startCol + currentLine.length();
                            terminal.setCursorCol(currentCol0); // put the cursor at the end of the line
                        }
                    }
                    break;
                case Unknown:
                    debug("unknown=" + keyStroke);
                    print(keyStroke.getCsi().rawCommand); // pass it back if we don't know what it is
                    break;
            }
        }
        return null;
    }

    public ISO6429Terminal getTerminal() {
        return terminal;
    }

    private int findPreviousBreak(StringBuilder x, int startPosition) {
        for (int i = startPosition - 1; 0 <= i; i--) {
            char c = x.charAt(i);
            if (c == '[' || c == '(' || c == ' ' || c == ',' || c == '#') return i;
        }
        return -1; // so start of string.
    }

    @Override
    public void print(Object x) {
        if (x == null) {
            return;
        }
        terminal.getCharPS().print(x);
        terminal.getCharPS().flush();
    }

    @Override
    public void println(Object x) {
        terminal.getCharPS().println(x);
        terminal.getCharPS().flush();

    }

    @Override
    public void flush() {
        terminal.getCharPS().flush();
    }

    ISO6429Terminal terminal;
    int defaultColor = 37;

    protected void init() throws IOException {
        terminal = new ISO6429Terminal(loggingFacade);
        terminal.setBold(true);
        terminal.setColor(defaultColor);
        commandCompletion = new ArrayList<>();
        int[] s = getScreenSize();
        if (s != null && s.length == 2) {
            System.out.println("screen size = " + s[0] + "x" + s[1]);
        }
        if (loggingFacade != null) {
            loggingFacade.setDebugOn(debugON);
        }
    }

    public int[] getScreenSize() throws IOException {
        if (screenSize == null) {
            screenSize = getTerminal().getScreenSize();
        }
        return screenSize;
    }

    int[] screenSize = null;

    public static void main(String[] args) {
        try {
            ISO6429IO clMinder = new ISO6429IO();
            clMinder.init();
            String current = "";
            while (!current.equals("exit")) {
                current = clMinder.readline("foo>");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected void info(String x) {
        if (loggingFacade != null) {
            loggingFacade.info(x);
        }
    }

    protected void warn(String x, Throwable t) {
        if (loggingFacade != null) {
            loggingFacade.warn(x, t);
        }
    }

    protected void debug(String x) {
        if (loggingFacade != null) {
            loggingFacade.debug(x);
        }
    }
}
/*
a
b
c
d
e
f

 */