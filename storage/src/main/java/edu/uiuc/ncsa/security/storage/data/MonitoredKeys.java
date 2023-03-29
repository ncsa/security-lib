package edu.uiuc.ncsa.security.storage.data;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  8:19 AM
 */
public class MonitoredKeys extends SerializationKeys {
      protected String lastAccessed = "last_accessed";
    String lastModified = "last_modified_ts";
    String creationTS = "creation_ts";
    public String creationTS(String... x) {
         if (0 < x.length) creationTS = x[0];
         return creationTS;
     }

    public String lastModifiedTS(String... x) {
         if (0 < x.length) lastModified = x[0];
         return lastModified;
     }
    public String lastAccessed(String... x) {
         if (0 < x.length) lastAccessed = x[0];
         return lastAccessed;
     }

}
