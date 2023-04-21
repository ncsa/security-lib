package edu.uiuc.ncsa.sas.webclient;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.thing.action.*;
import edu.uiuc.ncsa.sas.thing.response.LogonResponse;
import edu.uiuc.ncsa.sas.thing.response.NewKeyResponse;
import edu.uiuc.ncsa.sas.thing.response.OutputResponse;
import edu.uiuc.ncsa.sas.thing.response.Response;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.servlet.ServiceClient;
import edu.uiuc.ncsa.security.storage.XMLMap;
import edu.uiuc.ncsa.security.util.cli.ExitException;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import edu.uiuc.ncsa.security.util.crypto.DecryptUtils;
import edu.uiuc.ncsa.security.util.crypto.KeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKey;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil;
import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/18/22 at  3:14 PM
 */
public class Client extends ServiceClient implements SASConstants {

    public ResponseDeserializer getResponseDeserializer() {
        return responseDeserializer;
    }

    public void setResponseDeserializer(ResponseDeserializer responseDeserializer) {
        this.responseDeserializer = responseDeserializer;
    }

    ResponseDeserializer responseDeserializer = new ResponseDeserializer();

    public Client(URI address, SSLConfiguration sslConfiguration) {
        super(address, sslConfiguration);
    }

    public Client(URI address) {
        super(address);
    }

    public static final String FLAG_EDIT = "-edit";
    public static final String FLAG_CONFIG = "-cfg"; // name of config file
    public static final String FLAG_NEW = "-new"; // create a new configuration file
    public static final String FLAG_HELP = "--help"; // help
    public static final String FLAG_VERBOSE = "-v"; // help

    public static final String CONFIG_CLIENT_ID = "client_id";
    public static final String CONFIG_PRIVATE_KEY = "private_key";
    public static final String CONFIG_TR_FILE = "trust_root_path";
    public static final String CONFIG_TR_PASSWORD = "trust_root_password";
    public static final String CONFIG_TR_TYPE = "trust_root_type";
    public static final String CONFIG_TR_DN = "trust_root_dn";
    public static final String CONFIG_HOST = "host";

    public static void say(Object x) {
        System.out.println(x);
    }

    /**
     * Read a single line
     *
     * @return
     * @throws IOException
     */
    public static String readline() throws IOException {
        return getBufferedReader().readLine();
    }

