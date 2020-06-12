package edu.uiuc.ncsa.qdl.gui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.BasicIO;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/20 at  7:32 AM
 */
public class LanternaIO extends BasicIO {
    Terminal terminal;
    int line = 0;
    boolean isUnix = false;
    Screen screen;

    public LanternaIO(Terminal terminal, Screen screen) {
        this.terminal = terminal;
        isUnix = terminal instanceof UnixTerminal;
        defaultTextColor = TextColor.ANSI.WHITE;
        this.screen = screen;
    }

    @Override
    public String readline(String prompt) throws IOException {
        print(prompt);
        String out = readInput();
        cursorNewLine();
        return out;
    }

    @Override
    public String readline() throws IOException {
        String out = readInput();
        cursorNewLine();
        return out;
    }


    @Override
    public void print(Object x) {
        if (isUnix) {
            super.print(x);
            flush();
            return;
        }
        String y = null;
        if (x == null) {
            y = "null";
        } else {
            y = x.toString();
        }
        try {
            for (char c : y.toCharArray()) {
                terminal.putCharacter(c);
            }
            flush();
        } catch (IOException iox) {
            iox.printStackTrace();
        }
    }

    @Override
    public void println(Object x) {
        if (isUnix) {
            super.println(x);
            flush();
            return;
        }
        String y = null;
        if (x == null) {
            y = "null";
        } else {
            y = x.toString();
        }

        boolean addNewLine = y.endsWith("/n");
        try {
            for (char c : y.toCharArray()) {
                terminal.putCharacter(c);
            }
            if (!addNewLine) {
                cursorNewLine();
            }
            flush();
        } catch (IOException iox) {
            iox.printStackTrace();
        }

    }

