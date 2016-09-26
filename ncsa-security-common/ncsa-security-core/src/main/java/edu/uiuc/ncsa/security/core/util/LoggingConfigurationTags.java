package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.configuration.ConfigurationTags;

/**
 * Tags that are used in the logging XML element in a configuration file.
 * <p>Created by Jeff Gaynor<br>
 * on 4/12/12 at  9:46 AM
 */
public interface LoggingConfigurationTags extends ConfigurationTags {
    /**
     * Name of the component
     */
    public static final String LOGGING_COMPONENT = "logging"; // top level to look for
    /**
     * File name to log to.
     */
    public static final String LOG_FILE_NAME = "logFileName";
    /**
     * Name of the log. Each entry is prepended with this so that if there are multiple entries
     * in the file they can be disambiguated.
     */
    public static final String LOGGER_NAME = "logName";
    /**
     * Number of log files to have
     */
    public static final String LOG_FILE_COUNT = "logFileCount";
    /**
     * Maximum size of a log file
     */
    public static final String LOG_FILE_SIZE = "logFileSize";
    /**
     * Enable debugging.
     */
    public static final String DEBUG_ENABLED = "debug";
    /**
     * Append to existing log files on start or overwrite.
     */
    public static final String APPEND_ENABLED = "append";
    /**
     * Disable log 4 java. At times badly behaving libraries have poorly configured log 4 java
     * which can be very messy. This will kill off <b>ALL</b> log 4 java in the extreme case,
     * so only use it if you need it.
     */
    public static final String DISABLE_LOG4J = "disableLog4j";
}
