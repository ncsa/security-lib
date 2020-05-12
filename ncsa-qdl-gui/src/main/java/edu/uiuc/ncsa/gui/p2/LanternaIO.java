package edu.uiuc.ncsa.gui.p2;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
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

    public LanternaIO(Terminal terminal) {
        this.terminal = terminal;
        isUnix = terminal instanceof UnixTerminal;
        defaultTextColor = TextColor.ANSI.GREEN;
    }

    @Override
    public String readline(String prompt) throws IOException {
        print(prompt);
        String out =  readInput();
        cursorNewLine();
        return out;
    }

    @Override
    public String readline() throws IOException {
        String out =  readInput();
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
        System.out.println("in cursor newLine ");

        if (isUnix) {
            println("");
            return;
        }
        terminal.putCharacter('\n');
    }

    TextColor defaultTextColor;
    ArrayList<String> commandBuffer = new ArrayList<>();
    int commandBufferMaxWidth = 0;

    protected String readInput() throws IOException {
        terminal.setForegroundColor(defaultTextColor); // just in case

        boolean keepReading = true;
        int line = 0;
        StringBuffer stringBuffer = new StringBuffer();
        int currentBufferPosition = -1;
         boolean commandHistoryActive = false;

        while (keepReading) {
            KeyStroke keyStroke = terminal.readInput(); //Block input or this does not draw right at all.
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

                        String out;
                        if (currentBufferPosition < 0 || commandBuffer.isEmpty()) {
                            out = stringBuffer.toString();
                            System.out.println("Buffer = " + out);
                            System.out.println("History = " + commandBuffer);
                            commandBuffer.add(0, stringBuffer.toString());
                            commandBufferMaxWidth = Math.max(commandBufferMaxWidth, stringBuffer.length());
                        } else {
                            out = commandBuffer.get(currentBufferPosition);
                        }
                        //cursorNewLine();
                        return out;
                    case ArrowUp:
                        if (!commandBuffer.isEmpty()) {
                            terminal.setCursorPosition(0, terminal.getCursorPosition().getRow());
                            terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                            currentBufferPosition = Math.min(++currentBufferPosition, commandBuffer.size() - 1);
                            print(StringUtils.pad(commandBuffer.get(currentBufferPosition), commandBufferMaxWidth));
                            commandHistoryActive = true;
                        }
                        terminal.flush();
                        break;
                    case ArrowDown:
                        if (!commandBuffer.isEmpty()) {
                            terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
                            terminal.setCursorPosition(0, terminal.getCursorPosition().getRow());
                            currentBufferPosition = Math.max(--currentBufferPosition, 0);
                            print(commandBuffer.get(currentBufferPosition));
                            terminal.flush();
                        }
                        break;
                    case Character:
                        // currentBufferPosition = 0;
                        stringBuffer.append(keyStroke.getCharacter());
                       // terminal.putCharacter(keyStroke.getCharacter());
                        print(keyStroke.getCharacter());
                        flush();
                        break;
                    case ArrowLeft:
                        currentBufferPosition = 0;

                        terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case ArrowRight:
                        // Move cursor right, don't overrun end of line.
                        currentBufferPosition = 0;
                        terminal.setCursorPosition(
                                Math.min(stringBuffer.length() - 1, terminal.getCursorPosition().getColumn() + 1),
                                terminal.getCursorPosition().getRow());
                        terminal.flush();
                        break;
                    case Backspace:
                        if (stringBuffer != null && 0 < stringBuffer.length()) {
                            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                            terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            terminal.putCharacter(' '); // blank what was there
                            terminal.setCursorPosition(terminal.getCursorPosition().getColumn() - 1, terminal.getCursorPosition().getRow());
                            terminal.flush();
                        }
                        break;
                    default:

                }
            }
        }
        return "";
    }
}
