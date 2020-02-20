package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

import static edu.uiuc.ncsa.qdl.scripting.FileEntry.*;

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
        props.put(CONTENT, fileEntry.getText());
        json.put(TYPE, props);
        return json;
    }

    public static JSONObject toJSON(String id, File f) throws Throwable {
        XProperties xp = new XProperties();
        xp.put(Scripts.CREATE_TIME, Iso8601.date2String(new Date()));
        xp.put(Scripts.ID, id);
        String contents;
        if (isBinary(f)) {
            xp.put(CONTENT_TYPE, BINARY_TYPE);
            contents = FileUtil.readFileAsBinary(f.getAbsolutePath());
        } else {
            xp.put(CONTENT_TYPE, TEXT_TYPE);
            contents = FileUtil.readFileAsString(f.getAbsolutePath());
        }
        FileEntry fileEntry = new FileEntry(xp, contents);
        return toJSON(fileEntry);

    }

    public static JSONObject toJSON(File f) throws Throwable {
        return toJSON(f.getName(), f);
    }

    /**
     * Take and entry from the library and return a file entry.
     *
     * @param json
     * @return
     */
    public static FileEntry fromJSON(JSONObject json) {
        JSONObject content = json.getJSONObject(TYPE);
        String text = content.getString(CONTENT);
        content.remove(CONTENT);
        XProperties xProperties = new XProperties();
        xProperties.putAll(content);
        FileEntry fileEntry = new FileEntry(xProperties, text);
        return fileEntry;
    }

    protected static boolean isBinary(File f) throws IOException {
        String ftype = Files.probeContentType(f.toPath());
        if (ftype == null) return true; // just in case
        if (ftype.startsWith("text")) return false;
        return true;
    }

    public static JSONObject createConfig() {
        JSONObject jsonObject = new JSONObject();
        JSONArray scripts = new JSONArray();
        jsonObject.put(FILES_TAG, scripts);
        return jsonObject;
    }

    public static void addFile(JSONObject config, String id, File file) throws Throwable {
        JSONObject j = toJSON(id, file);
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
