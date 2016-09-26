package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.getFirstAttribute;

/**
 * Provides a logging facade.
 * <p>Created by Jeff Gaynor<br>
 * on 4/11/12 at  5:39 PM
 */
public class LoggerProvider implements Provider<MyLoggingFacade>, LoggingConfigurationTags {
    String logFile;
    boolean debugOn;
    String loggerName;
    int fileCount = -1;
    int maxFileSize = -1;
    boolean appendOn = true;
    boolean disableLog4j = true;


    public LoggerProvider(String logFile,
                          String loggerName,
                          int fileCount,
                          int maxFileSize,
                          boolean disableLog4j,
                          boolean debugOn,
                          boolean appendOn) {
        this.debugOn = debugOn;
        this.logFile = logFile;
        this.loggerName = loggerName;
        this.appendOn = appendOn;
        this.fileCount = fileCount;
        this.maxFileSize = maxFileSize;
        this.disableLog4j = disableLog4j;
    }

    public LoggerProvider(ConfigurationNode configurationNode) {
        this.configurationNode = configurationNode;
        setup();
    }


    protected void setup() {
        if (configurationNode == null) return;
        logFile = getFirstAttribute(configurationNode, LOG_FILE_NAME);
        loggerName = getFirstAttribute(configurationNode, LOGGER_NAME);
        try {
            debugOn = Boolean.parseBoolean(getFirstAttribute(configurationNode, DEBUG_ENABLED));
        } catch (Exception x) {
            // do nothing
            debugOn = false;
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
        try{
            disableLog4j = Boolean.parseBoolean(getFirstAttribute(configurationNode, DISABLE_LOG4J));
        }catch(Exception e){
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
                loggerName = "OAuth for MyProxy";
            }
            logger = new MyLoggingFacade(loggerName);
            logFileName = null;
            if (logFile != null) {
                FileHandler fileHandler = null;
                try {
                    File log = new File(logFile);
                    if (log.canWrite()) {
                        // assume this is not some pattern, but an actual file. Try to give the full path
                        logFileName = log.getCanonicalPath();
                    } else {
                        logFileName = logFile;
                    }
                    fileHandler = new FileHandler(logFile, maxFileSize, fileCount, true);
                    fileHandler.setFormatter(new SimpleFormatter()); // don't get carried away. XML is really verbose.
                    logger.getLogger().addHandler(fileHandler);
                    logger.getLogger().setUseParentHandlers(false); // suppresses console output.
                    logger.info("Logging to file " + logFileName);
                } catch (IOException e) {
                    // Don't blow up. Let everything load and just dump messages into the system log.
                    //throw new GeneralException("Error: could not setup logging to file. Logging to console.");
                    logger.info("Warning: could not setup logging to file. Message:\"" + e.getMessage() + "\". Logging to console. Processing will continue.");
                    logger.info("You probably should configure logging explicitly.");
                }
                logger.setDebugOn(debugOn);
            }
        }
        return logger;
    }
}
