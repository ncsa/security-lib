package edu.uiuc.ncsa.security.util.terminal;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.IOInterface;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * ISO 6429 (cursor addressing spec) compliant implementation of the {@link IOInterface}.
 * This has a basic command line history accessible with up and down arrows.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 6/9/20 at  7:52 AM
 */
public class ISO6429IO implements IOInterface {

    // This turns it on for everything and is ONLY a low-level debug hack.
    // This will put out a lot of output since it does spit out each keystroke!
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

    boolean bufferingOn = false;
    /*
     * For debugging, use the ansi shell script in ~/security-all (top-level). This will do the minimal
     * compile then start up the minimal QDL workspace from maven, so the entire build does not have to be done.
    */
    // This next flag will keep a running list of the debug output and paste it into the current
    // terminal when you toggle paste mode (ctrl+p). This is very useful in isolating exactly what you
    // just did rather than digging in the logs for it.
    // Use in conjunction with the ansi script mentioned above.
    boolean showDebugBuffer = false;

    @Override
    public String readline(String prompt) throws IOException {
        StringBuilder currentLine = new StringBuilder();
        if (prompt == null) {
            prompt = "";
        }
        int currentCol0 = -1;
        int startCol = 0;
        print(prompt);
        debug("Command buffering is " + (isBufferingOn() ? "ON" : "OFF"));
        if (isQueueEmpty()) {
            debug("queue empty");
            startCol = terminal.getCursorCol();
            currentCol0 = startCol;
            commandBuffer.add(0, currentLine); // push it on the stack
        } else {
            debug("queue not empty:" + queue.size() + " entries");

            String x = queue.get(0);

            queue.remove(0);
            if (isQueueEmpty()) {
                // no null termination=? partial line
                debug("partial line:\"" + x + "\"");
                currentLine.append(x);

                print(x);
                currentCol0 = x.length();
                startCol = currentCol0;
                terminal.setCursorCol(currentCol0);

            } else {
                if (queue.get(0) == null) {
                    queue.remove(0); // empty Q so no false positives
                    // This denotes that the current line ended with a CR/LF so return it.
                }
                debug("full line:\"" + x + "\"");

                println(x);
                return x;
            }
        }


        boolean keepLooping = true;
        // column where we start. It is best to ping the terminal itself for this, since some start
        // at 0 as the first column and some start at 1. Best to punt and just ask.
        int width = terminal.getScreenSize()[1];

        int currentBufferPosition = 0;  // start at current line
        boolean pasteModeOn = false;
        while (keepLooping) {
            KeyStroke keyStroke = terminal.getCharacter();
            debug("reading keystroke: " + keyStroke);
            switch (keyStroke.getKeyType()) {
                case Character:

                    int position = currentCol0 - startCol;
                    debug("in char, pos = " + position);
                    char character = keyStroke.getCharacter();
                    if (position < 0) {
                        position = 0;
                    }
                    if (currentLine.length() < position) {
                        position = currentLine.length() - 1;
                    }
                    debug("inserting char " + character);
                    currentLine.insert(position, character);
                    debug("preprint length = " + currentLine.length() + " position =" + position + " cursor=" + terminal.getCursorCol() + " my cursor=" + currentCol0);

                    print(currentLine.substring(position));
                    currentCol0++; // increment the cursor position.

                    debug("postprint length = " + currentLine.length() + " position =" + position + " cursor=" + terminal.getCursorCol() + " my cursor=" + currentCol0);
                    terminal.setCursorCol(currentCol0);
                    if (currentCol0 % width == 0) {
                        debug("adding CR/LF");
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
                    if(!pasteModeOn && showDebugBuffer){
                        System.out.println(stringBuffer.toString());
                        stringBuffer = new StringBuffer();
                    }
                    pasteModeOn = !pasteModeOn;
                    if (pasteModeOn) {
                        debug("paste mode ON");
                        terminal.setBold(false);
                        terminal.setColor(32);
                        println("<paste mode on. ^v pastes from clipboard, ^p toggles paste mode>");
                    } else {
                        debug("paste mode OFF");

                        flush();
                        println("\n<paste mode off>");
                        terminal.setBold(false);
                        terminal.setColor(defaultColor);
                    }
                    break;
                case ArrowUp:
                    if (!isBufferingOn()) {
                        break;
                    }

                    if (!commandBuffer.isEmpty()) {
                        terminal.clearLine(startCol);
                        currentBufferPosition = Math.min(++currentBufferPosition, commandBuffer.size() - 1);
                        currentLine = new StringBuilder(commandBuffer.get(currentBufferPosition));
                        print(currentLine);
                        currentCol0 = terminal.getCursorCol();
                    }
                    break;
                case ArrowDown:
                    if (!isBufferingOn()) {
                        break;
                    }
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
                    currentCol0 = Math.max(startCol, terminal.getCursorCol() - 1);
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
                    if (isBufferingOn()) {
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
                case ClipboardPaste:
                    debug("Got paste, pasting");

                    try {
                        Toolkit toolKit = Toolkit.getDefaultToolkit();
                        Clipboard clipboard = toolKit.getSystemClipboard();
                        String result = (String) clipboard.getData(DataFlavor.stringFlavor);

                        String[] lines = result.split("\n");
                        boolean hasCR = result.endsWith("\n");
                        debug("got " + lines.length + " lines:" + Arrays.toString(lines));
                        if (lines.length <= 1) {
                            // check for paste, no new line ==> insert text.
                            position = currentCol0 - startCol;

                            if (position < 0) {
                                position = 0;
                            }
                            if (currentLine.length() < position) {
                                position = currentLine.length() - 1;
                            }
                            currentLine.insert(position, result);
                            terminal.setCursorCol(startCol + position);
                            print(currentLine.substring(position));
                            currentCol0 = startCol + position + result.length();
                            terminal.setCursorCol(currentCol0);
                        } else {
                            for (String line : lines) {
                                queue.add(line);
                            }
                            if (hasCR) {
                                debug("Adding null termination");
                                queue.add(null); // marker! Paste may be null terminated => last line ended with a CR/LF
                            }
                            info("got from clipboard:" + result);
                            currentLine.append(queue.get(0));
                            queue.remove(0);
                            println(currentLine.toString());
                            return currentLine.toString();

                        }
                    } catch (Throwable e) {
                        println("cannot read from clipboard");
                        warn("unable to get clipboard", e);
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
      StringBuffer stringBuffer = new StringBuffer();
    protected void debug(String x) {
        if(showDebugBuffer) {
            stringBuffer.append(x + "\n");
        }
        if (loggingFacade != null) {
            loggingFacade.debug(x);
        }
    }

    ArrayList<String> queue = new ArrayList<>();

    @Override
    public void clearQueue() {
        queue.clear();
    }

    @Override
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    @Override
    public void setBufferingOn(boolean bufferingOn) {
        this.bufferingOn = bufferingOn;
    }

    @Override
    public boolean isBufferingOn() {
        return bufferingOn;
    }
}
/* for testing cut and paste
a
b
c
d
e
f

 */