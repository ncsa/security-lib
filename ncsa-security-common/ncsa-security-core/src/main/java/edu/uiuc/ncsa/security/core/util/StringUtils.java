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
        for(String z: y){System.out.println(z + " |"+z.length());}
        System.out.print("");
        y = wrap(19, x, 125);
        for(String z: y){System.out.println(z + " |"+z.length());}

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
                for(String x : flowedtext){

                    output.add(getBlanks(offset) + x);
                }

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

    /**
     * Pad a string with blanks as needed.
     * @param s
     * @param commandBufferMaxWidth
     * @return
     */
    public static String pad(String s, int commandBufferMaxWidth) {
          if(s.length() < commandBufferMaxWidth){
              return s;
          }
          return s + getBlanks(commandBufferMaxWidth - s.length());
    }
}
