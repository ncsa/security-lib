package edu.uiuc.ncsa.security.storage.sql.internals;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.storage.data.ConversionMap;

import java.net.URI;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

/**
 * Helper class with a bunch of built in casts. It contains key value pairs,
 * where the keys are SQL column names and the values are values. This
 * practically contains the information for a single row in a database.
 * <p/>
 * <p>Note that the keys are all column names AND that
 * the keys are automatically converted to/from lower case. This is because this object may have its
 * values given from a result set metadata object and most vendors allow only case-insensitive column names,
 * so they may or may not elect to convert them to lower or upper case as they see fit.
 * </>
 * <p>Created by Jeff Gaynor<br>
 * on 8/31/11 at  4:05 PM
 */
public class ColumnMap extends HashMap<String, Object> implements ConversionMap<String, Object> {
    @Override
    public Object put(String key, Object value) {
        return super.put(key.toLowerCase(), value);
    }

    @Override
    public Object get(Object key) {
        return super.get(key.toString().toLowerCase());
    }

    public String getString(String key) {

        Object obj = get(key);
        if (obj == null) return null;
        if (obj instanceof String) return (String) obj;
        return obj.toString();

    }

    /**
     * Returns zero if the value is null;
     * @param key
     * @return
     */
    public long getLong(String key) {
        Object obj = get(key);
        if(obj instanceof Long){
            return (Long) get(key);

        }
        if(obj == null) return 0L;
        return Long.parseLong(obj.toString());
    }

    public boolean getBoolean(String key) {
        Object obj = get(key);
        if(obj instanceof Boolean){
            return (Boolean) get(key);
        }
        if(obj == null) return false;
        return Boolean.parseBoolean(obj.toString());
    }

    @Override
    public Date getDate(String key) {
        Object obj = get(key);
       if(obj instanceof Date){
        return (Date) get(key);
       }
        try {
            return Iso8601.string2Date(obj.toString()).getTime();
        } catch (ParseException e) {
            throw new GeneralException("Error: Could not parse date\"" + obj + "\"", e);
        }
    }

    @Override
    public Identifier getIdentifier(String key) {
        Object obj = get(key);
        if(obj == null) return null;
        if (obj instanceof Identifier) return (Identifier) obj;
        return BasicIdentifier.newID(obj.toString());
    }

    @Override
    public URI getURI(String key) {
        Object obj = get(key);
        if(obj == null) return null;
        if (obj instanceof URI) return (URI) obj;
        return URI.create(obj.toString());
    }

    public byte[] getBytes(String key) {
        Object o = get(key.toLowerCase());
        // it is possible that SQL will return a string in ResultSet.getObject in the case that the byte array has zero elements.
        // Intercept that and rock on
        if (o instanceof String) {
            if (o == null || ((String) o).length() == 0) {
                return new byte[]{};
            }
        }
        return (byte[]) o;
    }

    public Timestamp getTimestamp(String key) {
        return (Timestamp) get(key);
    }


}
