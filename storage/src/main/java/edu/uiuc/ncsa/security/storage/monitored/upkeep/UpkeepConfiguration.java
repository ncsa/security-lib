package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.monitored.Monitored;

import java.time.LocalTime;
import java.util.*;

import static edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConstants.ACTION_NONE;

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

    public Boolean isSkipVersions() {
        return skipVersions;
    }

    public void setSkipVersions(Boolean skipVersions) {
        this.skipVersions = skipVersions;
    }

    Boolean skipVersions = null;

    public boolean hasSkipVersion(){
        return skipVersions != null;
    }

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

    boolean verbose = false;

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    int runCount = -1;


    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }


    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean hasOutput(){
        return output!=null && !output.isEmpty();
    }
    String output = null;

    public Map<String, RuleList> getRulesMap() {
        return rulesMap;
    }

    public void setRulesMap(Map<String, RuleList> rulesMap) {
        this.rulesMap = rulesMap;
    }

    Map<String, RuleList> rulesMap = new HashMap<>();

    public List<RuleList> getRuleList() {
        return ruleList;
    }

    List<RuleList> ruleList = new ArrayList<>();

    /**
     * Add to both the list of rules and the internal map so it may be retrieved by name.
     * @param ruleList
     */
    public void add(RuleList ruleList) {
        rulesMap.put(ruleList.getName(), ruleList);
        this.ruleList.add(ruleList);
    }

    @Override
    public String toString() {
        return "UpkeepConfiguration{" +
                "\nenabled=" + enabled +
                ", \ndebug=" + debug +
                ", \nskipVersions=" + skipVersions +
                ", \ntestOnly=" + testOnly +
                ", \nalarms=" + alarms +
                ", \ninterval=" + interval +
                ", \nverbose=" + verbose +
                ", \nrunCount=" + runCount +
                ", \noutput='" + output + '\'' +
                ", \nrulesMap=" + rulesMap +
                ", \nruleList=" + ruleList +
                '}';
    }

    public String[] applies(Identifier id, Long create, Long accessed, Long modified){
    // Do retain actions first!
        for(RuleList rr : ruleList ){
            if(rr.applies(id, create, accessed, modified)){
                return new String[]{rr.getAction(),rr.getName()};
            }
        }
        return new String[]{ACTION_NONE, ""}; // does not apply
    }
    public String[] applies(Monitored monitored){
        return applies(monitored.getIdentifier(), monitored.getCreationTS().getTime(), monitored.getLastAccessed()==null?null:monitored.getLastAccessed().getTime(),
                monitored.getLastModifiedTS()==null?null:monitored.getLastModifiedTS().getTime());
    }
}
