package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Version;
import edu.uiuc.ncsa.security.core.configuration.ConfigurationTags;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.inject.Provider;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/18/12 at  2:13 PM
 */
public abstract class LoggingConfigLoader<T extends AbstractEnvironment> implements Serializable, Version, ConfigurationLoader<T> {
    /**
     * Returns a string that identifies the version of this server. This will be automatically written to the
     * log as one of the first bootup messages.
     *
     * @return
     */
    public abstract String getVersionString();

    protected ConfigurationNode cn;

    public Provider<MyLoggingFacade> getLoggerProvider() {
        return loggerProvider;
    }

    public void setLoggerProvider(Provider<MyLoggingFacade> loggerProvider) {
        this.loggerProvider = loggerProvider;
    }

    protected Provider<MyLoggingFacade> loggerProvider;
    protected MyLoggingFacade myLogger = null;

    protected MetaDebugUtil debugger = null;
    /**
     * Checks for and sets up the debugging for this loader. Once this is set up, you may have to tell any environments that
     * use it that debugging is enabled.  Note that this is not used in this module, but in OA4MP proper, but has to b
     * here for visibility later.
     */
    public MetaDebugUtil getDebugger() {
        if(debugger == null){
            debugger = new MetaDebugUtil();
            String rawDebug = Configurations.getFirstAttribute(cn, ConfigurationTags.DEBUG);
            try {
                if (rawDebug == null || rawDebug.isEmpty()) {
                    debugger.setDebugLevel(DebugUtil.DEBUG_LEVEL_OFF);
                } else {
                    debugger.setDebugLevel(rawDebug);
                }
            //    debugger.trace(this, ".load: set debug to level " + DebugUtil.getDebugLevel());

            } catch (Throwable t) {
                // ok, so that didn't work, fall back to the old way
                debugger.setIsEnabled(Boolean.parseBoolean(rawDebug));
            }
        }
        return debugger;
    }


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

    public LoggingConfigLoader(String defaultFile,
                               String defaultName,
                               ConfigurationNode node, MyLoggingFacade logger) {
        // https://github.com/ncsa/oa4mp/issues/106 Check that this node is not null ==> bad config file
        // Don't blow up with an NPE, just exit gracefully.
        if (node == null) {
            if(DebugUtil.isEnabled()){
                System.out.println("No logging configured. Using default.");
            }
           this.myLogger = new MyLoggingFacade(Logger.getLogger("default"));
            return;
        }
        this.cn = node;
        if (defaultFile != null && !defaultFile.isEmpty()) {
            File d = new File(defaultFile);
            if (!d.isAbsolute()) {

                File tempDir = new File(System.getProperty("java.io.tmpdir"));
                d = new File(tempDir, defaultFile);
                defaultFile = d.getAbsolutePath();
            }
        }
        List list = node.getChildren(LoggingConfigurationTags.LOGGING_COMPONENT);
        ConfigurationNode currentNode = null;
        if (!list.isEmpty()) {
            currentNode = (ConfigurationNode) list.get(0);
        }
        if (logger == null) {
            if (currentNode != null) {
                loggerProvider = new LoggerProvider(currentNode);
            } else {
                loggerProvider = new LoggerProvider(defaultFile == null ? "log.xml" : defaultFile,
                        defaultName == null ? "default logging" : defaultName,
                        1,
                        1000000,
                        true,
                        false,
                        MyLoggingFacade.DEFAULT_LOG_LEVEL);
            }
        } else {
            loggerProvider = new MyLoggerProvider(logger);
        }
        // yes, dump it to the console.
        String startupVersion = getVersionString() + " startup on " + (new Date());
        this.myLogger = loggerProvider.get();
        // now we have a log to stick it in.
        info(startupVersion);
    }

    public LoggingConfigLoader(ConfigurationNode node, MyLoggingFacade logger) {
        this(null, null, node, logger);

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
