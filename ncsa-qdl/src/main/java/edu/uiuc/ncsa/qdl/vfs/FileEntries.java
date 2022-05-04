package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.util.QDLFileUtil;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;

import static edu.uiuc.ncsa.qdl.evaluate.AbstractFunctionEvaluator.*;
import static edu.uiuc.ncsa.qdl.vfs.FileEntry.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/19/20 at  6:19 AM
 */
public class FileEntries {
    public static String FILES_TAG = "files";

    public static JSONObject toJSON(FileEntry fileEntry) throws Throwable {
        JSONObject json = new JSONObject();
        JSONObject props = new JSONObject();
        props.putAll(fileEntry.getProperties());
        JSONArray array = new JSONArray();
        array.addAll(fileEntry.getLines());
        props.put(CONTENT, array);
        json.put(TYPE, props);
        return json;
    }

    public static FileEntry toEntry(ZipEntry zipEntry, byte[] content, int type) throws Throwable {
        XProperties xp = new XProperties();
        Date ts = new Date();
        if (zipEntry.getLastModifiedTime() != null) {
            ts.setTime(zipEntry.getLastModifiedTime().toMillis());
        }
        xp.put(FileEntryConstants.LAST_MODIFIED, Iso8601.date2String(ts));

        ts = new Date();
        if (zipEntry.getCreationTime() != null) {
            ts.setTime(zipEntry.getCreationTime().toMillis());
        }
        xp.put(FileEntryConstants.CREATE_TIME, Iso8601.date2String(ts));

        xp.put(FileEntryConstants.ID, zipEntry.getName());
        File f = new File(zipEntry.getName());
        List<String> contents;
        int length = 0;
        if(type == FILE_OP_READER){
            xp.put(CONTENT_TYPE, READER_TYPE);
            StringReader stringReader = new StringReader(new String(content));
            xp.put(FileEntryConstants.LENGTH, content.length);
            return new FileEntry(stringReader, xp);
        }
        if (isBinary(f, type)) {
            xp.put(CONTENT_TYPE, BINARY_TYPE);
            contents = new ArrayList<>();
            contents.add(Base64.encodeBase64String(content));  // one string
            length = contents.get(0).length();
        } else {
            xp.put(CONTENT_TYPE, TEXT_TYPE);
            String c = new String(content);
            contents = new ArrayList<>();

            for (String x : c.split("\n")) {
                contents.add(x);
                length = length + x.length();
            }
        }
        xp.put(FileEntryConstants.LENGTH, length);

        return new FileEntry(contents, xp);
    }

    public static FileEntry toEntryWithReader(String id, File f) throws Throwable {
        XProperties xp = getXProperties(id, f);
        return new FileEntry(new FileReader(f), xp);
    }

    public static JSONObject toJSON(String id, File f, int type) throws Throwable {
        XProperties xp = getXProperties(id, f);
        List<String> contents;

        if (isBinary(f, type)) {
            xp.put(CONTENT_TYPE, BINARY_TYPE);
            contents = new ArrayList<>();
            contents.add(QDLFileUtil.readFileAsBinary(f.getAbsolutePath()));  // one string
        } else {
            xp.put(CONTENT_TYPE, TEXT_TYPE);
            contents = QDLFileUtil.readFileAsLines(f.getAbsolutePath());
        }
        FileEntry fileEntry = new FileEntry(contents, xp);
        return toJSON(fileEntry);

    }

    private static XProperties getXProperties(String id, File f) {
        XProperties xp = new XProperties();
        Date ts = new Date();
        ts.setTime(f.lastModified());
        xp.put(FileEntryConstants.CREATE_TIME, Iso8601.date2String(ts));
        xp.put(FileEntryConstants.LAST_MODIFIED, Iso8601.date2String(ts));
        xp.put(FileEntryConstants.LENGTH, f.length());
        xp.put(FileEntryConstants.ID, id);
        return xp;
    }

    public static JSONObject toJSON(File f, int type) throws Throwable {
        return toJSON(f.getName(), f, type);
    }

    public static FileEntry fileToEntry(File file, int type) throws Throwable {
        if(type == FILE_OP_READER){
            return toEntryWithReader(file.getName(), file);
        }
        return fromJSON(toJSON(file, type));
    }

    public static FileEntry fileToEntry(String path, int type) throws Throwable {
        File f = new File(path);
        return fileToEntry(f, type);
    }

    /**
     * Take and entry from the library and return a file entry.
     *
     * @param json
     * @return
     */
    public static FileEntry fromJSON(JSONObject json) {
        JSONObject content = json.getJSONObject(TYPE);
        JSONArray array = content.getJSONArray(CONTENT);
        content.remove(CONTENT);
        XProperties xProperties = new XProperties();
        xProperties.putAll(content);
        FileEntry fileEntry = new FileEntry(array, xProperties);
        return fileEntry;
    }

    public static boolean isBinary(File f, int type) throws IOException {
        switch (type) {
            case FILE_OP_BINARY:
                return true;
            case FILE_OP_TEXT_INI:
            case FILE_OP_TEXT_STRING:
            case FILE_OP_TEXT_STEM:
                return false;
            case FILE_OP_AUTO:
                // pass through to system
        }
        if (f.getCanonicalPath().endsWith(QDLVersion.DEFAULT_FILE_EXTENSION)
                || f.getCanonicalPath().endsWith(QDLVersion.DEFAULT_MODULE_FILE_EXTENSION)
                || f.getCanonicalPath().endsWith(".json") // Some versions of Java 11 need this.
        ) {
            // Make sure QDL knows its own files are not binary!
            return false;
        }
        String ftype = Files.probeContentType(f.toPath());
        DebugUtil.trace(FileEntries.class, "returned file type of '" + ftype + "' for file '" + f.getCanonicalPath() + "'");
        if (ftype == null) return true; // just in case
        if (ftype.startsWith("text") ||
                ftype.endsWith("/xml") ||
                ftype.endsWith("/json") ||
                ftype.endsWith("/javascript") ||
                ftype.endsWith("/java") ||
                ftype.endsWith("/html") ||
                ftype.endsWith("/xhtml+xml") ||
                ftype.endsWith("/x-shellscript") ||
                ftype.endsWith("/x-sh")
        ) return false;
        return true; // safe way -- anything it can't figure out is binary. User can override this.
    }

    public static JSONObject createConfig() {
        JSONObject jsonObject = new JSONObject();
        JSONArray scripts = new JSONArray();
        jsonObject.put(FILES_TAG, scripts);
        return jsonObject;
    }

    public static void addFile(JSONObject config, String id, File file, int type) throws Throwable {
        JSONObject j = toJSON(id, file, type);
        addFile(config, j);
    }

    protected static void addFile(JSONObject config, JSONObject fileEntry) throws Throwable {
        if (!config.containsKey(FILES_TAG)) {
            JSONArray array = new JSONArray();
            config.put(FILES_TAG, array);
        }
        config.getJSONArray(FILES_TAG).add(fileEntry);

    }

    public static void addFile(JSONObject config, FileEntry fileEntry) throws Throwable {
        JSONObject j = toJSON(fileEntry);
        addFile(config, j);
    }
}
