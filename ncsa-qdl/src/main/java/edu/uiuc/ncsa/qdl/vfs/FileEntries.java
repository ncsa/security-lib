package edu.uiuc.ncsa.qdl.vfs;

import edu.uiuc.ncsa.qdl.scripting.Scripts;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        JSONArray array = new JSONArray();
        array.addAll(fileEntry.getLines());
        props.put(CONTENT, array);
        json.put(TYPE, props);
        return json;
    }

    public static JSONObject toJSON(String id, File f) throws Throwable {
        XProperties xp = new XProperties();
        Date ts = new Date();
        ts.setTime(f.lastModified());
        xp.put(Scripts.CREATE_TIME, Iso8601.date2String(ts));
        xp.put(Scripts.LAST_MODIFIED, Iso8601.date2String(ts));
        xp.put(Scripts.ID, id);
        List<String> contents;
        if (isBinary(f)) {
            xp.put(CONTENT_TYPE, BINARY_TYPE);
            contents = new ArrayList<>();
            contents.add(FileUtil.readFileAsBinary(f.getAbsolutePath()));  // one string
        } else {
            xp.put(CONTENT_TYPE, TEXT_TYPE);
            contents = FileUtil.readFileAsLines(f.getAbsolutePath());
        }
        FileEntry fileEntry = new FileEntry(contents, xp);
        return toJSON(fileEntry);

    }

    public static JSONObject toJSON(File f) throws Throwable {
        return toJSON(f.getName(), f);
    }

    public static FileEntry fileToEntry(String path) throws Throwable {
        File f = new File(path);
        return fromJSON(toJSON(f));
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

    protected static boolean isBinary(File f) throws IOException {
        String ftype = Files.probeContentType(f.toPath());
        if (ftype == null) return true; // just in case
        if (ftype.startsWith("text") ||
                ftype.endsWith("/xml") ||
                ftype.endsWith("/json") ||
                ftype.endsWith("/javascript") ||
                ftype.endsWith("/java") ||
                ftype.endsWith("/html")
        ) return false;
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
