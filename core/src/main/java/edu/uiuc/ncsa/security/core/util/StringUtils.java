package edu.uiuc.ncsa.security.core.util;


import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import java.util.*;

/**
 * A very heavily used class. This centralizes many idioms about strings, plus
 * it has a lot of simplified formatting utilities.
 * <p>Created by Jeff Gaynor<br>
 * on 4/23/20 at  6:34 AM
 */
public class StringUtils {
    protected static String blanks = "                                                                                                                        ";
    protected static int DEFAULT_DISPLAY_WIDTH = 120;
    public static boolean JUSTIFY_LEFT = false;
    public static boolean JUSTIFY_RIGHT = true;
    public static String ELLIPSIS = "...";

    /**
     * Right justify, so for the given width, the string is padded on the right. Note that
     * if width < x.length, the string is returned unchanged.
     *
     * @param x
     * @param width
     * @return
     */
    public static String RJustify(String x, int width) {
        return justify(x, width, JUSTIFY_RIGHT);
    }

    /**
     * Left justify, padding the string on the right. See note in {@link #RJustify(String, int)}
     *
     * @param x
     * @param width
     * @return
     */
    public static String LJustify(String x, int width) {
        return justify(x, width, JUSTIFY_LEFT);
    }

    /**
     * Get a string of blanks for the given width. If the width is non-positive,
     * the empty string is returned.
     *
     * @param width
     * @return
     */
    public static String getBlanks(int width) {
        if (width <= 0) {
            return "";
        }
        if (blanks.length() < width) {
            // never run out of blanks...
            while (blanks.length() < width) {
                blanks = blanks + blanks;
            }
        }
        return blanks.substring(0, width);
    }

    static String nbSpaces = "                          ";

    public static String getNBSpaces(int width) {
        if (width <= 0) return "";
        if (nbSpaces.length() < width) {
            while (nbSpaces.length() < width) {
                nbSpaces = nbSpaces + nbSpaces; // lots and lots of spaces
            }
        }
        return nbSpaces.substring(0, width);
    }

    /**
     * Right or right justify a snippet of text to the given width, e.g.
     * for right justify:
     * <pre>
     *     x = "abc";
     *     width = 5;
     *     output then is
     *     "  abc"
     * </pre>
     * Left justify effectively pads it on the
     *
     * @param x
     * @param width
     * @return
     */
    public static String justify(String x, int width, boolean rightJustify) {
        if (width <= x.length()) {
            return x;
        }

        // we know x.length() < width at this point.
        if (rightJustify) {

            return getBlanks(width - x.length()) + x;
        } else {
            return x + getBlanks(width - x.length());
        }
    }

    /**
     * If the string is either null or empty. This is a very common idiom for testing strings
     * but the built in {@link String#isEmpty()} of course cannot be used on a null object...
     *
     * @param x
     * @return
     */
    public static boolean isTrivial(String x) {
        return x == null || x.isEmpty() || x.isBlank();
    }

    /**
     * Some quick tests for this class. Used for development, debugging or just run it if you are curious
     * and look at the output.
     *
     * @param args
     */
    public static void main(String[] args) {
        tests();
        tableTest();
    }


