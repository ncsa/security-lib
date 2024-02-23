package edu.uiuc.ncsa.security.storage.filestore;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.util.TokenUtil;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.data.SerializationKeys;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * This is to replace the older {@link edu.uiuc.ncsa.security.storage.FileStore} which has too many issues with scaling
 * to be useful.
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/24 at  2:02 PM
 */
public class LuceneStore<V extends Identifiable> implements Store<V> {
    public static final String EXTENSION = ".xml";
    public static final String DATA_DIR = " data";
    public static final String INDEX_DIR = "lucene";
    public static final String IPC_DIR = "ipc"; // inter-process communication
    File indexDirectory = null;

    protected File getIndexDir() {
        if (indexDirectory == null) {
            indexDirectory = new File(getRoot(), INDEX_DIR);
        }
        return indexDirectory;
    }

    public File getDataDirectory() {
        if (dataDirectory == null) {
            dataDirectory = new File(getRoot(), DATA_DIR);
        }
        return dataDirectory;
    }

    public File getIpcDirectory() {
        if (ipcDirectory == null) {
            ipcDirectory = new File(getRoot(), IPC_DIR);
        }
        return ipcDirectory;
    }

    File dataDirectory = null;
    File ipcDirectory = null;

    protected void init() {
        if (!getIndexDir().exists()) {

        }
    }

    protected void setupIndex() throws IOException {
        org.apache.lucene.analysis.Analyzer analyzer = new StandardAnalyzer();

        // Store the index in memory:
        //Directory directory = new RAMDirectory();

        Directory directory = FSDirectory.open(Paths.get(getIndexDir().getAbsolutePath()));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
//        iwriter.addDocument(doc);
  //      iwriter.close();
    }

    protected String idToFileName(Identifier identifier) {
        return TokenUtil.b32EncodeToken(identifier.toString()) + EXTENSION;
    }

    protected String filenameToID(String filename) {
        return TokenUtil.b32DecodeToken(filename.substring(0, filename.length() - EXTENSION.length()));
    }

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    File root;

    /**
     * One issue with scalability that needs to be addressed is that dumping everything into a single
     * directory makes performance horrible. This will shave off the first and 2nd characters of the name
     * as the directory. So if the file has name abcdf.xml then it would land in the directory path/a/b.
     * This makes 32^2 = 1024 directories right off the bat.
     *
     * @param name
     * @param isQuery if this is a query, only check if the directories exist.
     * @return
     */
    protected File getRightDirectory(String name, boolean isQuery) {
        char first = name.charAt(0);
        char second = name.charAt(1);
        File firstDir = new File(getRoot(), String.valueOf(first));
        if (!firstDir.exists()) {
            if (isQuery) {
                return null;
            }
            firstDir.mkdir(); // returns true if the directory was created
        }
        File secondDir = new File(firstDir, String.valueOf(second));
        if (!secondDir.exists()) {
            if (isQuery) {
                return null;
            }
            secondDir.mkdir();
        }
        return secondDir;
    }

    @Override
    public V create() {
        return null;
    }

    @Override
    public void update(V value) {

    }

    @Override
    public void register(V value) {

    }

    @Override
    public void save(V value) {

    }

    @Override
    public List<V> getAll() {
        return null;
    }

    MapConverter<V> converter = null;

    @Override
    public XMLConverter<V> getXMLConverter() {
        return converter;
    }

    public MapConverter<V> getMapConverter() {
        return converter;
    }
    protected Document toLuceneDoc(V v){
        Map<String, Object> map = new HashMap<>();
        getMapConverter().toMap(v,map);
        SerializationKeys keys = getMapConverter().getKeys();
        List<String> allKeys = keys.allKeys();
        allKeys.remove(keys.identifier());
        Document doc = new Document();

        // StringField is not tokenized

        doc.add(new StringField(keys.identifier(), (String) map.get(keys.identifier()), Field.Store.YES));
        for(String k : allKeys){
            Object value = map.get(k);
            if(value instanceof String){
                doc.add(new Field(k, (String) value, TextField.TYPE_STORED));
            }
            if(value instanceof Boolean ){
                
            }
        }
        return doc;
    }
    @Override
    public List<V> search(String key, String condition, boolean isRegEx) {
        return null;
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx, List<String> attr) {
        return null;
    }

    @Override
    public List<V> search(String key, String condition, boolean isRegEx, List<String> attr, String dateField, Date before, Date after) {
        return null;
    }

    /**
     * Gets the number of files and the total byte count of the store too (since we are there).
     *
     * @param includeVersions
     * @return
     */
    protected long[] sizes(boolean includeVersions) {
        long byteCount = 0L;
        File[] rootFiles = getRoot().listFiles();
        long totalCount = 0;
        for (File f : rootFiles) {
            if (f.isDirectory()) {
                File[] moarFiles = f.listFiles();
                for (File f2 : moarFiles) {
                    if (!f2.isDirectory()) {
                        totalCount++;
                        byteCount += f.length();
                    }
                }
            } else {
                totalCount++;
                byteCount += f.length();
            }
        }
        return new long[]{totalCount, byteCount};
    }

    @Override
    public int size(boolean includeVersions) {
        long byteCount = 0L;
        File[] rootFiles = getRoot().listFiles();
        int totalCount = 0;
        for (File f : rootFiles) {
            if (f.isDirectory()) {
                File[] moarFiles = f.listFiles();
                for (File f2 : moarFiles) {
                    if (!f2.isDirectory()) {
                        totalCount++;
                        byteCount += f.length();
                    }
                }
            } else {
                totalCount++;
                byteCount += f.length();
            }
        }
        return totalCount;
    }

    @Override
    public boolean remove(List<Identifiable> objects) {
        return false;
    }

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object o) {
        return false;
    }

    @Override
    public boolean containsValue(Object o) {
        return false;
    }

    @Override
    public V get(Object o) {
        return null;
    }

    @Override
    public V put(Identifier identifier, V v) {
        return null;
    }

    @Override
    public V remove(Object o) {
        return null;
    }

    @Override
    public void putAll(Map<? extends Identifier, ? extends V> map) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<Identifier> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<Identifier, V>> entrySet() {
        return null;
    }
}
