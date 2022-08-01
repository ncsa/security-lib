package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Logable;

import static java.lang.System.currentTimeMillis;

/**
 * Simple bechmarking object. Set a logger. Every time you call {@link #lastTime()} the amount of time since you
 * called {@link #lastTime()} us printed. For the total time elapsed, call {@link #totalTime()}
 * There is a canned message that will be printed to the log too, showing both total and elapsed time.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 10, 2010 at  10:49:51 AM
 */
public class Benchmarker {
    long startTime = currentTimeMillis();
    long lastTime = currentTimeMillis();

    /**
     * Get the total elapsed time for this
     *
     * @return
     */
    public long totalTime() {
        return currentTimeMillis() - startTime;
    }

    /**
     * Get the time elapsed since last call
     *
     * @return
     */
    public long lastTime() {
        long x = currentTimeMillis() - lastTime;
        lastTime = currentTimeMillis();
        return x;
    }

    public void msg(String x) {
        logger.debug("** " + x + " (" + lastTime() + " ms. | " + totalTime() + " ms.)");
    }

    public Benchmarker(Logable myLogger) {
        this.logger = myLogger;
    }

    Logable logger;
}
