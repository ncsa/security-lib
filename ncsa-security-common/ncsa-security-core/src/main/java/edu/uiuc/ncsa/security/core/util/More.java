package edu.uiuc.ncsa.security.core.util;

import java.util.List;

/**
 * A class the more or less emulates UNIX's more command.
 * <p>Created by Jeff Gaynor<br>
 * on 5/3/20 at  6:13 AM
 */
public class More {
    public static final String PAGE_FORWARD="f";
    public static final String PAGE_BACKWARD="b";
    public static final String NEXT_N_LINES="n";
    public static final String DO_REGEX="/";
    public static final String QUIT="q";
    public static final String CURRENT_LINE_NUMBER="=";
    public static final String REPEAT_LAST_COMMAND=".";
    public static final String HELP_COMMAND="?";
    /*
       n | space - line forward
       f | return - screen forward

     */
     List<String> buffer;
     int pointer = 0;
     
}
