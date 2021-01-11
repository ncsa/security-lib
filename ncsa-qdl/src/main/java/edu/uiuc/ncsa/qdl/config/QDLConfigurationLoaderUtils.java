package edu.uiuc.ncsa.qdl.config;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.extensions.QDLLoader;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.vfs.*;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.mysql.MySQLConnectionPool;

import java.util.Map;

import static edu.uiuc.ncsa.qdl.config.QDLConfigurationConstants.*;
import static edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider.*;

/**
 * Takes  node from a QDL configuration and sets a bunch of information for a configuration.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  3:52 PM
 */
public class QDLConfigurationLoaderUtils {
    public static void setupVFS(QDLEnvironment config, State state) {
        if (!config.getVFSConfigurations().isEmpty()) {
            VFSFileProvider provider;
            for (VFSConfig vfsConfig : config.getVFSConfigurations()) {
                switch (vfsConfig.getType()) {
                    case VFS_TYPE_PASS_THROUGH:
                        VFSPassThroughConfig vpt = (VFSPassThroughConfig) vfsConfig;
                        provider = new VFSPassThruFileProvider(
                                vpt.getRootDir(), vpt.getScheme(), vpt.getMountPoint(),
                                vpt.canRead(), vpt.canWrite());
                        state.addVFSProvider(provider);
                        config.getMyLogger().info("VFS pass-through mount: " + vfsConfig.getScheme() +
                                VFSPaths.SCHEME_DELIMITER + vfsConfig.getMountPoint());
                        break;
                    case VFS_TYPE_MEMORY:
                        provider = new VFSMemoryFileProvider(
                                vfsConfig.getScheme(), vfsConfig.getMountPoint(), vfsConfig.canRead(), vfsConfig.canWrite());
                        state.addVFSProvider(provider);
                        config.getMyLogger().info("VFS in-memory mount: " + vfsConfig.getScheme() +
                                VFSPaths.SCHEME_DELIMITER + vfsConfig.getMountPoint());
                        break;
                    case VFS_TYPE_MYSQL:
                        provider = setupMySQLVFS(config, (VFSSQLConfig) vfsConfig);
                        state.addVFSProvider(provider);
                        config.getMyLogger().info("VFS MySQL mount: " + vfsConfig.getScheme() +
                                VFSPaths.SCHEME_DELIMITER + vfsConfig.getMountPoint());
                        break;
                    case VFS_TYPE_ZIP:
                        VFSZipFileConfig zc = (VFSZipFileConfig)vfsConfig;
                        provider = new VFSZipFileProvider(zc.getZipFilePath(), zc.getScheme(),zc.getMountPoint(),
                                zc.canRead(), zc.canWrite());
                        state.addVFSProvider(provider);
                        config.getMyLogger().info("VFS zip file mount: " + vfsConfig.getScheme() +
                                VFSPaths.SCHEME_DELIMITER + vfsConfig.getMountPoint());

                        break;
                    default:
                        config.getMyLogger().warn("VFS mount: Unknown VFS type " + vfsConfig.getType() + ", scheme= " + vfsConfig.getScheme() +
                                VFSPaths.SCHEME_DELIMITER + vfsConfig.getMountPoint());

                }

            }
        }
    }
     public static VFSDatabase setupMySQLDatabase(MyLoggingFacade logger, Map<String, String> map){
         int port = 3306; // means use default
         boolean useSSL = false; // also default
         if(!map.containsKey(DRIVER)){
             map.put(DRIVER,"com.mysql.cj.jdbc.Driver" );
         }
         if(!map.containsKey(HOST)){
             map.put(HOST, "localhost");
         }
         if(!map.containsKey(DATABASE)){
             map.put(DATABASE, map.get(SCHEMA));
         }
         try{
             if(map.containsKey(PORT)) {
                 port = Integer.parseInt(map.get(PORT));
             }
         }catch(Throwable t){
             if(logger != null)
             logger.warn("VFS mount: Could not determine port from value " + map.get(PORT));
         }
         try{
             if(map.containsKey(USE_SSL)) {
                 useSSL = Boolean.parseBoolean(map.get(USE_SSL));
             }
         }catch(Throwable t){
             if(logger!=null)
             logger.warn("VFS mount: Could not determine whether or not to use SSL from value " + map.get(USE_SSL));
         }
         MySQLConnectionParameters parameters = new MySQLConnectionParameters(
             map.get(USERNAME),
             map.get(PASSWORD),
             map.get(DATABASE),
             map.get(SCHEMA),
             map.get(HOST),
             port,
             map.get(DRIVER),
             useSSL,
             map.get(PARAMETERS)
         );
         MySQLConnectionPool connectionPool = new MySQLConnectionPool(parameters);

         return new VFSDatabase(connectionPool, map.get(StorageConfigurationTags.SQL_TABLENAME));

     }
    protected static VFSFileProvider setupMySQLVFS(QDLEnvironment config, VFSSQLConfig myCfg) {
        VFSFileProvider provider;
        Map<String,String> map = myCfg.getConnectionParameters();

        VFSDatabase db = setupMySQLDatabase(config.getMyLogger(), map);
        provider = new VFSMySQLProvider(db,
                myCfg.getScheme(),myCfg.mountPoint, myCfg.canRead(),myCfg.canWrite());
        return provider;
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
            QDLInterpreter interpreter = new QDLInterpreter(state);

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

    public static void setupJavaModule(State state, QDLLoader loader, boolean importASAP) {
        for (Module m : loader.load()) {
            m.setTemplate(true);
            state.addModule(m); // done!
            if (importASAP) {
                state.getImportManager().addImport(m.getNamespace(), m.getAlias());
                State state1 = state.newModuleState();
                Module mm = m.newInstance(state1);
                ((JavaModule)mm).init(state1);
                state.getImportedModules().put(m.getAlias(), mm);
            }
        }
    }

    public static String runBootScript(QDLEnvironment config, State state) {
        if (config.hasBootScript()) {
            QDLInterpreter interpreter = new QDLInterpreter(state);
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
