package edu.uiuc.ncsa.security.util.cli.json_edit;

import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.AbstractEditor;
import edu.uiuc.ncsa.security.util.cli.EditorInputLine;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * An editor for JSON objects. Mostly an experiment to see if this is viable for large JSON objects.
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/20 at  10:26 AM
 */
public class JSONEditor extends AbstractEditor {
    // Navigation
    public static final String CHANGE_NODE = "cd";
    public static final String LIST_NODE = "ls";
    public static final String CURRENT_NODE = "pwd";

    // adding and removing values
    public static final String ADD_NODE = "add";
    public static final String DELETE_NODE = "rm";
    public static final String SET_NODE_VALUE = "set";

    public static final String TREE_COMMAND = "tree";

    // File operations
    public static final String FILE_LOAD = "read";
    public static final String FILE_SAVE = "write";
    // return codes
    protected static int RC_CONTINUE = 1;
    protected static int RC_EXIT = -1;
    protected static int RC_OK = 0;
    protected static int RC_NO_OP = 10;


    public JSON getCurrentObject() {
        return currentObject;
    }

    public void setCurrentObject(JSON currentObject) {
        this.currentObject = currentObject;
    }

    JSON currentObject;

    public void execute() throws Throwable {
        say("CLI editor.");
        if (currentObject.isEmpty()) {
            say("Empty JSON Object. Type ? to see help.");
        }
        while (!isDone) {
            String command = readline(PROMPT);
            EditorInputLine eil = null;
            boolean isUnkownCommand = true;
            try {
                eil = new EditorInputLine(command);
            } catch (Throwable t) {
                say("Sorry, there was an error processing that command");
                continue;
            }

            switch (eil.getCommand()) {
                case QUIT_COMMAND:
                    isDone = true;
                    sayv("bye-bye...");
                    break;
                case CHANGE_NODE:
                    _doChangeNode(eil);
                    break;
                case VERBOSE_COMMAND:
                    _doVerbose(eil);
                    break;
                case CURRENT_NODE:
                    _doCurrentNode();
                    break;
                case LIST_NODE:
                    _doListNode(eil);
                    break;
                case ADD_NODE:
                    _doAddNode(eil);
                    break;
                case DELETE_NODE:
                    break;
                case SET_NODE_VALUE:
                    break;
                case HELP_COMMAND:
                    _doHelp(eil);
                    break;
                case TREE_COMMAND:
                    _doTree(eil);
                    break;
                case FILE_LOAD:
                    _doFileLoad(eil);
                    break;
                case FILE_SAVE:
                    _doFileSave(eil);
                    break;
                default:
                    say("unknown command.");
            }

        }
        say("done!");
    }

    protected void showFileSaveHelp(){
        say(FILE_SAVE + " file_path [node_path]");
        sayi("Save a node to the given file. Note that if you do not supply a specific node_path");
        sayi("within the current JSON object, then the entire object will be saved.");
        sayi("Also, this does not always result in JSON. If you select to save a single value, for instance");
        sayi("just that value is saved.");
    }
    protected void writeToFile(String filename, String contents) throws IOException {
        FileWriter fileWriter = new FileWriter(new File(filename));
            fileWriter.write(contents);
            fileWriter.flush();
            fileWriter.close();
    }
    protected int _doFileSave(EditorInputLine eil) {
        if(showHelp(eil)){
            showFileSaveHelp();
            return RC_OK;
        }
        JSON x = currentObject;
        if(eil.getArgCount() == 0){
            say("sorry, but I need at least a path to the file to save something.");
            return RC_CONTINUE;
        }
        String filePath = eil.getArg(1);

         if(1 <= eil.getArgCount()){
              String path = eil.getArg(2);
              x = navigate(JSONPaths.getComponents(path));
         }

        try {
            writeToFile(filePath, x.toString(1));
        } catch (IOException e) {
            say("sorry, but there was a problem writing to \"" + filePath + "\":" + e.getMessage());
        }
        return RC_OK;
    }
    // read /home/ncsa/Desktop/test-cfg.json
   // write /tmp/lines.txt /claims/preProcessing/script
    private int _doFileLoad(EditorInputLine eil) {
        if (showHelp(eil)) {
            showFileLoadHelp();
            return RC_OK;
        }
        if (eil.getArgCount() == 0) {
            say("sorry, you must supply at least a file path");
            return RC_CONTINUE;

        }
        String filePath = eil.getArg(1);

        try {
            currentObject  = fileToJSON(filePath);

        } catch (IOException e) {
            say("sorry, but I could not read \"" + filePath + "\"");
        }


        return RC_OK;
    }
     protected void insertNode(JSONObject json, JSON node, String path){
          String[] paths = JSONPaths.getComponents(path);
          JSON currentJ = json;
    /*      for(int i = 0; i < paths.length - 1; i++){
              currentJ = 
          }*/
     }
    private void showFileLoadHelp() {
        say(FILE_LOAD + " file_path [node_path] ");
        sayi("Loads a file. If there is no node_path, then the file is loaded and becomes the new");
        sayi("JSON object for the editor. If the node_path is given, the file contents are inserted at the given point");
        say("Examples.");
        sayi(FILE_LOAD + " /home/me/ldap-cfg.json /cfg/network/ldap");
        sayi("Loads the contents of the file ldap-cfg.json at the given node. NOTE how this works.");
        sayi("The resulting JSON would be");
        sayi("{");
        sayi(" \"cfg\":{");
        sayi("    \"network\":{");
        sayi("         \"ldap\":{");
        sayi("             <file_contents>");
        sayi("          }");
        sayi("       }");
        sayi("    }");
        sayi("}");
        say("See also:" + FILE_SAVE);
    }

