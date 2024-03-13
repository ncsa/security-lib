package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.storage.monitored.Monitored;
import edu.uiuc.ncsa.security.storage.monitored.MonitoredKeys;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  9:23 AM
 */
public class RuleList extends ArrayList<RuleEntry> implements UpkeepConstants {
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Boolean isSkipVersions() {
        return skipVersions;
    }

    public List<String> getExtendsList() {
        return extendsList;
    }

    public void setExtendsList(List<String> extendsList) {
        this.extendsList = extendsList;
    }

    List<String> extendsList = new ArrayList<>();

    public void setSkipVersions(Boolean skipVersions) {
        this.skipVersions = skipVersions;
    }

    public boolean hasSkipVersion() {
        return skipVersions != null;
    }

    Boolean skipVersions = null;
    public boolean isSkipCollateral() {
        return skipCollateral;
    }

    public void setSkipCollateral(boolean skipCollateral) {
        this.skipCollateral = skipCollateral;
    }

    boolean skipCollateral = true;
    boolean verbose = false;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        switch (action) {
            case ACTION_ARCHIVE:
            case ACTION_DELETE:
            case ACTION_RETAIN:
            case ACTION_TEST:
                this.action = action;
                return;
        }
        throw new IllegalArgumentException("unknown action \"" + action + "\"");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RuleList{" +
                "enabled=" + enabled +
                ", action='" + action + '\'' +
                ", name='" + name + '\'' +
                ", values=" + super.toString() +
                '}';
    }


    boolean enabled;
    String action;
    String name;

    public boolean applies(Monitored monitored) {
        return applies(monitored.getIdentifier(),
                monitored.getCreationTS().getTime(),
                monitored.getLastAccessed() == null ? null : monitored.getLastAccessed().getTime(),
                monitored.getLastModifiedTS().getTime());
    }
    Map<String, List<RuleEntry>> rulesByAction = new HashMap();
    public boolean applies(Identifier id, Long create, Long accessed, Long modified) {
        if((!hasSkipVersion() || isSkipVersions()) && 0<id.toString().indexOf(Store.VERSION_TAG)){return false;}
        // Implicit is that every rule entry has to apply, i.e., this is the logical AND of all entries.
        // Has to be in passes. IDEntries are OR'd and Date entries are AND'd
        // Name rules have an implicit OR between them, date rules an implicit AND.
        return  applyIDEntries(id, create,accessed, modified) || applyDateEntries(id, create, accessed, modified);
    }

    protected boolean applyIDEntries(Identifier id, Long create, Long accessed, Long modified) {
        for (RuleEntry ruleEntry : this) {
            if(ruleEntry instanceof IDEntry) {
                if (ruleEntry.applies(id, create, accessed, modified)) return true; // effective OR
            }
        }
      return false;
    }

    protected boolean applyDateEntries(Identifier id, Long create, Long accessed, Long modified) {
        boolean noEntries = true;
        for (RuleEntry ruleEntry : this) {
            if(ruleEntry instanceof DateEntry) {
                noEntries = false;
               if(! ruleEntry.applies(id, create, accessed, modified)) return false; // effective AND
            }
        }

        return !noEntries;
    }

    public String toSQLQuery(MonitoredKeys keys, String tableName) {
             return toSQLQuery(keys, tableName, !hasSkipVersion() || isSkipVersions());
    }

