package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/19/20 at  5:58 AM
 */
public interface VFSEntry extends Serializable {
    String SHEBANG_REGEX = "^#!.*";
    public String getText(String regexFilter);
    public String getText();
    public List<String> getLines();
    public XProperties getProperties();
    public void setProperties(XProperties xp);
    public void setProperty(String key, Object value);
    public Object getProperty(String key);
    byte[] getContents();
    String getType();
    boolean isBinaryType();
    StemVariable convertToStem();
    String getPath();
    void setPath(String newPath);
}
