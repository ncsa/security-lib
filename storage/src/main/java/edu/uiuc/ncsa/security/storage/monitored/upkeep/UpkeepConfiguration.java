package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifier;

import java.time.LocalTime;
import java.util.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/24 at  1:21 PM
 */
public class UpkeepConfiguration {
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    boolean enabled;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    boolean debug;

    public boolean isTestOnly() {
        return testOnly;
    }

    public void setTestOnly(boolean testOnly) {
        this.testOnly = testOnly;
    }

    boolean testOnly = false;

    public boolean hasAlarms() {
        return alarms != null;
    }

    public Collection<LocalTime> getAlarms() {
        return alarms;
    }

    public void setAlarms(Collection<LocalTime> alarms) {
        this.alarms = alarms;
    }

    public Collection<LocalTime> alarms = null;

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean hasInterval() {
        return interval != null;
    }

    Long interval = null;



    Map<String, RuleList> rules = new HashMap<>();

    public List<RuleList> getRuleList() {
        return ruleList;
    }

    List<RuleList> ruleList = new ArrayList<>();

    /**
     * Add to both the list of rules and the internal map so it may be retrieved by name.
     * @param ruleList
     */
    public void add(RuleList ruleList) {
        rules.put(ruleList.getName(), ruleList);
        this.ruleList.add(ruleList);
    }

    @Override
    public String toString() {
        return "UpkeepConfiguration{" +
                "enabled=" + enabled +
                ",\n debug=" + debug +
                ",\n testOnly=" + testOnly +
                ",\n alarms=" + alarms +
                ",\n interval=" + interval +
                ",\n rules=" + rules +
                ",\n ruleList=" + ruleList +
                '}';
    }

    public boolean applies(Identifier id, Long create, Long accessed, Long modified){
        for(RuleList rr : ruleList ){
            // Logical OR of the rules.
            if(rr.applies(id, create, accessed, modified)){
                return true;
            }
        }
        return false;
    }
}
