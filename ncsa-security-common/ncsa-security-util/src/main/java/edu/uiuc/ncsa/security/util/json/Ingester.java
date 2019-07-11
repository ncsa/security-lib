package edu.uiuc.ncsa.security.util.json;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This will ingest a file into the store. The format for a file is either a single JSONObject or an array of them.
 * Each object may have an id set with the {@link #STORE_ID_TAG}. If no store id is found then a random id is generated.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 6/6/19 at  2:14 PM
 */
public class Ingester {
    public Ingester(JSONStore<JSONEntry> store) {
        this.store = store;
    }

    JSONStore<JSONEntry> store;
    public static String STORE_ID_TAG = "#store_id"; // look for this in the ingestor

    public List<Identifier> ingest(File file, boolean safeModeOff) throws IOException {
        FileReader fileReader = new FileReader(file);
        return ingest(fileReader, safeModeOff);
    }

    public List<Identifier> ingest(Reader reader) throws IOException {
        return ingest(reader, false);
    }

    /**
     * Ingest the reader. The safe mode switch means that if an existing object with the same identifier is found,
     * it <b>will not</b> be over-written. Note that this returns a list of identifiers for objects added to the store.
     * Note that this closes the reader.
     *
     * @param reader
     * @param safeModeOff
     * @throws IOException
     */
    public List<Identifier> ingest(Reader reader, boolean safeModeOff) throws IOException {
        ArrayList<Identifier> addedIDs = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);

        String line = br.readLine();
        StringBuffer sb = new StringBuffer();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        br.close();
        String input = sb.toString();
        // Figure out what we have.
        JSONObject jsonObject = null;
        JSONArray array = null;
        try {
            jsonObject = JSONObject.fromObject(input);
            array = new JSONArray();
            array.add(jsonObject);
        } catch (Throwable t) {
            // ok,. not an object, try an array
            array = JSONArray.fromObject(input);
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject currentObject = array.getJSONObject(i);
            Identifier id = null;
            if (currentObject.containsKey(STORE_ID_TAG)) {
                // then the tag is in the object and we should use that.
                id = BasicIdentifier.newID(currentObject.getString(STORE_ID_TAG));
                currentObject.remove(STORE_ID_TAG); // Don't store tags, only use for ingestion.
            } else {
                id = newID();
            }
            boolean addEntry = safeModeOff && !store.containsKey(id);
            if (addEntry) {
                Object json = array.get(i);
                JSONEntry entry = new JSONEntry(id);
                if (json instanceof JSONArray) {
                    entry.setType(JSONEntry.TYPE_JSON_ARRAY);
                } else {
                    entry.setType(JSONEntry.TYPE_JSON_OBJECT);
                }
                entry.setRawContent(json.toString());
                store.put(id, entry);
                addedIDs.add(id);
            }
        }

        // so we have an array of JSONObjects and can loop through them.
        return addedIDs;
    }

    protected Identifier newID() {
        return BasicIdentifier.newID("urn:json_store:" + UUID.randomUUID());
    }

    public static void main(String[] args) {
        JSONStore<JSONEntry> store = new TestMemStore();
        File f = new File("/home/ncsa/dev/ncsa-git/oa4mp/oa4mp-server-admin-oauth2/src/main/resources/new-form/ldap-minimal.json");
        Ingester ingester = new Ingester(store);
        try {
            List<Identifier> ids = ingester.ingest(f, true);
            System.out.println("added ids = " + ids);
            System.out.println(store.get(ids.get(0)).getObject().toString(2));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
