package edu.uiuc.ncsa.security.core.inheritance;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/21 at  6:54 AM
 */
public class AliasAndOverrides {
    public AliasAndOverrides(String alias, List<String> overrides) {
        this.alias = alias;
        this.overrides = overrides;
    }

    public void addParents(List<String> parents) {
        this.overrides.addAll(parents);
    }

    String alias;
    List<String> overrides; // picks up intermediate inheritance on parsing.

    @Override
    public String toString() {
        return
                alias + ':' + overrides;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<String> overrides) {
        this.overrides = overrides;
    }
}