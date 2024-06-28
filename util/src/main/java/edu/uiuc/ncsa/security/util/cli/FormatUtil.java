package edu.uiuc.ncsa.security.util.cli;

import java.util.List;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.security.core.util.StringUtils.LJustify;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/25/21 at  5:32 PM
 */
public class FormatUtil {
    static String REGEX_SWITCH = "-r";
    static String COLUMNS_VIEW_SWITCH = "-col";
    static String DISPLAY_WIDTH_SWITCH = "-w";
    static String HELP_SWITCH= "--help";

    public static IOInterface getIoInterface() {
        if(ioInterface == null){
            ioInterface = new BasicIO();
        }
        return ioInterface;
    }

    /**
     * Allows to set the format utility to use the {@link IOInterface}. This is pretty specialized,
     * but it can be done.
     * @param ioInterface
     */
    public static void setIoInterface(IOInterface ioInterface) {
        FormatUtil.ioInterface = ioInterface;
    }

    static IOInterface ioInterface;
    public static void say(String x) {
        getIoInterface().println(x);
        //System.out.println(x);
    }

    public static void printFormatListHelp(IOInterface io, InputLine inputLine){
        printFormatListHelp(io, "", inputLine);
    }
    public static void printFormatListHelp(IOInterface io, String indent, InputLine inputLine){
        io.println(indent + inputLine.getCommand() + " [" + DISPLAY_WIDTH_SWITCH +
               " width | "+ COLUMNS_VIEW_SWITCH + " | "+ REGEX_SWITCH  + " regex]");
        io.println(indent +  DISPLAY_WIDTH_SWITCH + " width = sets the width of the output.");
        io.println(indent + COLUMNS_VIEW_SWITCH + " = single column only (overrides " + DISPLAY_WIDTH_SWITCH + ")");
        io.println(indent + REGEX_SWITCH + " regex = filter the result using a regex");
    }
    /**
      * This allows for setting the formatting
      * @param inputLine
      * @param listOf
      */

    public static void formatList(InputLine inputLine, List<String> listOf) {
        TreeSet<String> list;
        list = new TreeSet<>();
        list.addAll(listOf);
         formatList(inputLine, list);
    }
    public static void formatList(InputLine inputLine, TreeSet<String> list) {
         if(inputLine.hasArg(HELP_SWITCH)){
             printFormatListHelp(new BasicIO(), inputLine);
             return;
         }
         if (list.isEmpty()) {
             return;
         }
         if (list.size() == 1) {
             say(list.first());
             return;
         }


         Pattern pattern = null;
         if (inputLine.hasArg(REGEX_SWITCH)) {
             try {
                 pattern = Pattern.compile(inputLine.getNextArgFor(REGEX_SWITCH));
                 TreeSet<String> list2 = new TreeSet<>();
                 //pattern.
                 for (String x : list) {
                     if (pattern.matcher(x).matches()) {
                         list2.add(x);
                     }
                 }
                 list = list2;
             } catch (Throwable t) {
                 say("sorry but there was a problem with your regex \"" + inputLine.getNextArgFor(REGEX_SWITCH) + "\":" + t.getMessage());
             }

         }
         if (inputLine.hasArg(COLUMNS_VIEW_SWITCH)) {
             for (String func : list) {
                 say(func); // one per line
             }
             return;
         }
         int displayWidth = 120; // just to keep thing simple
         if (inputLine.hasArg(DISPLAY_WIDTH_SWITCH)) {
             try {
                 displayWidth = Integer.parseInt(inputLine.getNextArgFor(DISPLAY_WIDTH_SWITCH));
             } catch (Throwable t) {
                 say("sorry, but " + inputLine.getArg(0) + " is not a number. Formatting for default width of " + displayWidth);
             }
         }

         // Find longest entry
         int maxWidth = 0;
         for (String x : list) {
             maxWidth = Math.max(maxWidth, x.length());
         }
         // special case. If the longest element is too long, just print as columns
         if (displayWidth <= maxWidth) {
             for (String x : list) {
                 say(x);
             }
             return;
         }
         maxWidth = 2 + maxWidth; // so the widest + 2 chars to make it readable.
         // number of columns are display / width, possibly plus 1 if there is a remainder
         //int colCount = displayWidth / maxWidth + (displayWidth % maxWidth == 0 ? 0 : 1);
         int colCount = displayWidth / maxWidth;
         colCount = colCount + (colCount == 0 ? 1 : 0); // Make sure there is at least 1 columns
         int rowCount = list.size() / colCount;
         rowCount = rowCount + (rowCount == 0 ? 1 : 0); // and at least one row
         String[] output = new String[rowCount];
         for (int i = 0; i < rowCount; i++) {
             output[i] = ""; // initialize it
         }
         int pointer = 0;
         for (String func : list) {
             int currentLine = pointer++ % rowCount;
             if (rowCount == 1) {
                 // single row, so don't pad, just a blank between entries
                 output[currentLine] = output[currentLine] + func + "  ";
             } else {
                 output[currentLine] = output[currentLine] + LJustify(func, maxWidth);
             }
         }
         for (String x : output) {
             say(x);
         }

     }
}