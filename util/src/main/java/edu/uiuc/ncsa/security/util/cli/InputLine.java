package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.util.*;

/**
 * A utility to take an input line and turn it into a command line. The zero-th index is
 * always the command and is returned in lower case. The remaining arguments may be gotten
 * as a String (default from {@link #getArg(int)}) or as an integer (from {@link #getIntArg(int)}.
 * Supplying an empty command line will throw a {@link CommandNotFoundException} if you try to get it,
 * so check that the command line is not empty.
 * <h3>Use</h3>
 * <p>The basic idea is to "whittle while you work", meaning you process keys and values,
 * which may be in any position, and as
 * soon as you have them, remove them from the input line. This makes parsing simpler. The last
 * thing then to process are positional arguments. So a command like</p>
 * <pre>
 *     search ^gt;client_id -r .*fnal.*  -out [client_id, email, strict_scopes] -rs f_clients
 * </pre>
 * Would be processed by
 * <ol>
 *     <li>get the -out attribute and list</li>
 *     <li>get the key value (starts with &gt;)</li>
 *     <li>get the -r and -rs values</li>
 * </ol>
 * <p>Then there would be unambigious processing.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 5/17/13 at  11:10 AM
 */
public class InputLine {
    public static final String DELIMITER = " "; // what's between commands

    /**
     * Constructor with a parameter. This takes an already parsed list of strings
     * and creates an instance of this class from it.
     *
     * @param v
     */
    public InputLine(List<String> v) {
        parsedInput = v;
        originalLine = "";
        boolean isFirstPass = true;
        for (Object obj : v) {
            originalLine = originalLine + (isFirstPass ? "" : DELIMITER) + obj;
            if (isFirstPass) {
                isFirstPass = false;
            }
        }
    }

    protected InputLine() {
    }

    /**
     * Takes a <b>blank delimited</b> command string and turns it into an input line
     * <pre>
     *     )help -w 120 -modules
     * </pre>
     *
     * @param unparsedString
     */
    public InputLine(String unparsedString) {
        originalLine = unparsedString;
        reparse();
    }

    /**
     * If the original line is altered, reparse it.
     */
    public void reparse() {
        StringTokenizer stringTokenizer = new StringTokenizer(originalLine, DELIMITER);
        Vector<String> vector = new Vector<>();
        while (stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if (token.startsWith(LIST_START_DELIMITER)) {
                String x = token;
                while (!token.contains(LIST_END_DELIMITER)) {
                    token = stringTokenizer.nextToken();
                    x = x + token;
                }
                vector.add(x);
            } else {
                vector.add(token);
            }
        }
        parsedInput = vector;
    }

    /**
     * (Special case.) For use with constructing more complex command lines. The issue with {@link #InputLine(String)}
     * is that the assumed control flow is from the command line, where Java will parse the input then
     * call {@link #InputLine(List)}. So it is not possible to create a command line with components.
     * <br/><br/>
     * E.g.
     * new InputLine("set_param -a scopes \"a b c\"");
     * <br/><br/>
     * would create 3 arguments "a, b, c". Instead, use this with<br/></br>
     * new InputLine("set_param", "-a", "scopes", "a b c");
     *
     * @param strings
     */
    public InputLine(String... strings) {
        parsedInput = new ArrayList<>();
        originalLine = "";
        for (int i = 0; i < strings.length; i++) {
            parsedInput.add(strings[i]);
            originalLine = originalLine + (i == 0 ? "" : " ") + strings[i];
        }
    }

    public String getOriginalLine() {
        return originalLine;
    }

