package edu.uiuc.ncsa.security.util.cli;

import java.util.LinkedList;
import java.util.List;
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
            for (int i =1; i< parsedInput.size(); i++) {
                out[i-1] = parsedInput.get(i);
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

    List<String> parsedInput;

    /**
     * This returns the zero-th input.
     * @return
     */
    public String getCommand() {
        if (parsedInput.isEmpty()) throw new CommandNotFoundException();
        return parsedInput.get(0).toLowerCase();
    }

    public String getLastArg(){
        if(size() == 0){
            throw new ArgumentNotFoundException();
        }
        return getArg(size() - 1);
    }

    /**
     * Remember that the zero-th argument is the command, so that the arguments properly
     * begin at index = 1.
     * @param index
     * @return
     */
    public String getArg(int index) {
        if (0 <= parsedInput.size() && index + 1 <= parsedInput.size()) {
            return parsedInput.get(index);
        }
        throw new ArgumentNotFoundException();
    }

    public int getIntArg(int index) {
        try {
            return Integer.parseInt(getArg(index));
        } catch (NumberFormatException nfx) {
            throw new ArgumentNotFoundException("Error: the argument /" + getArg(index) + "/ cannot be parsed as an integer");
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
    public String getNextArgFor(String key){
        int index = indexOf(key);
        if(index <= 0){ // so if it is not found (-1) or is the name of the function (0) return null
            return null;
        }
        if(index ==getArgs().size()-1){ // so it is the last arg in the string and there cannot be another
            return null;
        }
        return getArg(1 + index); // finally, a result!
    }
}
