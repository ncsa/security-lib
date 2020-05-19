package edu.uiuc.ncsa.qdl.gui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.BasicIO;

import java.io.IOException;
import java.util.ArrayList;

/** trying to do the IO with the screen object not the terminal object in Lanterna.
 * In development!
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/20 at  7:32 AM
 */
public class LanternaScreenIO extends BasicIO {
    Terminal terminal;
    int line = 0;
    boolean isUnix = false;
    Screen screen;
    TextGraphics textGraphics;

    public LanternaScreenIO(Terminal terminal, Screen screen, TextGraphics textGraphics) {
        this.terminal = terminal;
        isUnix = terminal instanceof UnixTerminal;
        defaultTextColor = TextColor.ANSI.WHITE;
        this.screen = screen;
        this.textGraphics = textGraphics;
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
        String y = null;
        if (x == null) {
            y = "null";
        } else {
            y = x.toString();
        }
        TerminalPosition terminalPosition = screen.getCursorPosition();

        if (isUnix) {
            super.print(x);
        }else {
            textGraphics.putString(0, terminalPosition.getRow(), y);
        }
        screen.setCursorPosition(new TerminalPosition( y.length(), terminalPosition.getRow()));
        flush();
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

        print(y);
        cursorNewLine();
        flush();

        boolean addNewLine = y.endsWith("\n");
          if (!addNewLine) {
              cursorNewLine();
              flush();
          }
    }

    @Override
    public void flush() {
        if (isUnix) {
            super.flush();
            return;
        }
        try {
            screen.refresh(Screen.RefreshType.COMPLETE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    TerminalPosition getCurrentCursorPosition() {
        return screen.getCursorPosition();
    }

    protected void cursorNewLine() {
        if (isUnix) {
            println("");
            //return;
        }
        screen.setCursorPosition(new TerminalPosition(0, getCurrentCursorPosition().getRow() + 1));
    }

    TextColor defaultTextColor;
    ArrayList<StringBuilder> commandBuffer = new ArrayList<>();
    int commandBufferMaxWidth = 0;

    protected String readInput() throws IOException {
        //terminal.setForegroundColor(defaultTextColor); // just in case

        int startCol = getCurrentCursorPosition().getColumn(); // column where we start
        int startRow = getCurrentCursorPosition().getRow(); // row where we start
        boolean keepReading = true;
        int line = 0;
        StringBuilder currentLine = new StringBuilder();
        commandBuffer.add(0, currentLine); // push it on the stack
        int currentBufferPosition = 0;  // start at current line

        while (keepReading) {
            KeyStroke keyStroke = screen.readInput(); //Block input or this does not draw right at all(!).
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
                            screen.setCursorPosition(new TerminalPosition(startCol, screen.getCursorPosition().getRow()));
                            //    terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                            currentBufferPosition = Math.min(++currentBufferPosition, commandBuffer.size() - 1);
                            currentLine = new StringBuilder(commandBuffer.get(currentBufferPosition));
                            print(StringUtils.pad2(currentLine.toString(), commandBufferMaxWidth));
                            screen.setCursorPosition(new TerminalPosition(startCol, startRow));
                            flush();
                        }
                        break;
                    case ArrowDown:
                        if (!commandBuffer.isEmpty()) {
                            //      terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                            screen.setCursorPosition(new TerminalPosition(startCol, terminal.getCursorPosition().getRow()));
                            currentBufferPosition = Math.max(--currentBufferPosition, 0);
                            currentLine = new StringBuilder(commandBuffer.get(currentBufferPosition));
                            print(StringUtils.pad2(currentLine.toString(), commandBufferMaxWidth));
                            screen.setCursorPosition(new TerminalPosition(startCol, startRow));
                            flush();
                        }
                        break;
                    case Character:
                        int currentCol0 = screen.getCursorPosition().getColumn();
                        int position = currentCol0 - startCol;
                        char character = keyStroke.getCharacter();

                        if (position < 0) {
                            position = 0;
                        }
                        if (currentLine.length() < position) {
                            position = currentLine.length() - 1;
                        }

                        currentLine.insert(position, character);
                        screen.setCursorPosition(new TerminalPosition(startCol + position, terminal.getCursorPosition().getRow()));
                        print(currentLine.substring(position));
                        screen.setCursorPosition(new TerminalPosition(startCol + position + 1, terminal.getCursorPosition().getRow()));
                        flush();
                        break;
                    case ArrowLeft:
                        screen.setCursorPosition(new TerminalPosition(
                                Math.max(0, terminal.getCursorPosition().getColumn() - 1),
                                terminal.getCursorPosition().getRow()));
                        flush();
                        break;
                    case ArrowRight:
                        // Move cursor right, don't overrun end of line.
                        screen.setCursorPosition(new TerminalPosition(
                                Math.min(startCol + currentLine.length(),
                                        terminal.getCursorPosition().getColumn() + 1),
                                terminal.getCursorPosition().getRow()));
                        flush();
                        break;
                    case Backspace:
                        // delete character to LEFT of cursor and redraw
                        int currentCol = getCurrentCursorPosition().getColumn() - startCol;
                        int currentRow = getCurrentCursorPosition().getRow();
                        if (0 < currentCol && 0 < currentLine.length()) {
                            currentCol = Math.max(0, currentCol - 1);
                            currentLine = currentLine.deleteCharAt(currentCol);
                            screen.setCursorPosition(new TerminalPosition(startCol + currentCol, currentRow));
                            //     terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            // terminal.flush();
                            print(currentLine.substring(currentCol) + " "); // blanks out last char
                            screen.setCursorPosition(new TerminalPosition(startCol + currentCol, currentRow));
                            flush();
                        }
                        break;
                    case Delete:
                        // delete character to RIGHT of cursor and redraw
                        int currentCol1 = getCurrentCursorPosition().getColumn() - startCol;
                        int currentRow1 = getCurrentCursorPosition().getRow();

                        if (0 < currentCol1 && currentCol1 < currentLine.length()) {
                            currentCol1 = Math.min(startCol + currentLine.length() - 1, currentCol1);

                            currentLine = currentLine.deleteCharAt(currentCol1);
                            screen.setCursorPosition(new TerminalPosition(startCol + currentCol1, currentRow1));
                            print(currentLine.substring(currentCol1) + " "); // blanks out last char
                            screen.setCursorPosition(new TerminalPosition(startCol + currentCol1, currentRow1));
                            flush();
                        }
                        break;
                    case End:
                        screen.setCursorPosition(new TerminalPosition(startCol + currentLine.length(), startRow));
                        flush();
                        break;
                    case Home:
                        screen.setCursorPosition(new TerminalPosition(startCol, startRow));
                        flush();
                        break;
                    //    To do proper scrolling will require a lot more work. This starts page up and down.

                    case PageUp:
                        int x = terminal.getTerminalSize().getRows();
                   //     screen.clear();
                        screen.scrollLines(0, x, 10);
                       // screen.setCursorPosition(new TerminalPosition(0, 0));
                        flush();
                        break;
                    case PageDown:
                        x = terminal.getTerminalSize().getRows();
                     //   screen.clear();
                        screen.scrollLines(0, x, -10); // scroll in units of 25 lines.
                     //   screen.setCursorPosition(new TerminalPosition(0, 0));
                        flush();
                        break;
                    default:

                }
            }
        }
        return "";
    }
}
