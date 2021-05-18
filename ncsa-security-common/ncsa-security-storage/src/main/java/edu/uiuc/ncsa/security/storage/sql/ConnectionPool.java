package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.Pool;
import edu.uiuc.ncsa.security.core.util.PoolException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A pool for jdbc connections. All this needs is configuration for the right jdbc url to use.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  4:06:15 PM
 */
public class ConnectionPool<T extends ConnectionRecord> extends Pool<T> {
    public UUID getUuid() {
        return uuid;
    }

    public UUID uuid = UUID.randomUUID();

    public ConnectionParameters getConnectionParameters() {
        return connectionParameters;
    }

    ConnectionParameters connectionParameters;

    public ConnectionPool(SQLConnectionImpl connectionParameters) {
        this.connectionParameters = connectionParameters;
        stack = new StackMap<>();
    }
    public StackMap getStackMap(){
        return (StackMap) stack;
    }

    public void setStackMap(StackMap stackMap){
        stack = stackMap;
    }
    
    public T create() throws PoolException {
        trace("create pool id:" + getUuid() + ", connections: " + getStackMap().map.keySet());
        try {
            Connection con = DriverManager.getConnection(getConnectionParameters().getJdbcUrl());
            T connectionRecord = (T) new ConnectionRecord(con);
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
        } catch (SQLException x) {
            throw new PoolException(x);
        }
    }

    @Override
    public synchronized void push(T object) throws PoolException {
        trace("push id:" + getUuid() + ", connections: " + getStackMap().map.keySet());
        super.push(object);
        if(!object.isClosed){
            object.lastAccessed = System.currentTimeMillis();
        }
    }
    public synchronized T pop(long timeout) throws PoolException, InterruptedException {
          return (T) getStackMap().poll(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public synchronized T pop() throws PoolException {
        trace("pop pool id:" + getUuid() + ", connections: " + getStackMap().map.keySet());
        T cr= super.pop();
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
}
