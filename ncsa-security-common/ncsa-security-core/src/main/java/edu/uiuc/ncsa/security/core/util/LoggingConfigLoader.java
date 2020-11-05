package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Version;
import edu.uiuc.ncsa.security.core.configuration.ConfigurationTags;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.inject.Provider;
import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

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
    protected Provider<MyLoggingFacade> loggerProvider;
    protected MyLoggingFacade myLogger = null;

    /**
     * Checks for and sets up the debugging for this loader. Once this is set up, you may have to tell any environments that
     * use it that debugging is enabled.
     */
    protected void loadDebug() {
        String rawDebug = Configurations.getFirstAttribute(cn, ConfigurationTags.DEBUG);
        try {
            DebugUtil.trace(this, ".load: setting debug for \"" + rawDebug + "\"");
            if (rawDebug == null || rawDebug.isEmpty()) {
                DebugUtil.setDebugLevel(DebugUtil.DEBUG_LEVEL_OFF);
            } else {
                DebugUtil.setDebugLevel(rawDebug);
            }
            DebugUtil.trace(this, ".load: set debug to level " + DebugUtil.getDebugLevel());

        } catch (Throwable t) {
            // ok, so that didn't work, fall back to the old way
            DebugUtil.setIsEnabled(Boolean.parseBoolean(rawDebug));
        }
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
            LoggerProvider loggerProvider2 = null;
            if (currentNode != null) {
                 loggerProvider2 = new LoggerProvider(currentNode);
            } else {
                 loggerProvider2 = new LoggerProvider(defaultFile == null ? "delegation.xml" : defaultFile,
                        defaultName == null ? "NCSA Delegation" : defaultName,
                        1,
                        1000000,
                        true,
                        false,
                        true);
            }
            loggerProvider2.setPrintTimestamp(false);
            this.loggerProvider = loggerProvider2;
        } else {
            loggerProvider = new MyLoggerProvider(logger);
        }

        // yes, dump it to the console.
        String startupVersion = getVersionString() + " startup on " + (new Date());

        this.myLogger = loggerProvider.get();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String machineName =  ip.getHostName();
            if(-1 < machineName.indexOf(".")) {
                machineName = machineName.substring(0, machineName.indexOf("."));
            }
            if(StringUtils.isTrivial(machineName)){
                this.myLogger.setHost(machineName);
            }else{
                this.myLogger.setHost(ip.getHostName());
            }
        }catch(UnknownHostException u){
            // rock on. Cannot determine host
            DebugUtil.trace(this,"could note determine host name for the logger. ");
        }
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