    @Override
    public void flush() {
        if (isUnix) {
            super.flush();
            return;
        }
        try {
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void cursorNewLine() throws IOException {
        if (isUnix) {
            println("");
            return;
        }
        terminal.putCharacter('\n');
    }

    TextColor defaultTextColor;
    ArrayList<StringBuilder> commandBuffer = new ArrayList<>();
    int commandBufferMaxWidth = 0;

    /**
     * Turns out that Lanterna uses a polling method to get the input (and get around the inability of Java
     * to actually read individual characters). As such it polls every 1/4 second, limiting pasting, e.g.,
     * to
     * 4 char per second (!!!!) Making it impossible to paste longer text. 1600 chars (fair sized JSON blob)
     * takes 400 sec or about 12 minutes.
     * @return
     * @throws IOException
     */
    protected String readInput() throws IOException {
        terminal.setForegroundColor(defaultTextColor); // just in case
        int startCol = terminal.getCursorPosition().getColumn(); // column where we start
        int startRow = terminal.getCursorPosition().getRow(); // row where we start
        boolean keepReading = true;
        int line = 0;
        StringBuilder currentLine = new StringBuilder();
        commandBuffer.add(0, currentLine); // push it on the stack
        int currentBufferPosition = 0;  // start at current line

        while (keepReading) {
            KeyStroke keyStroke = terminal.readInput(); //Block input or this does not draw right at all(!).
            if (keyStroke != null) {
           

                switch (keyStroke.getKeyType()) {
                    case MouseEvent:
                        System.out.println("Yo!" + keyStroke);
                        break;


                    case Escape:
                        // return stringBuffer.toString() + "\n";
                    case EOF: // If there is some issue shutting down the JVM, it starts spitting these out. Just exit.
                        keepReading = false;
                        break;
                    case Enter:
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
                        return currentLine.toString();
                    case ArrowUp:
                        if (!commandBuffer.isEmpty()) {
                            terminal.setCursorPosition(startCol, terminal.getCursorPosition().getRow());
                            //    terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                            currentBufferPosition = Math.min(++currentBufferPosition, commandBuffer.size() - 1);
                            currentLine = new StringBuilder(commandBuffer.get(currentBufferPosition));
                            print(StringUtils.pad2(currentLine.toString(), commandBufferMaxWidth));
                            terminal.setCursorPosition(startCol, startRow);
                            flush();
                        }
                        break;
                    case ArrowDown:
                        if (!commandBuffer.isEmpty()) {
                            //      terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                            terminal.setCursorPosition(startCol, terminal.getCursorPosition().getRow());
                            currentBufferPosition = Math.max(--currentBufferPosition, 0);
                            currentLine = new StringBuilder(commandBuffer.get(currentBufferPosition));
                            print(StringUtils.pad2(currentLine.toString(), commandBufferMaxWidth));
                            terminal.setCursorPosition(startCol, startRow);
                            flush();
                        }
                        break;
                    case Character:
                        int currentCol0 = terminal.getCursorPosition().getColumn();
                        int position = currentCol0 - startCol;
                        char character = keyStroke.getCharacter();

                        if (position < 0) {
                            position = 0;
                        }
                        if (currentLine.length() < position) {
                            position = currentLine.length() - 1;
                        }

                        currentLine.insert(position, character);
                        terminal.setCursorPosition(startCol + position, terminal.getCursorPosition().getRow());
                        print(currentLine.substring(position));
                        terminal.setCursorPosition(startCol + position + 1, terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case ArrowLeft:
                        terminal.setCursorPosition(
                                Math.max(0, terminal.getCursorPosition().getColumn() - 1),
                                terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case ArrowRight:
                        // Move cursor right, don't overrun end of line.
                        terminal.setCursorPosition(
                                Math.min(startCol + currentLine.length(),
                                        terminal.getCursorPosition().getColumn() + 1),
                                terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case Backspace:
                        // delete character to LEFT of cursor and redraw
                        int currentCol = terminal.getCursorPosition().getColumn() - startCol;
                        int currentRow = terminal.getCursorPosition().getRow();
                        if (0 < currentCol && 0 < currentLine.length()) {
                            currentCol = Math.max(0, currentCol - 1);
                            currentLine = currentLine.deleteCharAt(currentCol);
                            terminal.setCursorPosition(startCol + currentCol, currentRow);
                            //     terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            // terminal.flush();
                            print(currentLine.substring(currentCol) + " "); // blanks out last char
                            terminal.setCursorPosition(startCol + currentCol, currentRow);
                            terminal.flush();
                        }
                        break;
                    case Delete:
                        // delete character to RIGHT of cursor and redraw
                        int currentCol1 = terminal.getCursorPosition().getColumn() - startCol;
                        int currentRow1 = terminal.getCursorPosition().getRow();

                        if (0 < currentCol1 && currentCol1 < currentLine.length()) {
                            currentCol1 = Math.min(startCol + currentLine.length() - 1, currentCol1);

                            currentLine = currentLine.deleteCharAt(currentCol1);
                            terminal.setCursorPosition(startCol + currentCol1, currentRow1);
                            print(currentLine.substring(currentCol1) + " "); // blanks out last char
                            terminal.setCursorPosition(startCol + currentCol1, currentRow1);
                            flush();
                        }
                        break;
                    case End:
                        terminal.setCursorPosition(startCol + currentLine.length(), startRow);
                        flush();
                        break;
                    case Home:
                        terminal.setCursorPosition(startCol, startRow);
                        flush();
                        break;
//         To do proper scrolling will require a lot more work. This starts page up and down.
                    // Warning that Lanterna clears away anything that is scrolled, apparently
                    // for good since there is no way to get it out of the screen's back buffer.
                    // They explicitly warn against redrawing the screen repeatedly by the
                    // use since that is really slow and clogs up the system. If they
                    // have a fully functioning scrolling system, I have yet to find it.
/*

                    case PageUp:
                        int x = terminal.getTerminalSize().getRows();
                        screen.scrollLines(0, x, 5);
                        screen.refresh(Screen.RefreshType.DELTA);
                        break;
                    case PageDown:
                        x = terminal.getTerminalSize().getRows();
                        screen.scrollLines(0, x, -5);
                        screen.refresh(Screen.RefreshType.DELTA);
                        break;

*/

                    default:

                }
            }
        }
        return "";
    }
}
