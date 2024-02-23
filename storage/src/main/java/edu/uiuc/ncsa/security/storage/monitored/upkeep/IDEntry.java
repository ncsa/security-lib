package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.monitored.Monitored;

/**
 * A single entry for checking an ID. It may be a regex
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  9:28 AM
 */
public class IDEntry implements RuleEntry{
    public IDEntry(boolean regex, String value) {
        this.regex = regex;
        this.value = value;
    }

    @Override
    public boolean applies(Monitored monitored){
     return      applies(monitored.getIdentifier(),
                   monitored.getCreationTS().getTime(),
                   monitored.getLastAccessed()==null?null:monitored.getLastAccessed().getTime(),
                   monitored.getLastModifiedTS().getTime());
    }

    @Override
    public boolean applies(Identifier id, Long created, Long accessed, Long modified) {
        if(isRegex()){
                return id.toString().matches(getValue());
        }
        return id.equals(getValue());
    }
    boolean regex = false;

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String value;

    @Override
    public String toString() {
        return "IDEntry{" +
                "regex=" + regex +
                ", value='" + value + '\'' +
                '}';
    }

    public boolean isNegation() {
        return negation;
    }

    public void setNegation(boolean negation) {
        this.negation = negation;
    }

    boolean negation = false;
}
