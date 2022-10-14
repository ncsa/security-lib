package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;

import java.sql.Connection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/21 at  10:25 AM
 */
public class ConnectionRecord implements Identifiable {
    @Override
    public boolean isReadOnly() {
        return false;  // Dummy value, for backwards compatibility.
    }

    @Override
    public void setReadOnly(boolean readOnly) {

    }

    public ConnectionRecord(Connection connection) {
        this.connection = connection;
        identifier = BasicIdentifier.randomID();
    }

    @Override
    public Identifiable clone() {
        throw new NotImplementedException("clone not supported for connections");
    }

    @Override
    public Identifier getIdentifier() {
        if (identifier == null) {
            return null;
        }
        return identifier;
    }

    @Override
    public String getIdentifierString() {
        if (identifier == null) {
            return null;
        }
        return identifier.toString();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    String description;
    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Connection connection;
    Identifier identifier;

    public long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    long lastAccessed = 0L;
    boolean isClosed = true;
    @Override
    public String toString() {
        return "ConnectionRecord{" +
                "identifier=" + identifier +
                ", hasConnection=" + (connection==null?"y":"n") +
                ", lastAccessed=" + lastAccessed +
                ", isClosed=" + isClosed +
                '}';
    }
}
