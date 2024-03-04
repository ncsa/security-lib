package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.monitored.Monitored;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  9:27 AM
 */
public class DateEntry implements RuleEntry {
    public DateEntry(String type, String when, DateValue dateValue) {
        this.dateValue = dateValue;
        this.when = when;
        this.type = type;
    }

    @Override
    public boolean applies(Monitored monitored) {
        return applies(monitored.getIdentifier(),
                monitored.getCreationTS().getTime(),
                monitored.getLastAccessed() == null ? null : monitored.getLastAccessed().getTime(),
                monitored.getLastModifiedTS().getTime());
    }

    @Override
    public boolean applies(Identifier id, Long created, Long accessed, Long modified) {
        Long targetValue = null;
        switch (getType()) {
            case TYPE_ACCESSED:
                targetValue = accessed;
                break;
            case TYPE_MODIFIED:
                targetValue = modified;
                break;
            case TYPE_CREATED:
                targetValue = created;
                break;
        }
        long now = System.currentTimeMillis();
        boolean rc = false;
        switch (getWhen()) {
            case WHEN_NEVER:
                rc = targetValue == null;
                break;
            case WHEN_AFTER:
                if (targetValue == null) {
                    rc = false;
                } else {
                    if (getDateValue().isRelative()) {
                        rc = now - dateValue.getRelativeDate() <= targetValue;
                    } else {
                        rc = dateValue.getIso8601().getTime() <= targetValue;
                    }
                }
                break;
            case WHEN_BEFORE:
                if (targetValue == null) {
                    rc = false;
                } else {
                    if (getDateValue().isRelative()) {
                        rc = targetValue <= now - dateValue.getRelativeDate();
                    } else {
                        rc = targetValue <= dateValue.getIso8601().getTime();
                    }
                }
                break;
        }
        return rc;
    }


    /*
        if (createdDates.containsKey(RuleFilter.WHEN_AFTER)) {
            DateThingy dateThingy = createdDates.get(RuleFilter.WHEN_AFTER);
            if (dateThingy.isRelative()) {

//WRONG:
//select client_id, creation_ts, last_modified_ts, last_accessed from oauth2.clients
//   where creation_ts<='2023-02-14 07:39:38.997' AND  '2023-08-18 14:39:38.997'<=creation_ts;
//
//RIGHT:
//select client_id, creation_ts, last_modified_ts, last_accessed from oauth2.clients where
//    '2023-02-14 07:39:38.997'<=creation_ts AND creation_ts<= '2023-08-18 14:39:38.997';

                //query = query + key + "<=" + (now - dateThingy.getRelativeDate());
                query = query + " '" + new Timestamp(now - dateThingy.getRelativeDate()) + "'<=" + key;
                //applies = created + dateThingy.relativeDate <= System.currentTimeMillis();
            } else {
                //query = query + key + "<=" + (dateThingy.getIso8601().getTime());
                query = query + " '" + new Timestamp(dateThingy.getIso8601().getTime()) + "'<=" + key;
                //applies = created <= dateThingy.iso8601.getTime();
            }
        }
     */
    protected void checkWhen(String w) {
        if (w == null) {
            throw new IllegalArgumentException("missing when parameter");
        }
        switch (w) {
            case WHEN_AFTER:
            case WHEN_BEFORE:
            case WHEN_NEVER:
                return;
        }
        throw new IllegalArgumentException("unknown when parameter \"" + w + "\"");
    }

    protected void checkType(String t) {
        if (t == null) {
            throw new IllegalArgumentException("missing type parameter");
        }
        switch (t) {
            case TYPE_ACCESSED:
            case TYPE_MODIFIED:
            case TYPE_CREATED:
                return;
        }
        throw new IllegalArgumentException("unknown type parameter \"" + t + "\"");
    }

    DateValue dateValue = null;

    public boolean hasDateValue() {
        return dateValue != null;
    }

    public DateValue getDateValue() {
        return dateValue;
    }

    public void setDateValue(DateValue dateValue) {
        this.dateValue = dateValue;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        checkWhen(when);
        this.when = when;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        checkType(type);
        this.type = type;
    }

    String when;
    String type;

    @Override
    public String toString() {
        return "DateEntry{" +
                "type='" + type + '\'' +
                ", when='" + when + '\'' +
                ", value=" + dateValue +
                '}';
    }


}
