package edu.uiuc.ncsa.security.util.jwk;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * A utility for <a href="https://tools.ietf.org/html/rfc7517">RFC 7515</a>, the JSON web key format.
 * <p>Created by Jeff Gaynor<br>
 * on 1/6/17 at  2:39 PM
 */
public class JSONWebKeyUtil {
    public static final String ALGORITHM = "alg";
    public static final String MODULUS = "n";
    public static final String PUBLIC_EXPONENT = "e";
    public static final String PRIVATE_EXPONENT = "d";
    public static final String KEY_ID = "kid";
    public static final String USE = "use";
    public static final String KEY_TYPE = "kty";
    public static final String KEYS = "keys";

    public static JSONWebKeys fromJSON(File file) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String x = br.readLine();
        String out = "";
        while (x != null) {
            out = out + x;
            x = br.readLine();

        }
        br.close();
        JSONWebKeys keys = fromJSON(out);
        return keys;
    }

    /**
     * Take a map of key pairs (keyed by a
     *
     * @param x
     * @return
     */
    public static JSONWebKeys fromJSON(String x) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject json = (JSONObject) JSONSerializer.toJSON(x);

        JSONArray array = json.getJSONArray(KEYS);
        JSONWebKeys keys = new JSONWebKeys(null);
        for (int i = 0; i < array.size(); i++) {
            JSONObject key = array.getJSONObject(i);
            JSONWebKey entry = new JSONWebKey();
            entry.id = key.getString(KEY_ID);
            entry.algorithm = key.getString(ALGORITHM);
            entry.use = key.getString(USE);
            entry.type = key.getString(KEY_TYPE);
            // have to figure out what is in this entry.
            if (key.containsKey(MODULUS) && key.containsKey(PUBLIC_EXPONENT)) {
                byte[] mod = Base64.decodeBase64(key.getString(MODULUS));
                BigInteger modulus = new BigInteger(mod);
                byte[] pubExp = Base64.decodeBase64(key.getString(PUBLIC_EXPONENT));
                BigInteger publicExponent = new BigInteger(pubExp);
                RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
                KeyFactory factory = KeyFactory.getInstance("RSA");
                PublicKey pub = factory.generatePublic(spec);
                entry.publicKey = pub;

                if (key.containsKey(PRIVATE_EXPONENT)) {
                    byte[] privExp = Base64.decodeBase64(key.getString(PRIVATE_EXPONENT));
                    BigInteger privateExponent = new BigInteger(privExp);
                    RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, privateExponent);
                    PrivateKey priv = factory.generatePrivate(privateSpec);
                    entry.privateKey = priv;
                }

                keys.put(entry.id, entry);
            } else {
                // skip it -- not enough info
            }
        }
        return keys;
    }

    protected static String bigIntToString(BigInteger bigInteger){
        return Base64.encodeBase64URLSafeString(bigInteger.toByteArray());
    }
    public static JSONObject toJSON(JSONWebKeys webKeys){
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for(String key : webKeys.keySet()){
            JSONWebKey webKey = webKeys.get(key);
            if(webKey.type.equals("RSA")) {
                JSONObject jsonKey = new JSONObject();
                RSAPublicKey rsaPub = (RSAPublicKey) webKey.publicKey;
                jsonKey.put(MODULUS,bigIntToString(rsaPub.getModulus()));
                jsonKey.put(PUBLIC_EXPONENT, bigIntToString(rsaPub.getPublicExponent()));
                jsonKey.put(ALGORITHM, webKey.algorithm);
                jsonKey.put(KEY_ID, webKey.id);
                jsonKey.put(USE, webKey.use);
                jsonKey.put(KEY_TYPE, "RSA");
                if(webKey.privateKey != null){
                    RSAPrivateKey privateKey = (RSAPrivateKey)webKey.privateKey;
                    jsonKey.put(PRIVATE_EXPONENT, bigIntToString(privateKey.getPrivateExponent()));
                }
                array.add(jsonKey);
            }
        }
        json.put("keys", array);
        return json;
    }
    public static JSONWebKeys makePublic(JSONWebKeys keys){
        JSONWebKeys newKeys = new JSONWebKeys(keys.getDefaultKeyID()) ;
        for(String key:keys.keySet()){
            try {
                JSONWebKey newKey = keys.get(key).clone();
                newKey.privateKey = null;
                newKeys.put(newKey);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        // now we have a clone with no private keys, we need to c
        return newKeys;
    }
    public static void main(String[] args) {
        try {
            System.out.println("Generate a new set of keys for the server");

            File file = new File("/home/ncsa/dev/csd/config/keys.jwk");
            JSONWebKeys keys = fromJSON(file);
            System.out.println("size = " + keys.size());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
