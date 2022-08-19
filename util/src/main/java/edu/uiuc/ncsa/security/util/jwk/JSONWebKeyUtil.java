package edu.uiuc.ncsa.security.util.jwk;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.util.pkcs.MyKeyUtil;
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
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * A utility for <a href="https://tools.ietf.org/html/rfc7517">RFC 7515</a>, the JSON web key format.
 * <b>ALSO</b> see <a href="https://www.rfc-editor.org/rfc/rfc7518.txt">RFC 7518</a> for the
 * values of various parameters such as "alg", "typ" etc.
 * <p>Created by Jeff Gaynor<br>
 * on 1/6/17 at  2:39 PM
 */
public class JSONWebKeyUtil {
    public static final String ALGORITHM = "alg";
    public static final String MODULUS = "n";
    public static final String PUBLIC_EXPONENT = "e";
    public static final String PRIVATE_EXPONENT = "d";
    public static final String RSA_PRIME_1 = "p";
    public static final String RSA_PRIME_2 = "q";
    public static final String RSA_EXPONENT_1 = "dp";
    public static final String RSA_EXPONENT_2 = "dq";
    public static final String RSA_COEFFICIENTS = "qi"; // q^(-1) mod p

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
            JSONWebKey entry = getJsonWebKey(key);
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
     * @param rawJSON
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static JSONWebKey getJsonWebKey(String rawJSON) throws NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject json = (JSONObject) JSONSerializer.toJSON(rawJSON);
                                                                   return getJsonWebKey(json);
    }


    /**
     * Convert the JSON for a <i>single</i> JSON web key to a single key.
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static JSONWebKey getJsonWebKey(JSONObject key) throws NoSuchAlgorithmException, InvalidKeySpecException {
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
        if (!(skipIt || key.containsKey(MODULUS))) {
            return null;
        }
        KeyFactory factory = KeyFactory.getInstance("RSA");

        BigInteger modulus = getBI(key, MODULUS);
        BigInteger publicExp = getBI(key, PUBLIC_EXPONENT);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExp);
        PublicKey pub = factory.generatePublic(spec);
        entry.publicKey = pub;

        if (key.containsKey(PRIVATE_EXPONENT)) {
            BigInteger privateExp = getBI(key, PRIVATE_EXPONENT);

            if (key.containsKey(RSA_EXPONENT_1)) { // simple minded test if it is a full RSA KEY
                // CIL-1193 The full explanation is the that CRT = Chinese Remainder Theorem and its use
                // is documented at https://www.rfc-editor.org/rfc/rfc8017.txt
                // Keys generated by e.g. Open SSL use this.
                BigInteger prime1 = getBI(key, RSA_PRIME_1);
                BigInteger prime2 = getBI(key, RSA_PRIME_2);
                BigInteger exp1 = getBI(key, RSA_EXPONENT_1);
                BigInteger exp2 = getBI(key, RSA_EXPONENT_2);
                BigInteger crtCoef = getBI(key, RSA_COEFFICIENTS);

                RSAPrivateCrtKeySpec keySpec =
                        new RSAPrivateCrtKeySpec(modulus,
                                publicExp,
                                privateExp,
                                prime1,
                                prime2,
                                exp1,
                                exp2,
                                crtCoef);
                PrivateKey privateKey = factory.generatePrivate(keySpec);
                entry.privateKey = privateKey;

            } else {
                RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(modulus, privateExp);
                entry.privateKey = factory.generatePrivate(privateSpec);
            }
        }
        return entry;
    }

    /**
     * Get the {@link BigInteger} from the component.
     *
     * @param key
     * @param component
     * @return
     */
    protected static BigInteger getBI(JSONObject key, String component) {
        if (!key.containsKey(component)) {
            throw new IllegalArgumentException("key is missing component \"" + component + "\"");
        }
        // CIL-1166 The modulus is always positive, so force the issue since some
        // python libraries might not send along quite the right sequence of bytes.
        // In particular, a positive number would be of the form [0,b0,b1,...] as a byte array.
        // Since the spec says that the modulus must be positive, some python libraries omit the
        // first byte and would send [b0,b1,...] which would yield a negative modulus which in turn
        // causes an exception. This next call forces the bytes to be interpreted as positive (so it basically
        // just adds an initial byte of 0 if its missing):
        return new BigInteger(1, Base64.decodeBase64(key.getString(component)));
    }

    protected static String bigIntToString(BigInteger bigInteger) {
        return Base64.encodeBase64URLSafeString(bigInteger.toByteArray());
        //return Base64.encodeBase64String(bigInteger.toByteArray());
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

    /**
     * Create a new  {@link JSONWebKey} from a key pair. This creates a new random id too.
     *
     * @param keyPair
     * @return
     */
    public static JSONWebKey create(KeyPair keyPair) {
        JSONWebKey jsonWebKey = new JSONWebKey();
        jsonWebKey.privateKey = keyPair.getPrivate();
        jsonWebKey.publicKey = keyPair.getPublic();
        if (jsonWebKey.privateKey instanceof RSAPrivateKey) {
            jsonWebKey.type = "RSA";
        } else {
            throw new IllegalArgumentException("Unknown keypair type. Only RSA is supported");
        }

        Random random = new Random();
        byte[] bytes = new byte[8];
        random.nextBytes(bytes);
        jsonWebKey.id = Base64.encodeBase64URLSafeString(bytes);
        jsonWebKey.use = "sig";
        jsonWebKey.algorithm = "RS256";
        return jsonWebKey;
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
                if (webKey.privateKey instanceof RSAPrivateCrtKey) {
                    // CIL-1193 Support CRT (Chinese remainder theorem) in keys.
                    RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) webKey.privateKey;
                    jsonKey.put(PRIVATE_EXPONENT, bigIntToString(privateCrtKey.getPrivateExponent()));
                    jsonKey.put(RSA_PRIME_1, bigIntToString(privateCrtKey.getPrimeP()));
                    jsonKey.put(RSA_PRIME_2, bigIntToString(privateCrtKey.getPrimeQ()));
                    jsonKey.put(RSA_EXPONENT_1, bigIntToString(privateCrtKey.getPrimeExponentP()));
                    jsonKey.put(RSA_EXPONENT_2, bigIntToString(privateCrtKey.getPrimeExponentQ()));
                    jsonKey.put(RSA_COEFFICIENTS, bigIntToString(privateCrtKey.getCrtCoefficient()));

                } else {
                    // bare bones -- best we can do
                    RSAPrivateKey privateKey = (RSAPrivateKey) webKey.privateKey;
                    jsonKey.put(PRIVATE_EXPONENT, bigIntToString(privateKey.getPrivateExponent()));
                }
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


    public static void main(String[] args) throws Throwable {

        JSONWebKeys jsonWebKeys;

        File f = new File("/home/ncsa/temp/zzz/python-keys.jwk");
        //  File f = new File("/home/ncsa/temp/zzz/jwks.jwk");
        JSONWebKeys jwks = fromJSON(f);
        JSONWebKey key = jwks.getDefault();
        System.out.println("keys valid? " + MyKeyUtil.validateKeyPair(key.publicKey, key.privateKey));
        System.out.println("full key=" + toJSON(key).toString(2));
        System.out.println();
        System.out.println("public keys = " + toJSON(makePublic(jwks)).toString(2));

        KeyPair kp = MyKeyUtil.generateKeyPair();
        System.out.println(toJSON(create(kp)).toString(2));
    }
}
