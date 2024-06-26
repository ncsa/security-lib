package edu.uiuc.ncsa.security.installer;

import java.util.Map;

import static edu.uiuc.ncsa.security.installer.InstallConfigurationImporter.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/24 at  11:24 AM
 */
public class FileEntry implements FileEntryInterface {
    Boolean executable = null;
    Boolean useTemplate = null;
    Boolean updateable = null;
    String sourceName;
    String targetName;

    public boolean isExecutableSet() {
        return executable != null;
    }

    public boolean isUseTemplateSet() {
        return useTemplate != null;
    }

    public boolean isUpdateableSet() {
        return updateable != null;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setExecutable(boolean executable) {
        this.executable = executable;
    }

    public boolean isUseTemplate() {
        return useTemplate;
    }

    public boolean hasUseTemplate() {
        return useTemplate != null;
    }

    public boolean hasUpdateable() {
        return updateable != null;
    }

    public boolean hasExecutale() {
        return executable != null;
    }

    public Boolean getUseTemplate() {
        return useTemplate;
    }

    public void setUseTemplate(boolean useTemplate) {
        this.useTemplate = useTemplate;
    }

    public boolean isUpdateable() {
        return updateable;
    }

    public void setUpdateable(boolean updateable) {
        this.updateable = updateable;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getTargetName() {
        if (targetName == null) {
            targetName = getSourceName();
        }
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public FileEntry(String sourceName) {
        this(sourceName, sourceName, false, false, false);
    }

    public FileEntry(Map currentMap) {
        this((String) currentMap.get(SOURCE_FILE));
        String source = (String) currentMap.get(SOURCE_FILE);
        String target = (String) currentMap.get(TARGET_FILE);
        setTargetName(target == null ? target = source : target); // same name
        setExecutable(getBoolean(currentMap, ATTR_EXECUTABLE) == Boolean.TRUE);
        setUpdateable(getBoolean(currentMap, ATTR_UPDATEABLE) == Boolean.TRUE);
        setUseTemplate(getBoolean(currentMap, ATTR_PREPROCESS)== Boolean.TRUE);
    }

    public FileEntry(String sourceName,
                     String targetName,
                     Boolean executable,
                     Boolean useTemplate,
                     Boolean updateable) {
        this.executable = executable;
        this.useTemplate = useTemplate;
        this.updateable = updateable;
        this.sourceName = sourceName;
        this.targetName = targetName;
    }
}
