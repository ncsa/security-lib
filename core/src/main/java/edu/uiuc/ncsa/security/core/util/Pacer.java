package edu.uiuc.ncsa.security.core.util;

/**
 * A class used in command line utilities. This has little thingies pacing back and forth
 * in front of the user -- it's an ASCII  status bar.
 * <h2>Usual use</h2>
 * <p>For a long-running task, rather than have the user stare at the screen and
 * having them wonder if the app has hung, start a thread with the task, register this
 * as a listener, then start this. It will pace back and forth until it gets notified
 * the task is done.</p>
 * <p>Invoke the main method of this to see a demo. Note that the pacing should happen
 * on one line. There are some monitors where this does not do that due to how Java
 * is implemented.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 3/26/20 at  10:36 AM
 */
public class Pacer {

    /**
     * String that is the size of the region pacer sits in.
     */
    private String frame;
    /**
     * Where pacer is in its pacing.
     */
    private int index = 0;
    /**
     * How long the area is where pacer is to pace.
     */
    private int len = 0;
    /**
     * Length of a region at the end of the pacer region.
     */
    private int appendLineLength = 0;
    private String message = " items done";

    /**
     * This sets how wide the pacer will be ( = 2*n). Be sure to leave space for any comments. A good value is 20.
     */
    public Pacer(int n) {
        super();
        len = 2 * n;
        char x[] = new char[len];

        for (int i = 0; i < len; i++)
            x[i] = '.';

        frame = frame.copyValueOf(x);
    }

    public Pacer(String message) {
        this(40);
        this.message = message;

    }

    /**
     * Clears the current line of text. Call this when you want to clear where pacer was.
     */
    public void clear() {
        char temp[] = new char[len + appendLineLength];
        for (int i = 0; i < len; i++)
            temp[i] = ' ';
        System.out.print(String.copyValueOf(temp) + "\n");
    }

    /**
     * Causes the pacer to pace once.
     */
    public void pace() {
        char temp[] = frame.toCharArray();
        temp[index] = '/';
        temp[len - index - 1] = '\\';
        System.out.print("\r" + frame.copyValueOf(temp));
        index = (index + 1) % (len);
    }

    /**
     * This will pace, and put a number + message after the pace line. Typical might be
     * pace(counter++, "records read")
     * Which might put out
     * <p>
     * .../.....................\... 1234 records read
     *
     * @param items long
     * @param tail  java.lang.String
     */
    public void pace(long items, String tail) {
        Long l = new Long(items);
        String appendLine = l.toString() + " " + tail;
        char temp[] = frame.toCharArray();
        temp[index] = '/';
        temp[len - index - 1] = '\\';
        if (appendLineLength < appendLine.length())
            appendLineLength = appendLine.length();
        System.out.print("\r" + frame.copyValueOf(temp) + "  " + appendLine);
        index = (index + 1) % (len);

    }

    public void pace(long items) {
        pace(items, message);

    }

    /**
     * How to use it example.
     *
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        int n = 2000;
        System.out.println("This will pace for " + n + " 'records' to show how this works as a status bar");
        Pacer pacer = new Pacer(20);
        for (int i = 0; i < n; i++) {
            if (0 == Math.floorMod(i, 20)) {
                pacer.pace(i, "records read");
                Thread.sleep(100);
            }
        }
    }
}
