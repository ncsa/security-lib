package edu.uiuc.ncsa.security.installer;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static edu.uiuc.ncsa.security.installer.WebInstaller.*;


/**
 * A map of the arguments to the installer. It eats the command line string and
 * <p>Created by Jeff Gaynor<br>
 * on 6/14/24 at  10:22 AM
 */
public class ArgMap extends HashMap {
    public ArgMap(String[] args) {
        setupArgMap(args, allOps);
    }

    /**
     * Constructor if you want to add your own operations. Remember that the list of
     * all operations is checked so if the user passes an unkown one, an exception is raised.
     * @param args
     * @param allOperations
     */
    public ArgMap(String[] args, List<String> allOperations) {
        setupArgMap(args, allOperations);
    }
    protected void setupArgMap(String[] args, List<String> allOps) {

        if (args.length == 0
                || args[0].equals(HELP_OPTION)
                || args[0].equals(HELP_FLAG)) {
            // if there are no options or the only one is help, just print help
            put(HELP_OPTION, true);
            return;
        }
        put(
                operationKey, args[0]);
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case HELP_FLAG:
                    put(HELP_OPTION, true);
                    return;
                case DEBUG_FLAG:
                    put(DEBUG_FLAG, true);
                    break;
                case DIR_ARG:
                    if ((i + 1) < args.length) {
                        // if this is the very last argument on the line, skip it
                        if (args[i + 1].startsWith("-")) {
                            throw new IllegalArgumentException("missing directory");
                        }
                        put(DIR_ARG, new File(args[++i]));
                    }
                    break;
                case ALL_FLAG:
                    put(ALL_FLAG, true);
                    break;
                case OA4MP_FLAG:
                    put(OA4MP_FLAG, true);
                    break;
                case VERSION_FLAG:
                    put(VERSION_FLAG, args[i]);
            }
        }
        if (!isShowHelp()) {
            if (!allOps.contains(getOperation())) {
                throw new IllegalArgumentException("unknown operation \"" + getOperation() + "\"");
            }
        }
    }

public String getVersion(){
        if(containsKey(VERSION_FLAG)){
            return getString(VERSION_FLAG);
        }
        return VERSION_LATEST;
}
    // Help functions. These SHOULD be in another class, but that would mean writing
    // a separate classloader for the executable jar -- way too much work

    /**
     * Checks that the key is a boolean
     * @param key
     * @return
     */
    public Boolean is(String key) {
        if (!containsKey(key)) return false;
        return (Boolean) get(key);
    }

    public File getRootDir() {
        if (!containsKey(DIR_ARG)) return null;
        return (File) get(DIR_ARG);
    }
    public String getString(String key){
        if(containsKey(key)){
            return (String)get(key);
        }
        return null;
    }
    public boolean isInstall() {
        return getOperation().equals(INSTALL_OPTION);
    }

    public boolean isRemove() {
        return getOperation().equals(REMOVE_OPTION);
    }

    public boolean isUpgrade() {
        return getOperation().equals(UPDATE_OPTION);
    }

    public boolean isShowHelp() {
        return is(HELP_OPTION) || getOperation().equals(HELP_OPTION);
    }

    public boolean isList() {
        return getOperation().equals(LIST_OPTION);
    }

    public boolean hasRootDir() {
        return getRootDir() != null;
    }

    public String getOperation() {
        return (String) get(operationKey);
    }

    public boolean isAll() {
        return is(ALL_FLAG);
    }


}
