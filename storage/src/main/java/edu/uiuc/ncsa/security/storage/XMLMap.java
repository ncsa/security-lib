package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

/**
 * Specialized extension to convert to and from XML.
* <p>Created by Jeff Gaynor<br>
* on 5/23/12 at  10:29 AM
*/
public class XMLMap extends ColumnMap {

    public void toXML(OutputStream outputStream) throws IOException {
        XProperties xp = new XProperties();
        for (String k : keySet()) {
            Object obj = get(k);
            if (obj != null) {
                if (obj instanceof Date) {
                    xp.put(k, Iso8601.date2String((Date) obj));
                } else {
                    xp.put(k, obj.toString());
                }
            }
        }
        xp.storeToXML(outputStream, "OA4MP stream store");
    }

    public void fromXML(InputStream inputStream) throws IOException {
        XProperties xp = new XProperties();
        xp.loadFromXML(inputStream);
        for (Object k : xp.keySet()) {
            put(k.toString(), xp.get(k));
        }
    }
}
