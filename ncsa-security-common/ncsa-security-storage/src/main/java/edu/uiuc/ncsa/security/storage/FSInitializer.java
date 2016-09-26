package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;

import java.io.File;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/23/12 at  10:29 AM
 */
public class FSInitializer extends StoreInitializer {
    public FSInitializer(File storeDirectory, File indexDirectory) {
        this.indexDirectory = indexDirectory;
        this.storeDirectory = storeDirectory;
    }

    File storeDirectory;

    File indexDirectory;

    @Override
    public boolean createNew() {
        boolean isOk = true;
        if(storeDirectory == null){
            throw new MyConfigurationException("Error: storage directory is null");
        }
        if(indexDirectory == null){
            throw new MyConfigurationException("Error: index directory is null");
        }

        if (!storeDirectory.exists()) {
            isOk = isOk && storeDirectory.mkdirs();
        } else {
            if (!storeDirectory.isDirectory()) {
                throw new MyConfigurationException("Error: The given directory \"" + storeDirectory.getAbsolutePath() + "\" is not a directory. Cannot be used as a file store.");
            }
        }
        if (!indexDirectory.exists()) {
            isOk = isOk && indexDirectory.mkdirs();
        } else {
            if (!indexDirectory.isDirectory()) {
                throw new MyConfigurationException("Error: The given directory \"" + storeDirectory.getAbsolutePath() + "\" is not a directory. Cannot be used as a file store.");
            }
        }

        return isOk;
    }

    @Override
    public boolean isCreated() {
        if (storeDirectory == null || indexDirectory == null) {
            return false;
        }
        return storeDirectory.exists() && indexDirectory.exists();

    }

    @Override
    public boolean isInitialized() {
        if (!isCreated()) {
            return false;
        }
        return storeDirectory.list().length == 0 && indexDirectory.list().length == 0;
    }


    @Override
    public boolean destroy() {
        // We have to destroy the contents of the directories and remove them
        if (isCreated()) {
            clearEntries();
        }
        boolean isOk = true;
        isOk = isOk && indexDirectory.delete();
        isOk = isOk && storeDirectory.delete();
        return isOk;
    }


    /**
     * Clears out any and all entries in the storage/index directories.
     */
    protected void clearEntries() {
        if (storeDirectory != null) {
            for (File f : storeDirectory.listFiles()) {
                f.delete();
            }
        }
        if (indexDirectory != null) {
            for (File f : indexDirectory.listFiles()) {
                f.delete();
            }
        }
    }

    public boolean init() {
        if (!isCreated()) {
            return false;
        }
        clearEntries();
        return true;
    }
}
