package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkBasic;

/**
 * Simple implementation of the {@link Identifiable} interface.
 * <p>Created by Jeff Gaynor<br>
 * on 4/5/12 at  10:12 AM
 */
public class IdentifiableImpl implements Identifiable, Cloneable {
    @Override
    public IdentifiableImpl clone() {
        return new IdentifiableImpl(getIdentifier());
    }

    public IdentifiableImpl(Identifier identifier) {
        this.identifier = identifier;
    }

    Identifier identifier;

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getIdentifierString() {
        if (identifier == null) return null;
        return identifier.toString();
    }

    @Override
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (!checkBasic(this, obj)) return false;
        Identifiable x = (Identifiable) obj;

        boolean rc = false;
        if (identifier == null) {
            if (x.getIdentifier() == null) {
                rc = true;
            }
        } else {
            rc = identifier.equals(x.getIdentifier());
        }
        if(isReadOnly() != x.isReadOnly()) return false;
        return rc;
    }

    boolean readOnly = false; //default
    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
