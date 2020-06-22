package edu.uiuc.ncsa.security.util.cli;

import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/20 at  6:57 AM
 */
public interface IOInterface {
    /**
     * read a line of input, printing the prompt (on the left) first. The prompt is not returned.
     * @param prompt
     * @return
     * @throws IOException
     */
    String readline(String prompt) throws IOException;

    /**
     * Read a line of input with no prompt.
     * @return
     * @throws IOException
     */
    String readline() throws IOException;

    /**
     * Print the string representation of an object, without a carriage return.
     * @param x
     */
    void print(Object x);

    /**
     * Print the string representation of an object with a carriage return.
     * @param x
     */
    void println(Object x);

    /**
     * Flush anything waiting to be printed. Normally this is done with the print methods.
     */
    void flush();

    /**
     * Some implementations will have a queue e.g. if the user paste text from the clipboard). The
     * {@link #readline(String)} methods will queue these up and return them in sequence -- this is done silently.
     * If for some reason you need only a single line and want to discard anything else, invoke this.
     */
    void clearQueue();

    /**
     * Check if the queue is empty.
     * @return
     */
    boolean isQueueEmpty();

    /**
     * <b>If</b> this object supports buffering of commands, this will  toggle it. Buffering means that every
     * result of {@link #readline(String)} will be stored and up down arrows will cycle through them.
     * @param bufferOn
     */
    void setBufferingOn(boolean bufferOn);

    /**
     * return current state of buffering.
     * @return
     */
    boolean isBufferingOn();
}