    public static String getInput(String prompt) {
        System.out.print(prompt + ">");
        try {
            return getBufferedReader().readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    static BufferedReader bufferedReader = null;

    public static BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }

    protected static XMLMap readConfig(String fileName) throws IOException {
        XMLMap map = new XMLMap();
        map.fromXML(new FileInputStream(new File(fileName)));
        return map;
    }


    /*
         -cfg path.properties is config
         -type is type.
         -new
     */

    /**
     * Create a new configuration file on the fly
     *
     * @param filename file to create (if null, user is prompted)
     * @return filename, either original or the one the user gave.
     * @throws IOException
     */
    public static String createConfig(String filename, boolean isNew) throws IOException {
        if (StringUtils.isTrivial(filename)) {
            if (!isNew) {
                say("no file to edit.");
                return null;
            }
            filename = getInput("Enter the fully qualified file you wish to create:");
        }
        File f = new File(filename);

        XMLMap xmlMap = new XMLMap();
        if (f.exists()) {
            if (!f.canWrite()) {
                say("Sorry, but you do not have permissions to write to \"" + f.getAbsolutePath() + "\"");
                return null;
            }
            xmlMap.fromXML(new FileInputStream(f));
        }
        if (xmlMap.isEmpty()) {
            newConfigFile(xmlMap);
        } else {
            editConfigFile(xmlMap);
        }


        if (f.exists()) {
            if (!"y".equals(getInput("overwrite " + f.getAbsolutePath() + "?"))) {
                say("save aborted. exiting...");
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        xmlMap.toXML(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        say("done!");
        return filename; // pass back what was created.
    }

    protected static void newConfigFile(XMLMap xmlMap) throws IOException {
        xmlMap.put(CONFIG_CLIENT_ID, getInput("enter client id"));
        say("enter private key, PKCS 8 or a single JWK");
        String x = multiLineInput("", CONFIG_PRIVATE_KEY);
        xmlMap.put(CONFIG_PRIVATE_KEY, x.trim()); // If a JWK, any leading blanks will cause JSON library to fail.
        xmlMap.put(CONFIG_HOST, getInput("enter host address"));

        if ("y".equals(getInput("enter ssl configuration (y/n)?"))) {
            xmlMap.put(CONFIG_TR_FILE, getInput("enter path to trust root file"));
            xmlMap.put(CONFIG_TR_PASSWORD, getInput("enter trust root password"));
            xmlMap.put(CONFIG_TR_TYPE, getInput("enter trust root store type, e.g. JKS"));
            xmlMap.put(CONFIG_TR_DN, getInput("enter trust root store cert DN"));
        }
    }

    protected static void editConfigFile(XMLMap xmlMap) throws IOException {
        updateItem(xmlMap, CONFIG_CLIENT_ID);
        updateItem(xmlMap, CONFIG_HOST);

        String oldJSON = "";
        if (xmlMap.containsKey(CONFIG_PRIVATE_KEY)) {
            if ("y".equals(getInput("Update private key(y/n/)?"))) {
                oldJSON = xmlMap.getString(CONFIG_PRIVATE_KEY);
            }
        } else {
            if ("y".equals(getInput("Enter private key(y/n/)?"))) {
                say("enter private key, PKCS 8 or a single JWK");
                String x = multiLineInput(oldJSON, CONFIG_PRIVATE_KEY);
                xmlMap.put(CONFIG_PRIVATE_KEY, x.trim()); // If a JWK, any leading blanks will cause JSON library to fail.
            }
        }
        updateItem(xmlMap, CONFIG_TR_FILE);
        updateItem(xmlMap, CONFIG_TR_PASSWORD);
        updateItem(xmlMap, CONFIG_TR_TYPE);
        updateItem(xmlMap, CONFIG_TR_DN);
    }

    protected static void updateItem(XMLMap xmlMap, String key) {
        String oldValue = xmlMap.getString(key);
        String prompt = key + " ";
        if (oldValue != null) {
            prompt = prompt + "\"" + oldValue + "\"";

        }
        prompt = prompt + ":";
        String resp = getInput(prompt);
        if (StringUtils.isTrivial(resp)) {
            // keep old value
        } else {
            xmlMap.put(key, resp);
        }
    }

    /**
     * Create a new instance of this client. The arguments are
     * <ul>
     *     <li>-cfg - path to the config file</li>
     *     <li>-v - verbose. Be yacky (mostly for debugging the client itself</li>
     *     <li>-edit - create a new config file if none exists. </li>
     * </ul>
     *
     * @param inputLine
     * @return
     * @throws Throwable
     */
    public static Client newInstance(InputLine inputLine) throws Throwable {
        boolean isVerbose = inputLine.hasArg(FLAG_VERBOSE);
       /* if (inputLine.hasArg(FLAG_EDIT)) {
            if (inputLine.hasArg(FLAG_CONFIG)) {
                File f = new File(inputLine.getNextArgFor(FLAG_CONFIG));
                if (!f.exists()) {
                    if ("y".equals(getInput("Create a new configuration?"))) {
                        createConfig(f.getAbsolutePath());
                        return null;
                    }
                }
            }

        }*/
        if (!inputLine.hasArg(FLAG_CONFIG)) {
            say("No configuration. exiting...");
        }


        XMLMap config;
        if (inputLine.hasArg(FLAG_CONFIG)) {
            try {
                config = readConfig(inputLine.getNextArgFor(FLAG_CONFIG));
            } catch (Throwable t) {
                say("could not read config:");
                if (isVerbose) {
                    t.printStackTrace();
                }
                return null;
            }
        } else {
            say("no configuration");
            return null;
        }


        SSLConfiguration sslConfiguration = new SSLConfiguration();
        sslConfiguration.setTrustRootPath(config.getString(CONFIG_TR_FILE));
        sslConfiguration.setTrustRootPassword(config.getString(CONFIG_TR_PASSWORD));
        sslConfiguration.setTrustRootType(config.getString(CONFIG_TR_TYPE));
        sslConfiguration.setTrustRootCertDN(config.getString(CONFIG_TR_DN));
        sslConfiguration.setUseDefaultTrustManager(false);

        Client client = new Client(URI.create(config.getString(CONFIG_HOST)), sslConfiguration);
        client.setConfig(config);
        String rawKey = config.getString(CONFIG_PRIVATE_KEY);
        PrivateKey privateKey = null;
        try {
            privateKey = KeyUtil.fromPKCS1PEM(rawKey);
        } catch (Throwable t) {
            JSONWebKey jwk = JSONWebKeyUtil.getJsonWebKey(rawKey);
            if (jwk != null) {
                privateKey = jwk.privateKey;
            }
        }
        if (privateKey == null) {
            say("Sorry: Could not determine private key. aborting...");
            return null;
        }
        client.setPrivateKey(privateKey);
        return client;
    }

    public static void main(String[] args) throws Throwable {
        Vector<String> vector = new Vector<>();
        vector.add("dummy"); // Dummy zero-th arg.
        for (String arg : args) {
            vector.add(arg);
        }
        InputLine inputLine = new InputLine(vector); // now we have a bunch of utilities for this
        if (inputLine.hasArg(FLAG_HELP)) {
            showHelp();
            return;
        }
        boolean newOrEdit = false;
        String filename = null;
        if (inputLine.hasArg(FLAG_EDIT)) {
            newOrEdit = true;
            if (inputLine.hasNextArgFor(FLAG_EDIT)) {
                filename = inputLine.getNextArgFor(FLAG_EDIT);
            } else {

            }
            if (filename == null || filename.startsWith("-")) {
                say("you  must supply a file name to edit it.");
                return;
            }
            createConfig(filename, false);
        }
        if (inputLine.hasArg(FLAG_NEW)) {
            if (!newOrEdit) {
                newOrEdit = true;
                if (inputLine.hasNextArgFor(FLAG_NEW)) {
                    filename = inputLine.getNextArgFor(FLAG_NEW);
                    if (filename.startsWith("-")) {
                        filename = null; // not a file name, discard
                    }
                }
                filename = createConfig(filename, true);
            }
        }

        if (newOrEdit) {
            if (filename == null) {
                return;
            }
            if (getInput("Did you want to run this now?(y/n)").equals("n")) {
                return;
            }
            // so they want to run this.
            inputLine = new InputLine("dummy", FLAG_CONFIG, filename);
        }

        Client client = newInstance(inputLine); // get the configuration, confiure the client
        client.cli(); // start it.
    }

    public XMLMap getConfig() {
        return config;
    }

    public void setConfig(XMLMap config) {
        this.config = config;
    }

    XMLMap config;

    /**
     * A <it>very, very</it> simple command line. This is normally not used for
     * anything except testing. When this class has main called, this is what you get.
     *
     * @throws Throwable
     */
    protected void cli() throws Throwable {
        boolean doIt = true;
        while (doIt) {
            String lineIn = getInput("sas");
            OutputResponse outputResponse = null;
            switch (lineIn) {
                case "/q":
                    return;
                case "/logon":
                    doLogon(BasicIdentifier.newID(getConfig().getString(CONFIG_CLIENT_ID)));
                    say("login complete");
                    break;
                case "/logoff":
                    doLogoff();
                    say("logged off..");
                    break;
                case "/help":
                case "--help":
                    say("Testing CLI. Commands are");
                    say("/q - quit");
                    say("/logon - logon");
                    say("/logoff - logoff");
                    say("/help or --help - this message");
                    say("(text) - calls execute, passes text");
                    say("/invoke function arg0 arg1 ... - invokes function and passes space separated arguments as string");
                    say("/invoke function json_array ... - invokes function and passes the arguments as a JSON array");
                    break;
                case "/new_key":
                    doNewKey(1024);
                    break;
                default:

                    if (lineIn.startsWith("/invoke ")) {
                        lineIn = lineIn.substring(8);
                        // ok, surgery. See if this ends with a JSON array
                        String name = lineIn.substring(0, lineIn.indexOf(" "));
                        String tail = lineIn.substring(1 + lineIn.indexOf(" "));
                        try {
                            JSONArray array = JSONArray.fromObject(tail.trim());
                            outputResponse = (OutputResponse) doInvoke(name, array); // second half parsed as an array.
                        } catch (Throwable t) {
                            outputResponse = (OutputResponse) doInvoke(lineIn); // Just a bunch of strings.
                        }
                    } else {
                        if (lineIn.startsWith("/new_key")) {
                            lineIn = lineIn.substring(8);
                            int keySize = 1024;
                            try {
                                keySize = Integer.parseInt(lineIn);

                            } catch (Throwable t) {
                                say("Could not parse \"" + lineIn + "\" as an integer. Getting key with size " + keySize);
                            }
                            doNewKey(keySize);
                            say("got new key");
                        } else {
                            outputResponse = (OutputResponse) doExecute(lineIn);
                        }
                    }
                    break;
            }
            if (outputResponse != null) {
                say(outputResponse.getContent());
            }
        }
    }

    public Response doNewKey(int keySize) throws Throwable {
        NewKeyAction newKeyAction = new NewKeyAction(keySize);
        NewKeyResponse response = (NewKeyResponse) doPost(newKeyAction);
        sKey = response.getKey();
        return response;
    }

    private static void showHelp() {
        say(Client.class.getName() + " " + FLAG_CONFIG + " config_file {" + FLAG_EDIT + "} {" + FLAG_HELP + "} {" + FLAG_VERBOSE + "}");
        say(FLAG_CONFIG + " the name of an existing configuration file ");
        say(FLAG_EDIT + " edit existing file or update current ");
        say(FLAG_HELP + " display this help message ");
        say(FLAG_VERBOSE + " print more output about functioning of this. Mostly for debugging.");
        say("If you simply supply the " + FLAG_EDIT + " flag, you will be prompted to create a new configuration file.");
    }

    UUID sessionID;

    public Response doLogon() throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, BadPaddingException, InvalidKeyException {
        return doLogon(BasicIdentifier.newID(getConfig().getString(CONFIG_CLIENT_ID)));
    }

    boolean loggedOn = false;

    public Response doLogon(Identifier identifier) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, BadPaddingException, InvalidKeyException {
        if (loggedOn) {
            return null;
        }
        LogonAction logonAction = new LogonAction();
        JSONObject top = new JSONObject();
        top.put(KEYS_SAS, logonAction.serialize());
        String raw = doPost(RSAEncrypt(top.toString()), identifier.toString(), "");
        String jj = RSADecrypt(raw);
        List<Response> responseList = responseDeserializer.deserialize(jj);
        LogonResponse response = (LogonResponse) responseList.get(0);
        sessionID = response.getSessionID();
        sKey = response.getsKey();
        loggedOn = true;
        return response;
    }

    public Response doLogoff() throws Throwable {
        LogoffAction logoffAction = new LogoffAction();
        loggedOn = false; // need better logic here --test logoff response for status etc.
        return doPost(logoffAction);
    }

    public Response doExecute(String contents) throws Throwable {
        ExecuteAction executeAction = new ExecuteAction();
        executeAction.setArg(contents);
        return execute(executeAction);
    }

    /**
     * Call this for an arbitrary {@link Action}. It will call the SAS  and return the
     * response.
     *
     * @param action
     * @return
     * @throws Throwable
     */
    public Response execute(Action action) throws Throwable {
        return doPost(action);
    }

    public Response doInvoke(String name, JSONArray args) throws Throwable {
        InvokeAction invokeAction = new InvokeAction();
        invokeAction.setName(name);
        invokeAction.setArgs(args);
        return execute(invokeAction);

    }

    public Response doInvoke(String x) throws Throwable {
        StringTokenizer stringTokenizer = new StringTokenizer(x);
        String name = null;
        JSONArray args = new JSONArray();
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            if (0 == i++) {
                name = stringTokenizer.nextToken();
            }
            args.add(stringTokenizer.nextToken());
        }
        return doInvoke(name, args);
    }

    protected String sEncrypt(String contents) {
        return DecryptUtils.sEncrypt(getsKey(), contents);
    }

    protected String sDecrypt(String content64) {
        return DecryptUtils.sDecrypt(getsKey(), content64);
    }

    protected String RSAEncrypt(String contents) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return DecryptUtils.encryptPrivate(getPrivateKey(), contents);
    }

    protected String RSADecrypt(String content64) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return DecryptUtils.decryptPrivate(getPrivateKey(), content64);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    PrivateKey privateKey;

    /**
     * The symmetric key.
     *
     * @return
     */
    public byte[] getsKey() {
        return sKey;
    }

    public void setsKey(byte[] sKey) {
        this.sKey = sKey;
    }

    byte[] sKey;

    public String doPost(String content, String id, String secret) {
        HttpPost post = new HttpPost(host().toString());

        try {
            post.setEntity(new StringEntity(content));
        } catch (UnsupportedEncodingException e) {
            throw new GeneralException("error encoding form \"" + e.getMessage() + "\"", e);
        }
        return doRequest(post, id, secret);
    }

    public String doPost(String contents, boolean rsaEncrypt) throws Throwable {
        HttpPost post = new HttpPost(host().toString());
        if (rsaEncrypt) {
            post.setEntity(new StringEntity(RSAEncrypt(contents)));
        } else {
            post.setEntity(new StringEntity(sEncrypt(contents)));
        }
        post.setHeader(HEADER_SESSION_ID, sessionID.toString());
        return doRequest(post);
    }

    /**
     * Wraps the action in and does the post. It does symmetric key decryption. Assumption is a single response
     *
     * @param action
     * @return
     * @throws Throwable
     */
    public Response doPost(Action action) throws Throwable {
        return doPost(action, false);
    }

    public Response doPost(Action action, boolean rsaEncrypt) throws Throwable {
        JSONObject top = new JSONObject();
        top.put(KEYS_SAS, action.serialize());
        String raw = doPost(top.toString(), rsaEncrypt);
        String jj;
        if (rsaEncrypt) {
            jj = RSADecrypt(raw);
        } else {
            jj = sDecrypt(raw);
        }
        List<Response> responseList = responseDeserializer.deserialize(jj);
        return responseList.get(0);
    }

    protected static String multiLineInput(String oldValue, String key) throws IOException {
        if (oldValue == null) {
            say("no current value for " + key);
        } else {
            say("current value for " + key + ":");
            say(oldValue);
        }
        String EXIT_COMMAND = "/exit";
        String CLEAR_BUFFER_COMMAND = "/c";
        say("Enter new value. An empty line terminates input. Entering a line with " + EXIT_COMMAND + " will terminate input losing changes.\n " +
                "Hitting " + CLEAR_BUFFER_COMMAND + " will clear the contents of this.");
        String rawInput = "";
        boolean redo = true;
        while (redo) {
            try {
                String inLine = readline();
                while (!StringUtils.isTrivial(inLine)) {
                    if (inLine.equals(EXIT_COMMAND)) {
                        say("losing changes");
                        return null;  // null means no changes
                    }
                    if (inLine.equals(CLEAR_BUFFER_COMMAND)) {
                        return ""; // empty string means clear the current contents
                    }
                    rawInput = rawInput + inLine + "\n";
                    inLine = readline();
                }
                return rawInput;
            } catch (ExitException x) {
                // ok, so user terminated input. This ends the whole thing
                return null;
            }
        }
        return null; // should never get here.
    }
}
