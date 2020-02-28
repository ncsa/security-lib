package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  1:09 PM
 */
public class QDLModuleConfig implements ModuleConfig{
    @Override
    public String getType() {
        return QDLConfigurationConstants.MODULE_TYPE_QDL;
    }

    boolean importOnStart = false;
    @Override
    public boolean isImportOnStart() {
        return importOnStart;
    }

    /**
     * The path to the module.
     * @return
     */
    public String getPath() {
        return path;
    }

    String path;

    @Override
    public String toString() {
        return "QDLModuleConfig{" +
                "loadOnStart=" + importOnStart +
                ", path='" + path + '\'' +
                '}';
    }
}
