package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.state.SKey;
import edu.uiuc.ncsa.security.core.state.SThing;

public class EnvEntry implements SThing {
    public EnvEntry(SKey key) {
        this.key = key;
    }

    public EnvEntry(SKey key, String value) {
        this.key = key;
        this.value = value;
    }

    public EnvEntry(String key, String value) {
        this.key = new SKey(key);
        this.value = value;
    }

    SKey key;

    @Override
    public String getName() {
        return key.getKey();
    }

    @Override
    public SKey getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    String value;
}
