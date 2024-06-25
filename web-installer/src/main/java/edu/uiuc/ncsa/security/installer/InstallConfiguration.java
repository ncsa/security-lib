package edu.uiuc.ncsa.security.installer;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/24 at  11:23 AM
 */
public class InstallConfiguration {
   public int getTotalFileCount(){
       int x = 0;
       for(SingleSourceSet sss : getSourceSets()){
           x = x + sss.size();
       }
       return x;
   }
    public List<SingleSourceSet> getSourceSets() {
        return sourceSets;
    }

    public void setSourceSets(List<SingleSourceSet> sourceSets) {
        this.sourceSets = sourceSets;
    }

    List<SingleSourceSet> sourceSets;

    @Override
    public String toString() {
        return "InstallConfiguration{" +
                "sourceSets=" + sourceSets +
                '}';
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    boolean failOnError = false;

    public boolean isCleanupOnFail() {
        return cleanupOnFail;
    }

    public void setCleanupOnFail(boolean cleanupOnFail) {
        this.cleanupOnFail = cleanupOnFail;
    }

    boolean cleanupOnFail = false;
    String appName = "NCSA Sec-Lib";

    public String getLastestVersionFile() {
        return lastestVersionFile;
    }

    public void setLastestVersionFile(String lastestVersionFile) {
        this.lastestVersionFile = lastestVersionFile;
    }

    String lastestVersionFile;
    public Versions getVersions() {
        return versions;
    }

    public void setVersions(Versions versions) {
        this.versions = versions;
    }

    Versions versions;

    public String getInstallerHelp() {
        return installerHelp;
    }

    public void setInstallerHelp(String installerHelp) {
        this.installerHelp = installerHelp;
    }

    public String getMoreArgHelp() {
        return moreArgHelp;
    }

    public void setArgHelp(String customHelp) {
        this.moreArgHelp = customHelp;
    }

    public String getExampleHelp() {
        return exampleHelp;
    }

    public void setExampleHelp(String exampleHelp) {
        this.exampleHelp = exampleHelp;
    }

    String exampleHelp;

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }

    String installerHelp;
    String moreArgHelp;
    String successMessage;

    /**
     * This is set after the system does the logic for the version.  It is merely used
     * for printing reports and other information.
     * @return
     */
    public String getActualVersion() {
        return actualVersion;
    }

    public void setActualVersion(String actualVersion) {
        this.actualVersion = actualVersion;
    }

    String actualVersion;
    /*
- app_name: NCSA sec-lib
  help: {app: /app_help.txt, installer: /help.txt, success: /success.txt}
  versions:
  - {file: /test-cfg1.yaml, name: v1, description: single script set}
  - {file: /test-cfg2.yaml, name: v2, description: two script sets}
  - {file: /v1, name: latest, description: latest release}
  cleanup_on_fail: false
  fail_on_error: false
     */
}
