package edu.uiuc.ncsa.security.util.configuration;

/**
 * Various constants relating to time.
 * <p>Created by Jeff Gaynor<br>
 * on 3/2/24 at  11:23 PM
 */
public interface TimeConstants {
    String UNITS_DAYS = "d";
    String UNITS_DAYS_LONG = "day";
    String UNITS_HOURS = "hr";
    String UNITS_HOURS_LONG = "hour";
    String UNITS_MILLISECONDS = "ms";
    String UNITS_MILLISECOND_LONG = "millisecond";
    String UNITS_MINUTES = "min";
    String UNITS_MINUTES_LONG = "min";
    String UNITS_MINUTES_LONG2 = "minute";
    String UNITS_MONTHS = "mo";
    String UNITS_MONTHS_LONG = "month";
    String UNITS_SECONDS = "s";
    String UNITS_SECONDS_LONG = "sec";
    String UNITS_SECONDS_LONG2 = "second";
    String UNITS_WEEK_LONG = "week";
    String UNITS_YEARS = "yr";
    String UNITS_YEARS_LONG = "year";
    String UNITS__WEEK = "wk";
    long UNITS_MILLISECOND_MULTIPLIER = 1L;
    long SECONDS = 1000L;
    long ONE_MINUTES = SECONDS * 60;
    long ONE_HOUR = ONE_MINUTES * 60;
    long ONE_DAY = ONE_HOUR * 24;
    long ONE_WEEK = ONE_DAY * 7;
    long ONE_MONTH = ONE_DAY * 30;
    long ONE_YEAR = ONE_DAY * 365 + ONE_HOUR * 6; // 365¼ days
}
