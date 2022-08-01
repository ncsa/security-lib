package edu.uiuc.ncsa.security.util.configuration;


import edu.uiuc.ncsa.security.core.exceptions.UnknownOptionException;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 * Tools for creating command line clients
 * <p>Created by Jeff Gaynor<br>
 * on Jun 25, 2010 at  2:15:40 PM
 */
public class CLITools {
    /**
     * A utility for getting user input. For command line clients. This display the first argument and encloses the
     * second in parentheses. If the user enters a value that is returned, otherwise the second argument is.
     *
     * @param prompt
     * @param defaultValue
     * @return
     */
    public static String prompt(String prompt, Object defaultValue) {
        System.out.print(prompt + " (" + (defaultValue == null ? "none" : defaultValue.toString()) + "):");
        Scanner scanner = new Scanner(System.in);
        String rawInput = scanner.nextLine();
        if (rawInput == null || rawInput.length() == 0) {
            return defaultValue == null ? null : defaultValue.toString();
        }
        return rawInput;

    }

    public static OutputStream getOutputStream() {
        if (outputStream == null) {
            outputStream = System.out;
        }
        return outputStream;
    }

    public static void setOutputStream(OutputStream outputStream) {
        CLITools.outputStream = outputStream;
    }

    static OutputStream outputStream;

    protected static PrintWriter getPrintWriter() {
        if (printWriter == null) {
            printWriter = new PrintWriter(getOutputStream());
        }
        return printWriter;
    }

    static PrintWriter printWriter;

    public static void say(String x) {
        getPrintWriter().println(x);
        getPrintWriter().flush();
    }

    /**
     * Very useful for debugging. Supply the local "this" to get the name of the object plus the current time.
     *
     * @param obj
     * @param x
     */
    public static void say(Object obj, String x) {
        String head = "(null)";
        if (obj != null) {
            head = obj.getClass().getName();
        }
        say(head + " (" + (new Date()) + "): " + x);
    }

    /**
     * This displays the head then each of the list choices which are numbered. The user enters the choice which is
     * converted to an integer then returned. This is perfect for switch statements, e.g. Each option displayed
     * is just the <code>toString</code> method for the object.
     *
     * @param head
     * @param options
     * @return
     */
    public static int listChoose(String head, Object[] options) {
        return listChoose(head, options, 0); // default is 0th list element
    }

    public static int listChoose(String head, Object[] options, int defaultChoice) {
        say(head);
        for (int i = 0; i < options.length; i++) {
            say(i + ". " + options[i].toString());
        }
        String choice = prompt("Enter the number of your choice", Integer.toString(defaultChoice));
        try {
            return Integer.parseInt(choice);
        } catch (NumberFormatException nx) {
            throw new UnknownOptionException();
        }
    }

    /**
     * Convenience method to give a list of choices from a set of options.
     *
     * @param head
     * @param options
     * @return
     */
    public static int listChoose(String head, List options, int defaultChoice) {
        String[] labels = new String[options.size()];
        Iterator iterator = options.iterator();
        for (int i = 0; i < options.size(); i++) {
            labels[i] = iterator.next().toString();
        }
        return listChoose(head, labels, defaultChoice);
    }

}
