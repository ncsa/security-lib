package edu.uiuc.ncsa.sat;

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
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeys;
import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;
import java.util.Vector;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/18/22 at  3:14 PM
 */
public class Client extends ServiceClient implements SATConstants {

    public Client(URI address, SSLConfiguration sslConfiguration) {
        super(address, sslConfiguration);
    }

    public Client(URI address) {
        super(address);
    }

    public static final String FLAG_NEW = "-edit";
    public static final String FLAG_CONFIG = "-cfg"; // name of config file
    public static final String FLAG_HELP = "--help"; // help
    public static final String FLAG_VERBOSE = "-v"; // help
    /*
            xmlMap.put("client_id", getInput("enter client id"));
        xmlMap.put("private_key", getInput("enter private key"));

        if ("y".equals("enter ssl configuration (y/n)?")) {
            xmlMap.put("trust_root_path", getInput("enter trust root path"));
            xmlMap.put("trust_root_password", getInput("enter trust root password"));
            xmlMap.put("trust_root_type", getInput("enter trust root store type, e.g. JKS"));
            xmlMap.put("trust_root_cert_dn", getInput("enter trust root store cert DN"));
     */
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
    protected static void createConfig(String filename) throws IOException {
        XMLMap xmlMap = new XMLMap();
        xmlMap.put(CONFIG_CLIENT_ID, getInput("enter client id"));
        say("enter private key, PKCS 8 or a single JWK");
        String x = multiLineInput("", CONFIG_PRIVATE_KEY);
        xmlMap.put(CONFIG_PRIVATE_KEY, x);
        xmlMap.put(CONFIG_HOST, getInput("enter server address"));

        if ("y".equals(getInput("enter ssl configuration (y/n)?"))) {
            xmlMap.put(CONFIG_TR_FILE, getInput("enter path to trust root file"));
            xmlMap.put(CONFIG_TR_PASSWORD, getInput("enter trust root password"));
            xmlMap.put(CONFIG_TR_TYPE, getInput("enter trust root store type, e.g. JKS"));
            xmlMap.put(CONFIG_TR_DN, getInput("enter trust root store cert DN"));
        }

        if (StringUtils.isTrivial(filename)) {
            filename = getInput("enter file name");
            File f = new File(filename);
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
        }
    }

    /*
            SSLConfiguration sslConfiguration = new SSLConfiguration();
        sslConfiguration.setTrustRootPath("/home/ncsa/certs/localhost-2020.jks");
        sslConfiguration.setTrustRootPassword("vnlH814i");
        sslConfiguration.setTrustRootType("JKS");
        sslConfiguration.setTrustRootCertDN("CN=localhost");
        sslConfiguration.setUseDefaultTrustManager(false);

        Client client = new Client(URI.create("https://localhost:9443/sat-server/sat"), sslConfiguration);
        JSONWebKeys keys = JSONWebKeyUtil.fromJSON(new File("/home/ncsa/dev/ncsa-git/security-lib/crypto/src/main/resources/keys.jwk"));
        keys.setDefaultKeyID("2D700DF531E09B455B9E74D018F9A133"); // for testing!
        client.setKeys(keys);
        client.doLogon(BasicIdentifier.newID("sat:client/debug_client"));
        client.doExecute("w00f!");
        client.doLogoff();

     */
    public static void main(String[] args) throws Throwable {
        JSONWebKeys keys = JSONWebKeyUtil.fromJSON(new File("/home/ncsa/dev/ncsa-git/security-lib/crypto/src/main/resources/keys.jwk"));
        keys.setDefaultKeyID("2D700DF531E09B455B9E74D018F9A133"); // for testing!
        System.out.println(JSONWebKeyUtil.toJSON(keys));
        Vector<String> vector = new Vector<>();
        vector.add("dummy"); // Dummy zero-th arg.
        for (String arg : args) {
            vector.add(arg);
        }
        InputLine inputLine = new InputLine(vector); // now we have a bunch of utilities for this

        boolean isVerbose = inputLine.hasArg(FLAG_VERBOSE);

        if (!inputLine.hasArg(FLAG_CONFIG)) {
            if ("y".equals(getInput("Create a new configuration?"))) {
                createConfig(null);
                return;
            }
            say("No configuration. exiting...");
        }

        if (inputLine.hasArg(FLAG_HELP)) {
            showHelp();
            return;
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
                return;
            }
        } else {
            say("no configuration");
            return;
        }

