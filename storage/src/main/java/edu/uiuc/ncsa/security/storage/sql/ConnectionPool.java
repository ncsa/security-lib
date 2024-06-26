package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.Pool;
import edu.uiuc.ncsa.security.core.util.PoolException;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A pool for jdbc connections. All this needs is configuration for the right jdbc url to use.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  4:06:15 PM
 */
public class ConnectionPool<T extends ConnectionRecord> extends Pool<T> {
    public ConnectionPool() {
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID uuid = UUID.randomUUID();

    public ConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }

    public void setConnectionParameters(ConnectionParameters connectionParameters) {
        this.connectionParameters = connectionParameters;
    }

    public void setType(int type) {
        this.type = type;
    }

    protected ConnectionParameters connectionParameters;

    public ConnectionPool(SQLConnectionImpl connectionParameters, int connectionType) {
        this.connectionParameters = connectionParameters;
        stack = new StackMap<>();
        type = connectionType;
        setupDriverManager();
    }

    protected void setupDriverManager() {
        //CIL-1318 fix
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // BUG in MariaDB driver.  The way the driver manager works is that the JDBC URL
            // is presented to the driver. If the driver accepts it, then it returns a connection and
            // if not, it should return a null. The MariaDB returns itself for both protocols mariadb
            // and mysql. This meant that, depending upon load order for the drivers, the MariaDB
            // driver might be returned. Up until MySQL version 8.0.29 or so, there were no differences really,
            // but now there SQL Connection errors, so we must explicitly intercept the attempt if it
            // fails and do it manually. Fortunately, I remember how to do this from Java 1.2. :(
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            ArrayList<Driver> reorderedDrivers = new ArrayList<>();

            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getCanonicalName().toLowerCase(Locale.ROOT).indexOf("mysql") != -1) {
                    reorderedDrivers.add(0, driver); // prepend MMySQL driver, so it gets called first.
                } else {
                    reorderedDrivers.add(driver);
                }
                DriverManager.deregisterDriver(driver);
            }
            for (Driver driver : reorderedDrivers) {
                DriverManager.registerDriver(driver);// this appends them.
            }
        } catch (Throwable t) {
            if (DEEP_DEBUG) {
                t.printStackTrace();
            }
            // Fix for https://github.com/ncsa/security-lib/issues/40
         //   throw new PoolException("could not register MySQL driver:" + t.getMessage(), t);
        }

    }

    public StackMap getStackMap() {
        return (StackMap) stack;
    }

    public void setStackMap(StackMap stackMap) {
        stack = stackMap;
    }

    public T create() throws PoolException {
        trace("create pool id:" + getUuid() + ", connections: " + getStackMap().map.keySet());
        try {
            Connection con = DriverManager.getConnection(getConnectionParameters().getJdbcUrl());
            if (con == null) {
                throw new IllegalStateException("Could not find any suitable JDBC driver. ");
            }
            T connectionRecord = (T) new ConnectionRecord(con);
            if (!con.isValid(0)) {
                trace("invalid connection: " + con);
                destroy(connectionRecord);
                con = DriverManager.getConnection(getConnectionParameters().getJdbcUrl());
                connectionRecord = (T) new ConnectionRecord(con); // one retry.
            }
            trace("create pool id:" + getUuid() + ", connection: " + con);
            totalCreated++;
            return connectionRecord;
        } catch (Exception x) {
            DebugUtil.trace(this, "Connection failure, JDBC URL=" + getConnectionParameters().getJdbcUrl());
            x.printStackTrace();
            throw new PoolException(x);
        }
    }

    public void destroy(T cc) throws PoolException {
        try {
            trace("destroy id:" + getUuid() + ", connections: " + getStackMap().map.keySet());
            Connection c = cc.connection;
            if (!c.isClosed()) {
                c.close();
            }
            cc.isClosed = true;
            totalDestroyed++;
        } catch (SQLException x) {
            throw new PoolException(x);
        }
    }

    @Override
    public synchronized void push(T object) throws PoolException {
        trace("push id:" + getUuid() + ", connections: " + getStackMap().map.keySet() + ", total created:" + totalCreated);
        super.push(object);
        if (!object.isClosed) {
            object.lastAccessed = System.currentTimeMillis();
        }
    }

    public synchronized T pop(long timeout) throws PoolException, InterruptedException {
        return (T) getStackMap().poll(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized T pop() throws PoolException {
        trace("pop pool id:" + getUuid() + ", connections: " + getStackMap().map.keySet());
        T cr = super.pop();
        cr.lastAccessed = -1L;
        return cr;
    }

    @Override
    public boolean isValid(T t) throws PoolException {
        trace("isValid pool id:" + getUuid() + ", connections: " + getStackMap().map.keySet());

        try {
            return t.connection.isValid(0);
        } catch (SQLException e) {
            throw new PoolException("Invalid object", e);
        }
    }

    public boolean isCleanupEnabled() {
        return cleanupEnabled;
    }

    public void setCleanupEnabled(boolean cleanupEnabled) {
        this.cleanupEnabled = cleanupEnabled;
    }

    boolean cleanupEnabled = false;


    long cleanupInterval;

    public long getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(long cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    public long getIdelLifetime() {
        return idelLifetime;
    }

    public void setIdelLifetime(long idelLifetime) {
        this.idelLifetime = idelLifetime;
    }

    long idelLifetime;
    boolean enableQueue = false;
    long queueInterval;

    public boolean isEnableQueue() {
        return enableQueue;
    }

    public void setEnableQueue(boolean enableQueue) {
        this.enableQueue = enableQueue;
    }

    public long getQueueInterval() {
        return queueInterval;
    }

    public void setQueueInterval(long queueInterval) {
        this.queueInterval = queueInterval;
    }

    public int getType() {
        return type;
    }

    int type = CONNECTION_TYPE_UNKNOWN;

    public static final int CONNECTION_TYPE_UNKNOWN = -1;
    public static final int CONNECTION_TYPE_MYSQL = 1;
    public static final int CONNECTION_TYPE_MARIADB = 2;
    public static final int CONNECTION_TYPE_POSTGRES = 3;
    public static final int CONNECTION_TYPE_DEBRY = 4;
    public static final int CONNECTION_TYPE_H2 = 5;

    protected boolean alreadyShutdown = false;
    /**
     * For those store that need some cleanup.
     */
    public void shutdown(){


    }
}
