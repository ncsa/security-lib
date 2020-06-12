package edu.uiuc.ncsa.security.util.terminal;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.io.PrintStream;
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
 * <p>Created by Jeff Gaynor<br>
 * on 6/5/20 at  8:42 AM
 */
public class ISO6429Terminal{
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
        Size size = terminal.getSize();
        return new int[]{size.getRows(), size.getColumns()};
/*      // Trick: Put the cursor in outer space and ask where it ended up. Kludgey but works across the board.
        int[] currentPos = getCursor();
        setCursor(10000, 10000);
        int[] newPos = getCursor();
        setCursor(currentPos[0], currentPos[1]);
        return newPos;

*/
    }

    Terminal terminal = null;
    NonBlockingReader reader = null;

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
        debug("getting cursor");
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
            debug(" got cursor for null");
            return new int[]{-1, -1};
        }
        if (keyStroke.getCsi().parameters.length == 1) {
            debug(" got cursor with 1 arg");
            return new int[]{1, keyStroke.getCsi().parameters[0]};
        }
        debug(" got cursor");

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
          //  cps = System.out;
            cps = System.err;
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
        debug("  > got int=" + x);
        CSI csi;
        switch (x) {
            case 9:
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 9);
                return new KeyStroke(KeyType.Tab, csi);

            case 13:
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 13);
                return new KeyStroke(KeyType.Enter, csi);
            case 16:
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 13);
                return new KeyStroke(KeyType.PasteMode, csi);

            case 127:
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 127);
                return new KeyStroke(KeyType.Backspace, csi);
        }
        if (x == 27) {
            if (reader.peek(1) == -2) {
                // trickery. If the user types a control key, then there is no appreciable
                // lag and this will never time out.
                // If the user hits just escape then waits a split second
                // this will pick it up.
                // There has to be a better way to pick up on this...
                csi = new CSI();
                csi.rawCommand = String.valueOf((char) 27);

                return new KeyStroke(KeyType.Escape, csi);
            }
            // start of standard escape sequence.
            int y = reader.read();
            if (y == 91) {
                csi = getCSI();
                // start of escape [ sequence, so called CSI
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
        return new KeyStroke((char) x);
    }

    public static void main(String[] args) {
        try {
            ISO6429Terminal t = new ISO6429Terminal(null);
            System.out.println("terminal Test:" + t.testTerminal());
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
        int[] parameters;
        char op;
        String intermediateCommands;
        String rawCommand = null;

        @Override
        public String toString() {
            return "CSI{" +
                    "raw=\"" + rawCommand + "\"" +
                    ", parameters=" + Arrays.toString(parameters) +
                    ", op=" + op +
                    ", intermediateCommands='" + intermediateCommands + '\'' +
                    '}';
        }
    }

    protected CSI getCSI() throws IOException {
        CSI csi = new CSI();
        String raw = "\033[";
        StringBuilder parameterBytes = new StringBuilder();
        int x = reader.read();
        // 0x30–0x3F
        while (0x30 <= x && x <= 0x3F) {
            parameterBytes.append((char) x);
            x = reader.read();
        }
        raw = raw + parameterBytes.toString();
        // This is the spec. that any chars in this range are allowed. IN PRACTICE however, only
        // integer lists delinited by semi-colons are allowed. There may be missing parameters

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
        if (!(0x40 <= x && x <= 0x7E)) {
            // not a final byte.
            throw new IllegalArgumentException("error: Unknown CSI terminator");
        }
        csi.op = (char) x;
        raw = raw + (char) x;
        csi.rawCommand = raw;

        return csi;
    }
    protected void info(String x){
        if(loggingFacade != null){
            loggingFacade.info(x);
        }
    }
    protected void warn(String x, Throwable t){
        if(loggingFacade != null){
            loggingFacade.warn(x, t);
        }
    }
    protected void debug(String x){
        if(loggingFacade != null){
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