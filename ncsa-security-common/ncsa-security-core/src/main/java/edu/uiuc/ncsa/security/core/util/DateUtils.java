package edu.uiuc.ncsa.security.core.util;


import edu.uiuc.ncsa.security.core.exceptions.InvalidTimestampException;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;

/**
 * General Date utilities. This allows for checking if a token has an expired timestamp
 * and conversions to or from SQL timestamps.
 * <p>Created by Jeff Gaynor<br>
 * on May 18, 2011 at  2:40:19 PM
 */
public class DateUtils {

    /**
     * Parse and retrieve the date from a token.
     *
     * @param token
     * @return
     */
    public static Date getDate(URI token) {
        return getDate(token.toString());
    }

    /**
     * Get the date from a token. Semantics of tokens are that the last path component
     * (i.e. everything after the last "/") is the date in milliseconds.
     * @param token
     * @return
     */
    public static Date getDate(String token) {
        token = token.substring(token.lastIndexOf("/") + 1);
        Date d = new Date();
        d.setTime(Long.parseLong(token));
        return d;
    }

    public static long MAX_TIMEOUT = 900000L;

    /**
     * Check the timestamp for the given token. The maxTime is the maximum life in ms that the
     * token is valid for.
     * @param token
     * @param maxTime
     */
    public static void checkTimestamp(String token, long maxTime) {
        if (token == null || token.length() == 0) {
            throw new InvalidTimestampException("Error: no token found");
        }
        long now = System.currentTimeMillis();
        Date rawTime = getDate(token);
        long issueTime = rawTime.getTime();
        long targetTime = issueTime + maxTime;
        if (now < targetTime) {
            return;
        }
        // print out and error that is readable by people.
        Date d = new Date();
        d.setTime(targetTime);
        throw new InvalidTimestampException("Error: Time expired. The request was only valid between " +
                rawTime + " and " + d + ". Request rejected.");

    }

    /**
     * Given a token, this will take the date and check that it lies within the
     * <code>MAX_TIMEOUT</code> range, throwing an {@link InvalidTimestampException}
     * if it does not.
     *
     * @param token
     */
    public static void checkTimestamp(String token) {
        checkTimestamp(token, MAX_TIMEOUT);
    }

    /**
     * SQL timestamps store their milliseconds slightly differently than dates, so equality will always
     * fail. This is really irritating since SQL timestamps extend Java dates, so the assumption would be
     * that there is no change in functionality.
     * The times as milliseconds, however, are the same. This utility converts between them. Use this
     * whenever you pull an SQL timestamp from a database and need to get a java Date.
     *
     * @param timestamp
     * @return
     */
    public static Date sqlTSToDate(Timestamp timestamp) {
        return new java.util.Date(timestamp.getTime());
    }

    /**
     * Date equality taking into account that one or both of these might be SQL timestamp objects.
     * @param date0
     * @param date1
     * @return
     */
     public static boolean compareDates(Date date0, Date date1) {
         long x0 = date0.getTime(), x1 = date1.getTime();
         return Math.abs(x0 - x1) < 1000;
    }

    /**
     * This compares the two dates down to the nearest millisecond. This is useful if you get back
     * an SQL timestamp that has been converted to a date since there will be random rounding errors
     * in the nanoseconds.
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean equals(Date date1, Date date2) {
        if (date1 == null) {
            if (date2 != null)
                return false;
        } else {
            if (date2 == null) return false;
            long getRidOfTime = 1000 * 60 * 60 * 24;
            long time1 = date1.getTime() / getRidOfTime;
            long time2 = date2.getTime() / getRidOfTime;

            if (time1 != time2) return false;
        }
        return true;
    }
}
