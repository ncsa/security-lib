package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Initializable;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfigUtils;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.io.IOException;

/**
 * Creates Filestores. This centralizes all the general checking to create these. Specific providers are needed
 * for specific store types.
 * <b>NOTE</b> If the configuration only supplies a single path, then this will create the storage and
 * index path automatically and will add the component of the {@link #getTarget()} (e.g. "clientApprovals") to the path).
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/12 at  2:11 PM
 */
public abstract class FSProvider<T extends FileStore> extends TypedProvider<T> {
    protected static final String PATH_KEY = StorageConfigurationTags.FS_PATH;
    protected static final String INDEX_KEY = StorageConfigurationTags.FS_INDEX;
    protected static final String DATA_KEY = StorageConfigurationTags.FS_DATA;

    Boolean removeEmptyFiles = null;
    Boolean removeFailedFiles = null;
    protected MapConverter converter;

    public boolean isRemoveEmptyFiles() {
        if (removeEmptyFiles == null) {
            String rawValue = Configurations.getFirstAttribute(getConfig(), StorageConfigurationTags.FS_REMOVE_EMPTY_FILES);
            if (rawValue == null || rawValue.isEmpty()) {
                removeEmptyFiles = true; //default
            }
            try {
                removeEmptyFiles = Boolean.parseBoolean(rawValue);
            } catch (Throwable t) {
                removeEmptyFiles = true; // default
            }
        }
        return removeEmptyFiles;
    }

    public boolean isRemoveFailedFiles() {
        if (removeFailedFiles == null) {
            String rawValue = Configurations.getFirstAttribute(getConfig(), StorageConfigurationTags.FS_REMOVE_FAILED_FILES);
            if (rawValue == null || rawValue.isEmpty()) {
                removeEmptyFiles = false; //default
            }
            try {
                removeFailedFiles = Boolean.parseBoolean(rawValue);
            } catch (Throwable t) {
                removeFailedFiles = false; // default
            }
        }
        return removeFailedFiles;
    }

    public FSProvider(ConfigurationNode config, String type, String target, MapConverter converter) {
        super(config, type, target);
        this.converter = converter;

    }


    protected FSProvider(String type, String target, MapConverter converter) {
        super(type, target);
        this.converter = converter;
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        if (checkEvent(configurationEvent)) {
            return get();
        }

        return null;
    }

    /**
     * It is up to you to add the appropriate logic to check for the correct store type (e.g. transaction store)
     * and instantiate it in the {@link #produce(File, File, boolean, boolean)} method. This method simply invokes the
     * {@link #produce(java.io.File, java.io.File, boolean, boolean)}
     * method and returns that result.
     *
     * @return
     */
    @Override
    public T get() {
        T fs = null;
        String path = getTypeAttribute(PATH_KEY);
        String dataPath = null;
        String indexPath = null;


        // if path is given, use that
        if (path != null) {
            if (!path.endsWith(File.separator)) {
                path = path + File.separator;
            }
            if (getTarget() != null) {
                path = path + getTarget();
            }
            File dp = new File(path, DATA_KEY);
            File ip = new File(path, INDEX_KEY);
            try {
                if (!dp.exists()) {
                    dp.mkdirs();
                }
                if (!ip.exists()) {
                    ip.mkdirs();
                }
                dataPath = dp.getCanonicalPath();
                indexPath = ip.getCanonicalPath();
            } catch (IOException e) {

            }
        } else {
            /*
             Supplying the data and index paths creates nothing. This means that everything is left
             up to the administrator. Also, you can only specify a single component (e.g clients) per path,
             so explicit control of these is something of an edge case.
             Since this is pretty primitive, might want to just deprecate this ability.
             */
            dataPath = getTypeAttribute(DATA_KEY);
            if (dataPath == null) {
                throw new MyConfigurationException("Error: file store has no dataPath configured");
            }
            indexPath = getTypeAttribute(INDEX_KEY);
            if (indexPath == null) {
                throw new MyConfigurationException("Error: file store has no indexPath configured");
            }
        }
        File indexDirectory = new File(indexPath);
        File storeDirectory = new File(dataPath);
        fs = produce(storeDirectory, indexDirectory, isRemoveEmptyFiles(), isRemoveFailedFiles());
        Initializable initializable = new FSInitializer(storeDirectory, indexDirectory);
        if (!initializable.isCreated()) {
            initializable.createNew();
        }
        return fs;
    }

    /**
     * Put the actual instantiation of the store here. {@link #get()} does the grunt work of getting everything
     * out of the configuration for you and checking that it all works as planned.
     *
     * @param dataPath
     * @param indexPath
     * @return
     */
    protected abstract T produce(File dataPath, File indexPath, boolean removeEmptyFiles, boolean removeFailedFiles);

    public UpkeepConfiguration getUpkeepConfiguration() {
        return upkeepConfiguration;
    }

    public void setUpkeepConfiguration(UpkeepConfiguration upkeepConfiguration) {
        this.upkeepConfiguration = upkeepConfiguration;
    }

    UpkeepConfiguration upkeepConfiguration;

    @Override
    public void setConfig(ConfigurationNode config) {
        super.setConfig(config);
        ConfigurationNode upkeepNode = Configurations.getFirstNode(getConfig(), UpkeepConfigUtils.UPKEEP_TAG);
        if (upkeepNode != null) {
            try {
                upkeepConfiguration = UpkeepConfigUtils.processUpkeep(Configurations.getFirstNode(getConfig(), UpkeepConfigUtils.UPKEEP_TAG));
            }catch (Throwable t){
                System.err.println("could not load configuration for " + getClass().getSimpleName() + ":" + t.getMessage());
                throw t;
            }
        }
    }
}
