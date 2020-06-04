package edu.uiuc.ncsa.demo;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

/**
 * JLine library test. It is actually very hard to get Java to read a character from input.
 * This library does that. This will read input. Note that for cursor keys, the print command
 * is intercepted by the terminal (since it is an ANSI code) and the cursor moves around the screen.
 * Same for various other keys (page up, down etc.) So this gives very primitive cursor addressing as is.
 * This is in line for us to support ANSI standard = ECMA-48 = ISO-6429 specs.
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/20 at  2:25 PM
 */
public class JLineTest {
    public static void main(String[] args) throws Throwable {
        Terminal terminal = TerminalBuilder.terminal();
        terminal.enterRawMode();
        NonBlockingReader reader = terminal.reader();
        String queue = "";
        while (true) {
            // control characters typically are returned as sets of numbers. So
            // back arrow is 279168 which parses as 27 91 68 < -- >  \033[D
            // IN this way control characters from input are embedded in the response.
            // Regular ascii characters are simple
            int c = reader.read();
            //System.out.println(c);
            if (c == 'q') {
                System.out.println("\nqueue=" + queue);
                return;
            }
            queue = queue + c;
            if(c < 255) {
                System.out.print((char) c);
                System.out.flush();   // or nothing displays
            }
        }
    }
}
