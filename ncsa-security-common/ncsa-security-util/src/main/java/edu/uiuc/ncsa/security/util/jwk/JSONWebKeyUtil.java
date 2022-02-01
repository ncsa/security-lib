package edu.uiuc.ncsa.security.util.jwk;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.codec.binary.Base64;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
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

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

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


    /**
     * Read a set of keys from a file. The format of the file is that of the spec.
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws IOException
     */
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
     * Take a raw string of text that is the JSON for the keys and convert it into a set of keys.
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
            // Key type is required
            boolean skipIt = false;
            if (key.containsKey(KEY_TYPE)) {
                entry.type = key.getString(KEY_TYPE);
                // Note that OA4MP only supports RSA keys at this time, since these are by far the most widely used.
                if (!entry.type.toLowerCase().startsWith("rsa")) {
                    DebugUtil.trace(JSONWebKeyUtil.class, "loading JSON webkeys and ignoring key of type " + entry.type);
                    skipIt = true;
                    // throw new GeneralException("Unsupported key type. Must be RSA");
                }
            } else {
                throw new IllegalStateException("Error: missing key type");
            }
            if (key.containsKey(KEY_ID)) {
                entry.id = key.getString(KEY_ID);
            }
            // The algorithm is optional and not every site supports it.
            if (key.containsKey(ALGORITHM)) {
                entry.algorithm = key.getString(ALGORITHM);
            }
            if (key.containsKey(USE)) {
                entry.use = key.getString(USE);
            }
            // have to figure out what is in this entry.
            if (!skipIt && key.containsKey(MODULUS) && key.containsKey(PUBLIC_EXPONENT)) {
                byte[] mod = Base64.decodeBase64(key.getString(MODULUS));
                // CIL-1166 The modulus is always positive, so force the issue since some
                // python libraries might not send along quite the right sequence of bytes.
                // In particular, a positive number would be of the form [0,b0,b1,...] as a byte array.
                // Since the spec says that the modulus must be positive, some python libraries omit the
                // first byte and would send [b0,b1,...] which would yield a negative modulus which in turn
                // causes an exception. This next call forces the bytes to be interpreted as positive (so it basically
                // just adds an initial byte of 0 if its missing):
                BigInteger modulus = new BigInteger(1,mod);
                byte[] pubExp = Base64.decodeBase64(key.getString(PUBLIC_EXPONENT));
                // Same note as per above. Force the public exponent to be positive no matter what.
                BigInteger publicExponent = new BigInteger(1,pubExp);
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

    protected static String bigIntToString(BigInteger bigInteger) {
        return Base64.encodeBase64URLSafeString(bigInteger.toByteArray());
    }

    /**
     * Serialize a set of keys (as a java object) to JSON.
     *
     * @param webKeys
     * @return
     */
    public static JSONObject toJSON(JSONWebKeys webKeys) {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (String key : webKeys.keySet()) {
            JSONWebKey webKey = webKeys.get(key);
            array.add(toJSON(webKeys.get(key)));
        }
        json.put("keys", array);
        return json;
    }

    public static JSONObject toJSON(JSONWebKey webKey) {

        if (webKey.type.equals("RSA")) {
            JSONObject jsonKey = new JSONObject();
            RSAPublicKey rsaPub = (RSAPublicKey) webKey.publicKey;
            jsonKey.put(MODULUS, bigIntToString(rsaPub.getModulus()));
            jsonKey.put(PUBLIC_EXPONENT, bigIntToString(rsaPub.getPublicExponent()));
            jsonKey.put(ALGORITHM, webKey.algorithm);
            jsonKey.put(KEY_ID, webKey.id);
            jsonKey.put(USE, webKey.use);
            jsonKey.put(KEY_TYPE, "RSA");
            if (webKey.privateKey != null) {
                RSAPrivateKey privateKey = (RSAPrivateKey) webKey.privateKey;
                jsonKey.put(PRIVATE_EXPONENT, bigIntToString(privateKey.getPrivateExponent()));
            }
            return jsonKey;
        }
        throw new IllegalArgumentException("Unsupported webkey type \"" + webKey.type + "\".");
    }

    /**
     * Very useful utility to take a set of keys and return another set of keys that are only the public parts.
     * This set, for instance, can be returned as a response to public requests.
     *
     * @param keys
     * @return
     */
    public static JSONWebKeys makePublic(JSONWebKeys keys) {
        JSONWebKeys newKeys = new JSONWebKeys(keys.getDefaultKeyID());
        for (String key : keys.keySet()) {
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


    public static final String DEFAULT_KEY_ID_TAG = "default_key_id";
    public static final String JSON_WEB_KEYS_TAG = "json_web_keys";

    /**
     * Not a complete serialization -- this is used to insert JSONweb keys into a larger
     * serialization scheme.
     *
     * @param jwks
     * @param xsw
     * @throws XMLStreamException
     */
    public static void toXML(JSONWebKeys jwks, XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(JSON_WEB_KEYS_TAG);
        if (!isTrivial(jwks.getDefaultKeyID())) {
            xsw.writeAttribute(DEFAULT_KEY_ID_TAG, jwks.getDefaultKeyID());
        }
        xsw.writeCData(Base64.encodeBase64URLSafeString(toJSON(jwks).toString().getBytes()));
        xsw.writeEndElement(); // end JSON web keys
    }

    /**
     * This is not a complete deserialization, but is part of a larger scheme. The assumption is that
     * the cursor for the stream is positioned at the start tag for JSON web keys.
     *
     * @param xer
     * @throws XMLStreamException
     */
    public static JSONWebKeys fromXML(XMLEventReader xer) throws XMLStreamException, InvalidKeySpecException, NoSuchAlgorithmException {
        XMLEvent xe = xer.peek();
        String defaultKeyId = "";
        Attribute a = xe.asStartElement().getAttributeByName(new QName(DEFAULT_KEY_ID_TAG));
        if (a != null) {
            defaultKeyId = a.getValue();
        }
        JSONWebKeys jwks = new JSONWebKeys(defaultKeyId);

        while (xer.hasNext()) {
            switch (xe.getEventType()) {
                case XMLEvent.CHARACTERS:
                    if (!xe.asCharacters().isWhiteSpace()) {
                        byte[] b = Base64.decodeBase64(xe.asCharacters().getData());
                        jwks = fromJSON(new String(b));
                    }

                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(JSON_WEB_KEYS_TAG)) {
                        return jwks;
                    }
                    break;
            }

            xe = xer.nextEvent();
        }
        throw new IllegalArgumentException("Error: missing closing tag for element " + JSON_WEB_KEYS_TAG);
    }

    public static void main(String[] args) throws Throwable{
        File f = new File("/tmp/pub.jwk");
        JSONWebKeys jwks = fromJSON(f);
        System.out.println(jwks);
        byte[] pos = new byte[]{0,53,107,-113};
        byte[]  neg = new byte[]{53,107,-113};
        BigInteger bdpos = new BigInteger(pos);

        BigInteger bdneg = new BigInteger(1, neg);
        System.out.println(bdpos);
        System.out.println(bdneg);
    }
}
