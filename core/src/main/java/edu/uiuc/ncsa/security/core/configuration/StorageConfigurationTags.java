package edu.uiuc.ncsa.security.core.configuration;

/**
 * A collection of tags for configuration files. These occur in the XML configuration file and are used
 * by providers to get the associated values.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/12 at  10:26 AM
 */
public interface StorageConfigurationTags extends ConfigurationTags {
    // Basic store types
     String MEMORY_STORE = "memoryStore";
     String FILE_STORE = "fileStore";
     String MYSQL_STORE = "mysql";
     String MARIADB_STORE = "mariadb";
     String POSTGRESQL_STORE = "postgresql";
     String DERBY_STORE = "derby";
     String DERBY_STORE_TYPE_MEMORY = "memory";
     String DERBY_STORE_TYPE_FILE = "file";
     String DERBY_STORE_TYPE_SERVER = "server";


     String FS_PATH = "path";
     String FS_INDEX = "indexPath";
     String FS_DATA = "dataPath";
     String FS_REMOVE_EMPTY_FILES = "removeEmptyFiles";
     String FS_REMOVE_FAILED_FILES = "removeFailedFiles";



     String SQL_TABLENAME = "tablename";
     String SQL_PREFIX = "tablePrefix";
     String SQL_SCHEMA = "schema";
     String SQL_DATABASE = "database";
}