    protected String TREE_PIPE = "| ";
    protected String TREE_PLUS = "+-";

    protected JSON fileToJSON(String filename) throws IOException {
        String raw = fileToString(filename);
        if (StringUtils.isTrivial(raw)) {
            throw new FileNotFoundException("The file \"" + filename + "\" was empty.");
        }
        JSON json = null;
        boolean jsonok = false;
        try {
            json = JSONObject.fromObject(raw);
            jsonok = true;
        } catch (Exception e) {

        }
        if (!jsonok) {
            try {
                json = JSONArray.fromObject(raw);
            } catch (Exception e) {
                say("sorry, but that file does not seem to be valid JSON:" + e.getMessage());
            }
        }
        return json;
    }

    protected String fileToString(String fileName) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        Path path = Paths.get(fileName);

        List<String> contents = Files.readAllLines(path);
        int i = 0;
        //Read from the stream
        for (String content : contents) {
            stringBuffer.append(content + "\n");
        }

        return stringBuffer.toString();
    }

    private int _doTree(EditorInputLine eil) {
        if (showHelp(eil)) {
            showTreeHelp();
            return RC_OK;
        }
        JSON json = currentObject;
        String filename = null;
        if (eil.hasArg(FILE_FLAG)) {
            filename = eil.getNextArgFor(FILE_FLAG);
            try {
                JSON x = fileToJSON(filename);
                if (x == null) {
                    say("sorry, but the file \"" + filename + " does not seem to be valid JSON");
                    return RC_CONTINUE;
                }
                json = x;
            }catch(Exception x){
                say("sorry, but the file \"" + filename + " does not seem to be valid JSON");
             return RC_CONTINUE;
            }
        }
        ///home/ncsa/Desktop/test-cfg.json
        if (json.isArray()) {
            printArrayTree("", "/", (JSONArray) json);
        } else {
            printObjectTree("", "/", (JSONObject) json);
        }

        return RC_OK;
    }

    protected void printObjectTree(String indent, String nodeName, JSONObject jsonObject) {
        say(indent + TREE_PLUS + nodeName + "(" + jsonObject.size() + ")");
        String newIndent = indent + TREE_PIPE;
        for (Object key : jsonObject.keySet()) {
            Object object = jsonObject.get(key);
            if (object instanceof JSONObject) {
                printObjectTree(newIndent, key.toString(), (JSONObject) object);
            }
            if (object instanceof JSONArray) {
                printArrayTree(newIndent, key.toString(), (JSONArray) object);
            }
        }

    }

    protected void printArrayTree(String indent, String nodeName, JSONArray array) {
        say(indent + TREE_PLUS + nodeName + "[" + array.size() + "]");
        String newIndent = indent + TREE_PIPE;

        for (int i = 0; i < array.size(); i++) {
            Object object = array.get(i);
            if (object instanceof JSONObject) {
                printObjectTree(newIndent, Integer.toString(i), (JSONObject) object);
            }
            if (object instanceof JSONArray) {
                printArrayTree(newIndent, Integer.toString(i), (JSONArray) object);
            }
        }

    }

    public static final String FILE_FLAG = "-file";
    private void showTreeHelp() {
        say(TREE_COMMAND + " [path | " + FILE_FLAG + " fs_path]");
        sayi("Show the tree structure for a node. This consists of all embedded  objects, not simple values.");
        sayi("You may supply a path in the current object to start from.");
        sayi("Alternately, you may specify a file with a fully qualified path.");
        say("Example. Here is JSON object");
        say("{");
        say(" \"a\": 1,");
        say(" \"b\": {\"q\": \"xyz\"},");
        say(" \"array\":  [");
        say("  1,");
        say("  true,");
        say("  \"foo\"");
        say(" ]");
        say("}");
        say("Issuing the tree command against this yields the nicely compact");
        say("+-/(3)");
        say("| +-b(1)");
        say("| +-array[3]");
        sayi("Objects have the number of elements in () and arrays have them in [] (this is how you tell them apart.)");
        sayi("In this case, the root object has 3 entries, 2 od which are JSON (the 3rd is a simple value of some sort).");
        sayi("There is a node with key \"b\" and a single element. The array has 3 elements, all of which are simple.");
    }

    private void _doHelp(EditorInputLine eil) {
        say("The JSON object editor. This allows you to edit a JSON object and navigate it like");
        say("It has a directory structure. Supported commands are:");
        int length = 6;
        sayi(StringUtils.RJustify(CHANGE_NODE, length) + " - change the location in the object");
    }


    protected void addNodeHelp() {
        say(ADD_NODE + " path " + VALUE_FLAG + " value [" + TYPE_FLAG + " type]");
        sayi("add a new node with the given value. The assumed type is a string unless otherwise given.");
        sayi("This will fail if the node already exists. ");
        sayi("Note that you can add empty objects and nodes and populate them, but cannot set the value directly.");
        printTypes();
        say("See also " + SET_NODE_VALUE + ", " + DELETE_NODE);
    }

    protected static String TYPE_FLAG = "-type";
    protected static String VALUE_FLAG = "-value";


    protected int _doAddNode(EditorInputLine eil) {
        if (showHelp(eil)) {
            addNodeHelp();
            return RC_OK;
        }
        if (!eil.hasArg(VALUE_FLAG)) {
            say("sorry. You must specify a value when adding a node.");
            return RC_OK;
        }
        String type = FORMAT_TYPE_STRING; //default
        if (eil.hasArg(TYPE_FLAG)) {
            type = eil.getNextArgFor(TYPE_FLAG);
            eil.removeSwitchAndValue(TYPE_FLAG);
        }
        String[] path = JSONPaths.getComponents(eil.getArg(1));
        JSON j = navigate(path);
        if (j == null) {
            say("invalid path");
            return RC_OK;
        }
        // have to check that the last element does not exist.
        String lastComponent = path[path.length - 1];
        if (!eil.hasArg(VALUE_FLAG)) {
            say("sorry, but you must specify a switch with the " + VALUE_FLAG + " switch");
        }
        //      eil.removeSwitch(VALUE_FLAG);
        // add /array/3 -value "{\"zz\":\"cc\"} -type J
        String value = eil.getNextArgFor(VALUE_FLAG);

        if (j.isArray()) {
            JSONArray array = (JSONArray) j;
            int index = Integer.parseInt(lastComponent);
            if (array.size() != index) {
                say("sorry, but the index of " + index + " cannot be added to an array of size " + array.size());
                return RC_OK;
            }
            array.add(convertByType(type, value));
            return RC_OK;
        } else {
            JSONObject jsonObject = (JSONObject) j;
            if (jsonObject.containsKey(lastComponent)) {
                say("sorry, but there is already an element named " + lastComponent);
                return RC_CONTINUE;
            }
            jsonObject.put(lastComponent, convertByType(type, value));
            return RC_OK;
        }
    }

    protected Object convertByType(String type, String rawValue) {
        switch (type) {
            case FORMAT_TYPE_BOOLEAN:
                return Boolean.parseBoolean(rawValue);

            case FORMAT_TYPE_JSON_ARRAY:
                //return JSONArray.fromObject(rawValue);
                return new JSONArray();
            case FORMAT_TYPE_NUMBER:
                // Two cases.
                if (rawValue.indexOf(".") == -1) {
                    // its an integer
                    return Long.parseLong(rawValue);
                } else {

                }
                return Double.parseDouble(rawValue);
            default:
            case FORMAT_TYPE_STRING:
                return rawValue;
            case FORMAT_TYPE_JSON_OBJECT:
                //return JSONObject.fromObject(rawValue);
                return new JSONObject();

        }

    }

    protected void listNodeHelp() {
        say(LIST_NODE + " [path] [" + PRINT_RAW_FLAG + "]");
        sayi("no arg -- lists current location.");
        sayi("list the absolute path or if the path is relative, resolves against the current location");
        say("Examples");
        sayi("ls "); 
        sayi("   Lists the currently active location");
        sayi("ls foo/bar");
        sayi("   Resolves the realtive path foo/bar against the current location");
        sayi("ls /x/y/0/z");
        sayi("   Ignores currently active location and lists exactly the given absolute path.");
        sayi("If you include the " + PRINT_RAW_FLAG + ", this will only print the values without the");
        sayi("information such as keys or type. Useful if you want to, e.g., output something for cut and paste.");
        say("See also:" + CHANGE_NODE);
    }

    public static final String PRINT_RAW_FLAG = "-raw";
    /**
     * Navigates to the next to last component for a full path. If this returns a null, then there was no such path
     *
     * @param path
     * @return
     */
    protected JSON navigate(String[] path) {
        JSON j = currentObject;
        for (int i = 0; i < path.length - 1; i++) {
            if ((j).isArray()) {
                j = ((JSONArray) j).getJSONObject(Integer.parseInt(path[i]));
            } else {
                j = (JSON) ((JSONObject) j).get(path[i]);
            }
            if (j == null) return null; // so component not found.
        }
        return j;
    }

    protected int _doListNode(EditorInputLine eil) {
        if (showHelp(eil)) {
            // show some help
            listNodeHelp();
            return RC_OK;
        }
        boolean rawPrint = eil.hasArg(PRINT_RAW_FLAG);
        String[] path;
        String thisPath;
        List<formatRecord> formatRecords = new ArrayList<>();
        boolean isScalar = false;
        if (eil.size() == 1) {  // size is includes command, so no args means length 1.
            thisPath = "";
            if (currentPath == null) {
                currentPath = JSONPaths.PATH_SEPARATOR; // set it to root
            }
        } else {
            thisPath = eil.getArg(1);
        }


        if (JSONPaths.isAbsolute(thisPath)) {
            path = JSONPaths.getComponents(thisPath);
        } else {
            path = JSONPaths.getComponents(JSONPaths.resolve(currentPath, thisPath));
        }
        JSON j;
        try {
             j = navigate(path);
        }catch(Throwable t){
            say("invalid path:" + t.getMessage());
            return RC_CONTINUE;
        }
        if (j == null) {
            say("invalid path");
            return RC_OK;
        }
        // only last entry of path requires some thought.

        if ((j).isArray()) {
            int index = Integer.parseInt(path[path.length - 1]);
            JSONArray jsonArray = (JSONArray) j;
            if (jsonArray.size() < index) {
                say("invalid index");
                return RC_CONTINUE;
            }
            Object object = ((JSONArray) j).get(index);
            if (object == null) {
                say("sorry, no such entry");
                return RC_CONTINUE;
            }
            if (!(object instanceof JSON)) {
                say(formatScalar(object));
                return RC_CONTINUE;
            }
            // so this is either an array or an object.
            j = ((JSONArray) j).getJSONObject(Integer.parseInt(path[path.length - 1]));

        } else {
            // Might not be a JSON Object but a simple value.
            Object obj = null;
            if (path.length == 0) {
                obj = currentObject; // path is / so they want the entire top level.
            } else {
                obj = ((JSONObject) j).get(path[path.length - 1]);
            }
            if (obj == null) {
                say("invalid path -- no such entry");
                return RC_CONTINUE;
            }
            if (obj instanceof JSON) {
                // list contents
            } else {
                say(formatScalar(obj));
                return RC_CONTINUE;
            }

            j = (JSON) obj;
        }

        // last component is now JSON of some sort. Print the contents

        formatRecord fr;

        if (j.isArray()) {
            JSONArray jsonArray = (JSONArray) j;
            for (int i = 0; i < j.size(); i++) {
                fr = new formatRecord(Integer.toString(i), (jsonArray.get(i)));
                formatRecords.add(fr);
            }

        } else {
            JSONObject jsonObject = (JSONObject) j;
            for (Object key : jsonObject.keySet()) {
                fr = new formatRecord(key.toString(), jsonObject.get(key));
                formatRecords.add(fr);
            }
        }

        printRecords(formatRecords, rawPrint);
        return RC_OK;
    }

    protected void _doCurrentNode() {
        if (currentPath == null) {
            say("(null)");
        } else {
            say(currentPath);
        }
    }

    protected int _doVerbose(EditorInputLine eil) {
        if (showHelp(eil)) {
            verboseHelp();
            return RC_OK;
        }
        verboseOn = !verboseOn;
        say("verbose mode is now " + (verboseOn ? "ON" : "OFF"));
        return RC_OK;

    }

    protected void _doChangeNode(EditorInputLine eil) {
        if (1 <= eil.size()) {
            // Only do something if there is an arg.
            String newPath = eil.getArg(1);
            if (currentPath == null) {
                currentPath = JSONPaths.PATH_SEPARATOR;
            }
            if (JSONPaths.isAbsolute(newPath)) {
                currentPath = newPath;
            } else {
                currentPath = JSONPaths.resolve(currentPath, newPath);
            }
            currentPath = JSONPaths.normalize(currentPath);  // Tidy it up in case they typed in extra stuff
        }
    }

    String currentPath = null;

    public static void main(String[] args) {
        try {
            JSONEditor jsonEditor = new JSONEditor();
            JSONObject jsonObject = JSONObject.fromObject("{\"a\":1,\"b\":{\"q\":\"xyz\"}, \"array\":[1,true,\"foo\"]}");
            System.out.print(jsonObject.toString(1));
            jsonEditor.setCurrentObject(jsonObject);
            jsonEditor.execute();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static final String FORMAT_TYPE_JSON_OBJECT = "J";
    public static final String FORMAT_TYPE_JSON_ARRAY = "A";
    public static final String FORMAT_TYPE_BOOLEAN = "B";
    public static final String FORMAT_TYPE_NUMBER = "N";
    public static final String FORMAT_TYPE_STRING = "S";
    public static final String FORMAT_TYPE_NULL = "-";
    public static final String FORMAT_TYPE_UNKNOWN = "?";

    protected void printTypes() {
        sayi("Supported types are");
        sayi(FORMAT_TYPE_JSON_OBJECT + " = A JSON Object");
        sayi(FORMAT_TYPE_JSON_ARRAY + " = A JSON array");
        sayi(FORMAT_TYPE_BOOLEAN + " = A simple boolean (true/false)");
        sayi(FORMAT_TYPE_NUMBER + " = A number. This may be an integer or decimal.");
        sayi(FORMAT_TYPE_STRING + " = A string.");
        sayi(FORMAT_TYPE_UNKNOWN + " = Unknown type.");
    }

    /**
     * Entries look like
     * <pre>
     *     key | type | entry
     * </pre>
     * types are
     * <pre>
     *      + = JSON object
     *      * = array
     *      b = boolean
     *      n = number
     *      s = string
     *  </pre>
     *
     * @param obj
     * @return
     */
    protected String formatSingleEntry(String key, Object obj) {
        String rc = "";
        String type = getEntryType(obj);
        rc = key + " | " + type + " | ";
        if (obj instanceof JSON) {
            rc = rc + "<" + ((JSON) obj).size() + ">";
        } else {
            rc = rc + obj;
        }
        return rc;
    }

    protected String formatScalar(Object object) {
        return getEntryType(object) + " | " + object.toString();
    }

    protected void printRecords(List<formatRecord> formatRecordList, boolean rawOnly) {
        int displayWidth = 120;
        int maxWidth = 0;
        for (formatRecord fr : formatRecordList) {
            maxWidth = Math.max(maxWidth, fr.key.length());
        }
        // now we can format this reasonably.
        for (formatRecord fr : formatRecordList) {
            if(rawOnly){
                say( fr.value==null?"(null)":fr.value.toString());
            }else{
                say(formatSingleEntry(StringUtils.RJustify(fr.key, maxWidth), fr.value));
            }
        }

    }

    protected String getEntryType(Object obj) {
        if (obj == null) return FORMAT_TYPE_NULL;
        if (obj instanceof JSONArray) return FORMAT_TYPE_JSON_ARRAY;
        if (obj instanceof JSONObject) return FORMAT_TYPE_JSON_OBJECT;
        if (obj instanceof Boolean) return FORMAT_TYPE_BOOLEAN;
        if (obj instanceof String) return FORMAT_TYPE_STRING;
        if (obj instanceof Integer ||
                obj instanceof Double ||
                obj instanceof Long ||
                obj instanceof Float
        ) return FORMAT_TYPE_NUMBER;
        return FORMAT_TYPE_UNKNOWN;
    }

    public static class formatRecord {
        public formatRecord(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        String key;
        Object value;

    }
}
