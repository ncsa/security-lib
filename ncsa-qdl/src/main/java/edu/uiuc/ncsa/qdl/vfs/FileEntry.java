package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic entry for the virtual file system. Note that these are not designed to be mutable.
 * If there is a write, just replace this object with a new one.
 * <p>Created by Jeff Gaynor<br>
 * on 2/19/20 at  5:58 AM
 */
public class FileEntry implements VFSEntry {
    public static String CONTENT = "content";
    public static String CONTENT_TYPE = "content_type";
    public static String TEXT_TYPE = "text";
    public static String BINARY_TYPE = "binary";
    public static String INPUT_STREAM_TYPE = "input_stream";
    public static String TYPE = "file";
    XProperties xp = new XProperties();
    String text;
    List<String> lines = new ArrayList<>();

    public boolean isBinaryType() {
        return getType().equals(BINARY_TYPE);
    }

    public boolean hasContent() {
        return !lines.isEmpty();
    }

    public FileEntry(XProperties xp) {
        this.xp = xp;
    }

    public FileEntry(List<String> lines, XProperties xp) {
        this.xp = xp;
        this.lines = lines;
    }

    public FileEntry(InputStream inputStream, XProperties xp) {
        this.inputStream = inputStream;
        this.xp = xp;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public byte[] getContents() {
        if (getProperties().getString(CONTENT_TYPE).equals(TEXT_TYPE)) {
            return getText().getBytes();
        }
        return Base64.decodeBase64(getText());
    }


    /**
     * Filters lines. If a line matches the regex, then it is omitted.
     *
     * @param regexFilter
     * @return
     */
    @Override
    public String getText(String regexFilter) {
        String text = "";
        for (String line : lines) {
            if (!line.matches(regexFilter)) {
                text = text + line + "\n";
            }
        }
        return text;
    }

    @Override
    public String getText() {
        if (text == null) {
            text = "";
            for (String line : lines) {
                text = text + line + "\n";
            }
        }
        return text;
    }

    /**
     * These properties are for external systems that must manage when or how the scripts are run.
     * For instance, if there is a version of the script. QDL does not care what version the author
     * has of this, but it must be preserved. These are set and managed externally -- QDL itself never
     * touches these or cares about them.
     *
     * @return
     */

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

    @Override
    public void setProperty(String key, Object value) {
        getProperties().put(key, value);
    }

    @Override
    public Object getProperty(String key) {
        return getProperties().get(key);
    }

    @Override
    public StemVariable convertToStem() {
        StemVariable out = new StemVariable();
        int i = 0;
        //Read from the stream
        for (String content : getLines()) {
            out.put(Integer.toString(i++), content);
        }
        return out;
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    String path;

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    InputStream inputStream = null;

    /**
     * In the case of actual files, the reader will be set to a {@link Reader}.
     * In other cases (such as from a zip file or collection of lines
     * in a memory VFS) about the best we can do is
     * return a reader for iterating. Note that you should get this once
     * and use it until you close it. This is because there are no well
     * defined ways in Java to check if a generic {@link InputStream} has
     * been closed without trying something (like reading some bytes) and
     * seeing if it throws an exception. Therefore, this will take the backing
     * set of lines and return a new byte array output stream every time.
     *
     * @return
     */
    @Override
    public InputStream getInputStream() {

        if (inputStream == null) {
            if (getText() == null || getText().isEmpty()) {
                return new ByteArrayInputStream(new byte[]{});
            } else {
                return new ByteArrayInputStream(getText().getBytes(StandardCharsets.UTF_8));
            }
        }

        return inputStream;
    }
}
