/*******************************************************************************
 * University of Illinois/NCSA
 * Open Source License
 *
 * Copyright (c) 2010, NCSA.  All rights reserved.
 *
 * Developed by:
 * Cyberenvironments and Technologies (CET)
 * http://cet.ncsa.illinois.edu/
 *
 * National Center for Supercomputing Applications (NCSA)
 * http://www.ncsa.illinois.edu/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal with the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimers.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimers in the
 *   documentation and/or other materials provided with the distribution.
 * - Neither the names of CET, University of Illinois/NCSA, nor the names
 *   of its contributors may be used to endorse or promote products
 *   derived from this Software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE SOFTWARE.
 *******************************************************************************/
package edu.uiuc.ncsa.security.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * Note: This is taken from the old defunct  Tupelo project at the NCSA. The handling of
 * dates was very hard to get right and rather than introduce a dependency on
 * that (very large) project,
 * it was deemed easier to simply re-use the code here, with appropriate
 * legal notice.
 *
 * This class provides some utilities for creating and parsing ISO 8601 dates.
 * <H3>Commentary</H3>
 * This has a few things that need to be mentioned. As per contract
 * <br>date to string conversions always only return a date in UTC, i.e., ending with a "Z"
 * <br> string to date allows 0 - 3 digits accuracy and various (incl. fractional) time zones
 * which can be notated as +-xxxx or +-xx:xx.
 * <br><br>therefore round-tripping of dates is consistent but not quite straightforward.
 * The actual values of the dates never
 * change but string comparisons might fail. For instance, a date in one timezone will in general not
 * be equal as a formatted string to another in a different timezone, but might be completely identical as
 * date objects.<br><br>
 * <b>Important technical note:</b> There is a "feature" in java's SimpleDateFormat class. In particular,
 * fractions of seconds are handled in an unintiutive way so that errors were being consistently introduced. The method
 * <code>fixSeconds</code> fixes this and there are regression tests to catch this now as well.
 *
 * @author Joe Futrelle
 */
public class Iso8601 {
    /**
     * Convert a date to a string in the format
     * <blockquote><pre>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</pre></blockquote>
     * where "Z" represents UTC.
     *
     * @param d the date in milliseconds
     * @return the string representation of the date
     */
    public static String date2String(long d) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(d);
        return date2String(c);
    }

    /**
     * Convert a date to a string in the format
     * <blockquote><pre>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</pre></blockquote>
     * where "Z" represents UTC.
     *
     * @param d the date
     * @return the string representation of the date
     */
    public static String date2String(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return date2String(c);
    }

    /**
     * Convert a date to a string in the format
     * <blockquote><pre>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</pre></blockquote>
     * where "Z" represents UTC.
     *
     * @param c the date
     * @return the string representation of the date
     */
    public static String date2String(Calendar c) {
        return getUTCDateFormat().format(c.getTime());
    }

    /**
     * Provides the date format for UTC used by this utility. Normally it is a good idea to use the built
     * in methods of this class and this is provided for backwards compatibility with earlier releases.
     *
     * @return
     */
    public static DateFormat getUTCDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    protected static String zeros = "00000"; // padding for date milliseconds

    /**
     * Provides the DateFormat used by this utility.
     *
     * @return
     */
    public static DateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    /**
     * This normalizes the date so that it always has 3 places for the number of milliseconds.
     * This is unavoidable since there is a serious feature in java's simple date format
     * that will introduce errors.
     *
     * @param rawDate
     * @return
     */
    protected static String fixSeconds(String rawDate) throws ParseException {
        try {
            int decimalPointLocation = rawDate.lastIndexOf(".");
            if (decimalPointLocation == -1) {
                return rawDate + ".000";
            }
            // now compute how many characters long the seconds are
            int secondsLength = rawDate.length() - decimalPointLocation - 1;
            if (3 < secondsLength) {
                // too many digits, so truncate it (the "4" is so we get point + 3 digits
                return rawDate.substring(0, decimalPointLocation + 4);
            }
            return rawDate + zeros.substring(0, 3 - secondsLength);
        } catch (Throwable t) {
            throw new ParseException("error parsing date \"" + rawDate + "\"", 0);
        }
    }

    static int dateIndex = 0;
    static int timezoneIndex = 1;

    synchronized public static String[] splitDate(String rawDate) throws ParseException {
        try {
            String[] result = new String[2];
            if (rawDate.endsWith("Z")) {
                result[dateIndex] = fixSeconds(rawDate.substring(0, rawDate.length() - 1));
                result[timezoneIndex] = "+0000";
            } else {
                // colon offset = 0 if no colon in timeone, 1 if there is a colon,
                int colonOffset = (rawDate.lastIndexOf(":") == rawDate.length() - 3) ? 1 : 0;
                int plusMinusLocation = rawDate.length() - (5 + colonOffset);
                result[dateIndex] = fixSeconds(rawDate.substring(0, plusMinusLocation));
                result[timezoneIndex] = rawDate.substring(plusMinusLocation, plusMinusLocation + 3) + rawDate.substring(rawDate.length() - 2);
            }
            return result;
        } catch (Throwable t) {
            throw new ParseException("error parsing date \"" + rawDate + "\'", 0);
        }
    }


    /**
     * Convert a string in the format
     * <blockquote><pre>yyyy-MM-dd'T'HH:mm:ss.S[tzd]</pre></blockquote>
     * where tzd is either "Z" for UTC or an offset in the format [+-]hh:mm
     * to a date.
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static Calendar string2Date(String s) throws ParseException {
        String[] date = splitDate(s);
        Calendar c = mintCalendar();
        c.setTime(getDateFormat().parse(date[dateIndex] + date[timezoneIndex]));
        return c;
    }

    protected static Calendar mintCalendar() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("UTC"));
        return c;
    }
}


