package edu.uiuc.ncsa.qdl.config;

import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.scripting.VFSFileProvider;
import edu.uiuc.ncsa.qdl.scripting.VFSPassThruFileProvider;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.FileUtil;

import static edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants.*;

/**
 * Takes  node from a QDL configuration and sets a bunch of information for a configuration.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  3:52 PM
 */
public class QDLConfigurationLoaderUtils {
    public static void setupVFS(QDLEnvironment config, State state) {
        if (!config.getVFSConfigurations().isEmpty()) {
            for (VFSConfig vfsConfig : config.getVFSConfigurations()) {
                if (vfsConfig.getType().equals(VFS_TYPE_PASS_THRU)) {
                    VFSPassThroughConfig vpt = (VFSPassThroughConfig) vfsConfig;
                    VFSPassThruFileProvider provider = new VFSPassThruFileProvider(
                            vpt.getRootDir(), vpt.getScheme(), vpt.getMountPoint(),
                            vpt.canRead(), vpt.canWrite());
                    state.addVFSProvider(provider);
                    config.getMyLogger().info("VFS mount: " + vpt.getScheme() + VFSFileProvider.SCHEME_DELIMITER + vpt.getMountPoint());
                }
            }
        }
    }

    public static int JAVA_MODULE_INDEX = 0;
    public static int QDL_MODULE_INDEX = 1;

    public static String[] setupModules(QDLEnvironment config, State state) throws Throwable {
        String[] x = new String[2];

        if (!config.getModuleConfigs().isEmpty()) {
            String foundClasses = "";
            String foundModules = "";
            boolean isFirstJavaModules = true;
            boolean isFirstQDLModules = true;
            QDLParser interpreter = new QDLParser(state);

            for (ModuleConfig moduleConfig : config.getModuleConfigs()) {
                if (moduleConfig.getType().equals(MODULE_TYPE_JAVA)) {
                    JavaModuleConfig jmc = (JavaModuleConfig) moduleConfig;
                    String className = jmc.getClassName();
                    try {

                        Class klasse = state.getClass().forName(className);
                        QDLLoader qdlLoader = (QDLLoader) klasse.newInstance();
                        setupJavaModule(state, qdlLoader, jmc.isImportOnStart());

                    /*
                    Next stuff just makes the entry for the environment
                     */
                        config.getMyLogger().info("loaded module:" + klasse.getSimpleName());
                        if (isFirstJavaModules) {
                            isFirstJavaModules = false;
                            foundClasses = className;
                        } else {
                            foundClasses = foundClasses + "," + className;
                        }
                    } catch (Throwable t) {

                        config.getMyLogger().error(
                                "WARNING: module \"" + className + "\" could not be loaded:" + t.getMessage(),
                                t);
                    }// end try
                }// end if for java modules
                if (moduleConfig.getType().equals(MODULE_TYPE_QDL)) {
                    QDLModuleConfig qmc = (QDLModuleConfig) moduleConfig;
                    String module = FileUtil.readFileAsString(qmc.getPath());
                    interpreter.execute(module);
                    config.getMyLogger().info("loaded qdl module " + module);
                    if (isFirstQDLModules) {
                        isFirstQDLModules = false;
                        foundModules = module;
                    } else {
                        foundModules = foundModules + "," + module;
                    }
                }
            }// end for loop
            if (!foundClasses.isEmpty()) {
                x[JAVA_MODULE_INDEX] = foundClasses;
            }
            if (!foundModules.isEmpty()) {
                x[QDL_MODULE_INDEX] = foundModules;

            }
        } // end if loop
        return x;
    }

    protected static void setupJavaModule(State state, QDLLoader loader, boolean importASAP) {
        for (Module m : loader.load()) {
            state.addModule(m); // done!
            if (importASAP) {
                state.getImportedModules().addImport(m.getNamespace(), m.getAlias());
            }
        }
    }

    public static String runBootScript(QDLEnvironment config, State state) {
        if (config.hasBootScript()) {
            QDLParser interpreter = new QDLParser(state);
            String bootFile = config.getBootScript();
            try {
                String bootScript = FileUtil.readFileAsString(bootFile);
                interpreter.execute(bootScript);

                config.getMyLogger().info("loaded boot script " + bootFile);
                return bootFile;
            } catch (Throwable t) {
                config.getMyLogger().error("warning: Could not load boot script\"" + bootFile + "\": " + t.getMessage(), t);
            }
        }
        return null;
    }
}
