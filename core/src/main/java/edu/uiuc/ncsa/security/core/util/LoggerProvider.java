package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.util.logging.*;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.getFirstAttribute;

/**
 * Provides a logging facade.
 * <p>Created by Jeff Gaynor<br>
 * on 4/11/12 at  5:39 PM
 */
public class LoggerProvider implements Provider<MyLoggingFacade>, LoggingConfigurationTags {
    public String getLogFile() {
        return logFile;
    }

    String logFile = null;

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    String loggerName = null;
    int fileCount = -1;
    int maxFileSize = -1;
    boolean appendOn = true;

    Level logLevel = Level.INFO; // default
    boolean disableLog4j = true;

    public String getLoggerName() {
        return loggerName;
    }

    int level;

    public LoggerProvider(String logFile,
                          String loggerName,
                          int fileCount,
                          int maxFileSize,
                          boolean disableLog4j,
                          boolean appendOn,
                          Level logLevel) {
        this.logFile = logFile;
        this.loggerName = loggerName;
        this.appendOn = appendOn;
        this.fileCount = fileCount;
        this.maxFileSize = maxFileSize;
        this.disableLog4j = disableLog4j;
        this.logLevel = logLevel;
    }

    public LoggerProvider(ConfigurationNode configurationNode) {
        this.configurationNode = configurationNode;
        setup();
    }


    protected void setup() {
        if (configurationNode == null) return;
        logFile = getFirstAttribute(configurationNode, LOG_FILE_NAME);
        loggerName = getFirstAttribute(configurationNode, LOGGER_NAME);
        String rawDebug = getFirstAttribute(configurationNode, DEBUG_ENABLED);
        String rawLogLevel = getFirstAttribute(configurationNode, LOG_LEVEL);
        if (rawDebug == null) {
            if (rawLogLevel == null) {
                logLevel = Level.INFO;
            } else {
                rawLogLevel = rawLogLevel.toLowerCase();
                switch (rawLogLevel) {
                    case LOG_LEVEL_OFF:
                        logLevel = Level.OFF;
                        break;
                    case LOG_LEVEL_TRACE:
                        logLevel = Level.FINEST;
                        break;
                    case LOG_LEVEL_INFO:
                        logLevel = Level.INFO;
                        break;
                    case LOG_LEVEL_ERROR:
                        logLevel = Level.SEVERE;
                        break;
                    case LOG_LEVEL_WARN:
                        logLevel = Level.WARNING;
                        break;
                    default:
                        logLevel = MyLoggingFacade.DEFAULT_LOG_LEVEL;
                        break;
                }
            }
        } else {
            try {
                boolean debugOn = Boolean.parseBoolean(rawDebug);
                if (debugOn) {
                    logLevel = Level.FINEST;
                } else {
                    logLevel = MyLoggingFacade.DEFAULT_LOG_LEVEL;
                }
            } catch (Throwable tttt) {
                logLevel = MyLoggingFacade.DEFAULT_LOG_LEVEL;
            }
        }
        try {
            if (Boolean.parseBoolean(getFirstAttribute(configurationNode, DEBUG_ENABLED))) {
                logLevel = Level.FINEST;
            }
        } catch (Exception x) {
            // do nothing
            logLevel = Level.INFO;
        }
        try {
            fileCount = Integer.parseInt(getFirstAttribute(configurationNode, LOG_FILE_COUNT));
        } catch (Exception x) {
            fileCount = 1;
        }
        try {
            maxFileSize = Integer.parseInt(getFirstAttribute(configurationNode, LOG_FILE_SIZE));
        } catch (Exception e) {
            maxFileSize = 1000000;
        }
        try {
            appendOn = Boolean.parseBoolean(getFirstAttribute(configurationNode, APPEND_ENABLED));
        } catch (Exception e) {
            appendOn = true;
        }
        try {
            disableLog4j = Boolean.parseBoolean(getFirstAttribute(configurationNode, DISABLE_LOG4J));
        } catch (Exception e) {
            disableLog4j = false;
        }

    }

    ConfigurationNode configurationNode;
    MyLoggingFacade logger;

    String logFileName = null;

    public String getLogFileName() {
        return logFileName;
    }

    @Override
    public MyLoggingFacade get() {

        if (logger == null) {
            if (disableLog4j) {
                Configurations.killLog4J();
            }
            if (loggerName == null) {
                loggerName = "NCSA-sec-lib";
            }
            logger = new MyLoggingFacade(loggerName);
            //  logger = java.util.logging.Logger.getLogger(MyLoggingFacade.class.getCanonicalName());
            logger.setLogLevel(logLevel);
            logFileName = null;
            if (logFile != null) {
                try {
                    File log = new File(logFile);
                    if (log.canWrite()) {
                        // assume this is not some pattern, but an actual file. Try to give the full path
                        logFileName = log.getCanonicalPath();
                    } else {
                        logFileName = logFile;
                    }
                    // Fix https://github.com/ncsa/security-lib/issues/32
                    StreamHandler streamHandler;
                    if (logFileName.equals("/dev/stdout") || logFileName.equals("/dev/stderr")) {
                        System.err.println(getClass().getSimpleName() + ": setting handler to console");
                        streamHandler = new ConsoleHandler();
                    } else {
                        System.err.println(getClass().getSimpleName() + ": setting handler to file");
                        streamHandler = new FileHandler(logFile, maxFileSize, fileCount, appendOn);
                    }
                    streamHandler.setFormatter(new SimpleFormatter()); // don't get carried away. XML is really verbose.
                    logger.getLogger().addHandler(streamHandler);
                    logger.getLogger().setUseParentHandlers(false); // suppresses console output.
                    logger.info("Logging to file " + logFileName);
                    logger.setFileName(logFileName);
                } catch (IOException e) {
                    // Don't blow up. Let everything load and just dump messages into the system log.
                    //throw new GeneralException("Error: could not setup logging to file. Logging to console.");
                    logger.info("Warning: could not setup logging to file. Message:\"" + e.getMessage() + "\". Logging to console. Processing will continue.");
                    logger.info("You probably should configure logging explicitly.");
                }
            } else {
                StreamHandler streamHandler = new ConsoleHandler();
                streamHandler.setFormatter(new SimpleFormatter()); // don't get carried away. XML is really verbose.
                logger.getLogger().addHandler(streamHandler);
                logger.getLogger().setUseParentHandlers(false); // suppresses console output.
                logger.info("Logging to console");
                System.err.println(getClass().getSimpleName() + ": logging to console");
            }
        }
        return logger;
    }
}
