package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import org.apache.commons.codec.binary.Base64;

/**
 * Entry in a script library that si not a script. This is
 * <p>Created by Jeff Gaynor<br>
 * on 2/19/20 at  5:58 AM
 */
public class FileEntry implements LibraryEntry {
    public static String CONTENT = "content";
    public static String CONTENT_TYPE = "content_type";
    public static String TEXT_TYPE = "text";
    public static String BINARY_TYPE = "binary";
    public static String TYPE = "file";
    XProperties xp = new XProperties();
    String text;

    public FileEntry(XProperties xp, String text) {
        this.xp = xp;
        this.text = text;
    }

    @Override
    public byte[] getContents() {
        if (getProperties().getString(CONTENT_TYPE).equals(TEXT_TYPE)) {
            return text.getBytes();
        }
        return Base64.decodeBase64(getText());
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public XProperties getProperties() {
        return xp;
    }

    @Override
    public void setProperties(XProperties xp) {
        this.xp = xp;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
