package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.security.core.configuration.XProperties;

import java.io.Serializable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/19/20 at  5:58 AM
 */
public interface LibraryEntry extends Serializable {
    public String getText();
    public XProperties getProperties();
    public void setProperties(XProperties xp);
    byte[] getContents();
    String getType();
}
