package edu.uiuc.ncsa.security.util.configuration;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.MultiConfigurations;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.inheritance.InheritanceList;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.util.List;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Static utilities for working with configurations.
 * <p>Created by Jeff Gaynor<br>
 * on 3/23/12 at  8:23 AM
 */
public class XMLConfigUtil {
    /**
     * Finds the configuration in an XML file, given the filename, name of the configuration
     * (or null if you want to use the only one there) and the top node tag
     * (E.g. "client" or "server").  This only permits single inheritance.
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
     * Entry point for multiple-inheritance.
     * @param fileName
     * @param cfgName
     * @param cfgTagName
     * @return
     * @throws ConfigurationException
     */
    public static ConfigurationNode findMultiNode(String fileName, String cfgName, String cfgTagName) throws ConfigurationException {
        MultiConfigurations configurations2 = getConfigurations2(fileName, cfgTagName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        List<ConfigurationNode> nodes = configurations2.getNamedConfig(cfgName);
        if (nodes.isEmpty()) {
            return null;
        }
        if (1 < nodes.size()) {
            throw new ConfigurationException("Multiple configurations named " + cfgName + " found in file " + fileName);
        }
        return nodes.get(0); // There is one exactly. Return it.
    }

    protected static MultiConfigurations getConfigurations2(String fileName, String cfgTagName) throws ConfigurationException {
        XMLConfiguration xmlConfiguration = Configurations.getConfiguration(new File(fileName));
        MultiConfigurations configurations2 = new MultiConfigurations();
        configurations2.ingestConfig(xmlConfiguration, cfgTagName);
        return configurations2;
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
    public static final String UNITS_MINUTES = "min";
    public static final String UNITS_MINUTES_LONG = "mins";
    public static final String UNITS_HOURS = "hr";
    public static final String UNITS_HOURS_LONG = "hrs";
    public static final String UNITS_DAYS = "day";
    public static final String UNITS_DAYS_LONG = "days";
    public static final String UNITS_MILLISECONDS = "ms";

    /**
     * For getting times in configuration files. The acceptable entries are
     * <pre>
     * x + sec for seconds
     * x + s for seconds
     * x + ms for milliseconds
     * x (no units) for milliseconds
     * </pre>
     * Using this assumes that the default is milliseconds for the field. If you need
     * to set the default for the field as seconds, use {@link XMLConfigUtil#getValueSecsOrMillis(String, boolean)}.
     *
     * @param x
     * @return
     */
    public static Long getValueSecsOrMillis(String x) {
        return getValueSecsOrMillis(x, false);
    }

    /**
     * @param x         un-parsed number
     * @param isSeconds no units means it is seconds (true) or milliseconds (false)
     * @return
     */
    public static Long getValueSecsOrMillis(String x, boolean isSeconds) {
        if (isTrivial(x)) {
            return null;
        }
        // CIL-1629, allow for other time units
        TimeThingy timeThingy = createTimeThingy(x, isSeconds);

        long rawValue = Long.parseLong(timeThingy.rawValue.trim()); // blanks make it blow up, FYI...
        return rawValue * timeThingy.unitMultiplier;

    }

    public static class TimeThingy {
        long unitMultiplier;
        String rawValue;

        public TimeThingy(long unitMultiplier, String rawValue) {
            this.unitMultiplier = unitMultiplier;
            this.rawValue = rawValue;
        }
    }

    protected static TimeThingy createTimeThingy(String x, boolean isSeconds) {
        x = x.trim();
        if (x.endsWith(".")) {
            // allows for "100 sec." or "100000 ms."
            x = x.substring(0, x.length() - 1);
        }
        // do in order of length of units or collisions happen.
        if (x.endsWith(UNITS_MILLISECONDS)) {
            return new TimeThingy(1L, x.substring(0, x.length() - UNITS_MILLISECONDS.length()));
        }
        if (x.endsWith(UNITS_SECONDS_LONG)) {
            return new TimeThingy(1000L, x.substring(0, x.length() - UNITS_SECONDS_LONG.length()));
        }
        if (x.endsWith(UNITS_MINUTES)) {
            return new TimeThingy(1000L * 60L, x.substring(0, x.length() - UNITS_MINUTES.length()));
        }
        if (x.endsWith(UNITS_MINUTES_LONG)) {
            return new TimeThingy(1000L * 60L, x.substring(0, x.length() - UNITS_MINUTES_LONG.length()));
        }
        if (x.endsWith(UNITS_HOURS)) {
            return new TimeThingy(1000L * 3600L, x.substring(0, x.length() - UNITS_HOURS.length()));
        }
        if (x.endsWith(UNITS_HOURS_LONG)) {
            return new TimeThingy(1000L * 3600L, x.substring(0, x.length() - UNITS_HOURS_LONG.length()));
        }
        if (x.endsWith(UNITS_DAYS)) {
            return new TimeThingy(1000L * 3600L * 24, x.substring(0, x.length() - UNITS_DAYS.length()));
        }
        if (x.endsWith(UNITS_DAYS_LONG)) {
            return new TimeThingy(1000L * 3600L * 24, x.substring(0, x.length() - UNITS_DAYS_LONG.length()));
        }
        // do seconds last since plural times (e.g. hrs) get flagged with this test and everything
        // is interpeted as seconds otherwise.
        if (x.endsWith(UNITS_SECONDS)) {
            return new TimeThingy(1000L, x.substring(0, x.length() - UNITS_SECONDS.length()));
        }
        return new TimeThingy(isSeconds ? 1000L : 1L, x); // nothing to do, it's already to go, just need to know if default is seconds.

    }

    public static void main(String[] args) {
        String[] values = new String[]{"1000", "300 sec", "1000 ms", "2 hrs", "1hr", "1day", "2 days", "20 min"};
        for (String value : values) {
            System.out.println(value + "= " + getValueSecsOrMillis(value) + " in ms.");
        }
    }

/*    protected ConfigurationNode getNode(String filename, String configName, String componentName) {
                        return getNodeOLD(filename, configName, componentName);
 //        return getNodeNEW(filename, configName, componentName);
     }

     protected ConfigurationNode getNodeOLD(String filename, String configName, String componentName) {
         return ConfigUtil.findConfiguration(filename, configName, componentName);
     }*/

 /*
     protected ConfigurationNode getNodeNEW(String filename, String configName, String componentName) {
         XMLConfiguration xmlConfiguration = Configurations.getConfiguration(new File(filename));
         MultiConfigurations configurations2 = new MultiConfigurations();
         configurations2.ingestConfig(xmlConfiguration, componentName);
         //Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
         List<ConfigurationNode> nodes = configurations2.getNamedConfig(configName);
         // This is the list, in inhiertance order of the nodes. You must resolve this with
         // configurations2.getFirstAttribute
         // or
         //configurations2.getFirstNode
         // calls
         // configurations2.getFirstAttribute(nodes, "myattrib").equals("attribute X");
         //
         // At this point, single inheritance is used so just return that

         if (1 < nodes.size()) {
             throw new MyConfigurationException("Ambiguous configuration. Too many nodes with are name " + configName);
         }
         return nodes.get(0);
     }
 */
}