        SSLConfiguration sslConfiguration = new SSLConfiguration();
        sslConfiguration.setTrustRootPath(config.getString(CONFIG_TR_FILE));
        sslConfiguration.setTrustRootPassword(config.getString(CONFIG_TR_PASSWORD));
        sslConfiguration.setTrustRootType(config.getString(CONFIG_TR_TYPE));
        sslConfiguration.setTrustRootCertDN(config.getString(CONFIG_TR_DN));
        sslConfiguration.setUseDefaultTrustManager(false);

        //Client client = new Client(URI.create("https://localhost:9443/sat-server/sat"), sslConfiguration);
        Client client = new Client(URI.create(config.getString(CONFIG_HOST)), sslConfiguration);
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
            return;
        }
        client.setPrivateKey(privateKey);
        client.doLogon(BasicIdentifier.newID(config.getString(CONFIG_CLIENT_ID)));
        //client.doLogon(BasicIdentifier.newID("sat:client/debug_client"));
        client.doExecute("w00f!");
        client.doLogoff();
    }

    private static void showHelp() {
        say("Help for " + Client.class.getName());
    }

    UUID sessionID;

    protected void doLogon(Identifier identifier) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, BadPaddingException, InvalidKeyException {
        JSONObject top = new JSONObject();
        JSONObject sat = new JSONObject();


        sat.put(KEYS_ACTION, ACTION_LOGON);
        top.put(KEYS_SAT, sat);
        String raw = doPost(RSAEncrypt(top.toString()), identifier.toString(), "");
        String jj = RSADecrypt(raw);
        System.out.println(jj);
        JSONObject json = JSONObject.fromObject(jj); // This is the full on body
        if (json.getInt(RESPONSE_STATUS) == 0) {
            sessionID = UUID.fromString(json.getString(RESPONSE_SESSION_ID));
            sKey = Base64.decodeBase64(json.getString(RESPONSE_SYMMETRIC_KEY));
        } else {
            throw new GeneralException("error logging on to service, status != 0");
        }
    }

    protected void doLogoff() throws Throwable {
        JSONObject top = new JSONObject();
        JSONObject sat = new JSONObject();

        sat.put(KEYS_ACTION, ACTION_LOGOFF);
        top.put(SATConstants.KEYS_SAT, sat);
        String raw = doPost(top.toString());
        String jj = sDecrypt(raw);
        System.out.println(jj);

    }

    protected void doExecute(String contents) throws Throwable {
        JSONObject top = new JSONObject();
        JSONObject sat = new JSONObject();
        sat.put(KEYS_ACTION, ACTION_EXECUTE);
        sat.put(KEYS_ARGUMENT, Base64.encodeBase64URLSafeString(contents.getBytes(StandardCharsets.UTF_8)));
        top.put(KEYS_SAT, sat);
        String raw = doPost(top.toString());
        String jj = sDecrypt(raw);
        System.out.println(jj);

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

    public String doPost(String contents) throws Throwable {
        HttpPost post = new HttpPost(host().toString());
        post.setEntity(new StringEntity(sEncrypt(contents)));
        post.setHeader(HEADER_SESSION_ID, sessionID.toString());
        return doRequest(post);
    }
    /*
          <ssl debug="false"
             useJavaTrustStore="true">
            <trustStore>
                <path>/home/ncsa/certs/localhost-2020.jks</path>
                <password><![CDATA[vnlH814i]]></password>
                <type>JKS</type>
                <certDN><![CDATA[CN=localhost]]></certDN>
            </trustStore>
        </ssl>

     */

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
