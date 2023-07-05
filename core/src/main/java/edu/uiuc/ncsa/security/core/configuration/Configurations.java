package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Utility class for getting configurations and things in them. Generally every call is
 * done anew, so anything you want to keep you should store.
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  11:20 AM
 */
public class Configurations {

    public static final String FILE_TAG = "file";
    public static final String INCLUDE_TAG = "include";

    /**
     * Useful utility call. This explicitly kills off logging from log 4 java. This is needed since some
     * dependency (don't know which) has a misconfigured log4j properties file in its library which
     * spews forth random messages. Invoke this at server start to definitely kill off any log4j activity.
     * It is annoying to have to add a dependency to clean up after someone else!
     */
    public static void killLog4J() {
     //   DebugUtil.trace(Configurations.class, "Killing off log 4 java!");
        // CIL-1145 fix. More recent log4j versions now have a way to reset all levels.
       // Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.OFF);
       /* List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
        loggers.add(LogManager.getRootLogger());
        for (Logger logger : loggers) {
            logger.setLevel(Level.OFF);
        }*/
   //     DebugUtil.trace(Configurations.class, "log 4 java has been dispatched!");
    }

    /**
     * Loads the configuration from the given file. Various checks are done to give information about
     * failures.
     *
     * @param file
     * @return
     */
    public static XMLConfiguration getConfiguration(File file) {
        if (file == null) {
            throw new MyConfigurationException("Error: no configuration file");
        }
        if (!file.exists()) {
            throw new MyConfigurationException("Error: file \"" + file.getAbsolutePath() + "\" does not exist");
        }
        if (!file.isFile()) {
            throw new MyConfigurationException("Error: \"" + file.getAbsolutePath() + "\" is not a file");
        }
        AbstractConfiguration.setDefaultListDelimiter((char) 0);
        //return new XMLConfiguration(file);
        return resolveFileReferences(file);
    }

    /**
     * This will look for file references to include in the current XML configuration and load them all
     * into a single {@link XMLConfiguration}
     *
     * @param file
     * @return
     */
    public static XMLConfiguration resolveFileReferences(File file) {
        TreeSet<String> visitedFiles = new TreeSet<>();
        visitedFiles.add(file.getAbsolutePath());
        try {
            XMLConfiguration cfg = new XMLConfiguration(file);
            return resolveFileReferences(cfg, visitedFiles);
        } catch (ConfigurationException e) {
            throw new MyConfigurationException("Error: Could not create configuration from file \"" + file + "\"", e);
        }
    }

    /**
     * All the actual work for loading files is done here.
     * @param cfg
     * @param visitedFiles
     * @return
     * @throws ConfigurationException
     */
    protected static XMLConfiguration resolveFileReferences(XMLConfiguration cfg, TreeSet<String> visitedFiles) throws ConfigurationException {
        List list = cfg.getRoot().getChildren(FILE_TAG);
        for (int i = 0; i < list.size(); i++) {
            ConfigurationNode cn = (ConfigurationNode) list.get(i);
            List nameAttribs = cn.getAttributes(INCLUDE_TAG);
            for (int j = 0; j < nameAttribs.size(); j++) {
                ConfigurationNode attrcn = (ConfigurationNode) nameAttribs.get(j);
                String currentFile = (String) attrcn.getValue();
                if (!visitedFiles.contains(currentFile)) {
                    // hit a cycle, bail
                    visitedFiles.add(currentFile);
                    FileInputStream fis = null;
                    try {
                        File x = new File(currentFile);
                        if (x.isDirectory()) {
                            System.out.println("Configuration error: The file named \"" + currentFile + "\" is a directory. Skipping...");
                            continue;
                        }
                        if (!x.exists()) {
                            System.out.println("Configuration error: The file named \"" + currentFile + "\" does not exist. Skipping...");
                            continue;
                        }
                        if (x.canRead()) {
                            fis = new FileInputStream(x);
                            cfg.load(fis);
                            fis.close();
                            resolveFileReferences(cfg, visitedFiles);
                        } else {
                            System.out.println("Configuration error: The file named \"" + currentFile + "\" cannot be read (permission issue?). Skipping...");
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("Configuration error: Could not find the file named \"" + currentFile + "\". Skipping...");
                    } catch (IOException e) {
                        System.out.println("Configuration error: Some error processing \"" + currentFile +
                                "\" happened. Message read \"" + e.getMessage() + "\". Skipping...");
                    } finally {
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (IOException e) {
                                System.out.println("IOException. Could not close the file \"" + currentFile + "\". Skipping...");

                            }
                        }
                    }
                }
            }
        }
        return cfg;
    }

    /**
     * Gets the configuration via the resource name.
     *
     * @param resourceName
     * @return
     */
    public static XMLConfiguration getConfiguration(URL resourceName) {
        // disable automatic list processing (which splits every value at any comma) since this plays hob with
        // values.
        try {
            AbstractConfiguration.setDefaultListDelimiter((char) 0);
            return new XMLConfiguration(resourceName);
        } catch (ConfigurationException cx) {
            throw new MyConfigurationException("Error: could not get configuration for resource " + resourceName, cx);
        }
    }


