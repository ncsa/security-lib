package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.monitored.Monitored;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  9:23 AM
 */
public class RuleList extends ArrayList<RuleEntry> implements UpkeepConstants{
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public Boolean isSkipVersions() {
        return skipVersions;
    }

    public List<String> getExtendsList() {
        return extendsList;
    }

    public void setExtendsList(List<String> extendsList) {
        this.extendsList = extendsList;
    }

    List<String> extendsList = new ArrayList<>();
    public void setSkipVersions(Boolean skipVersions) {
        this.skipVersions = skipVersions;
    }

    public boolean hasSkipVersion(){
        return skipVersions != null;
    }
    Boolean skipVersions = null;
    boolean verbose = false;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        switch (action){
            case ACTION_ARCHIVE:
            case ACTION_DELETE:
            case ACTION_RETAIN:
            case ACTION_TEST:
                this.action = action;
                return;
        }
        throw new IllegalArgumentException("unknown action \"" + action + "\"");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RuleList{" +
                "enabled=" + enabled +
                ", action='" + action + '\'' +
                ", name='" + name + '\'' +
                ", values=" + super.toString() + 
                '}';
    }

    boolean enabled;
   String action;
   String name;

   public boolean applies(Monitored monitored){
    return      applies(monitored.getIdentifier(),
                  monitored.getCreationTS().getTime(),
                  monitored.getLastAccessed()==null?null:monitored.getLastAccessed().getTime(),
                  monitored.getLastModifiedTS().getTime());
   }
   public boolean applies(Identifier id, Long create, Long accessed, Long modified){
       // Implicit is that every rule entry has to apply, i.e., this is the logical AND of all entries.
       for(RuleEntry ruleEntry : this){
           if(!ruleEntry.applies(id, create, accessed, modified)){
               return false;
           }
       }
       return true;
   }
}
