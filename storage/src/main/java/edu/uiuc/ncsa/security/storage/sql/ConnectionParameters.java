package edu.uiuc.ncsa.security.storage.sql;

/**
 * Class that wraps the JDBC connection string for an SQL database. This class should
 * see to it that the appropriate driver is loaded and that the url will work
 * when requested, attending to any other state initialization.
 * <p>Created by Jeff Gaynor<br>
 * on 12/16/11 at  9:52 AM
 */
public interface ConnectionParameters {

    String getUsername();
    /**
     * In general every vendor has their own format for this.Construct it as needed.
     *
     * @return
     */
    String getJdbcUrl();
}
