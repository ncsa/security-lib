package edu.uiuc.ncsa.security.core.util;

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
                      return justify(x,width,JUSTIFY_RIGHT);
    }
    public static String LJustify(String x, int width) {
                          return justify(x,width,JUSTIFY_LEFT);
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
     *  Left justify effectively pads it on the
     * @param x
     * @param width
     * @return
     */
    public static String justify(String x, int width, boolean rightJustify) {
        if (width <= x.length()) {
            return x;
        }

        if (blanks.length() < width) {
            // never run out of blanks...
            while (blanks.length() < width) {
                blanks = blanks + blanks;
            }
        }
        // we know x.length() < width at this point.
        if(rightJustify){

            return blanks.substring(0, width - x.length()) + x;
        }   else{
            return x + blanks.substring(0, width - x.length());
        }
    }

    /**
     * If the string is either null or empty. This is a very common idiom for testing strings
     * but the built in {@link String#isEmpty()} of course cannot be used on a null object...
     * @param x
     * @return
     */
    public static boolean isTrivial(String x) {
        return x == null || x.isEmpty();
    }

    /**
     * Some quick tests for this class.
     * @param args
     */
    public static void main(String[] args){
        // Colon is so we can tell where the end of the strings are.
        System.out.println(justify("abc",10,JUSTIFY_RIGHT) + ":");
        System.out.println(RJustify("abc",10) + ":");
        System.out.println(justify("abc",10,JUSTIFY_LEFT) + ":");
        System.out.println(LJustify("abc",10) + ":");
        System.out.println(truncate("abcdefghijklmnopqrs",10));
        System.out.println(truncate("abcdefghijklmnopqrs",100));
        String x = null;
        System.out.println(isTrivial(x));

    }

    /**
     * Truncate with the default {@link #ELLIPSIS}.
     * @param x
     * @param width
     * @return
     */
    public static String truncate(String x, int width){
             return truncate(x,width,ELLIPSIS);
    }

    /**
     * Truncate with all the defaults.
     * @param x
     * @return
     */
    public static String truncate(String x){
              return truncate(x,DEFAULT_DISPLAY_WIDTH,ELLIPSIS);
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
     * @param x
     * @param width
     * @param ellipsis
     * @return
     */
    public static String truncate(String x, int width, String ellipsis){
        if (x.length() <= width) {
            return x;
        }
        if(isTrivial(ellipsis)){
            ellipsis = ELLIPSIS;
        }
        return x.substring(0,width-ellipsis.length()) + ellipsis;
    }


}
