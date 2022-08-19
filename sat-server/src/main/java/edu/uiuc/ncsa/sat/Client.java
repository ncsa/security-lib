package edu.uiuc.ncsa.sat;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.servlet.ServiceClient;
import edu.uiuc.ncsa.security.util.crypto.DecryptUtils;
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
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/18/22 at  3:14 PM
 */
public class Client extends ServiceClient implements SATConstants{

    public Client(URI address, SSLConfiguration sslConfiguration) {
        super(address, sslConfiguration);
    }

    public Client(URI address) {
        super(address);
    }

    public static void main(String[] args) throws Throwable{
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
        if(json.getInt(RESPONSE_STATUS) == 0){
                 sessionID = UUID.fromString(json.getString(RESPONSE_SESSION_ID));
                 sKey = Base64.decodeBase64(json.getString(RESPONSE_SYMMETRIC_KEY));
        }else{
            throw new GeneralException("error logging on to service, status != 0");
        }
    }
    protected void doLogoff()throws Throwable{
        JSONObject top = new JSONObject();
        JSONObject sat = new JSONObject();

        sat.put(KEYS_ACTION, ACTION_LOGOFF);
        sat.put(SATConstants.KEYS_SAT, sat);
        String raw = doPost(top.toString());
        String jj = sDecrypt(raw);
        System.out.println(jj);

    }
    protected void doExecute(String contents) throws Throwable{
        JSONObject top = new JSONObject();
        JSONObject sat = new JSONObject();
        sat.put(KEYS_ACTION, ACTION_EXECUTE);
        sat.put(KEYS_CONTENT, Base64.encodeBase64URLSafeString(contents.getBytes(StandardCharsets.UTF_8)));
        top.put(KEYS_SAT, sat);
        String raw = doPost(top.toString());
        String jj = sDecrypt(raw);
        System.out.println(jj);

    }
    protected String sEncrypt(String contents){
        return DecryptUtils.sEncrypt(getsKey(), contents);
    }
    protected String sDecrypt(String content64){
        return DecryptUtils.sDecrypt(getsKey(), content64);
    }
    protected String RSAEncrypt(String contents) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return DecryptUtils.encryptPrivate(getKeys().getDefault().privateKey, contents);
    }
    protected String RSADecrypt(String content64) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        return DecryptUtils.decryptPrivate(getKeys().getDefault().privateKey, content64);
    }

    public JSONWebKeys getKeys() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return keys;
    }

    public void setKeys(JSONWebKeys keys) {
        this.keys = keys;
    }

    JSONWebKeys keys;

    /**
     * The symmetric key.
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

    public String doPost(String contents)throws Throwable{
        HttpPost post = new HttpPost(host().toString());
        post.setEntity(new StringEntity(sEncrypt(contents)));
        post.setHeader(HEADER_SESSION_ID, sessionID.toString() );
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
}
