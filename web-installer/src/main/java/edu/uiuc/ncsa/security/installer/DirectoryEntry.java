package edu.uiuc.ncsa.security.installer;

import java.util.List;

/**
 * This models a set of directory entries on the target system.
 * There may be several of these that point to the same  directory, each
 * with different properties (e.g. a set of executables, a set of config files.)
 * This has the same flags as each individual file, meaning every file in the target
 * directory is processed the same, unless the file is specifically overridden.
 * files, etc).
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/24 at  11:28 AM
 */
public class DirectoryEntry {
    String targetDir;
    String source;
    Boolean executable = null;
    Boolean useTemplates = null;
    Boolean updateable = null;
    List<FileEntry> files;


    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public boolean hasExecutable(){
        return executable != null;
    }
    public boolean hasUseTemplates(){
        return useTemplates != null;
    }
    public boolean hasUpdateable(){
        return updateable != null;
    }

    public Boolean isExecutable() {
        return executable;
    }

    public void setExecutable(Boolean executable) {
        this.executable = executable;
    }

    public Boolean isUseTemplates() {
        return useTemplates;
    }

    public void setUseTemplates(Boolean useTemplates) {
        this.useTemplates = useTemplates;
    }

    public Boolean isUpdateable() {
        return updateable;
    }

    public void setUpdateable(Boolean updateable) {
        this.updateable = updateable;
    }

    public List<FileEntry> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntry> files) {
        this.files = files;
    }
}