    /**
     * Convenience method for getting the value of a single node, i.e. the contents, so
     * <br><br>
     * &lt;foo&gt;value&lt;/foo&gt;<br><br> would have name equal to 'foo' and return the
     * string 'value'.
     *
     * @param node
     * @param name
     * @return
     */
    public static String getNodeValue(ConfigurationNode node, String name) {
        if(node == null){
            return null;
        }
        ConfigurationNode node1 = getFirstNode(node, name);
        if (node1 == null) return null;
        return node1.getValue().toString();
    }

    /**
     * Return the first named child configuration node of the given node or null if there is
     * no such named child. Very useful if your specification only allows for a single child node.
     *
     * @param node
     * @param name
     * @return
     */
    public static ConfigurationNode getFirstNode(ConfigurationNode node, String name) {
        if(node==null) return null;
        List list = node.getChildren(name);
        if (list.isEmpty()) {
            return null;
        }
        return ((ConfigurationNode) list.get(0));
    }

    /**
     * Get the very first attribute with the given name from the configuration node.
     * A null is returned if there is no such value.
     *
     * @param node
     * @param name
     * @return
     */
    public static String getFirstAttribute(ConfigurationNode node, String name) {
        if(node == null){
            return null;
        }
        List list = node.getAttributes(name);
        if (list.isEmpty()) {
            return null;
        }
        ConfigurationNode cn = (ConfigurationNode) list.get(0);
        return cn.getValue().toString();

    }

    public static boolean getFirstBooleanValue(ConfigurationNode node, String attrib, boolean defaultValue) {
         if (node == null) return defaultValue;
         try {
             String x = getFirstAttribute(node, attrib);
             if (isTrivial(x)) {
                 return defaultValue;
             } //  Null argument returns false.
             return Boolean.parseBoolean(x);
         } catch (Throwable t) {

         }
         return defaultValue;
     }

    /**
     * Return the first long value of an attribute. Must have the default value specified. There is no
     * promise as to what "first value" means thanks to the XML spec., so effectively you should restrict
     * your XML to have at most one such value.
     * @param node
     * @param attrib
     * @param defaultValue
     * @return
     */
     public static long getFirstLongValue(ConfigurationNode node, String attrib, long defaultValue){
         if (node == null) return defaultValue;
         try {
             String x = getFirstAttribute(node, attrib);
             if (isTrivial(x)) {
                 return defaultValue;
             } //  Null argument returns false.
             return Long.parseLong(x);
         } catch (Throwable t) {

         }
         return defaultValue;
     }



    /**
     * Return the default value if none is found.
     *
     * @param node
     * @param name
     * @param defaultValue
     * @return
     */
    public static String getNodeValue(ConfigurationNode node, String name, String defaultValue) {
        if(node == null) return defaultValue;
        String x = getNodeValue(node, name);
        return x == null ? defaultValue : x;
    }

    /**
     * Loads the named configuration that resides under the topNodeName, e.g. topNodeName might be
     * 'config' and configName might be 'my-server'. This supports aliases too, so if the top level
     * tag contains an alias="x" attribute, then the configuration named "x" is loaded instead. This
     * allows for things like a "default" or "current" configuration which need never change once deployed.
     * The aim is to make this all as configuration driven as possible.
     *
     * @param cfg
     * @param topNodeName
     * @param configName
     * @return
     */
    public static ConfigurationNode getConfig(XMLConfiguration cfg, String topNodeName, String configName) {
        // start the recursion
        TreeSet<String> checkedAliases = new TreeSet<>();
        return getConfig(cfg, topNodeName, configName, checkedAliases);
    }

    /**
     *
     * @param cfg
     * @param topNodeName The tag inside the outermost tag you want, e.g. "service", "client"
     * @param configName The name (i.e. value of name attributes)
     * @param checkedAliases Any aliases encountered so far.
     * @return
     */
    protected static ConfigurationNode getConfig(XMLConfiguration cfg, String topNodeName, String configName, TreeSet<String> checkedAliases) {
        List list = cfg.configurationsAt(topNodeName);
        ConfigurationNode configurationNode = null;
        for (int i = 0; i < list.size(); i++) {
            SubnodeConfiguration cn = (SubnodeConfiguration) list.get(i);
            List nameAttribs = cn.getRootNode().getAttributes("name");
            for (int j = 0; j < nameAttribs.size(); j++) {
                ConfigurationNode attrcn = (ConfigurationNode) nameAttribs.get(j);
                if (attrcn.getValue().equals(configName)) {
                    configurationNode = cn.getRootNode();
                }
            }
            if (configurationNode != null) {
                String alias = getFirstAttribute(configurationNode, "alias");
                if (alias != null && 0 < alias.length()) {
                    if (checkedAliases.contains(alias)) {
                        throw new MyConfigurationException("Recursive error. The configuration \"" + configName +
                                "\" contains an alias \"" + alias + "\" which refers to itself on resolution. Configuration load is aborted.");
                    }
                    checkedAliases.add(alias);
                    return getConfig(cfg, topNodeName, alias, checkedAliases);
                }
                return configurationNode;
            }
        }
        throw new MyConfigurationException("Configuration \"" + configName + "\" not found");
    }
}
