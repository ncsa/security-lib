package edu.uiuc.ncsa.security.storage.monitored;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.storage.MonitoredStoreDelegate;
import edu.uiuc.ncsa.security.storage.MonitoredStoreInterface;
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
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  10:24 AM
 */
public abstract class MonitoredSQLStore<V extends Identifiable> extends SQLStore<V> implements MonitoredStoreInterface<V> {
    public MonitoredSQLStore(ConnectionPool connectionPool, Table table, Provider<V> identifiableProvider, MapConverter<V> converter) {
        super(connectionPool, table, identifiableProvider, converter);
    }

    public MonitoredSQLStore() {
    }


    MonitoredStoreDelegate<V> monitoredStoreDelegate = new MonitoredStoreDelegate<>();

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }

    @Override
    public List<LastAccessedEventListener> getLastAccessedEventListeners() {
        return monitoredStoreDelegate.getLastAccessedEventListeners();
    }

    @Override
    public UUID getUuid() {
        return monitoredStoreDelegate.getUuid();
    }

    @Override
    public void addLastAccessedEventListener(LastAccessedEventListener lastAccessedEventListener) {
        monitoredStoreDelegate.addLastAccessedEventListener(lastAccessedEventListener);
    }

    @Override
    public void fireLastAccessedEvent(MonitoredStoreInterface store, Identifier identifier) {
        monitoredStoreDelegate.fireLastAccessedEvent(store, identifier);
    }

    @Override
    public boolean isMonitorEnabled() {
        return monitoredStoreDelegate.isMonitorEnabled();
    }

    @Override

    public void setMonitorEnabled(boolean x) {
        monitoredStoreDelegate.setMonitorEnabled(x);
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
        monitoredStoreDelegate.fireLastAccessedEvent(this, (Identifier) o);
        return v;
    }

    protected MonitoredKeys getKeys() {
        return (MonitoredKeys) getMapConverter().getKeys();
    }

    @Override
    public UpkeepResponse doUpkeep(AbstractEnvironment environment) {
        UpkeepResponse upkeepResponse = new UpkeepResponse();
        UpkeepConfiguration cfg = getUpkeepConfiguration();  // just keep it short.
        if (!cfg.isEnabled()) {
            return upkeepResponse;
        }
        /*
         Next is for stats
         */

        upkeepResponse.testModeOnly = cfg.isTestOnly();
        upkeepResponse.name = "upkeep for " + getClass().getSimpleName();
        MonitoredKeys keys = getKeys();
          /*
            SQL stuff
           */
        ConnectionRecord cr = getConnection();
        Connection c = cr.connection;
        StoreArchiver storeArchiver = new StoreArchiver(this);        // Each rule is processed in turn.
        for (RuleList ruleList : cfg.getRuleList()) {
            if (ruleList.isEmpty() || !ruleList.isEnabled() || (ruleList.getAction() == UpkeepConstants.ACTION_RETAIN)) {
                continue;
            }
            // If the configuration is test only, all rules are in test mode.
            boolean skipVersions = true;// default
            if (cfg.hasSkipVersion()) {
                skipVersions = cfg.isSkipVersions();
            }
            if (ruleList.hasSkipVersion()) {
                skipVersions = ruleList.isSkipVersions();
            }
            String query = ruleList.toSQLQuery(keys, getTable().getFQTablename(), skipVersions);
            if (cfg.isDebug()) {
                System.err.println("upkeep SQL query for rule " + ruleList.getName() + " is \"" + query + "\"");
            }
            try {
                PreparedStatement deletepStmt = null;
                PreparedStatement archiveStmt = null;
                Statement stmt = c.createStatement();
                if (!cfg.isTestOnly()) {    // globally set testMode means all test actions are overridden
                    String aQuery = storeArchiver.createVersionStatement();
                    String deleteStmt = "delete from " + getTable().getFQTablename() + " where " + keys.identifier() + "=?";
                    System.out.println(getClass().getSimpleName() + " a query=\"" + aQuery + "\"");
                    deletepStmt = c.prepareStatement(deleteStmt);
                    archiveStmt = c.prepareStatement(aQuery);
                }

                ResultSet rs = stmt.executeQuery(query);
                List<Identifier> idList = new ArrayList<>();
                while (rs.next()) {
                    String idString = rs.getString(keys.identifier());
                    Identifier identifier = BasicIdentifier.newID(idString);
                    idList.add(identifier);
                    upkeepResponse.attempted++;
                    if (upkeepResponse.retainedList.contains(idString)) { // If a rule to retain is done, retain it
                        upkeepResponse.skipped++;
                        if (cfg.isDebug()) {
                            System.err.println(getClass().getSimpleName() + " skipped:" + idString);
                        }
                        continue;                                 // even if another rule says not to.
                    }
                    switch (ruleList.getAction()) {
                        case UpkeepConstants.ACTION_TEST:
                            // do nothing
                            upkeepResponse.testedCount++;
                            break;
                        case UpkeepConstants.ACTION_RETAIN:
                            upkeepResponse.retainedCount++;
                            upkeepResponse.retainedList.add(idString);
                            break;
                        case UpkeepConstants.ACTION_ARCHIVE:
                            if (!cfg.isTestOnly()) {
                                storeArchiver.addToBatch(archiveStmt, identifier);
                                upkeepResponse.archivedList.add(idString);
                            }
                            upkeepResponse.archiveCount++;
                            /// archive implies delete from main store too...
                        case UpkeepConstants.ACTION_DELETE:
                            upkeepResponse.deleteCount++;
                            if (!cfg.isTestOnly()) {
                                deletepStmt.setString(1, identifier.toString());
                                deletepStmt.addBatch();
                                upkeepResponse.deletedList.add(idString);
                            }
                        case UpkeepConstants.ACTION_NONE:
                            // Do nothing.
                            break;
                        default:
                            throw new NFWException("unknown action \"" + ruleList.getAction() + "\"");
                    }
                }
                rs.close();
                stmt.close();                if (!cfg.isTestOnly()) {
                    // must do archives before deletes.
                    int[] archivedRecords = archiveStmt.executeBatch();
                    upkeepResponse.archivedStats.add(gatherStats(archivedRecords));
                    int[] deletedRecords = deletepStmt.executeBatch();
                    upkeepResponse.deletedStats.add(gatherStats(deletedRecords));
                    deletepStmt.close();
                    archiveStmt.close();
                }
                updateHook(ruleList.getAction(), environment, idList);
            } catch (SQLException sqlException) {
                destroyConnection(cr);
                if (cfg.isDebug() || DebugUtil.isEnabled()) {
                    sqlException.printStackTrace();
                }
            }
        }// end for
        releaseConnection(cr);
        if (cfg.hasOutput()) {
            try {
                FileWriter fos = new FileWriter(cfg.getOutput());
                fos.write(upkeepResponse.toJSON().toString(1));
                fos.flush();
                fos.close();
            } catch (IOException ioException) {
                // Not being able to write the output should not be a hard fail.
                DebugUtil.warn("could not write file \"" + cfg.getOutput() + "\":" + ioException.getMessage());
            }
        }
        return upkeepResponse;
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


    @Override
    public void setUpkeepConfiguration(UpkeepConfiguration upkeepConfiguration) {
        monitoredStoreDelegate.setUpkeepConfiguration(upkeepConfiguration);
    }

    @Override
    public UpkeepConfiguration getUpkeepConfiguration() {
        return monitoredStoreDelegate.getUpkeepConfiguration();
    }

    @Override
    public void updateHook(String action, AbstractEnvironment environment, List<Identifier> identifiers) {

    }
}
