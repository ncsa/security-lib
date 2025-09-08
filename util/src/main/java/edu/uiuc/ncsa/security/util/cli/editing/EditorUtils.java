package edu.uiuc.ncsa.security.util.cli.editing;

import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.*;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/17/21 at  6:47 AM
 */
public class EditorUtils {

    // for the editor configuration
    public static final String EDITORS_TAG = "editors";
    public static final String EDITOR_TAG = "editor";
    public static final String EDITOR_ARG_TAG = "arg";
    public static final String EDITOR_NAME_ATTR = "name";
    public static final String EDITOR_EXEC_ATTR = "exec";
    public static final String EDITOR_CLEAR_SCREEN_ATTR = "clear_screen";
    public static final String EDITOR_ARG_FLAG_ATTR = "flag";
    public static final String EDITOR_ARG_CONNECTOR_ATTR = "connector";
    public static final String EDITOR_ARG_VALUE_ATTR = "value";

    public static final int EDITOR_RC_OK = 0;
    public static final int EDITOR_RC_ERROR = -1;

    /*  Example
        <editors>
             <editor name="nano"
                     exec="/bin/nano"
                     clear_screen="true>
                <arg flag="--rcfile"
                     connector="="
                     value="/path/to/syntaxfile"/>
                <!-- can have several args -->
             </editor>
        </editors>

      */
    public static Editors getEditors(ConfigurationNode cn) {
        Editors editors = new Editors(); // never null
        if (cn == null) {
            return editors;
        }
        ConfigurationNode node = getFirstNode(cn, EDITORS_TAG);
        if (node != null) {
            // Loop through all the editor elements
            for (ConfigurationNode kid : node.getChildren(EDITOR_TAG)) {
                String name = getFirstAttribute(kid, EDITOR_NAME_ATTR);
                if (StringUtils.isTrivial(name)) {
                    continue; // not a valid node.
                }
                EditorEntry qdlEditor = new EditorEntry();
                qdlEditor.name = name;
                qdlEditor.exec = getFirstAttribute(kid, EDITOR_EXEC_ATTR);
                qdlEditor.clearScreen = getFirstBooleanValue(kid, EDITOR_CLEAR_SCREEN_ATTR, false);
                for (ConfigurationNode arg : kid.getChildren(EDITOR_ARG_TAG)) {
                    EditorArg qdlEditorArg = new EditorArg();
                    String flag = getFirstAttribute(arg, EDITOR_ARG_FLAG_ATTR);
                    if (isTrivial(flag)) {
                        continue;
                    }
                    qdlEditorArg.flag = flag;
                    qdlEditorArg.connector = getFirstAttribute(arg, EDITOR_ARG_CONNECTOR_ATTR);
                    qdlEditorArg.value = getFirstAttribute(arg, EDITOR_ARG_VALUE_ATTR);
                    qdlEditor.args.add(qdlEditorArg);
                }
                editors.put(qdlEditor);
            }
        }
        return editors;
    }
    public static Editors getEditors(CFNode cfNode) {
        Editors editors = new Editors(); // never null
        if (cfNode == null) {
            return editors;
        }
        CFNode node = cfNode.getFirstNode( EDITORS_TAG);
        if (node != null) {
            // Loop through all the editor elements
            for (CFNode kid : node.getChildren(EDITOR_TAG)) {
                String name = kid.getFirstAttribute(EDITOR_NAME_ATTR);
                if (StringUtils.isTrivial(name)) {
                    continue; // not a valid node.
                }
                EditorEntry qdlEditor = new EditorEntry();
                qdlEditor.name = name;
                qdlEditor.exec = kid.getFirstAttribute( EDITOR_EXEC_ATTR);
                qdlEditor.clearScreen = kid.getFirstBooleanValue( EDITOR_CLEAR_SCREEN_ATTR, false);
                for (CFNode arg : kid.getChildren(EDITOR_ARG_TAG)) {
                    EditorArg qdlEditorArg = new EditorArg();
                    String flag = arg.getFirstAttribute(EDITOR_ARG_FLAG_ATTR);
                    if (isTrivial(flag)) {
                        continue;
                    }
                    qdlEditorArg.flag = flag;
                    qdlEditorArg.connector = arg.getFirstAttribute( EDITOR_ARG_CONNECTOR_ATTR);
                    qdlEditorArg.value = arg.getFirstAttribute( EDITOR_ARG_VALUE_ATTR);
                    qdlEditor.args.add(qdlEditorArg);
                }
                editors.put(qdlEditor);
            }
        }
        return editors;
    }


    protected static void say(String x) {
        System.out.println(x);
    }

    /**
     * Facility for updating the editors. (Under construction -- issue is that each CLI has very
     * specialized needs, so a general facility just might not work).
     *
     * @param editors
     * @param inputLine
     */
    public static void updateEditors(Editors editors, InputLine inputLine) {
        if (inputLine.hasArg("--help")) {
            say("update the set of active editors.");
            say("-add [name] = add an editor");
            say("-rm name = remove an editor");
            say("-edit name = edit an editor");
            say("-list = list current editors");
            return;
        }
        boolean addEditor = inputLine.hasArg("-add");
        String name;
        if (addEditor) {
            name = inputLine.getNextArgFor("-add");
        }
        inputLine.removeSwitch("-add");
    }

    /**
     * Edits a file. Return code is {@link #EDITOR_RC_OK} if everything worked and
     * {@link #EDITOR_RC_ERROR} if not.
     *
     * @param editorEntry
     * @param targetFile
     * @return
     */
    public static int editFile(EditorEntry editorEntry, File targetFile) {
        List<String> commands = new ArrayList<>();
        commands.add(editorEntry.exec);
        commands.add(targetFile.getAbsolutePath());
        for (EditorArg arg : editorEntry.args) {
            String a = arg.flag;
            a = a + (arg.hasConnector() ? arg.connector : "");
            a = a + (arg.hasValue() ? " " + arg.value : "");
            commands.add(a);
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(commands);
        processBuilder.inheritIO();
        int exitCode = 0;
        List<String> content = null;
        try {
            Process process = processBuilder.start();
            exitCode = process.waitFor();
            if (0 == exitCode) {
                return EDITOR_RC_OK;
            } else {
                return EDITOR_RC_ERROR;
            }
        } catch (Throwable t) {
            if (exitCode == 0) {
                exitCode = EDITOR_RC_ERROR; // trigger message below after any screen clear so the user sees it.
            }

        } finally {
            if (editorEntry.clearScreen) {
                say("\u001b[2J"); // clear screen
                say("\u001b[0;0H"); // cursor at top
            }
        }

        return exitCode;
    }
}
