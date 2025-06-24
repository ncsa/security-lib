package edu.uiuc.ncsa.security.util.terminal;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * A <b>basic</b> implementation of an ISO 6429 terminal. This sends along device control characters to do hardware
 * cursor addressing using standard I/O. Several caveats come with this.
 * <ul>
 *     <li>This is not a complete implementation -- the spec. even states a full implementation is not possible.
 *     We want to allow for a command history and several other things which Java just does not do consistently cross platform.</li>
 *     <li>The old ANSI (late 1970's) terminal standard was replaced by the ECMA-48 (early 1990's) standard which in turn
 *     was replaced by the ISO 6429 standard (in the 2000's).
 *     This means that ANSI terminals do support this.</li>
 *     <li>Unix command lines are <i>de facto</i> ANSI standard because they started that way and nobody wants to invest the time to change it.
 *     In theory this might not work in some unix environments.</li>
 *     <li>DOS and therefore Windows did not support ANSI natively. As of Windows 10, however, their command line support the ISO
 *     standard. This means that by and large this should now work with Unix, Windows and Mac. For Windows users, it may require
 *     some configuration in the OS to enable ANSI support in command lines though.</li>
 * </ul>
 * Typically, if the device does not support this, you start seeing sets of weird characters around your text.
 * A bunch of control sequence are pretty well laid out here : https://en.wikipedia.org/wiki/ANSI_escape_code
 * <p>Created by Jeff Gaynor<br>
 * on 6/5/20 at  8:42 AM
 */
public class ISO6429Terminal {
    MyLoggingFacade loggingFacade;

    public ISO6429Terminal(MyLoggingFacade loggingFacade) throws IOException {
        init();
        this.loggingFacade = loggingFacade;
    }

    public boolean testTerminal() throws IOException {
        getCommandPS().print("\033[5n");
        getCommandPS().flush();
        KeyStroke resp = getCharacter();
        // If this is compliant, the terminal should respond with
        return resp.getCsi().op == 'n' && resp.getCsi().parameters.length == 1 && resp.getCsi().parameters[0] == 0;

    }

    public int[] getScreenSize() throws IOException {
        Size size = terminal.getSize(); // Um... *Should* work.
        if(size.getColumns() != 0 && size.getRows() != 0) {
            return new int[]{size.getRows(), size.getColumns()};
        }
      // Trick: Put the cursor in outer space and ask where it ended up. Kludgey but works across the board.
        int[] currentPos = getCursor();
        setCursor(10000, 10000);
        int[] newPos = getCursor();
        setCursor(currentPos[0], currentPos[1]);
        return newPos;

    }

    Terminal terminal = null;
    protected NonBlockingReader reader = null;

    protected void init() throws IOException {
        terminal = TerminalBuilder.terminal();
        terminal.enterRawMode();
        reader = terminal.reader();
    }

    public String getName() {
        return terminal.getName();
    }

    public void setCursorCol(int col) {
        getCommandPS().print("\033[" + col + "G");
        getCommandPS().flush();
    }

    public void setCursor(int row, int col) {
        getCommandPS().print("\033[" + row + ";" + col + "H");
        getCommandPS().flush();
    }

    /**
     * clear the entire current line. Put the cursor at the front of the line
     */
    public void clearLine(int startCol) {
        getCommandPS().print("\033[" + startCol + "G\033[0K"); // put cursor, delete to end of line
        getCommandPS().flush();
    }

    public int getCursorCol() throws IOException {
        return getCursor()[1];
    }

    public int getCursorRow() throws IOException {
        return getCursor()[0];
    }

    public int[] getCursor() throws IOException {
   //     debug("getting cursor");
/*        IntConsumer intConsumer = new IntConsumer() {
            @Override
            public void accept(int value) {
                debug(" int consumer v=" + value);
            }
        };
        Cursor cursor = terminal.getCursorPosition(intConsumer);
        debug("got cursor");
        return new int[]{cursor.getX(), cursor.getY()};*/

        int[] cPos = new int[2]; // row and column
        getCommandPS().print("\033[6n");
        getCommandPS().flush();
        KeyStroke keyStroke = getCharacter(); // read the response
        if (keyStroke.csi == null) {
  //          debug(" got cursor for null");
            return new int[]{-1, -1};
        }
        if (keyStroke.getCsi().parameters.length == 1) {
    //        debug(" got cursor with 1 arg");
            return new int[]{1, keyStroke.getCsi().parameters[0]};
        }
   //     debug(" got cursor");

        return keyStroke.getCsi().parameters;

    }

    public void setColor(int color) {
        getCommandPS().print("\033[" + color + "m");
        getCommandPS().flush();

    }

    public void setBold(boolean setOn) {
        getCommandPS().print("\033[" + (setOn ? "1" : "0") + "m");
        getCommandPS().flush();

    }

    PrintStream cps = null;

    protected PrintStream getCommandPS() {
        //    return getCharPS();

        if (cps == null) {
              cps = System.out;
            //cps = System.err;
        }
        return cps;

    }

    PrintStream charPS = null;

    protected PrintStream getCharPS() {
        if (charPS == null) {
            charPS = System.out;
        }
        return charPS;
    }


    protected KeyStroke getCharacter() throws IOException {
        int x = reader.read();
     //   debug("  > 1st char =" + x);
        CSI csi;
        // returned number is exactly a control char
        switch (x) {
            // Actually, intercepting ^c does not work from the CLI because the
            // OS intercepts it and shuts down the JVM, so Java is out of the loop.
            case 3: // ^C
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 9);
                return new KeyStroke(KeyType.ControlC, csi);

            case 9: // Tab
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 9);
                return new KeyStroke(KeyType.Tab, csi);

            case 13: // ^M = return
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 13);
                return new KeyStroke(KeyType.Enter, csi);
            case 16:// ^P
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 13);
                return new KeyStroke(KeyType.PasteMode, csi);
            case 22: // ^V
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 22);
                return new KeyStroke(KeyType.ClipboardPaste, csi);
            case 127: // Backspace
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 127);
                return new KeyStroke(KeyType.Backspace, csi);
        }
        // Process escape. Sequence come as e.g. ESC [ A (== up arrow)
        // OR the user just might press escape. This tests that if the user
        // just presses escape and waits then it is interpreted as an escape.
        if (x == 27) {
            if (reader.peek(1) == -2) {
                // trickery. If the user types a control key, then there is no appreciable
                // lag and this will never time out.
                // If the user hits just escape then waits a split second
                // this will pick it up.
                // There has to be a better way to pick up on this...
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 27);
    //            debug("returning escape");

                return new KeyStroke(KeyType.Escape, csi);
            }
            // start of standard escape sequence.
            int y = reader.read();
    //        debug("  > 2nd char =" + y);

            // handle ALT char remappings first
            // These are of the form 'ESC char' and are case sensitive
            // so
            // ESC A = alt+shift + a was pressed vs.
            // ESC a = alt + a was pressed
            // If there is a byte after this, then it is part of a control
            // sequence, so do not convert to a special char.
            KeyStroke x1 = getKeyRemap((char) y);
            if (x1 != null) return x1;

            if (y == 91) { // ASCII 91 == [
                try {
                    csi = getCSI();
                } catch (IllegalArgumentException iax) {
                    // If the user types in something strange, just ignore it.
                    return new KeyStroke((char) ' ');
                }
     //           debug(" Got CSI=" + csi);
                // start of escape [ sequence, so called CSI = "Control Sequence Introducer"
                switch (csi.op) {
                    case 'A':
                        return new KeyStroke(KeyType.ArrowUp, csi);
                    case 'B':
                        return new KeyStroke(KeyType.ArrowDown, csi);
                    case 'C':
                        return new KeyStroke(KeyType.ArrowRight, csi);
                    case 'D':
                        return new KeyStroke(KeyType.ArrowLeft, csi);
                    case 'E':
                        return new KeyStroke(KeyType.Enter, csi);
                    case '~':
                        switch (csi.parameters[0]) {
                            case 2:
                                return new KeyStroke(KeyType.Insert, csi);
                            case 3:
                                return new KeyStroke(KeyType.Delete, csi);
                            case 5:
                                return new KeyStroke(KeyType.PageUp, csi);
                            case 6:
                                return new KeyStroke(KeyType.PageDown, csi);
                        }

                    case 'F':
                        return new KeyStroke(KeyType.End, csi);
                    case 'H':
                        return new KeyStroke(KeyType.Home, csi);
                    case 'G':
                    case 'I':
                    case 'J':
                    case 'K':
                    case 'S':
                    case 'T':
                    default:
                        KeyStroke k = new KeyStroke(KeyType.Unknown, csi);
                        k.setCsi(csi);
                        return k;

                }
            }
            csi = new CSI();
            csi.rawCommand = "\033";
            return new KeyStroke(KeyType.Escape, csi);
        }

      //  debug(" returning char");
        return new KeyStroke((char) x);
    }

    /**
     * If you want to add key remappings (like QDL) override this.
     *
     * @param y the second byte after and escape from the terminal. If there is no more input after that, interpret
     *          it as being an alt or other key stroke.
     * @return
     * @throws IOException
     */
    protected KeyStroke getKeyRemap(char y) throws IOException {
        return null;
    }

    /**
     * You can run this with
     * mvn compile exec:java -Dexec.mainClass="edu.uiuc.ncsa.security.util.terminal.ISO6429Terminal"
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            // In Java, you can add a shutdown hook, but cannot stop
            // the actual JVM from shutting down with control+c. This
            // is because ^c is intercepted by the OS which sends a SigTerm.
            // Java processes the SigTerm and will let you add a shutdown hook
            // (e.g. below) but you cannot stop the JVM from exiting.
            // FYI since the JVM is multi-threaded, there is zero promise that
            // anything in the shutdown hook actually works. About the best you can
            // do is print a message saying you are shutting down.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("Inside Add Shutdown Hook");
                }
            });
            ISO6429Terminal t = new ISO6429Terminal(null);
            //       System.out.println("terminal Test:" + t.testTerminal());
            boolean keepReading = true;
            t.setColor(33);
            t.setCursor(10, 25);
            t.getCharPS().print("cursor addressing is fun!");
            t.getCursor();
            t.setCursorCol(15);
            t.getCharPS().print("set cursor");
            t.getCharPS().flush();
            t.setBold(true);
            t.setCursorCol(85);

            t.getCharPS().print("set cursor here");
            t.getCharPS().flush();
            t.setCursorCol(0);

            t.getCharPS().flush();

            while (keepReading) {

                KeyStroke keyStroke = t.getCharacter();
                System.out.println(keyStroke);
                if (keyStroke.getKeyType() == KeyType.Character) {
                    System.out.println(keyStroke.getCharacter());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CSI {
        static int MISSING_PARAMETER = -1;
        int[] parameters = new int[]{};
        char op;
        String intermediateCommands;
        String rawCommand = null;

        @Override
        public String toString() {
            return "CSI{" +
                    "raw=\"" + rawCommand.getBytes(StandardCharsets.UTF_8)[0] + "\"" +
                    ", parameters=" + Arrays.toString(parameters) +
                    ", op=" + op +
                    ", intermediateCommands='" + intermediateCommands + '\'' +
                    '}';
        }
    }

    protected CSI getCSI() throws IOException {
    //    debug("In getCSI()");
        CSI csi = new CSI();
        String raw = "\033[";
        StringBuilder parameterBytes = new StringBuilder();
        int x = reader.read();
   //     debug("csi x = " + x);
        // 0x30–0x3F
        while (0x30 <= x && x <= 0x3F) {
            parameterBytes.append((char) x);
            x = reader.read();
        }
        raw = raw + parameterBytes.toString();
        // This is the spec. that any chars in this range are allowed. IN PRACTICE however, only
        // integer lists delimited by semi-colons are allowed. There may be missing parameters

        if (parameterBytes.length() == 0) {
            csi.parameters = new int[]{CSI.MISSING_PARAMETER};
        } else {
            String pb = parameterBytes.toString();
            ArrayList<Integer> array = new ArrayList<>();

            if (pb.contains(";")) {
                StringTokenizer st = new StringTokenizer(pb, ";");
                while (st.hasMoreTokens()) {

                    String nt = st.nextToken();
                    if (StringUtils.isTrivial(nt)) {
                        array.add(CSI.MISSING_PARAMETER);
                    } else {
                        array.add(Integer.parseInt(nt));
                    }
                }
            } else {
                array.add(Integer.parseInt(pb)); // only an integer
            }

            csi.parameters = new int[array.size()];
            for (int i = 0; i < csi.parameters.length; i++) {
                csi.parameters[i] = array.get(i);
            }

        }
        StringBuilder intermediateBytes = new StringBuilder();
        // 0x20–0x2F
        while (0x20 <= x && x <= 0x2F) {
            intermediateBytes.append((char) x);
            x = reader.read();
        }
        raw = raw + intermediateBytes.toString();

        csi.intermediateCommands = intermediateBytes.toString();
        // so there should be a single final byte in this range:
        // 0x40–0x7E
    //    debug("after read x = " + x);
        if (!(0x40 <= x && x <= 0x7E)) {
            // not a final byte.
            throw new IllegalArgumentException("error: Unknown CSI terminator");
        }
        csi.op = (char) x;
        raw = raw + (char) x;
        csi.rawCommand = raw;

        return csi;
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
        // next line prints out everything asap to the command line.
        // Useful for hard to track down errors, but each character will
        // have a full output stack printed.
        //     System.out.println(x);
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