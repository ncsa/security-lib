package edu.uiuc.ncsa.security.util.cli;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/31/18 at  3:02 PM
 */
public class EditorInputLine extends InputLine {
    public EditorInputLine(String raw) {
        CommandLineTokenizer clt = new CommandLineTokenizer();
        parseIt(clt.tokenize(raw));
    }

    public EditorInputLine(Vector v) {
        parseIt(v);
    }

    /**
     * Checks that one of the commands applies. This allows for things like short and long form commands
     * to be checked with as single call, e.g.
     * <pre>
     *     isCommand("r","read");
     * </pre>
     *
     * @param x
     * @return
     */
    public boolean isCommand(String... x) {
        if (x == null || x.length == 0) {
            return false;
        }
        boolean rc = false;
        try {
            for (String z : x) {
                rc = rc || getCommand().equals(z);
            }
            return rc;
        } catch (CommandNotFoundException cf) {
            // If the command is really munged then just catch this and return false.
            return false;
        }

    }

    protected void parseIt(Vector v) {
        if (v.size() <= 1) {
            parsedInput = v;
            return;
        }
        String arg1 = v.get(1).toString().trim();

        if (arg1.startsWith("[")) {
            if (arg1.endsWith("]")) {
                v.removeElementAt(1);
            } else {
                int bracketIndex = 2;
                while (!arg1.endsWith("]")) {
                    arg1 = arg1 + ((String) v.get(bracketIndex++)).trim();
                }
                for (int i = 1; i < bracketIndex; i++) {
                    v.removeElementAt(1); // yes remove the element at 1 because we are whittling down the vector and it is changing length
                }
            }

            // this is now of the form [x,...]
            arg1 = arg1.substring(1);// chop off [
            arg1 = arg1.substring(0, arg1.length() - 1);   // strip ]
            StringTokenizer stringTokenizer = new StringTokenizer(arg1, ","); // tokenize by ,
            switch (stringTokenizer.countTokens()) {
                case 0:
                    indices = null;
                    break;
                case 1:
                    //A single number
                    indices = new int[1];
                    indices[0] = Integer.parseInt(stringTokenizer.nextToken());
                    break;
                case 2:
                    // two values
                    indices = new int[2];
                    indices[0] = Integer.parseInt(stringTokenizer.nextToken());
                    indices[1] = Integer.parseInt(stringTokenizer.nextToken());
                    break;
                default:
                    indices = new int[3];
                    indices[0] = Integer.parseInt(stringTokenizer.nextToken());
                    indices[1] = Integer.parseInt(stringTokenizer.nextToken());
                    indices[2] = Integer.parseInt(stringTokenizer.nextToken());
            }

        }
        parsedInput = v; // whatever is left over.

    }

    int[] indices;

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public boolean hasIndices() {
        return indices != null && 0 < indices.length;
    }

    public int getIndexSize() {
        if (!hasIndices()) return 0;
        return indices.length;
    }
}