    /**
     * Sets the original line. This is useful if there has to be some specialized processing of
     * it. Be sure to call {@link #reparse()} if you update it.
     *
     * @param originalLine
     */
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
        String[] out;
        if (parsedInput != null && !parsedInput.isEmpty()) {
            out = new String[parsedInput.size() - 1];
            // first pass tells whether to put a blank between arguments. The zero-th
            // element of the vector is the function, so omit that.
            for (int i = 1; i < parsedInput.size(); i++) {
                out[i - 1] = parsedInput.get(i);
            }
        } else {
            return new String[0]; // do not return a null, just an empty array.
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
     * <h3>NOTE</h3>
     * This does not remove lists as of yet.
     *
     * @param value
     */
    public void removeSwitchAndValue(String value) {
        if (!hasArg(value)) {
            return;
        }
        String x = getNextArgFor(value);
        if (StringUtils.isTrivial(x)) {
            // case is that they asked to remove a switch and value but there is no value.
            removeSwitch(value);
            return;
        }
        if (x.startsWith(LIST_START_DELIMITER)) {
            if (x.endsWith(LIST_END_DELIMITER)) {
                removeSwitch(x);
                removeSwitch(value);
            } else {
                int ndx = indexOf(value);
                int nextIndex = ndx;
                boolean loop = true;
                while (loop) {
                    String y = parsedInput.get(nextIndex++);
                    if (y.endsWith(LIST_END_DELIMITER)) {
                        loop = false;
                    }
                }
                List<String> newPI = new ArrayList<>();
                newPI.addAll(parsedInput.subList(0, ndx));
                newPI.addAll(parsedInput.subList(nextIndex, parsedInput.size()));
                parsedInput = newPI;
            }
        } else {
            removeSwitch(x);
            removeSwitch(value);
        }
    }

    /**
     * removes a list of switches and their values. This may be used for related switchs, e.g.
     * <pre>
     *     removeSwitchAndValue(MY_SWITCH, MY_SHORT_SWITCH, MY_SHORTER_SWITCH);
     * </pre>
     * or unrelated ones as well.
     * <p>See also: {@link #hasArg(String...)}, {@link #getNextArgFor(String...)}, {@link #removeSwitch(String...)};</p>
     *
     * @param values
     */
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

    /**
     * Removes several switches without touching anything else. Use for removing flags
     * (i.e., values that are there or not, no arguments).
     * <p>See also: {@link #hasArg(String...)}, {@link #removeSwitchAndValue(String...)}, {@link #getNextArgFor(String...)};</p>
     *
     * @param value
     */
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

    public InputLine removeLastArg() {
        if (parsedInput != null) {
            removeArgAt(getArgCount());
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
                    out = out + DELIMITER + x;
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
        return stringToInt(getArg(index));
    }

    /**
     * Contract is to return an integer if it parses and a {@link ArgumentNotFoundException}
     * if it does not rather than a {@link NumberFormatException}.
     *
     * @param value
     * @return
     */
    protected int stringToInt(String value) {
        if(value == null) {
            throw new ArgumentNotFoundException("Error: missing value.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfx) {
            throw new ArgumentNotFoundException("Error: the value \"" + value + "\" cannot be parsed as an integer.");
        }
    }

    protected boolean isStringAnInt(String value) {
        try {
            stringToInt(value);
            return true;
        } catch (ArgumentNotFoundException nfx) {
            return false;
        }
    }

    /**
     * Get the next argument for the keys, returning an integer. This will
     * throw a {@link ArgumentNotFoundException} if the value is not an integer.
     * @param keys
     * @return
     */
    public int getIntNextArg(String... keys) {
        return stringToInt(getNextArgFor(keys));
    }

    /**
     * Same as {@link #getIntNextArg(String...)}. Just regularlizing the naming of methods
     * so they can be easily guessed.
     * @param keys
     * @return
     * @deprecated
     */
    public int getNextIntArg(String... keys) {
        return getIntNextArg();
    }

    public int getIntLastArg(String key) {
        return stringToInt(getLastArg());
    }

    public boolean isIntLastArg(String key) {
        return isStringAnInt(getLastArg());
    }

    public boolean isIntNextArg(String key) {
        return isStringAnInt(getNextArgFor(key));
    }

    public boolean isBooleanNextArg(String key) {
        return null != getBooleanNextArgFor(key);
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
     * <p>See also: {@link #getNextArgFor(String...)} (String...)}, {@link #removeSwitchAndValue(String...)}, {@link #removeSwitch(String...)};</p>
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
     * For a list of keys, return the first one it finds, null if no such key.
     *
     * @param keys
     * @return
     */
    public String whichArg(String... keys) {
        if (keys.length == 0) {
            return null;
        }
        for (String arg : keys) {
            if (indexOf(arg) != -1) {
                return arg;
            }
        }
        return null;
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
     * Returns the first index of the target in the input line or a -1 if it does not occur.
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
     * Analog of {@link #hasArg(String...)}, where one of several (related) switches is checked.
     * First hit wins.
     * <p>See also: {@link #hasArg(String...)}, {@link #removeSwitchAndValue(String...)}, {@link #removeSwitch(String...)};</p>
     *
     * @param keys
     * @return
     */
    public String getNextArgFor(String... keys) {
        if (keys.length == 0) {
            return null;
        }
        for (String arg : keys) {
            int index = indexOf(arg);
            if (index != -1) {
                if (index == getArgs().size()) { // so it is the last arg in the string and there cannot be another
                    return null;
                }
                return getArg(1 + index);
            }
        }
        return null;
    }

    public static final String SWITCH = "-";

    /**
     * Converts the argument to a boolean. If the argument is missing or cannot be determined to be a boolean,
     * a null is returned. This accepts "true", "on", "false", "off" as arguments.
     *
     * @param keys
     * @return
     */
    public Boolean getBooleanNextArgFor(String... keys) {
        for(String key : keys ){
            String raw = getNextArgFor(key);
            if(raw != null) {
                return stringToBoolean(raw);
            }
        }
        return null;
    }
    public boolean isBooleanNextArgFor(String... keys) {
        return isStringABoolean(getNextArgFor(keys));
    }
    public boolean isBooleanLastArg(String key){
        return isStringABoolean(getLastArg());
    }

    protected Boolean stringToBoolean(String raw) {
        if (StringUtils.isTrivial(raw)) {
            return null;
        }
        if (raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("on")) {
            return Boolean.TRUE;
        }
        if (raw.equalsIgnoreCase("false") || raw.equalsIgnoreCase("off")) {
            return Boolean.FALSE;
        }
        return null;
    }
    protected boolean isStringABoolean(String value){
        return null != stringToBoolean(value);
    }

    public Boolean getBooleanLastArg() {
        return stringToBoolean(getLastArg());
    }
    public Boolean getBooleanArgArg(int index) {
        return stringToBoolean(getArg(index));
    }

    /**
     * checks if the very next component is an argument, not a switch.
     *
     * @param key
     * @return
     */
    public boolean hasNextArgFor(String key) {
        int index = indexOf(key);
        // NOTE that the indexOf command starts at 1, since the zeroth index is always omitted
        if (index == getArgs().size()) { // so it is the last arg in the string and there cannot be another
            return false;
        }

        return !getArg(1 + index).startsWith(SWITCH); // finally, a result!
    }

    /**
     * Returns the number of arguments for this input line. This does <b>not</b> include the original
     * command, so e.g. a value of zero means no arguments were passed.
     *
     * @return
     */
    public int getArgCount() {
        if (parsedInput == null || parsedInput.isEmpty()) {
            return 0;
        }
        return parsedInput.size() - 1;
    }

    String LIST_START_DELIMITER = "[";
    String LIST_END_DELIMITER = "]";
    String LIST_SEPARATOR = ",";

    /**
     * Read an argument as a list. The format is
     * <pre>
     *     -key [a,b,...]
     * </pre>
     * Embedded commas are not allowed between list elements. Elements have whitespace trimmed.
     * Note that this processes the <i>entire</i> input line, so that it finds the flag
     * and starts snooping for the start delimiter.
     * <p>At the end, the key and list are removed. See {@link #getArgList(String, boolean)}
     * This is because a list is merely bookends with comma between elements, so a list like</p>
     * <pre>
     *     -key [-key, -id, 42]
     * </pre>
     * <p>where the key is -key which is also an element in the list. If we did not remove it, then
     * thedictum that keys are unique in an input line is vilated and parsing may or may not
     * work as expected. To avoid ambiguity, grab the lists from an input line first.</p>
     *
     * @param flag
     * @return
     */
    public List<String> getArgList(String flag) {
        return getArgList(flag, true);
    }

    /**
     * If whittle is true, then the key and its value are moved from the input line and the
     * input line is reparsed if needed.
     *
     * @param key
     * @param whittle
     * @return
     */
    public List<String> getArgList(String key, boolean whittle) {
        List<String> list = new ArrayList<>();
        String rawLine = getOriginalLine();

        if (rawLine == null || rawLine.isEmpty()) {
            return list;
        }
        // Next bit checks if rather than a list, a single value is passed in.
        // Fixes https://github.com/ncsa/security-lib/issues/29
        String partial = getNextArgFor(key);
/*        if(partial.trim().startsWith("-") ) {
            throw new IllegalArgumentException("The key \"" + key + "\" has an unparseable list, \"" + partial+"\"");
        }*/

        // The list may have embedded blanks and such, so more parsing is needed
        int keyIndex = rawLine.indexOf(key);
        int nextSwitch = rawLine.indexOf("-", keyIndex + key.length());
        int startListIndex = rawLine.indexOf(LIST_START_DELIMITER, keyIndex);
        if (startListIndex == -1) {
            //Assume single value to be taken as a list
            list.add(partial);
            if (whittle) {
                removeSwitchAndValue(key);
            }
            return list;
        }
        int endListIndex;
        String rawList;
        if (nextSwitch == -1) {
            // this is the rest of the line.
            // just in case, use that
            endListIndex = rawLine.length();
            rawList = rawLine.substring(startListIndex);
            rawLine = rawLine.substring(0, keyIndex);
        } else {
            if (nextSwitch < startListIndex) {
                // means no list between this and next flag, so return empty list
                // foo -zero -one [a,b,c]
                // We don't want a request for -zero to just return the next list.
                return list;
            }
            endListIndex = rawLine.indexOf(LIST_END_DELIMITER, keyIndex);
            rawList = rawLine.substring(startListIndex, endListIndex + 1); // include end list delimiter
            rawLine = rawLine.substring(0, keyIndex) + rawLine.substring(endListIndex + 1);
        }
        //String rawList = rawLine.substring(startListIndex + 1, endListIndex);
        // Now, chop off bookends
        rawList = rawList.substring(rawList.indexOf(LIST_START_DELIMITER) + 1, rawList.indexOf(LIST_END_DELIMITER));
        StringTokenizer st = new StringTokenizer(rawList, LIST_SEPARATOR);
        while (st.hasMoreElements()) {
            list.add(st.nextToken().trim());
        }
        if (whittle) {
            setOriginalLine(rawLine);
            reparse();
        }
        return list;
    }

    /**
     * Checks if the given flag's argument is a list.
     *
     * <p>See also: {@link #getArgList(String)}</p>
     *
     * @param flag
     * @return
     */
    public boolean hasArgList(String flag) {
        List<String> list = new ArrayList<>();
        String rawLine = getOriginalLine();

        if (rawLine == null || rawLine.isEmpty()) {
            return false;
        }
        int ndx = rawLine.indexOf(flag);
        int nextSwitch = rawLine.indexOf("-", ndx + flag.length());
        int startListIndex = rawLine.indexOf(LIST_START_DELIMITER, ndx);
        if (startListIndex == -1) {
            return false; // end of story -- no lists anywhere.
        }
        if ((-1 < nextSwitch) && (nextSwitch < startListIndex)) {
            // -1 for next switch means this is the last argument
            // -1 < nextSwitch means that there is another switch with a list, like
            // foo -zero -one [a,b,c]
            // We don't want a request for -zero to just return the next list.
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String originalLine = "foo -zero -one [2,   3,4   ] -arf blarf0  -woof -two [abc,  def, g] -fnord 3455.34665 -- [  a,b,   c]";
        InputLine inputLine = new InputLine(originalLine);

        System.out.println(inputLine.getArgList("-zero")); // should be empty
        System.out.println(inputLine.hasArgList("-arf")); // should be empty
        System.out.println(inputLine.hasArg("-one"));
        System.out.println(inputLine.getArgList("-one"));
        System.out.println(inputLine.getArgList("-two"));
        System.out.println(inputLine.getArgList("--"));
        System.out.println(inputLine.getArgList("-arf")); // should return values as list
        System.out.println("-arf = " + inputLine.getNextArgFor("-arf"));

        System.out.println("original line before removing lists:");
        System.out.println(originalLine);
        System.out.println("inputLine after removing lists:");
        System.out.println(inputLine);


        InputLine inputLine1 = new InputLine("set_param", "-a", "scope", "a b c d");
        System.out.println(inputLine1);
        inputLine1 = new InputLine("set_param", "-a", "scope", "read: write: x.y:");
        System.out.println(inputLine1);
    }

}
