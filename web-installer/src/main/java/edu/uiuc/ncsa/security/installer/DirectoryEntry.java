package edu.uiuc.ncsa.security.installer;

import java.util.ArrayList;
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

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    String sourceURL;
    Boolean executable = null;
    Boolean useTemplates = null;
    Boolean updateable = null;
    List<FileEntryInterface> files;

    public int size() {
        if (getFiles() == null) return 0;
        return getFiles().size();
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public boolean hasExecutable() {
        return executable != null;
    }

    public boolean hasUseTemplates() {
        return useTemplates != null;
    }

    public boolean hasUpdateable() {
        return updateable != null;
    }

    public boolean hasFiles() {
        return files != null && !files.isEmpty();
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

    public List<FileEntryInterface> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntryInterface> files) {
        this.files = files;
    }

    /**
     * In jar archives, a list of files and directories to ignore
     *
     * @return
     */
    public List<String> getIgnoredFiles() {
        return ignoredFiles;
    }

    public void setIgnoredFiles(List<String> ignoredFiles) {
        this.ignoredFiles = ignoredFiles;
    }

    List<String> ignoredFiles = null;

    public boolean hasExcludedFiles() {
        return !(ignoredFiles == null || ignoredFiles.isEmpty());
    }

    /**
     * Sniff through the excluded files and returns the ones that end in /,
     * i.e., the directories. This is computed anew each time, so just stash it
     * @return
     */
    public List<String> getIgnoredDirectories() {
        List<String> out = new ArrayList<>();
        if (hasExcludedFiles()) {
            for (String f : getIgnoredFiles()) {
                if (f.endsWith("/")) {
                    out.add(f);
                }
            }
        }
        return out;
    }
}
