package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  1:07 PM
 */
public class JavaModuleConfig implements ModuleConfig {
    @Override
    public String getType() {
        return QDLConfigurationConstants.MODULE_TYPE_JAVA;
    }

    boolean loadOnStart = false;

    @Override
    public boolean isImportOnStart() {
        return loadOnStart;
    }

    String className;

    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return "JavaModuleConfig{" +
                "loadOnStart=" + loadOnStart +
                ", className='" + className + '\'' +
                '}';
    }
}