    private static void tableTest() {
        System.out.println("center:\"" + center("abcd", 10) + "\""); // 8 sec
        System.out.println("center:\"" + center("abcd", 15) + "\""); // 8 sec
        System.out.println("center:\"" + center("abcd", 6) + "\""); // 8 sec

        System.out.println(formatElapsedTime(8000)); // 8 sec
        System.out.println(formatElapsedTime(4 * 3600 * 1000));  //4 hours
        System.out.println(formatByteCount(1000000L));
        System.out.println(formatByteCount(123456789L));
        System.out.println(formatByteCount(74123456789L));
        System.out.println(formatHerz(200, System.currentTimeMillis() - 100000));
        System.out.println(formatHerz(54321, System.currentTimeMillis() - 100));


        System.out.println();
        System.out.println("\n===== table test =====");
        Random random = new Random();
        int cols = 5;
        int rows = 7;
        int maxEntrySize = 20; // gets base 64 encoded
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                int entryLength = random.nextInt(maxEntrySize);
                if (entryLength == 0) {
                    row.add(null);
                    continue;
                }
                byte[] b = new byte[entryLength];
                random.nextBytes(b);
                row.add(Base64.getEncoder().encodeToString(b));
            }
            table.add(row);
        }
        List<String> output = formatTable(null, table, -1, true);
        for (int i = 0; i < output.size(); i++) {
            System.out.println(output.get(i));
        }
    }

    private static void tests() {
        // Colon is so we can tell where the end of the strings are.
        System.out.println("Some examples of text justification, truncation");
        System.out.println(justify("abc", 10, JUSTIFY_RIGHT) + ":");
        System.out.println(RJustify("abc", 10) + ":");
        System.out.println(justify("abc", 10, JUSTIFY_LEFT) + ":");
        System.out.println(LJustify("abc", 10) + ":");
        System.out.println(truncate("abcdefghijklmnopqrs", 10));
        System.out.println(truncate("abcdefghijklmnopqrs", 100));
        List<String> y = new ArrayList<>();
        y.add("       foo : azxz");
        y.add("foo bar afg: adfg sdfg sdfadgw546 rhg 54erthvbg sfgsdfg");
        y.add("   foo bar : baz sd sdfg sd dfg dfg d dfgdfgdfgsdfg zsewyfg dzg9g8fd98 98sa9-8ur9-gb8");
        System.out.println(fromList(justify(10, y, 40)));
        System.out.println(fromList(justify(0, y, 30)));
        String x = null;
        System.out.println(isTrivial(x));
        testWrap();
        System.out.println("\n------ test map formatting -----");
        HashMap<String, Object> testMap = new HashMap<>();
        testMap.put("mairzy", "doats");
        testMap.put("and does", "eat stoats");
        testMap.put("and little", "lambsey divey, A kiddle-dee divey too wouldn't you??");
        testMap.put("latin", "Lorem ipsum dolor sit amet\nconsectetur adipiscing elit\nsed do eiusmod tempor incididunt ut labore\net dolore magna aliqua.\nUt enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ");
        testMap.put("now", new Date());
        List<String> formattedMap = formatMap(testMap,
                null, // do not take a subset
                false,// do not sort the keys
                true, // split the values over several lines if needed
                3,// indent the whole output 3 spaces from the left.
                60 // The total display must fit within 60 chars.
        );
        System.out.println("width = 60:");
        for (String formattedLine : formattedMap) {
            System.out.println(formattedLine);
        }

        formattedMap = formatMap(testMap,
                null, // do not take a subset
                false,// do not sort the keys
                false, // do NOT split the values over several lines if needed
                0,// indent the whole output 3 spaces from the left.
                50 // The total display must fit within 50 chars.
        );
        System.out.println("repeat with width = 50:");
        for (String formattedLine : formattedMap) {
            System.out.println(formattedLine);
        }

        formattedMap = formatMap(testMap,
                null, // do not take a subset
                false,// do not sort the keys
                true, // (inconsistent with width = -1 on purpose): split the values over several lines if needed
                0,// indent the whole output 3 spaces from the left.
                -1 // The total display is unlimited
        );
        System.out.println("repeat with width  = -1:");
        for (String formattedLine : formattedMap) {
            System.out.println(formattedLine);
        }
    }

    /*
          callback_uri : ["https://client.example.org/callback","https://client.example.org/callback2"]
               cfg : {"new":"config"}
         client_id : oa4mp:/client_id/52d39e92ab5347c880fa19f3b9cb4204
       creation_ts : 2020-05-04 13:47:41.0
  last_modified_ts : 2020-05-04 13:48:21.0
              name : New Test name
     proxy_limited : false
     public_client : true
        public_key : ZOq88bMz4wIxYDbqfKpU2d4CTMx8vcXL2aYy1_XvkV1yZva1M0fHUFIqPRyZPrWapiTqeEzoXk2sJVzByDJgLp7psBaghosJfkq
                     VTDQX_M17FBLYXZv7MviF6wB3_8MmCm2nHtKd5Ud6V6SvLj1tH5YmRcChCSS7evnXH94x3aqkIRfkQL0fJmbUDQQgOm-40vwBZt
                     WSag3NImftUJch3twz
     */
    protected static void testWrap() {
        ArrayList<String> x = new ArrayList<>();
        x.add("    callback_uri : [\"https://client.example.org/callback\",\"https://client.example.org/callback2\"]");
        x.add("             cfg : {\"new\":\"config\"}");
        x.add("     creation_ts : 2020-05-04 13:47:41.0");
        x.add("      public_key : ZOq88bMz4wIxYDbqfKpU2d4CTMx8vcXL2aYy1_XvkV1yZva1M0fHUFIqPRyZPrWapiTqeEzoXk2sJVzByDJgLp7psBaghosVTDQX_M17FBLYXZv7MviF6wB3_8MmCm2nHtKd5Ud6V6SvLj1tH5YmRcChCSS7evnXH94x3aqkIRf");
        x.add("            name : New Test name");
        x.add("last_modified_ts : 2020-05-04 13:48:21.0");
        // NOTE that the length of eac line is printed at the end
        List<String> y = wrap(19, x, 80);
        for (String z : y) {
            System.out.println(z + " |" + z.length());
        }
        System.out.print("");
        y = wrap(19, x, 125);
        for (String z : y) {
            System.out.println(z + " |" + z.length());
        }
        System.out.println("Wrap a line");
        System.out.println(wrap("ZOq88bMz4wIxYDbqfKpU2d4CTMx8vcXL2aYy1_XvkV1yZva1M0fHUFIqPRyZPrWapiTqeEzoXk2sJVzByDJgLp7psBaghosVTDQX_M17FBLYXZv7MviF6wB3_8MmCm2nHtKd5Ud6V6SvLj1tH5YmRcChCSS7evnXH94x3aqkIRf", 80));
    }

    /**
     * Truncate with the default {@link #ELLIPSIS}.
     *
     * @param x
     * @param width
     * @return
     */
    public static String truncate(String x, int width) {
        return truncate(x, width, ELLIPSIS);
    }

    /**
     * Truncate with all the defaults.
     *
     * @param x
     * @return
     */
    public static String truncate(String x) {
        return truncate(x, DEFAULT_DISPLAY_WIDTH, ELLIPSIS);
    }

    /**
     * truncate the string x to the given width. If the string is too long,
     * use the ellipsis, e.g.
     * <pre>
     *     x = "abcdefghijklmnopqrs";
     *     ellipsis = "...";
     *     width = 10;
     * </pre>
     * Then this would yield
     * <pre>
     *     abcdefg...
     * </pre>
     * If width &lt; 0, then do not truncate.<br/>
     * If ellipsis is null or empty, use the default {@link #ELLIPSIS}
     *
     * @param x
     * @param width
     * @param ellipsis
     * @return
     */
    public static String truncate(String x, int width, String ellipsis) {
        if (width < 0) {
            return x;
        }
        if (x.length() <= width) {
            return x;
        }
        if (isTrivial(ellipsis)) {
            ellipsis = ELLIPSIS;
        }
        return x.substring(0, width - ellipsis.length()) + ellipsis;
    }

    /**
     * Left and right margin are column numbers, so 10, string 80 means the resulting string
     * will be padded with 10 characters on the left and 80 and the right. Each line will be
     * justifid within the margins separately
     *
     * @param leftMargin
     * @param source
     * @param rightMargin
     * @return
     */
    public static String justify(int leftMargin, String source, int rightMargin) {
        return fromList(justify(leftMargin, toList(source), rightMargin));

    }

    public static List<String> justify(int leftMargin, List<String> source, int rightMargin) {
        List<String> output = new ArrayList<>();
        int realWidth = rightMargin - leftMargin;
        String lPad = blanks.substring(0, leftMargin);
        for (String x : source) {
            x = x.trim();
            if (x.length() < realWidth) {
                output.add(lPad + x);
            } else {
                int lineCount = x.length() / realWidth; // integer div for lines
                for (int i = 0; i < lineCount; i++) {
                    output.add(lPad + x.substring(i * realWidth, (i + 1) * realWidth));
                }
                output.add(lPad + x.substring((lineCount) * realWidth)); // end of string
            }
        }
        return output;
    }

    /**
     * For output like
     * <pre>
     *     abc : foo
     *  swwe e : bar fgd
     *           ddd ryf
     * </pre>
     * This outdents the first line and indents to rest for the wrap.
     *
     * @param offset      length of whole left block
     * @param source
     * @param rightMargin
     * @return
     */
    public static List<String> wrap(int offset, List<String> source, int rightMargin) {
        List<String> output = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            String currentLine = source.get(i);
            if (currentLine.length() < rightMargin) {
                output.add(currentLine);
            } else {
                output.add(currentLine.substring(0, rightMargin));
                List<String> flowedtext = StringUtils.wrap(0,
                        StringUtils.toList(currentLine.substring(rightMargin)),
                        rightMargin - offset);
                for (String x : flowedtext) {

                    output.add(getBlanks(offset) + x);
                }

            }
        }
        return output;
    }

    /**
     * Convience to wrap a single string.
     *
     * @param source
     * @param width
     * @return
     */
    public static String wrap(String source, int width) {
        String formattedString = "";

        for (int i = 0; i < source.length() / width; i++) {
            formattedString = formattedString + source.substring(i * width, (i + 1) * width) + "\n";
        }
        if (0 != source.length() % width) {
            // if there is anything left over, append it, otherwise, skip this.
            formattedString = formattedString + source.substring(source.length() - source.length() % width);
        }
        return formattedString;
    }

    /**
     * Convert a string with embedded line feeds to a list os strings. Inverse of {@link #fromList(List)}
     *
     * @param x
     * @return
     */
    public static List<String> toList(String x) {
        StringTokenizer st = new StringTokenizer(x, "\n");
        List<String> out = new ArrayList<>();
        while (st.hasMoreTokens()) {
            out.add(st.nextToken());
        }
        return out;
    }

    /**
     * Convert a list of strings into a single string with linefeeds. Inverse of {@link #toList(String)}.
     *
     * @param listOfStrings
     * @return
     */
    public static String fromList(List<String> listOfStrings) {
        StringBuffer sb = new StringBuffer();
        for (String x : listOfStrings) {
            sb.append(x + "\n");
        }
        return sb.toString();
    }

    /**
     * Checks if towo strings have equal content.
     *
     * @param x
     * @param y
     * @param ignoreCase ignore case. Everything is converted to lower case before checking
     * @param trimEnds   remove any lead or trailing whitespace before checking
     * @return
     */
    public static boolean equals(String x,
                                 String y,
                                 boolean ignoreCase,
                                 boolean trimEnds) {
        // all the stimple cases
        if (x == null && y == null) return true;
        if (x == null && y != null) return false;
        if (x != null && y == null) return false;
        if (trimEnds) {
            x = x.trim();
            y = y.trim();
        }
        if (ignoreCase) {
            x = x.toLowerCase();
            y = y.toLowerCase();
        }

        if (x.length() != y.length()) return false;
        char[] yChar = y.toCharArray();
        char[] xChar = x.toCharArray();
        for (int i = 0; i < xChar.length; i++) {
            if (xChar[i] != yChar[i]) return false;
        }
        return true;
    }

    public static boolean equals(String x,
                                 String y) {
        return equals(x, y, false, false);
    }

    /**
     * Pad a string with blanks as needed. This does <b>not</b> truncate if the string is too
     * long. If you want truncation, use {@link #pad2(String, int)}.
     *
     * @param s
     * @param commandBufferMaxWidth
     * @return
     */
    public static String pad(String s, int commandBufferMaxWidth) {
        if (isTrivial(s)) {
            return getBlanks(commandBufferMaxWidth);
        }
        if (commandBufferMaxWidth < s.length()) {
            return s;
        }
        return s + getBlanks(commandBufferMaxWidth - s.length());
    }

    protected static String STARS = "**************************************************************************************";

    public static String pad2(int value, int commandBufferMaxWidth) {
        String x = Integer.toString(value);
        return pad2(x, false, commandBufferMaxWidth);
    }

    public static String pad2(Date value, boolean isISO, int commandBufferMaxWidth) {
        String x;
        if (isISO) {
            x = Iso8601.date2String(value);
        } else {
            x = value.toString();
        }
        return pad2(x, false, commandBufferMaxWidth);
    }

    /**
     * Default is ISO 8601 dates
     *
     * @param value
     * @param commandBufferMaxWidth
     * @return
     */
    public static String pad2(Date value, int commandBufferMaxWidth) {

        return pad2(value, true, commandBufferMaxWidth);
    }

    /**
     * Pad the string to the given length with blanks. This makes sure every line
     * is the same length. If the line is too long, it is  truncated
     *
     * @param s
     * @param commandBufferMaxWidth
     * @return
     */

    public static String pad2(String s, int commandBufferMaxWidth) {
        return pad2(s, true, commandBufferMaxWidth); // default is to truncate lines too long
    }

    public static String pad2(String s, boolean isTruncate, int commandBufferMaxWidth) {
        if (isTrivial(s)) {
            return getBlanks(commandBufferMaxWidth);
        }
        if (commandBufferMaxWidth <= s.length()) {
            if (isTruncate) {
                s = s.substring(0, commandBufferMaxWidth);
            } else {
                s = STARS.substring(0, Math.min(5, commandBufferMaxWidth));
            }
        }
        return s + getBlanks(commandBufferMaxWidth - s.length());

    }

    /**
     * Format a map of objects as easily readable key value pairs.
     *
     * @param map          The map to be displayed
     * @param keySubset    An optional (may be null or empty) subset of keys. Only these will be used id present
     * @param sortKeys     if true, the keys in the map will be sorted.
     * @param multiLine    Split the formatting of the values in the map over several lines (true) or truncate with ellipses (false)
     * @param indent       The amount for the whole thing to be indented from the left
     * @param displayWidth The total width that this must fit in.
     * @return
     */


    public static List<String> formatMap(Map map,
                                         List<String> keySubset,
                                         boolean sortKeys,
                                         boolean multiLine,
                                         int indent,
                                         int displayWidth
    ) {
        return formatMap(map, keySubset, sortKeys, multiLine, indent, displayWidth, true);
    }

    /**
     * The tryJSON flag means that if an entry might be JSON, try to interpret it and use JSON
     * formatting guidelines. This should be set false in cases where you know that is not the
     * case, e.g., in calls from QDL.
     *
     * @param map
     * @param keySubset
     * @param sortKeys
     * @param multiLine
     * @param indent
     * @param displayWidth
     * @param tryJSON
     * @return
     */
    public static List<String> formatMap(Map map,

                                         List<String> keySubset,
                                         boolean sortKeys,
                                         boolean multiLine,
                                         int indent,
                                         int displayWidth,
                                         boolean tryJSON) {

        List<String> outputList = new ArrayList<>();
        Map<String, Object> tMap;
        if (sortKeys) {
            tMap = new TreeMap<>(); // sorts the keys
        } else {
            tMap = new HashMap<>();
        }
        if (keySubset == null || keySubset.isEmpty()) {
            try {
                tMap.putAll(map);
            } catch (ClassCastException cce) {
                // So the map has different types of keys (e.g., integer and string). Slow approach is to convert them
                tMap.clear();
                for (Object obj : map.keySet()) {
                    tMap.put(obj.toString(), map.get(obj));
                }
            }
        } else {
            // take only a subset
            for (String k : keySubset) {
                if (map.containsKey(k)) {
                    tMap.put(k, map.get(k));
                }
            }
        }
        int width = 0;
        for (Object key : tMap.keySet()) {
            width = Math.max(width, key.toString().length());
        }
        // Use the order of the tmap (so its sorted) but the XMLMap has information we need to get these.
        for (Object key : tMap.keySet()) {
            //String v = map.getString(key);
            Object rawValue = tMap.get(key);
            if (rawValue == null) {
                continue;
            }

            String v = tMap.get(key).toString();
            if (!StringUtils.isTrivial(v)) {
                if (rawValue instanceof JSON) {
                    v = ((JSON) rawValue).toString(1);
                } else {
                    if (rawValue instanceof Date) {
                        v = Iso8601.date2String((Date) rawValue);
                    } else {
                        v = rawValue.toString();
                        if (tryJSON) {
                            try {
                                // Check if it's serialized JSON.
                                JSON json = JSONSerializer.toJSON(v);
                                v = json.toString(1);
                            } catch (Throwable t) {

                            }
                        }
                    }

                }
                outputList.add(formatMapEntry(key.toString(), v, indent, displayWidth, width, multiLine));
            }
        }
        return outputList;
    }

    /**
     * Format an entry from a map. This will take a (key, value) pair within margins so that colons align
     * and format it as a line of the form (multiLine false):
     * <pre>
     *     key : value...
     * </pre>
     * This truncates the value to fit on a single line and within the displayWidth. If multiLine is true it
     * will be of the form:
     * <pre>
     *     key : value split
     *           over several lines as
     *           needed.
     * </pre>
     * Note that the leftColumWidth is the length of the longest key in the map, usually. The key will be right
     * justified within this field and hence all the colons will end up in the same place, making the output
     * extremely readable.
     *
     * @param key            The key from the entry
     * @param value          The value of the entry
     * @param indentWidth    The indent on the left of the whole entry
     * @param displayWidth   The actual width that this is to fit in
     * @param leftColumWidth The total width of the key field.
     * @param multiLine      Split the value over several lines if too long, otherwise truncate with ellipsis.
     * @return
     */
    public static String formatMapEntry(String key,
                                        String value,
                                        int indentWidth,
                                        int displayWidth,
                                        int leftColumWidth,
                                        boolean multiLine) {
        // the given indent plus space for the " : " in the middle
        indentWidth = indentWidth + 3;
        if (displayWidth < 0) {
            multiLine = false; // displayWidth =-1 means infinite width, so truncate is not possible.
        }

        int realWidth = displayWidth - indentWidth;
        boolean shortLine = value.length() + leftColumWidth + 1 <= realWidth;
        if (multiLine) {

            List<String> flowedtext = wrap(0, StringUtils.toList(value), realWidth - leftColumWidth);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(RJustify(key, leftColumWidth) + " : " + flowedtext.get(0) + ((flowedtext.size() <= 1 && shortLine) ? "" : "\n"));
            boolean isFirstLine = true;
            for (int i = 1; i < flowedtext.size(); i++) {
                if (isFirstLine) {
                    isFirstLine = false;
                    stringBuffer.append(getBlanks(indentWidth + leftColumWidth) + flowedtext.get(i));
                } else {
                    stringBuffer.append("\n" + getBlanks(indentWidth + leftColumWidth) + flowedtext.get(i));
                }
            }
            return stringBuffer.toString();

        }
        /* This strips out the new lines or the right hand tile does not stay in its bounds -- it adds new lines
            So rather than a : b\nc\nd becoming
             a :b
                c
                d
             you'd get
             a : b
             c
             d
             Only way to fix it is to either have some sort of indent replacement for each linefeed \n + indents
             which implies finding the line breaks and truncating each line??
         */
/*
        if(multiLine){
            String bbb = getBlanks(indentWidth + leftColumWidth + 3);
            return RJustify(key,indentWidth + leftColumWidth) + " : " +
                    truncate(value.replace("\n", "\n"+bbb).replace("\r", "\r"+bbb), realWidth);
        }
*/
        return RJustify(key, indentWidth + leftColumWidth) + " : " +
                truncate(value.replace("\n", "").replace("\r", ""), realWidth);
    }

    /**
     * Converts a list of strings to a single string with embedded linefeeds.
     *
     * @param list
     * @return
     */
    public static String listToString(List<String> list) {
        String x = "";
        boolean isFirst = true;
        for (String s : list) {
            if (s == null) {
                continue;
            }
            if (isFirst) {
                isFirst = false;
                x = x + s;
            } else {
                x = x + "\n" + s;
            }
        }
        return x;
    }

    /**
     * Converts a string with embedded linefeeds into a list of strings, one per line.
     * The line editor needs this.
     *
     * @param s
     * @return
     */
    public static List<String> stringToList(String s) {
        String lines[] = s.split("\\r?\\n");
        return new ArrayList<>(Arrays.asList(lines));
    }

    public static final String tableFieldDelimiter = "|";

    /**
     * Takes a table -- defined as a list of rows, each row is a separate column amd returns
     * a list of formatted rows that can just be printed (piped through a stream or whatever)
     * It is assumed there are no missing columns, though null columns are fine and are rendered
     * as blank.
     *
     * @param table
     * @param maxFieldWidth maximum width of a column. Output is truncated if positive.
     * @param showDelimiter
     */
    public static List<String> formatTable(List<String> headers, List<List<String>> table, int maxFieldWidth, boolean showDelimiter) {
        List<String> output = new ArrayList<>();
        boolean truncateFields = 0 < maxFieldWidth;
        boolean showHeaders = !(headers == null || headers.isEmpty());
        List<Integer> columnWidths = new ArrayList<>();
        int columnCount = -1; // number of columns. If this is not identical in all rows, throw an exception later
        if (showHeaders) {
            for (int i = 0; i < headers.size(); i++) {
                columnWidths.add(headers.get(i).length());
            }
            columnCount = headers.size();
        } else {
            for (int i = 0; i < table.get(0).size(); i++) {
                columnWidths.add(0); // fill up with initial values.
            }
            columnCount = table.get(0).size();
        }
        // To get a nicely formatted result, it will take a pass to set the sizes.
        for (int i = 0; i < table.size(); i++) {
            List<String> row = table.get(i);
            if (row.size() != columnCount) {
                throw new IllegalArgumentException("column of size " + row.size() + " found. Must be " + columnCount);
            }
            for (int j = 0; j < row.size(); j++) {
                columnWidths.set(j, Math.max(columnWidths.get(j), (row.get(j) == null) ? 0 : row.get(j).length()));
            }
        }
        // Now for the pass where we fill this all in.
        if (showHeaders) {
            boolean firstPass = true;
            String r = "";
            for (int i = 0; i < headers.size(); i++) {
                r = r + (firstPass ? "" : (showDelimiter ? " " + tableFieldDelimiter + " " : "")) + justify(headers.get(i), columnWidths.get(i), false);
                if (firstPass) {
                    firstPass = false;
                }
            }
            output.add(r);
        }
        for (int i = 0; i < table.size(); i++) {
            boolean firstPass = true;
            String r = "";
            List<String> row = table.get(i);
            for (int j = 0; j < row.size(); j++) {
                String delim = showDelimiter ? (" " + tableFieldDelimiter + " ") : "";
                String entry;
                if (StringUtils.isTrivial(row.get(j))) {
                    entry = getBlanks(columnWidths.get(j));
                } else {
                    entry = justify(row.get(j), columnWidths.get(j), false);
                }
                r = r + (firstPass ? "" : delim) + entry;
                if (firstPass) {
                    firstPass = false;
                }

            }
            output.add(r);
        }


        return output;
    }

    /**
     * Returns the unicode for a single character
     *
     * @param ch
     * @return
     */
    public static String toUnicode(char ch) {
        return String.format("\\u%04x", (int) ch);
    }

    public static String toUnicode(String x) {
        char[] chars = x.toCharArray();
        if (chars.length != 1) {
            throw new IllegalArgumentException(StringUtils.class.getSimpleName() + ".toUnicode accepts a single character, not \"" + x + "\"");
        }
        return toUnicode(chars[0]);
    }

    /**
     * Make E.g. byte counts from files human readable.
     *
     * @param count
     * @return
     */
    public static String formatByteCount(long count) {
        return formatCount(count, "B");
    }

    /**
     * Formats the count and units with the correct unit prefix, formatting the count. So if
     * formatCount(54321, "b") --> 54.321 Kb, here "b" is the unit
     *
     * @param count
     * @param unit
     * @return
     */
    public static String formatCount(long count, String unit) {
        long acount = Math.abs(count);
        if (acount <= 999) {
            return Long.toString(count) + " " + unit;
        }
        if (1000 <= acount && acount <= 999999) {
            return (count < 0 ? "-" : "") + String.format("%,.4f", acount / 1000.0d) + " K" + unit;
        }
        if (1000000 <= acount && acount <= 999999999) {
            return (count < 0 ? "-" : "") + String.format("%,.4f", acount / 1000000.0d) + " M" + unit;
        }
        if (1000000000 <= acount && acount <= 999999999999L) {
            return (count < 0 ? "-" : "") + String.format("%,.4f", acount / 1000000000.0d) + " G" + unit;
        }
        if (1000000000000L <= acount && acount <= 999999999999999L) {
            return (count < 0 ? "-" : "") + String.format("%,.4f", acount / 1000000000000.0d) + " T" + unit;
        }

        return Long.toString(count);
    }

    /**
     * Makes elapsed times in milliseconds  human readable.
     *
     * @param elapsedTime The actual elapsed time in ms. E.g. 1000 is 1 sec.
     * @return
     */
    public static String formatElapsedTime(long elapsedTime) {
        long a = Math.abs(elapsedTime);
        if (a <= 999) {
            return a + " ms.";
        }
        if (1000 <= a && a <= (60 * 1000L - 1)) {
            return (elapsedTime < 0 ? "-" : "") + String.format("%,.4f", a / (1000.0d)) + " sec.";
        }
        if (60 * 1000L <= a && a <= 60 * 60 * 1000L - 1) {
            return (elapsedTime < 0 ? "-" : "") + String.format("%,.4f", a / (60 * 1000.0d)) + " min.";
        }
        if (60 * 60 * 1000L <= a && a <= 24 * 60 * 60 * 1000L - 1) {
            return (elapsedTime < 0 ? "-" : "") + String.format("%,.4f", a / (60 * 60 * 1000.0d)) + " hr.";
        }
        return (elapsedTime < 0 ? "-" : "") + String.format("%,.4f", a / (24 * 60 * 60 * 1000.0d)) + " days";

    }

    /**
     * Formats the number of cycles from the starting time. This will return properly formatted
     * results to 4 places with units e.g. 12345 Hz is returned as 12.345 KHz
     *
     * @param cycles
     * @param startTime
     * @return
     */
    public static String formatHerz(long cycles, long startTime) {
        long elapsedTime = (System.currentTimeMillis() - startTime);
        if (elapsedTime == 0L) {
            elapsedTime = 1; // no divide by zero
        }
        return formatCount(1000 * cycles / elapsedTime, "Hz"); // converts to seconds
    }

    /**
     * Centers the string within the width. If the string is longer than the width, nothing is
     * done and no truncation results. The result is a string of length equal to width,
     * with the text in centered.
     *
     * @param text
     * @param width
     * @return
     */
    public static String center(String text, int width) {
        if (width <= text.length()) {
            return text;
        }
        int padLeft = (width - text.length()) / 2;
        return getBlanks(padLeft) + text + getBlanks(width - text.length() - padLeft);
    }
}
