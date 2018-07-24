package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Version;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.inject.Provider;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/18/12 at  2:13 PM
 */
public abstract class LoggingConfigLoader<T extends AbstractEnvironment> implements Serializable, Version,ConfigurationLoader<T> {
    /**
     * Returns a string that identifies the version of this server. This will be automatically written to the
     * log as one of the first bootup messages.
     * @return
     */
    public abstract String getVersionString();
    protected ConfigurationNode cn;
    protected Provider<MyLoggingFacade> loggerProvider;
    protected MyLoggingFacade myLogger = null;

    protected class MyLoggerProvider implements Provider<MyLoggingFacade> {
        MyLoggingFacade logger;

        @Override
        public MyLoggingFacade get() {
            return logger;
        }

        public MyLoggerProvider(MyLoggingFacade logger) {
            this.logger = logger;
        }
    }

    public LoggingConfigLoader(ConfigurationNode node, MyLoggingFacade logger) {
        this.cn = node;

        List list = node.getChildren(LoggingConfigurationTags.LOGGING_COMPONENT);
        ConfigurationNode currentNode = null;
        if (!list.isEmpty()) {
            currentNode = (ConfigurationNode) list.get(0);
        }
        if (logger == null) {
            if (currentNode != null) {
                loggerProvider = new LoggerProvider(currentNode);
            } else {
                loggerProvider = new LoggerProvider("delegation.xml", "NCSA Delegation", 1, 1000000, true, false, true);
            }
        } else {
            loggerProvider = new MyLoggerProvider(logger);
        }
        // yes, dump it to the console.
        String startupVersion = getVersionString() + " startup on " + (new Date());
        System.out.println(startupVersion);
        this.myLogger = loggerProvider.get();
        // now we have a log to stick it in.
        info(startupVersion);
    }
    public LoggingConfigLoader(ConfigurationNode node) {
           this(node, null);
       }


    protected void warn(Object infoString) {
           if (myLogger != null) {
               myLogger.warn(infoString.toString());
           }
       }
    protected void info(Object infoString) {
        if (myLogger != null) {
            myLogger.info(infoString.toString());
        }
    }

    protected void debug(Object infoString) {
        if (myLogger != null) {
            myLogger.debug(infoString.toString());
        }
    }

}
