package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.inject.Provider;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

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
    boolean debugOn = false;
    String loggerName = null;
    int fileCount = -1;
    int maxFileSize = -1;
    boolean appendOn = true;
    boolean disableLog4j = true;
    public String getLoggerName() {
        return loggerName;
    }


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
                    fileHandler = new FileHandler(logFile, maxFileSize, fileCount, appendOn);

                    // The formatting strings to use are here: https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html
                    // Timestamp format is yyyy-MM-dd'T'HH:mm:ss.SSSZ
                    // 1$ is the timestamp
                    // 2$ is the source
                    // 3$ logger name
                    // 4$ is the log level
                    // 5$ log message
                    // 6$ is the stack trace (if any)
                    fileHandler.setFormatter(new SimpleFormatter(){
                        //private static final String format = "%1$tF %1$tT %2$-7s %3$s %n";
                        private static final String format = "%1$tY-%1$tm-%1$tdT%1$tH:%1$tm:%1tS%1$tz %2$s %3$s %n";

                                 @Override
                                 public synchronized String format(LogRecord lr) {
                                     return String.format(format,
                                             new Date(lr.getMillis()),
                                             lr.getLevel().getLocalizedName(),
                                             lr.getMessage()
                                     );
                                 }
                    }); // don't get carried away. XML is really verbose.
                    logger.getLogger().addHandler(fileHandler);
                    logger.getLogger().setUseParentHandlers(false); // suppresses console output.
                    logger.info("Logging to file " + logFileName);
                    logger.setFileName(logFileName);
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
