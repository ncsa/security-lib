package edu.uiuc.ncsa.security.util.json;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This models a JSONObject that has a unique identifier associated with it.
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/19 at  4:14 PM
 */

public class JSONEntry extends IdentifiableImpl {
    public static final String TYPE_JSON_ARRAY = "JSONArray";
    public static final String TYPE_JSON_OBJECT = "JSONObject";
    public static final String TYPE_STRING = "string";
    public static final String TYPE_PROCEDURE = "procedure";
    public static final String TYPE_SCRIPT = "script";

    public JSONEntry(Identifier identifier) {
        super(identifier);
    }
    

    public boolean isArray() {
        return getType().equals(TYPE_JSON_ARRAY);
    }

    public boolean isJSONObject(){
        return getType().equals(TYPE_JSON_OBJECT);
    }

    public boolean isProcedure(){
        return getType().equals(TYPE_PROCEDURE);
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    String rawContent;
    String type;

    public JSONArray getArray() {
        if (rawContent == null) {
            return null;
        }
        if (isArray()) {
          return  JSONArray.fromObject(rawContent);
        }
        throw new IllegalStateException("Error: This is not a JSONArray");
    }

    public JSONObject getObject() {
        if (rawContent == null) {
            return null;
        }
        if (isJSONObject()) {
            return JSONObject.fromObject(rawContent);
        }
        throw new IllegalStateException("Error: This is not a JSONObject");

    }

    /**
     * Procedures are stored as JSON arrays of string. When one is gotten, it is returned as a list of strings.
     * @return
     */
    public List<String> getProcedure(){
        if(rawContent == null){
            return new LinkedList<>();
        }
        try {
            return JSONArray.fromObject(rawContent);
        }catch(Throwable t){
            throw new IllegalStateException("Error: This is not an array/ Message is \"" + t.getMessage() + "\"");

        }

    }
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    Date lastModified = new Date();

    Date creationTimestamp = new Date();

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    @Override
    public IdentifiableImpl clone() {
        JSONEntry rc = new JSONEntry(getIdentifier());
        rc.setCreationTimestamp(getCreationTimestamp());
        rc.setLastModified(getLastModified());
        rc.setRawContent(getRawContent());
        rc.setType(getType());
        return rc;
    }
}
