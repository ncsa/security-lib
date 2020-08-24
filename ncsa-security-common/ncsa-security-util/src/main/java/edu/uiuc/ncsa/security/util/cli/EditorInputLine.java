package edu.uiuc.ncsa.security.util.cli;

import java.util.Vector;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/20 at  3:14 PM
 */
public class EditorInputLine extends InputLine {

    /**
     * Basic constructor that turns a raw input line (such as types in by a user) to one of
     * these.
     *
     * @param rawLine
     */
    public EditorInputLine(String rawLine) {
        CommandLineTokenizer clt = new CommandLineTokenizer();
        parsedInput = clt.tokenize(rawLine);

    }

    public EditorInputLine(Vector v) {
        super(v);
    }

    /**
     * Checks that one of the commands applies. This allows for things like short and long form commands
     * to be checked with as single call, e.g.
     * <pre>
     *     isCommand("r","read");
     * </pre>
     *  It also handles missing commands.
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

}
