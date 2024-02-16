package edu.uiuc.ncsa.security.storage.monitored;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.storage.AbstractListeningStore;
import edu.uiuc.ncsa.security.storage.ListeningStoreInterface;
import edu.uiuc.ncsa.security.storage.cli.StoreArchiver;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.events.IDMap;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEventListener;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.*;
import edu.uiuc.ncsa.security.storage.sql.ConnectionPool;
import edu.uiuc.ncsa.security.storage.sql.ConnectionRecord;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;

import javax.inject.Provider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConstants.WHEN_BEFORE;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  10:24 AM
 */
public abstract class MonitoredSQLStore<V extends Identifiable> extends SQLStore<V> implements ListeningStoreInterface<V> {
    public MonitoredSQLStore(ConnectionPool connectionPool, Table table, Provider<V> identifiableProvider, MapConverter<V> converter) {
        super(connectionPool, table, identifiableProvider, converter);
    }

    public MonitoredSQLStore() {
    }


    AbstractListeningStore<V> listeningStore = new AbstractListeningStore<>();

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }

    @Override
    public List<LastAccessedEventListener> getLastAccessedEventListeners() {
        return listeningStore.getLastAccessedEventListeners();
    }

    @Override
    public UUID getUuid() {
        return listeningStore.getUuid();
    }

    @Override
    public void addLastAccessedEventListener(LastAccessedEventListener lastAccessedEventListener) {
        listeningStore.addLastAccessedEventListener(lastAccessedEventListener);
    }

    @Override
    public void fireLastAccessedEvent(ListeningStoreInterface store, Identifier identifier) {
        listeningStore.fireLastAccessedEvent(store, identifier);
    }

    @Override
    public boolean isMonitorEnabled() {
        return listeningStore.isMonitorEnabled();
    }

    @Override

    public void setMonitorEnabled(boolean x) {
        listeningStore.setMonitorEnabled(x);
    }

    boolean DEEP_DEBUG = false;

    @Override
    public void lastAccessUpdate(IDMap idMap) {
        MonitoredKeys keys = (MonitoredKeys) getMapConverter().getKeys();
        // Note that prepared statement like "= ?" (with a space!) will not prepare correctly!
        // They seem to have the space embedded in the argument. Always use "=?"
        String sql = "update " + getTable().getFQTablename() + " set " + keys.lastAccessed() + "=?" +
                " where (" + keys.identifier() + " =?) AND (" + keys.lastAccessed() + " IS NULL OR " + keys.lastAccessed() + "<?)";
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        try {
            PreparedStatement pStmt = c.prepareStatement(sql);
            for (Identifier id : idMap.keySet()) {
                pStmt.setLong(1, idMap.get(id));
                pStmt.setString(2, id.toString());
                pStmt.setLong(3, idMap.get(id));
                pStmt.addBatch();
                if (DEEP_DEBUG) {
                    System.out.println("MonitoredSQLStore: updating id=" + id + ", access time=" + idMap.get(id));
                }
            }
            int[] affectedRecords = pStmt.executeBatch();
            long success = 0;
            long noInfo = 0;
            long failed = 0;
            long unknown = 0;
            for (int i = 0; i < affectedRecords.length; i++) {
                int current = affectedRecords[i];
                switch (current) {
                    case Statement.SUCCESS_NO_INFO:
                        noInfo++;
                        break;
                    case Statement.EXECUTE_FAILED:
                        failed++;
                        break;
                    default:
                        if (current < 0) {
                            unknown += current;
                        } else {
                            success += current;
                        }
                        break;
                }
            }
            DebugUtil.trace(this, "updated last_accessed:" +
                    "\n   attempted : " + affectedRecords.length +
                    "\n          ok : " + success +
                    "\n ok, no info : " + noInfo +
                    "\n     unknown : " + unknown +
                    "\n      failed : " + failed);
            releaseConnection(cr);

        } catch (SQLException sqlException) {
            destroyConnection(cr);
            throw new GeneralException("Unable to set last time:" + sqlException.getMessage());
        }

    }


    @Override
    public V get(Object o) {
        V v = super.get(o);
        listeningStore.fireLastAccessedEvent(this, (Identifier) o);
        return v;
    }

    protected MonitoredKeys getKeys() {
        return (MonitoredKeys) getMapConverter().getKeys();
    }

    protected String createUpkeepQuery(RuleList ruleList) {
        MonitoredKeys keys = getKeys();
        String query = "select " + keys.identifier() + ", " + keys.creationTS() + ", " + keys.lastModifiedTS() + ", " + keys.lastAccessed()
                + " from " + getTable().getFQTablename() + " where ";

        for (RuleEntry ruleEntry : ruleList) {
            query = query + toSQLClause(ruleEntry, keys);
        }
        return query;
    }

    public UpkeepResponse upkeep(UpkeepConfiguration upkeepConfiguration) {
        UpkeepResponse upkeepResponse = new UpkeepResponse();

        if (!upkeepConfiguration.isEnabled()) {
            return upkeepResponse;
        }
        /*
         Next is for stats
         */
        int totalFound = 0;
        int numberProcessed = 0;
        int totalTested = 0;
        int totalRetained = 0;
        int skipped = 0;
        List<String> toRemove = new ArrayList<>();
        List<String> toArchive = new ArrayList<>();
        List<String> toRetain = new ArrayList<>();
        MonitoredKeys keys = getKeys();
          /*
            SQL stuff
           */
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        String deleteStmt = "delete from " + getTable().getFQTablename() + " where " + keys.identifier() + "=?";

        StoreArchiver storeArchiver = new StoreArchiver(this);        // Each rule is processed in turn.
        for (RuleList ruleList : upkeepConfiguration.getRuleList()) {
            if (ruleList.isEmpty() || !ruleList.isEnabled()) {
                continue;
            }
            // If the configuration is test only, all rules are in test mode.

            String query = createUpkeepQuery(ruleList);
            try {
                Statement stmt = c.createStatement();
                PreparedStatement deletepStmt = c.prepareStatement(deleteStmt);
                String aQuery = storeArchiver.createVersionStatement();
                System.out.println(getClass().getSimpleName() + " a query=\"" + aQuery + "\"");
                PreparedStatement archiveStmt = c.prepareStatement(aQuery);


                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    Identifier identifier = BasicIdentifier.newID(rs.getString(keys.identifier()));
                    totalFound++;
                    if (toRetain.contains(identifier.toString())) { // If a rule to retain is done, retain it
                        continue;                                 // even if another rule says not to.
                    }
                    switch (ruleList.getAction()) {
                        case UpkeepConstants.ACTION_TEST:
                            // do nothing
                            totalTested++;
                            break;
                        case UpkeepConstants.ACTION_ARCHIVE:
                            if (!upkeepConfiguration.isTestOnly()) {
                                storeArchiver.addToBatch(archiveStmt, identifier);
                            }
                            toArchive.add(identifier.toString());
                            break;
                        case UpkeepConstants.ACTION_RETAIN:
                            totalRetained++;
                            break;
                        case UpkeepConstants.ACTION_DELETE:
                            toRemove.add(identifier.toString());
                            if (!upkeepConfiguration.isTestOnly()) {
                                deletepStmt.setString(1, identifier.toString());
                                deletepStmt.addBatch();
                            }
                            break;
                        default:
                            throw new NFWException("unknown action \"" + ruleList.getAction() + "\"");
                    }
                }
                rs.close();
                stmt.close();
                int[] deletedRecords = deletepStmt.executeBatch();
                UpkeepStats deletedStats = gatherStats(deletedRecords);
                int[] archivedRecords = archiveStmt.executeBatch();
                UpkeepStats archivedStats = gatherStats(archivedRecords);
                deletepStmt.close();
                archiveStmt.close();
                releaseConnection(cr);
                upkeepResponse.archivedStats = archivedStats;
                upkeepResponse.deletedStats = deletedStats;
                upkeepResponse.attempted = numberProcessed;
                upkeepResponse.total = totalFound;
                upkeepResponse.skipped = skipped;
                upkeepResponse.found = toRemove;
                upkeepResponse.archived = toArchive;
                upkeepResponse.retained = toRetain;
            } catch (SQLException sqlException) {
                destroyConnection(cr);
                if (DebugUtil.isEnabled()) {
                    sqlException.printStackTrace();
                }
            }
        }
        return upkeepResponse;
    }

    protected String getTypeKey(MonitoredKeys keys, String type) {
        switch (type) {
            case UpkeepConstants.TYPE_CREATED:
                return keys.creationTS();
            case UpkeepConstants.TYPE_ACCESSED:
                return keys.lastAccessed;
            case UpkeepConstants.TYPE_MODIFIED:
                return keys.lastModified;
        }
        throw new IllegalArgumentException("unknown type \"" + type + "\"");
    }

    public String toSQLClause(RuleEntry ruleEntry, MonitoredKeys keys) {
        if (ruleEntry instanceof IDEntry) {
            return toSQLClause((IDEntry) ruleEntry, keys);
        }
        return toSQLClause((DateEntry) ruleEntry, keys);
    }

    public String toSQLClause(IDEntry idEntry, MonitoredKeys keys) {
        return " " + keys.identifier() + (idEntry.isRegex() ? " REGEXP " : " = '") + idEntry.getValue() + "' ";
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
        System.out.println(getClass().getSimpleName() + "filter query=" + query);
        return query;
    }

    protected UpkeepStats gatherStats(int[] records) {
        int success = 0;
        int noInfo = 0;
        int failed = 0;
        int unknown = 0;
        for (int i = 0; i < records.length; i++) {
            int current = records[i];
            switch (current) {
                case Statement.SUCCESS_NO_INFO:
                    noInfo++;
                    break;
                case Statement.EXECUTE_FAILED:
                    failed++;
                    break;
                default:
                    if (current < 0) {
                        unknown += current;
                    } else {
                        success += current;
                    }
                    break;
            }
        }
        UpkeepStats upkeepStats
                = new UpkeepStats(success, noInfo, failed, unknown);
        return upkeepStats;
    }

}
