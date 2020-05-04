package edu.uiuc.ncsa.security.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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

            return getBlanks( width - x.length()) + x;
        } else {
            return x + getBlanks(  width - x.length());
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
        y.add("foo");
        y.add("foo bar afg adfg sdfg sdfadgw546 rhg 54erthvbg sfgsdfg");
        y.add("foo bar baz sd sdfg sd dfg dfg d dfgdfgdfgsdfg zsewyfg dzg9g8fd98 98sa9-8ur9-gb8");
        System.out.println(fromList(wrap(10, y, 40)));
        System.out.println(fromList(wrap(0, y, 30)));
        String x = null;
        System.out.println(isTrivial(x));

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
     * will be padded with 10 characters on the left and 80 and the right. Each line will be wrapped separately.
     *
     * @param leftMargin
     * @param source
     * @param rightMargin
     * @return
     */
    public static String wrap(int leftMargin, String source, int rightMargin) {
        return fromList(wrap(leftMargin, toList(source), rightMargin));

    }

    public static List<String> wrap(int leftMargin, List<String> source, int rightMargin) {
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

}
