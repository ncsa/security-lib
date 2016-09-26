package edu.uiuc.ncsa.security.core.configuration;

/**
 * A collection of tags for configuration files. These occur in the XML configuration file and are used
 * by providers to get the associated values.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/12 at  10:26 AM
 */
public interface StorageConfigurationTags extends ConfigurationTags {
    // Basic store types
    public static final String MEMORY_STORE = "memoryStore";
    public static final String FILE_STORE = "fileStore";
    public static final String MYSQL_STORE = "mysql";
    public static final String MARIADB_STORE = "mariadb";
    public static final String POSTGRESQL_STORE = "postgresql";

    // for file stores
    public static final String FS_PATH = "path";
    public static final String FS_INDEX = "indexPath";
    public static final String FS_DATA = "dataPath";

    // for SQL stores

    public static final String SQL_TABLENAME = "tablename";
    public static final String SQL_PREFIX = "tablePrefix";
    public static final String SQL_SCHEMA = "schema";
    public static final String SQL_DATABASE = "database";
}
