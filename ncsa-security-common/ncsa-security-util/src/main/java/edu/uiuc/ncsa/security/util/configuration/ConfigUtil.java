package edu.uiuc.ncsa.security.util.configuration;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Static utilities for working with configurations.
 * <p>Created by Jeff Gaynor<br>
 * on 3/23/12 at  8:23 AM
 */
public class ConfigUtil {
    /**
     * Finds the configuration in an XML file, given the filename, name of the configuration
     * (or null if you want to use the only one there) and the top node tag
     * (E.g. "client" or "server").
     *
     * @param fileName
     * @param configName
     * @param topNodeTag
     * @return
     */
    public static ConfigurationNode findConfiguration(String fileName,
                                                      String configName,
                                                      String topNodeTag) {
        if (fileName == null || fileName.length() == 0) {
            throw new MyConfigurationException("Error: No configuration file specified");
        }
        XMLConfiguration cfg = null;
        // A properties file is supplied. Use that.
        File file = new File(fileName);
        if (!(file.exists() && file.isFile())) {
            throw new MyConfigurationException("Error: file \"" + file + "\" does not exist");
        }
        cfg = Configurations.getConfiguration(new File(fileName));
        return findNamedConfig(cfg, configName, topNodeTag);

    }


    /**
     * This takes a configuration and looks in it for a given name for the tag. Called by {@link #findConfiguration(String, String, String)}.
     *
     * @param cfg
     * @param cfgName
     * @param topNodeTag
     * @return
     */
    protected static ConfigurationNode findNamedConfig(XMLConfiguration cfg, String cfgName, String topNodeTag) {
        ConfigurationNode cn = null;
        if (cfgName == null) {
            cn = cfg.configurationAt(topNodeTag).getRootNode();
        } else {
            cn = Configurations.getConfig(cfg, topNodeTag, cfgName);
        }

        return cn;
    }

    public static final String UNITS_SECONDS = "s";
    public static final String UNITS_SECONDS_LONG = "sec";
    public static final String UNITS_MILLISECONDS = "ms";

    /**
     * For getting times in configuration files. The acceptable entries are
     * <pre>
     * x + sec for seconds
     * x + s for seconds
     * x + ms for milliseconds
     * x (no units) for milliseconds
     * </pre>
     * Using this assumes that the default is milliseconds for the field.
     * @param x
     * @return
     */
    public static Long getValueSecsOrMillis(String x) {
            return getValueSecsOrMillis(x, false);
    }

    /**
     *
     * @param x un-parsed number
     * @param isSeconds no units means it is seconds (true) or milliseconds (false)
     * @return
     */
    public static Long getValueSecsOrMillis(String x, boolean isSeconds) {
        if (isTrivial(x)) {
            return null;
        }
        x = x.trim();
        if(x.endsWith(".")){
            // allows for "100 sec." or "100000 ms."
            x = x.substring(0, x.length() - 1);
        }
        // do in order of length of units or collisions happen.
        if(x.endsWith(UNITS_SECONDS_LONG)){
            x = x.substring(0,x.length() - UNITS_SECONDS_LONG.length());
        }
        if (x.endsWith(UNITS_MILLISECONDS)) {
            x = x.substring(0, x.length() - UNITS_MILLISECONDS.length());
        }
        if (x.endsWith(UNITS_SECONDS)) {
            x = x.substring(0, x.length() - UNITS_SECONDS.length());
            isSeconds = true;
        }
        long rawValue = Long.parseLong(x.trim()); // blanks make it blow up, FYI...
        if (isSeconds) {
            return rawValue * 1000;
        }
        return rawValue;

    }

}