    /**
     * Convert this rule to an SQL query.
     * @param keys
     * @param tableName
     * @param skipVersions
     * @return
     */
    public String toSQLQuery(MonitoredKeys keys, String tableName, boolean skipVersions) {
        String query = "select " + keys.identifier() + ", " + keys.creationTS() + ", " + keys.lastModifiedTS() + ", " + keys.lastAccessed()
                + " from " + tableName;

        if (isEmpty()) {
            if (skipVersions) {
                query = query + " where " + keys.identifier() + " NOT REGEXP '.*#version.*' ";
            }
            return query;
        }
        query = query + " where ";
        boolean firstPassAnds = true;
        boolean firstPassOrs = true;
        String ands = "";
        String ors = "";

        for (RuleEntry ruleEntry : this) {

            if (ruleEntry instanceof IDEntry) {
                String x = toSQLClause((IDEntry) ruleEntry, keys);
                if (firstPassOrs) {
                    firstPassOrs = false;
                    ors = ors + x;
                } else {
                    ors = ors + " OR " + x;
                }
            }
            if (ruleEntry instanceof DateEntry) {
                String x = toSQLClause((DateEntry) ruleEntry, keys);
                if (firstPassAnds) {
                    firstPassAnds = false;
                    ands = ands + toSQLClause((DateEntry) ruleEntry, keys);
                } else {
                    ands = ands + " AND " + toSQLClause((DateEntry) ruleEntry, keys);
                }
            }
        } // end for
        if (!ands.isEmpty()) {
            query = query + " (" + ands + ")";
            if (!ors.isEmpty()) {
                query = query + " AND ";
            }
        }
        if (!ors.isEmpty()) {
            query = query + " (" + ors + ")";
        }
        if (skipVersions) {
            query = query + " AND (" + keys.identifier() + " NOT REGEXP '." + Store.VERSION_TAG+".*' )";
        }
        return query;
    }

    public String toSQLClause(IDEntry idEntry, MonitoredKeys keys) {
        return " " + keys.identifier() +
                (idEntry.isNegation() ? " NOT " : "") +
                (idEntry.isRegex() ? " REGEXP " : " = ") + "'" +
                idEntry.getValue() + "' ";
    }


    public String toSQLClause(DateEntry dateEntry, MonitoredKeys keys) {
        String query = "";
        if (dateEntry.getWhen().equals(UpkeepConstants.WHEN_NEVER)) {
            return " " + getTypeKey(keys, dateEntry.getType()) + " IS NULL ";
        }
        // date value is not null
        DateValue dv = dateEntry.getDateValue();
        String key = getTypeKey(keys, dateEntry.getType());
        long now = System.currentTimeMillis();
        if (dateEntry.getWhen().equals(UpkeepConstants.WHEN_AFTER)) {
            if (dv.isRelative()) {
                  /*
       WRONG:
       select client_id, creation_ts, last_modified_ts, last_accessed from oauth2.clients
          where creation_ts<='2023-02-14 07:39:38.997' AND  '2023-08-18 14:39:38.997'<=creation_ts;

       RIGHT:
       select client_id, creation_ts, last_modified_ts, last_accessed from oauth2.clients where
           '2023-02-14 07:39:38.997'<=creation_ts AND creation_ts<= '2023-08-18 14:39:38.997';
                   */
                //query = query + key + "<=" + (now - dateThingy.getRelativeDate());
                query = query + " '" + new Timestamp(now - dv.getRelativeDate()) + "'<=" + key + " ";
                //applies = created + dateThingy.relativeDate <= System.currentTimeMillis();
            } else {
                //query = query + key + "<=" + (dateThingy.getIso8601().getTime());
                query = query + " '" + new Timestamp(dv.getIso8601().getTime()) + "'<=" + key + " ";
                //applies = created <= dateThingy.iso8601.getTime();
            }
        }
        if (dateEntry.getWhen().equals(WHEN_BEFORE)) {
            if (dv.isRelative()) {
                //query = query + (now - dateThingy.getRelativeDate()) + "<=" + key;
                query = query + " " + key + "<='" + new Timestamp(now - dv.getRelativeDate()) + "' ";
                //applies = applies && (  System.currentTimeMillis() <= created + dateThingy.relativeDate);
            } else {
                //query = query + dateThingy.getIso8601().getTime() + "<=" + key;
                query = query + " " + key + "<='" + new Timestamp(dv.getIso8601().getTime()) + "' ";
                // applies = applies && (  dateThingy.iso8601.getTime() <= created);
            }
        }
        return query;
    }

    protected String getTypeKey(MonitoredKeys keys, String type) {
        switch (type) {
            case UpkeepConstants.TYPE_CREATED:
                return keys.creationTS();
            case UpkeepConstants.TYPE_ACCESSED:
                return keys.lastAccessed();
            case UpkeepConstants.TYPE_MODIFIED:
                return keys.lastModifiedTS();
        }
        throw new IllegalArgumentException("unknown type \"" + type + "\"");
    }
}
