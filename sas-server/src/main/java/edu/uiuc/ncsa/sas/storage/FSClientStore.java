package edu.uiuc.ncsa.sas.storage;

import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.storage.FileStore;
import edu.uiuc.ncsa.security.storage.data.MapConverter;

import java.io.File;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:10 PM
 */
public class FSClientStore<V extends SASClient> extends FileStore<V> implements SASClientStore<V> {
    public FSClientStore(File storeDirectory, File indexDirectory, IdentifiableProvider<V> identifiableProvider, MapConverter<V> converter,
                         boolean removeEmptyFiles, boolean removeFailedFiles) {
        super(storeDirectory, indexDirectory, identifiableProvider, converter, removeEmptyFiles,removeFailedFiles);
    }

    public FSClientStore(File directory, IdentifiableProvider<V> idp, MapConverter<V> cp, boolean removeEmptyFiles, boolean removeFailedFiles) {
        super(directory, idp, cp, removeEmptyFiles,removeFailedFiles);
    }

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }

    @Override
    public String toString() {
        return "FSClientStore{" +
                "indexDirectory=" + indexDirectory +
                ", storageDirectory=" + storageDirectory +
                '}';
    }
}
