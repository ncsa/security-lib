package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.cache.SimpleEntryImpl;
import edu.uiuc.ncsa.security.core.exceptions.FilePermissionsException;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.exceptions.UnregisteredObjectException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.storage.data.MapConverter;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A store backed by the file system. This works with all implementations since it just serializes whatever
 * item (which must be of type {@link edu.uiuc.ncsa.security.core.Identifiable}) it has.
 * <h3>How's it work?</h3>
 * There are two directories, one, the storage directory, contains the serialized items themselves. The file names are
 * hashes of the unique identifiers. The other directory, the index directory, contains files whose names are hashes of
 * the other information (e.g. temporary credentials, client identifier, verifier, access token,...).  You will have
 * to override to get the right index entries. (See below)
 * Each of these index files contains a single line which is
 * the file name in the storage directory. So a request to get by the temporary credential will hash the credential,
 * grab the index file, read the name of the actual transaction file and load that.
 * <p>This does no caching of any sort. If you want caching (strongly suggested) create a transaction cache and
 * set its backing cache to be an instance of this.
 * <h3>Usage</h3>
 * To use this, you need to override a couple of methods:<br><br>
 * <ul>
 * <li>{@link #create} to return whatever V really is.</li>
 * <li>{@link #realSave(boolean, edu.uiuc.ncsa.security.core.Identifiable)} Optional if you need some other processing before or after the save.
 * In the case, e.g., of transactions, you want to save them by access token and verifier too, e.g. by invoking
 * {@link #createIndexEntry(String, String)} of the real identifier and the other one. Retrieval of these is
 * done with the method {@link #getIndexEntry(String)}.</li>
 * <li>{@link #realRemove(edu.uiuc.ncsa.security.core.Identifiable)} After calling super on the object, remove all index entries with
 * {@link #removeIndexEntry(String)}.</li>
 * </ul>
 * A store that uses a file system.
 * <p>Created by Jeff Gaynor<br>
 * on 11/3/11 at  1:54 PM
 */
public abstract class FileStore<V extends Identifiable> extends IndexedStreamStore<V> {

    /**
     * Since administrators can and have inadvertently changed directory or file permissions while
     * the server is running, here is a method to check if the directory has acceptable permissions.
     * Each call to the store should invoke this as its first action.
     */

    protected void checkPermissions() {
        if (this.storageDirectory == null) {
            throw new MyConfigurationException("Error: There is no storage directory specified for this file store.");
        }
        if (!this.storageDirectory.exists()) {
            throw new FilePermissionsException("Error: the storage directory " + this.storageDirectory.getAbsolutePath() + " does not exist.");
        }
        if (!this.storageDirectory.canRead()) {
            throw new FilePermissionsException("Error: the storage directory " + this.storageDirectory.getAbsolutePath() + " cannot be read.");
        }
        if (!this.storageDirectory.canWrite()) {
            throw new FilePermissionsException("Error: the store cannot write to the storage directory " + this.storageDirectory.getAbsolutePath() + ".");
        }
        if (this.indexDirectory == null) {
            throw new MyConfigurationException("Error: There is no index directory specified for this file store.");
        }
        if (!this.indexDirectory.exists()) {
            throw new FilePermissionsException("Error: the index directory " + this.indexDirectory.getAbsolutePath() + " does not exist.");
        }

        if (!this.indexDirectory.canRead()) {
            throw new FilePermissionsException("Error: the index directory " + this.storageDirectory.getAbsolutePath() + " cannot be read.");
        }
        if (!this.indexDirectory.canWrite()) {
            throw new FilePermissionsException("Error: the store cannot write to the index directory " + this.storageDirectory.getAbsolutePath() + ".");
        }
    }

    /**
     * For the case where the data and index directories are explicitly given.
     *
     * @param storeDirectory
     * @param indexDirectory
     * @param identifiableProvider
     * @param converter
     */
    protected FileStore(File storeDirectory,
                        File indexDirectory,
                        IdentifiableProvider<V> identifiableProvider,
                        MapConverter<V> converter,
                        boolean removeEmptyFiles) {
        doSetup(storeDirectory, indexDirectory, identifiableProvider, converter, removeEmptyFiles);
    }

    protected void doSetup(File storeDirectory,
                           File indexDirectory,
                           IdentifiableProvider<V> identifiableProvider,
                           MapConverter<V> converter,
                           boolean removeEmptyFiles) {
        initializer = new FSInitializer(storeDirectory, indexDirectory);
        if (!initializer.isCreated()) {
            if (!initializer.createNew()) {
                throw new GeneralException("Error: Could not create the store directory \"" +
                        storeDirectory.getAbsolutePath() + "\" or maybe the index directory \"" +
                        indexDirectory.getAbsolutePath() + "\". Please check paths and permissions");
            }
        }
        this.indexDirectory = indexDirectory;
        this.storageDirectory = storeDirectory;
        this.identifiableProvider = identifiableProvider;
        this.converter = converter;
        this.removeEmptyFiles =removeEmptyFiles;
    }

    /**
     * Accepts a directory for both the index and data and creates the subdirectories.
     *
     * @param directory
     * @param idp
     * @param cp
     */
    public FileStore(File directory, IdentifiableProvider<V> idp, MapConverter<V> cp, boolean removeEmptyFiles) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Error: the given directory \"" + directory.getAbsolutePath() + "\" is not a directory. Cannot create sub-directories.");
        }
        File storeDir = new File(directory, "store");
        File index = new File(directory, "index");
        doSetup(storeDir, index,idp, cp, removeEmptyFiles);
    }

    protected File indexDirectory;

    /**
     * Make sure everything exists like it's supposed to
     *
     * @param directory
     */

    protected File storageDirectory = null;


    protected File getItemFile(V t) {
        return getItemFile(t.getIdentifierString());
    }

    HashSet<String> failures = new HashSet<>();

    protected File getItemFile(String identifier) {
        checkPermissions();
        if (identifier == null || identifier.length() == 0) {
            throw new IllegalArgumentException("Error: no identifier found. Cannot retrieve any store entry");
        }
        return new File(storageDirectory, hashString(identifier));
    }


    /**
     * Does the actual work of writing everything to the data directory. Override this as needed and
     * invoke {@link #createIndexEntry(String, String)} to put and entry for the item into the index.
     * When overriding, call this via super first or the item itself will not be saved.
     *
     * @param checkExists
     * @param t
     */
    public void realSave(boolean checkExists, V t) {
        checkPermissions();
        FileOutputStream fos = null;
        File f = getItemFile(t);
        if (checkExists && !f.exists()) {
            throw new UnregisteredObjectException("Error: Cannot update a non-existent store entry. Save it first.");
        }
        getCreatedItems().remove(t.getIdentifierString());
        try {

            fos = new FileOutputStream(f);
            if (converter != null) {
                XMLMap map = new XMLMap();
                converter.toMap(t, map);
                map.toXML(fos);
                fos.flush();
                fos.close();
            } else {
                throw new IllegalStateException("Error: no converter");
            }
        } catch (FileNotFoundException e) {
            try {
                throw new GeneralException("Error loading file \"" + f.getCanonicalPath() + "\" for store entry " + t, e);
            } catch (IOException e1) {
                throw new GeneralException("Error loading file \"" + f + "\" for store entry " + t, e1);
            }
        } catch (IOException e) {
            throw new GeneralException("Error serializing store entry " + t + "to file \"" + f, e);
        }
    }


    /**
     * Add an index entry for an item that is not the unique identifier.
     *
     * @param otherKey   the other key to index
     * @param identifier the unique identifier for this item
     * @throws IOException
     */
    protected void createIndexEntry(String otherKey, String identifier) throws IOException {
        String h = hashString(otherKey);
        File f = new File(indexDirectory, h);
        FileWriter fw = new FileWriter(f);
        fw.write(hashString(identifier));
        fw.flush();
        fw.close();

    }


    /**
     * Finds a file with the given index value. This will look in the index directory for the file with
     * the same name as the lookup, then read the contents of the lookup which is a hashed uri
     *
     * @param hashedName
     * @return
     * @throws IOException
     */
    protected V loadFromIndex(String hashedName) {
        // This *might* just be the identifier.
        checkPermissions();
        File f = new File(indexDirectory, hashedName);
        if (!f.exists() || !f.isFile()) {
            return null;
        }
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            String realFilename = br.readLine();
            return loadFile(new File(storageDirectory, realFilename));
        } catch (IOException e) {
            throw new GeneralException("Error: could not load file from index dir with hashed name \"" + hashedName + "\"", e);
        }
    }

    protected V loadByIdentifier(String identifier) {
        try {
            return loadFile(getItemFile(identifier));
        } catch (FilePermissionsException e) {
            // Let this throw file permission exceptions or the user will have a very hard time
            // figuring this error out.
            throw e;
        } catch (Throwable t) {
            return null;
        }

    }

    boolean removeEmptyFiles = true;

    protected V loadFile(File f) {
        if (f.length() == 0) {
            //Fixes CIL-518. Can't quite tell if there is a failure on create with a zero length file (since there
            // are practically too many places it could fail. Best we can do is delete any we find.
            if (removeEmptyFiles) {
                f.delete();
                DebugUtil.trace(this, "Deleting empty file:" + f);
                return null;
            } else {
                DebugUtil.trace(this, "Skipping file of length zero:" + f);
                return null;
            }
        }
        FileInputStream fis = null;
        checkPermissions();
        try {
            fis = new FileInputStream(f);

            return loadStream(fis);
        } catch (Throwable e) {
            if (DebugUtil.isEnabled()) {
                System.err.println("Could not load file \"" + f.getAbsolutePath() + "\", printing stack trace...");
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public void update(V t) {
        realSave(true, t);
    }

    @Override
    public void register(V t) {
        realSave(false, t);
    }


    /**
     * Required by the map interface
     */
    public void clear() {
        initializer.init();
    }


    @Override
    public void save(V t) {
        realSave(false, t);
    }

    public Set<Identifier> keySet() {
        checkPermissions();
        HashSet<Identifier> ids = new HashSet<Identifier>(); // have to work with a copy or get concurrent modification exceptions
        String[] filenames = storageDirectory.list();
        for (String filename : filenames) {

            File f = new File(storageDirectory, filename);
            try {
                V t = null;
                if (!failures.contains(f.getAbsolutePath())) {
                    t = loadFile(f);
                    if (t != null) {
                        ids.add(t.getIdentifier());
                    }
                }
            } catch (Throwable t) {
                failures.add(f.getAbsolutePath());
                System.out.println(t.getMessage());
            }
        }
        return ids;
    }

    /**
     * Not an efficient way to get the values, but this will get them all.
     *
     * @return
     */
    public Collection<V> values() {
        checkPermissions();
        Collection<V> allOfThem = new LinkedList<V>();
        for (File f : storageDirectory.listFiles()) {
            V t = loadFile(f);
            if (t != null) {
                allOfThem.add(t);
            }
        }
        return allOfThem;
    }

    public Set<Entry<Identifier, V>> entrySet() {
        checkPermissions();
        Set<Entry<Identifier, V>> entries = new HashSet<Entry<Identifier, V>>();
        for (File f : storageDirectory.listFiles()) {
            V t = loadFile(f);
            if (t != null) {
                entries.add(new SimpleEntryImpl<Identifier, V>(t.getIdentifier(), (V) t));
            }
        }
        return entries;
    }

    public int size() {
        return storageDirectory.list().length;
    }


    class IdentifierFileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.equals(id);
        }

        String id;

        IdentifierFileFilter(String identifier) {
            id = hashString(identifier);
        }
    }

    public boolean containsKey(Object key) {
        checkPermissions();
        if (key == null) {
            return false;
        }
        return storageDirectory.list(new IdentifierFileFilter(key.toString())).length == 1;
    }

    public boolean containsValue(Object value) {
        V t = (V) value;
        return containsKey(t.getIdentifierString());
    }

    public V get(Object key) {
        return (V) loadByIdentifier(key.toString());
    }

    @Override
    public List<V> getAll() {
        LinkedList<V> allEntries = new LinkedList<>();
        for (Identifier d : keySet()) {
            allEntries.add(get(d));
        }
        return allEntries;
    }

    public boolean delete(String identifier) {
        V t = loadByIdentifier(identifier);
        try {
            realRemove(t);
        } catch (Throwable throwable) {
            return false;
        }
        return true;
    }

    /**
     * Does the actual removal of the item from the store. Be sure to override this to remove any index
     * entries if you need to.
     *
     * @param oldItem The item (which is Identifiable) to be removed.
     * @return
     */
    protected V realRemove(V oldItem) {
        File f = getItemFile(oldItem.getIdentifierString());
        if (f.exists() && !f.isDirectory()) {
            f.delete();
        }
        if (oldItem.getIdentifierString() != null) {
            removeIndexEntry(oldItem.getIdentifierString()); // The main index
        }

        return oldItem;
    }

    /**
     * This is required by the map interface. The argument is really the identifier. This returns the transaction
     * if there was one already associated with this identifier
     *
     * @param key
     * @return
     */
    public V remove(Object key) {
        if (!containsKey(key)) {
            return null;
        }
        V t = (V) loadByIdentifier(key.toString());
        V x = realRemove(t);
        return x;
    }

    public void putAll(Map m) {
        for (Object e : m.entrySet()) {
            put((V) ((Map.Entry) e).getValue());
        }
    }

    /**
     * Remove an index entry (not the actual item!). To remove the item, use {@link #remove(Object)}.
     *
     * @param token
     */
    protected boolean removeIndexEntry(String token) {
        File f = new File(indexDirectory, hashString(token));
        return f.delete();
    }

    /**
     * Get a stored item using a key other than the identifier.
     *
     * @param token
     * @return
     */
    protected V getIndexEntry(String token) {
        return (V) loadFromIndex(hashString(token));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[dataPath=" + storageDirectory.getAbsolutePath() + ", indexPath=" + indexDirectory.getAbsolutePath() + "]";
    }

    @Override
    public V create() {
        checkPermissions();
        return super.create();
    }

    public XMLConverter<V> getXMLConverter() {
        return converter;
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx) {
        /*
        This can be a very expensive way to do this, but it does allow for searching through an
        entire file store for things.
        MemoryStore has this code boilerplated into it...
         */
        ArrayList<V> results = new ArrayList();
        Collection<V> values = values();
        Iterator iterator = values.iterator();
        Pattern pattern = null;
        if (isRegEx) {
            pattern = Pattern.compile(condition);
        }
        while (iterator.hasNext()) {
            V v = (V) iterator.next();
            XMLMap map = new XMLMap();

            getXMLConverter().toMap(v, map);
            String targetValue = map.get(key).toString();
            if (isRegEx) {
                if (pattern.matcher(targetValue).matches()) {
                    results.add(v);
                }
            } else {
                if (targetValue.equals(condition)) {
                    results.add(v);
                }
            }

        }
        return results;
    }

}
