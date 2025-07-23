package edu.uiuc.ncsa.security.core.state;

import java.io.Serializable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/26/21 at  7:44 AM
 */
public class SKey implements Serializable {
    public SKey() {
    }

    public SKey(String key) {
        setKey(key);
    }

    public String getKey() {
        return key;
    }

    protected void setKey(String newKey) {
        key = newKey;
        hashCode = key.hashCode();
        hashCodeInit = true;
    }

    protected String key;
    int hashCode;
    boolean hashCodeInit = false;

    @Override
    public int hashCode() {
        if (!hashCodeInit) {
            hashCode = getKey().hashCode();
            hashCodeInit = true;
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SKey)) {
            return false;
        }
        return key.equals(((SKey) obj).getKey());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
                "key='" + key + '\'' +
                ']';
    }
}
