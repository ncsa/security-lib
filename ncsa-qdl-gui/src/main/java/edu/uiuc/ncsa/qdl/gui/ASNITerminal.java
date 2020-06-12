package edu.uiuc.ncsa.qdl.gui;

import edu.uiuc.ncsa.security.util.cli.IOInterface;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/5/20 at  11:48 AM
 */
public class ASNITerminal implements IOInterface {
    public ASNITerminal() throws IOException {
        terminal = TerminalBuilder.terminal();
        terminal.enterRawMode();
        reader = terminal.reader();
    }
    static final int Escape = 270000;
    static final int Bracket = 9100;
    static final int ArrowUp= Escape + Bracket + 'A';
    static final int ArrowDown= Escape + Bracket + 'B';
    static final int ArrowRight= Escape + Bracket + 'C';
    static final int ArrowLeft= Escape + Bracket + 'D';
    NonBlockingReader reader = null;
    Terminal terminal = null;
    @Override
    public String readline(String prompt) throws IOException {
        boolean keepReading = true;
        while(keepReading){
            int c = reader.read();
            switch (c){
                case ArrowUp:
                    break;
                case ArrowDown:
                    break;
                case ArrowLeft:
                    break;
                case ArrowRight:
                    break;
                default:
                    char nextChar = (char)c;
            }
        }
        return null;
    }

    @Override
    public String readline() throws IOException {
        return null;
    }

    @Override
    public void print(Object x) {

    }

    @Override
    public void println(Object x) {

    }

    @Override
    public void flush() {

    }
}
