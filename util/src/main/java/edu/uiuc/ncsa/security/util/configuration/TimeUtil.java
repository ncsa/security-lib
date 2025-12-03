package edu.uiuc.ncsa.security.util.configuration;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * tatic utilities for working with XML configurations.S This also handles times!
 * <p>Created by Jeff Gaynor<br>
 * on 3/23/12 at  8:23 AM
 */
public class TimeUtil implements TimeConstants {



/*
    protected static MultiConfigurations getConfigurations2(String fileName, String cfgTagName) throws ConfigurationException {
        XMLConfiguration xmlConfiguration = Configurations.getConfiguration(new File(fileName));
        MultiConfigurations configurations2 = new MultiConfigurations();
        configurations2.ingestConfig(xmlConfiguration, cfgTagName);
        return configurations2;
    }*/

    /**
     * For getting times in configuration files. The acceptable entries are
     * <pre>
     * x + sec for seconds
     * x + s for seconds
     * x + ms for milliseconds
     * x (no units) for milliseconds
     * </pre>
     * Using this assumes that the default is milliseconds for the field. If you need
     * to set the default for the field as seconds, use {@link TimeUtil#getValueSecsOrMillis(String, boolean)}.
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
                return SECONDS;
            case UNITS_MINUTES:
            case UNITS_MINUTES_LONG2:
                return ONE_MINUTES;
            case UNITS_HOURS:
            case UNITS_HOURS_LONG:
                return ONE_HOUR;
            case UNITS_DAYS:
            case UNITS_DAYS_LONG:
                return ONE_DAY;
            case UNITS__WEEK:
            case UNITS_WEEK_LONG:
                return ONE_WEEK;
            case UNITS_MONTHS:
            case UNITS_MONTHS_LONG:
                return ONE_MONTH;
            case UNITS_YEARS:
            case UNITS_YEARS_LONG:
                return ONE_YEAR;
            default:
                throw new IllegalArgumentException("unknown unit \"" + units + "\"");

        }
     }

    /**
     * Quick double check of this class. Run it and peruse the output.
     * @param args
     */
    public static void main(String[] args) throws Throwable{
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
