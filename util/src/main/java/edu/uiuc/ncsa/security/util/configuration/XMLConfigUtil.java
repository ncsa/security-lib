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
     *
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

    public static final String UNITS_DAYS = "d";
    public static final String UNITS_DAYS_LONG = "day";
    public static final String UNITS_HOURS = "hr";
    public static final String UNITS_HOURS_LONG = "hour";
    public static final String UNITS_MILLISECONDS = "ms";
    public static final String UNITS_MILLISECOND_LONG = "millisecond";
    public static final String UNITS_MINUTES = "min";
    public static final String UNITS_MINUTES_LONG = "min";
    public static final String UNITS_MINUTES_LONG2 = "minute";
    public static final String UNITS_MONTHS = "mo";
    public static final String UNITS_MONTHS_LONG = "month";
    public static final String UNITS_SECONDS = "s";
    public static final String UNITS_SECONDS_LONG = "sec";
    public static final String UNITS_SECONDS_LONG2 = "second";
    public static final String UNITS_WEEK_LONG = "week";
    public static final String UNITS_YEARS = "yr";
    public static final String UNITS_YEARS_LONG = "year";
    public static final String UNITS__WEEK = "wk";

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
        try {
            long rawValue = Long.parseLong(timeThingy.rawValue.trim()); // blanks make it blow up, FYI...
            return rawValue * timeThingy.unitMultiplier;
        }catch(NumberFormatException nfx){
            throw new IllegalArgumentException("could not parse integer \"" + timeThingy.rawValue + "\"");
        }
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
        return NEWcreateTimeThingy(x, isSeconds);
    }

    // Fixes https://github.com/ncsa/security-lib/issues/33
    protected static TimeThingy NEWcreateTimeThingy(String s, boolean isSeconds) {
        String[] x = splitNumber(s, isSeconds);
        return new TimeThingy(getMultiplier(x[1]), x[0]);
    }

    /**
     * Splits a time into number and unit, normalizing.
     * @param x
     * @return
     */
     protected static String[] splitNumber(String x, boolean isSeconds){
        String y = x.toLowerCase().trim();
         String[] units = y.split("[0-9]");
         String[] number = y.split("\\s*[a-zA-Z]+\\s*");
         String u;
         if(units.length == 0){
             // no units
             u = isSeconds?UNITS_SECONDS_LONG2:UNITS_MILLISECOND_LONG;
         }else{
             u = units[units.length-1].trim();
         }
         if (u.endsWith(".")) {
                 // allows for "100 sec." or "100000 ms."
                 u = u.substring(0, u.length() - 1);
             }
         // special cases are s for second or ms for millisecond. This allows
         // for plurals like days or weeks to be processed
         if(u.equals("s")){
             u = UNITS_SECONDS_LONG2;
         }else{
             if(u.equals(UNITS_MILLISECONDS)){
                 u = UNITS_MILLISECOND_LONG;
             }else{
                 if(u.endsWith("s")){
                     u = u.substring(0,u.length()-1); // shave odd plural marker
                 }
             }
         }
         return new String[]{number[0], u};
     }

    /**
     * Gets the correct multiplier for the units.
     * @param units
     * @return
     */
     protected static long getMultiplier(String units){
        // Note that the Java compiler actually computes a hash of the string and uses that
         // integer for the
         // cases, so that in practice, this is  extremely fast.
        switch (units){
            case UNITS_MILLISECONDS:
            case UNITS_MILLISECOND_LONG:
                return UNITS_MILLISECOND_MULTIPLIER;
            case UNITS_SECONDS:
            case UNITS_SECONDS_LONG:
            case UNITS_SECONDS_LONG2:
                return UNITS_SECOND_MULTIPLIER;
            case UNITS_MINUTES:
            case UNITS_MINUTES_LONG2:
                return UNITS_MINUTE_MULTIPLIER;
            case UNITS_HOURS:
            case UNITS_HOURS_LONG:
                return UNITS_HOUR_MULTIPLIER;
            case UNITS_DAYS:
            case UNITS_DAYS_LONG:
                return UNITS_DAY_MULTIPLIER;
            case UNITS__WEEK:
            case UNITS_WEEK_LONG:
                return UNITS_WEEK_MULTIPLIER;
            case UNITS_MONTHS:
            case UNITS_MONTHS_LONG:
                return UNITS_MONTH_MULTIPLIER;
            case UNITS_YEARS:
            case UNITS_YEARS_LONG:
                return UNITS_YEAR_MULTIPLIER;
            default:
                throw new IllegalArgumentException("unknown unit \"" + units + "\"");

        }
     }
    public static final long UNITS_MILLISECOND_MULTIPLIER = 1L;
    public static final long UNITS_SECOND_MULTIPLIER = 1000L;
    public static final long UNITS_MINUTE_MULTIPLIER = UNITS_SECOND_MULTIPLIER * 60;
    public static final long UNITS_HOUR_MULTIPLIER = UNITS_MINUTE_MULTIPLIER * 60;
    public static final long UNITS_DAY_MULTIPLIER = UNITS_HOUR_MULTIPLIER * 24;
    public static final long UNITS_WEEK_MULTIPLIER = UNITS_DAY_MULTIPLIER * 7;
    public static final long UNITS_MONTH_MULTIPLIER = UNITS_DAY_MULTIPLIER * 30;
    public static final long UNITS_YEAR_MULTIPLIER = UNITS_DAY_MULTIPLIER * 365 + UNITS_HOUR_MULTIPLIER * 6; // 365¼ days

    // Old way. Clunky direct parsing that just continued to grow. New way just uses regexs and is much better.
    // Does not support weeks months or years
 /*   protected static TimeThingy OLDcreateTimeThingy(String x, boolean isSeconds) {
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

    }*/

    /**
     * Quick double check of this class. Run it and peruse the output.
     * @param args
     */
    public static void main(String[] args) {
        String[] values = new String[]{
                "1000",
                "300 sec",
                "300 sEc.",
                "1000 ms",
                "2 hrs",
                "1hr",
                "1day",
                "2 days",
                "20 min",
                "1    yr",
                "2 weeks"};
        for (String value : values) {
            System.out.println(value + "= " + getValueSecsOrMillis(value) + " in ms.");
        }
        String[] bad = new String[]{
                "2.5 days",   // no decimals
                "2 5 days",   // no blanks
                "1 2 3",     //no blanks in numbers
                "1000 se c", // no blanks in units
                "1000 se.c", // no misplaced periods
                "5 woof"};   // wholly wrong units
        for (String value : bad) {
            try{
                System.out.println(value + "= " + getValueSecsOrMillis(value) + " in ms.");
            }catch(Throwable t){
                System.out.println("error:" + t.getMessage());
            }
        }
    }


}
