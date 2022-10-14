package edu.uiuc.ncsa.security.core;

import java.io.Serializable;

/**
 * An interface for things that may be identified uniquely.
 * <p>Created by Jeff Gaynor<br>
 * on May 24, 2011 at  3:43:41 PM
 */
public interface Identifiable extends Serializable, Cloneable {

    public Identifiable clone();

    /**
     * Get the identifer
     *
     * @return
     */
    Identifier getIdentifier();

    /**
     * Convenience call to cast the identifier to a string.
     *
     * @return
     */
    String getIdentifierString();

    /**
     * Set the identifier.
     *
     * @param identifier
     */
    void setIdentifier(Identifier identifier);

    boolean isReadOnly();

    void setReadOnly(boolean readOnly);
   String getDescription();
   void setDescription(String description);

}
