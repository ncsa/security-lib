package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import java.util.Date;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/12/24 at  1:56 PM
 */
public class DateValue {
    public DateValue(long relativeDate) {
        this.relativeDate = relativeDate;
        relative = true;
    }

    public DateValue(Date iso8601) {
        this.iso8601 = iso8601;
        relative = false;
    }

    public long getRelativeDate() {
        return relativeDate;
    }

    public Date getIso8601() {
        return iso8601;
    }

    long relativeDate = -1L;
    Date iso8601 = null;

    public boolean isRelative() {
        return relative;
    }

    public void setRelative(boolean relative) {
        this.relative = relative;
    }

    public boolean relative = false;

    @Override
    public String toString() {
        return "DateValue{" +
                (relative?("relative=" + relativeDate):("iso8601=" + iso8601) ) +
                '}';
    }
}
