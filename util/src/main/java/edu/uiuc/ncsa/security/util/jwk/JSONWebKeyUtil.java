package edu.uiuc.ncsa.security.util.jwk;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;
import static edu.uiuc.ncsa.security.util.jwk.JWKUtil2.KEYS;

/**
 * A utility for <a href="https://tools.ietf.org/html/rfc7517">RFC 7515</a>, the JSON web key format.
 * <b>ALSO</b> see <a href="https://www.rfc-editor.org/rfc/rfc7518.txt">RFC 7518</a> for the
 * values of various parameters such as "alg", "typ" etc.
 * <p>Created by Jeff Gaynor<br>
 * on 1/6/17 at  2:39 PM
 */
public class JSONWebKeyUtil {
    public static JWKUtil2 getJwkUtil2() {
        if (jwkUtil2 == null) {
            jwkUtil2 = new JWKUtil2();
        }
        return jwkUtil2;
    }

    public static void setJwkUtil2(JWKUtil2 jwkUtil) {
        jwkUtil2 = jwkUtil;
    }

    protected static JWKUtil2 jwkUtil2 = null;
    public static final String ALGORITHM = "alg";
    public static final String MODULUS = "n";
    public static final String PUBLIC_EXPONENT = "e";
    public static final String PRIVATE_EXPONENT = "d";
    public static final String RSA_PRIME_1 = "p";
    public static final String RSA_PRIME_2 = "q";
    public static final String RSA_EXPONENT_1 = "dp";
    public static final String RSA_EXPONENT_2 = "dq";
    public static final String RSA_COEFFICIENTS = "qi"; // q^(-1) mod p


    /**
     * Read a set of keys from a file. The format of the file is that of the spec.
     *
     * @param file
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws IOException
     */
    public static JSONWebKeys fromJSON(File file) throws IOException {
        return getJwkUtil2().fromJSON(file);
    }

    public static JSONWebKeys fromJSON(String raw) {
        return getJwkUtil2().fromJSON(raw); // used in OA4MP
    }

    /**
     * Takes either a standard JSON object of keys: {"keys":[...]} or a single
     * key and turns it in a set of web keys
     *
     * @param json
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static JSONWebKeys fromJSON(JSON json) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JSONArray array;
        if (json.isArray()) {
            array = (JSONArray) json;
        } else {
            JSONObject jsonObject = (JSONObject) json;
            if (jsonObject.containsKey(KEYS)) {
                array = jsonObject.getJSONArray(KEYS);
            } else {
                // throw new IllegalArgumentException("No keys found");
                // assumes that if there is no keys entry, the argument is a single key.
                // This may bomb later.
                array = new JSONArray();
                array.add(jsonObject);
            }
        }
        JSONWebKeys keys = new JSONWebKeys(null);
        for (int i = 0; i < array.size(); i++) {
            JSONObject key = array.getJSONObject(i);
            JSONWebKey entry = getJwkUtil2().getJsonWebKey(key);
            if (entry == null) continue; // do not process unless there is enough information to resolve the key
            keys.put(entry.id, entry);
        }
        if (keys.size() == 1) {
            if (keys.getDefaultKeyID() == null) {
                JSONWebKey key = keys.entrySet().iterator().next().getValue();
                keys.setDefaultKeyID(key.id);
            }
        }
        return keys;
    }

    /**
     * Takes the serialized form of a <i>single</i> JSON web key and returns the object. This is a compliment
     * to {@link  #toJSON(JSONWebKey)}.
     *
     * @param rawJSON
     * @return
     */
    public static JSONWebKey getJsonWebKey(String rawJSON) {
        return getJwkUtil2().getJsonWebKey(JSONObject.fromObject(rawJSON));
    }


    /**
     * Convert the JSON for a <i>single</i> JSON web key to a single key.
     *
     * @param key
     * @return
     */

    // Fix https://github.com/ncsa/oa4mp/issues/131


    /**
     * Serialize a set of keys (as a java object) to JSON.
     *
     * @param webKeys
     * @return
     */
    public static JSONObject toJSON(JSONWebKeys webKeys) {
        return getJwkUtil2().toJSON(webKeys);
    }

    public static JSONObject toJSON(JSONWebKey webKey) {
        return getJwkUtil2().toJSON(webKey);
    }

    /**
     * Create a new  {@link JSONWebKey} from a key pair. This creates a new random id too.
     * This defaults to an algorithm of RS256. Remember that the algorithm is intended to show
     * which algorithm consumers of this key expect, but has zero bearing on the key structure itself.
     * If you want a different algorithm, such as RS384 or RS512, just change it.
     *
     * @param keyPair
     * @return
     */
    public static JSONWebKey create(KeyPair keyPair) {
        JSONWebKey jsonWebKey = new JSONWebKey();
        jsonWebKey.privateKey = keyPair.getPrivate();
        jsonWebKey.publicKey = keyPair.getPublic();
        boolean gotType = false;
        if (jsonWebKey.privateKey instanceof ECPrivateKey) {
            jsonWebKey.type = "EC";
            jsonWebKey.algorithm = "ES256";

        }
        if (jsonWebKey.privateKey instanceof RSAPrivateKey) {
            jsonWebKey.type = "RSA";
            jsonWebKey.algorithm = "RS256";
            gotType = true;
        }

        if (!gotType) {
            throw new IllegalArgumentException("Unknown keypair type. Only RSA, Elliptic curves is supported");
        }

        Random random = new Random();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        jsonWebKey.id = Base64.encodeBase64URLSafeString(bytes);
        jsonWebKey.use = "sig";
        return jsonWebKey;
    }


    /**
     * Sets common elements for all keys.
     *
     * @param webKey
     * @return
     */


    /**
     * Very useful utility to take a set of keys and return another set of keys that are only the public parts.
     * This set, for instance, can be returned as a response to public requests.
     *
     * @param keys
     * @return
     */
    public static JSONWebKeys makePublic(JSONWebKeys keys) {
        return getJwkUtil2().makePublic(keys);
    }

    public static JSONWebKey makePublic(JSONWebKey key) {
        return getJwkUtil2().makePublic(key);

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
    // THIS and from XML are used by QDL Modules that have to serialize the state.
    public static void toXML(JSONWebKeys jwks, XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(JSON_WEB_KEYS_TAG);
        if (!isTrivial(jwks.getDefaultKeyID())) {
            xsw.writeAttribute(DEFAULT_KEY_ID_TAG, jwks.getDefaultKeyID());
        }
        xsw.writeCData(Base64.encodeBase64URLSafeString(getJwkUtil2().toJSON(jwks).toString().getBytes()));
        xsw.writeEndElement(); // end JSON web keys
    }

    /**
     * This is not a complete deserialization, but is part of a larger scheme. The assumption is that
     * the cursor for the stream is positioned at the start tag for JSON web keys.
     *
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
                        jwks = getJwkUtil2().fromJSON(new String(b));
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

    /*
    Comparison of key size strength for RSA vs elliptic
    RSA key size (bits)	: ECC key size (bits)
            1024        :      160
            2048        :      224
            3072        :      256
            7680        :      384
            15360       :      521
     */
}
