package edu.uiuc.ncsa.security.core.util;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import java.util.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/23/20 at  6:34 AM
 */
public class StringUtils {
    protected static String blanks = "                                                                                                                        ";
    protected static int DEFAULT_DISPLAY_WIDTH = 120;
    public static boolean JUSTIFY_LEFT = false;
    public static boolean JUSTIFY_RIGHT = true;
    public static String ELLIPSIS = "...";

    public static String RJustify(String x, int width) {
        return justify(x, width, JUSTIFY_RIGHT);
    }

    public static String LJustify(String x, int width) {
        return justify(x, width, JUSTIFY_LEFT);
    }

    public static String getBlanks(int width) {
        if (blanks.length() < width) {
            // never run out of blanks...
            while (blanks.length() < width) {
                blanks = blanks + blanks;
            }
        }
        return blanks.substring(0, width);
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
        return x == null || x.isEmpty();
    }

    /**
     * Some quick tests for this class.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Colon is so we can tell where the end of the strings are.
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
        testMap.put("now", new Date());
        List<String> formattedMap = formatMap(testMap,
                null, // do not take a subset
                false,// do not sort the keys
                true, // split the values over several lines if needed
                3,// indent the whole output 3 spaces from the left.
                60 // The total display must fit within 60 chars.
        );
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
     *
     * @param x
     * @param width
     * @param ellipsis
     * @return
     */
    public static String truncate(String x, int width, String ellipsis) {
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

    public static List<String> toList(String x) {
        StringTokenizer st = new StringTokenizer(x, "\n");
        List<String> out = new ArrayList<>();
        while (st.hasMoreTokens()) {
            out.add(st.nextToken());
        }
        return out;
    }

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
     * Pad a string with blanks as needed.
     *
     * @param s
     * @param commandBufferMaxWidth
     * @return
     */
    public static String pad(String s, int commandBufferMaxWidth) {
        if (s.length() < commandBufferMaxWidth) {
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
        if(isTrivial(s)){
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
     * Format a map of objects.
     *
     * @param map          The map to be displayed
     * @param keySubset    An optional (may be null or empty) subset of keys. Only these will be used id present
     * @param sortKeys     if true, the keys in the map will be sorted.
     * @param multiLine    Split the formatting of the vlues in the map over several lines (true) or truncate with ellipses (false)
     * @param indent       The amount for the whole thing to be indented from the left
     * @param displayWidth The total width that this must fit in.
     * @return
     */
    public static List<String> formatMap(Map<String, Object> map,
                                         List<String> keySubset,
                                         boolean sortKeys,
                                         boolean multiLine,
                                         int indent,
                                         int displayWidth) {

        List<String> outputList = new ArrayList<>();
        Map<String, Object> tMap;
        if (sortKeys) {
            tMap = new TreeMap<>(); // sorts the keys
        } else {
            tMap = new HashMap<>();
        }
        if (keySubset == null || keySubset.isEmpty()) {
            tMap.putAll(map);
        } else {
            // take only a subset
            for (String k : keySubset) {
                if (map.containsKey(k)) {
                    tMap.put(k, map.get(k));
                }
            }
        }
        int width = 0;
        for (String key : tMap.keySet()) {
            width = Math.max(width, key.length());
        }
        // Use the order of the tmap (so its sorted) but the XMLMap has information we need to get these.
        for (String key : tMap.keySet()) {
            //String v = map.getString(key);
            Object rawValue = map.get(key);
            if(rawValue==null){
                continue;
            }

            String v = map.get(key).toString();
            if (!StringUtils.isTrivial(v)) {
                if (rawValue instanceof JSON) {
                    v = ((JSON) rawValue).toString(1);
                } else {
                    if (rawValue instanceof Date) {
                        v = Iso8601.date2String((Date) rawValue);
                    } else {
                        v = rawValue.toString();
                        try {
                            // Check if it's serialized JSON.
                            JSON json = JSONSerializer.toJSON(v);
                            v = json.toString(1);
                        } catch (Throwable t) {

                        }
                    }

                }
                outputList.add(formatMapEntry(key, v, indent, displayWidth, width, multiLine));
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
        return RJustify(key, leftColumWidth) + " : " + truncate(value.replace("\n", "").replace("\r", ""));
    }
}
