package edu.uiuc.ncsa.security.util.cli;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * A utility to take an input line and turn it into a command line. The zero-th index is
 * always the command and is returned in lower case. The remaining arguments may be gotten
 * as a String (default from {@link #getArg(int)}) or as an integer (from {@link #getIntArg(int)}.
 * Supplying an empty command line will throw a {@link CommandNotFoundException} if you try to get it,
 * so check that the command line is not empty.
 * <p>Created by Jeff Gaynor<br>
 * on 5/17/13 at  11:10 AM
 */
public class InputLine {
    public static final String COMMAND_DELIMITER = "";

    public InputLine(Vector v) {
        parsedInput = v;
    }

    protected InputLine() {
    }

    /**
     * Takes a <b>blank delimited</b> command string and turns it into an input line
     * <pre>
     *     )help -w 120 -modules
     * </pre>
     * @param unparsedString
     */
    public InputLine(String unparsedString) {
        StringTokenizer stringTokenizer = new StringTokenizer(unparsedString, " ");
        Vector<String> vector = new Vector<>();
        while(stringTokenizer.hasMoreTokens()){
            vector.add(stringTokenizer.nextToken());
        }
        parsedInput = vector;
    }

    public String getOriginalLine() {
        return originalLine;
    }

    public void setOriginalLine(String originalLine) {
        this.originalLine = originalLine;
    }

    String originalLine = "";

    /**
     * This returns this as a string
     *
     * @return
     */
    public String[] argsToStringArray() {
        String[] out = null;
        if (parsedInput != null && !parsedInput.isEmpty()) {
            out = new String[parsedInput.size() - 1];
            // first pass tells whether to put a blank between arguments. The zero-th
            // element of the vector is the function, so omit that.
            for (int i = 1; i < parsedInput.size(); i++) {
                out[i - 1] = parsedInput.get(i);
            }
        }
        return out;
    }

    /**
     * returns the arguments as a vector. This omits the name of the function, returning only the arguments themselves
     *
     * @return
     */
    public Vector argsToVector() {
        Vector v = new Vector();
        if (parsedInput == null) {
            return v;
        }
        for (int i = 1; i < parsedInput.size(); i++) {
            v.add(getArg(i));
        }
        return v;
    }

    /**
     * Use for switches with value, e.g. if you have "-foo bar" then invoke this with "-foo" and bar will be removed too.
     * To remove a single value, use {@link #removeSwitch(String)}
     *
     * @param value
     */
    public void removeSwitchAndValue(String value) {
        if (!hasArg(value)) {
            return;
        }
        String x = getNextArgFor(value);
        removeSwitch(x);
        removeSwitch(value);
    }

    public void removeSwitchAndValue(String... values) {
        for (String value : values) {

            if (!hasArg(value)) {
                continue;
            }
            String x = getNextArgFor(value);
            removeSwitch(x);
            removeSwitch(value);
        }

    }

    /**
     * Remove a value. NOTE that removing a switch does not remove its value!!! To do this all at once, use
     * {@link #removeSwitchAndValue(String)}
     *
     * @param value
     */
    public void removeSwitch(String value) {
        if (parsedInput != null) {
            parsedInput.remove(value);
        }
    }

    public void removeSwitch(String... value) {
        if (parsedInput != null) {
            for (String v : value) {
                parsedInput.remove(v);
            }
        }
    }

    public InputLine removeArgAt(int index) {
        if (parsedInput != null) {
            parsedInput.remove(index);
        }
        return this;
    }
    @Override

    public String toString() {
        return "InputLine[" + parsedInput + "]";
    }

    public String format() {
        String out = "";
        String[] args = argsToStringArray();

        if (!(args == null || args.length == 0)) {
            boolean firstPass = true;
            for (String x : args) {
                if (firstPass) {
                    firstPass = false;
                    out = x;
                } else {
                    out = out + " " + x;
                }
            }
        }
        return out;
    }

    public List<String> getArgs() {
        if (parsedInput.isEmpty() || parsedInput.size() == 1) {
            return new LinkedList<String>();
        }
        return parsedInput.subList(1, parsedInput.size());
    }

    protected List<String> parsedInput;

    /**
     * This returns the zero-th input.
     *
     * @return
     */
    public String getCommand() {
        if (parsedInput.isEmpty()) throw new CommandNotFoundException();
        return parsedInput.get(0).toLowerCase();
    }

    public String getLastArg() {
        if (size() == 0) {
            throw new ArgumentNotFoundException();
        }
        return getArg(size() - 1);
    }

    public void setLastArg(String newValue) {
        if (size() == 0) {
            throw new ArgumentNotFoundException();
        }
        parsedInput.set(size() - 1, newValue);
    }

    /**
     * Remember that the zero-th argument is the command, so that the arguments properly
     * begin at index = 1. <br/>
     * So if the command was <br/><br/>
     * foo -A b -C -D<br/><br/>
     * getArg(0) returns "foo" and getArg(1) returns "-A";
     *
     * @param index
     * @return
     */
    public String getArg(int index) {
        if (0 <= parsedInput.size() && index + 1 <= parsedInput.size()) {
            return parsedInput.get(index);
        }
        throw new ArgumentNotFoundException();
    }

    /**
     * Sets the specific argument at the given index. If the index is invalid, then an
     * exception is thrown, Note that the zero-tj argument is the calling function,
     * so it cannot be set with the method.
     *
     * @param index
     * @param value
     */
    public void setArg(int index, String value) {
        if (0 <= parsedInput.size() && index + 1 <= parsedInput.size()) {
            parsedInput.set(index, value);
            return;
        }
        throw new ArgumentNotFoundException();

    }

    /**
     * Append the argument to the end.
     *
     * @param value
     */
    public void appendArg(String value) {
        parsedInput.add(value);
    }

    /**
     * Tries to get the index as an integer. If it is not an integer an
     * {@link ArgumentNotFoundException} is thrown.
     *
     * @param index
     * @return
     */
    public int getIntArg(int index) {
        try {
            return Integer.parseInt(getArg(index));
        } catch (NumberFormatException nfx) {
            throw new ArgumentNotFoundException("Error: the argument /" + getArg(index) + "/ cannot be parsed. Did you forget the object index?");
        }
    }

    public int getNextIntArg(String key) {
        try {
            return Integer.parseInt(getNextArgFor(key));
        } catch (NumberFormatException nfx) {
            throw new ArgumentNotFoundException("Error: the argument /" + getNextArgFor(key) + "/ cannot be parsed. Did you forget the object index?");
        }
    }

    /**
     * Returns true if this command line was created with an empty string.
     *
     * @return
     */
    public boolean isEmpty() {
        return parsedInput.isEmpty();
    }

    /**
     * This number of all arguments *including* the original command. To get the number of arguments,
     * call {@link #getArgCount()}.
     *
     * @return
     */
    public int size() {
        return parsedInput.size();
    }


    /**
     * Check if the input line has the given argument. False is returned otherwise.
     *
     * @param arg
     * @return
     */
    public boolean hasArg(String arg) {
        return -1 != indexOf(arg);
    }

    /**
     * Check a list of args, e.g.
     * hasArg(SWITCH, SHORT_SWITCH, SHORTER_SWITCH)
     *
     * @param args
     * @return
     */
    public boolean hasArg(String... args) {
        if (args.length == 0) {
            return false;
        }
        for (String arg : args) {
            if (indexOf(arg) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is there a given arg at the given index? Useful if certain arguments are expected at certain positions.
     *
     * @param index
     * @return
     */
    public boolean hasArgAt(int index) {
        return index <= getArgCount();
    }

    /**
     * If this command line has arguments at all.
     *
     * @return
     */
    public boolean hasArgs() {
        return 1 < size();
    }

    /**
     * Returns the index of the target in the input line or a -1 if it does not occur.
     *
     * @param arg
     * @return
     */
    public int indexOf(String arg) {
        int index = 1; // zero-th argument is the name of the method, so getArgs start at index 1.
        for (String x : getArgs()) {
            if (x.equals(arg)) return index;
            index++;
        }
        return -1;
    }

    /**
     * This will find the index for the "key" and return the very next argument. This is a
     * very, very use idiom for retrieving arguments for options.<br/><br/>
     * E.g. If the command line were
     * <pre>
     *    myfunc -x foo -y fnord -blarg
     * </pre>
     * Then
     * <pre>
     *       getNextArgFor("-x");
     * </pre>
     * would return "foo". On the other hand
     * <pre>
     *       getNextArgFor("-blarg");
     * </pre>
     * would return a null, since there is no possible argument for it.
     *
     * @param key
     * @return
     */
    public String getNextArgFor(String key) {
        int index = indexOf(key);
        // NOTE that the indexOf command starts at 1, since the zeroth index is always omitted
        if (index == getArgs().size()) { // so it is the last arg in the string and there cannot be another
            return null;
        }
        return getArg(1 + index); // finally, a result!
    }

    /**
     * Returns the number of arguments for this input line. This does <b>not</b> include the original
     * command, so e.g. a value fo zero means no arguments were passed.
     *
     * @return
     */
    public int getArgCount() {
        if (parsedInput == null || parsedInput.isEmpty()) {
            return 0;
        }
        return parsedInput.size() - 1;
    }
}
