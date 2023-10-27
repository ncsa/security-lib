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
import java.math.BigInteger;
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
            if (!jsonObject.containsKey(KEYS)) {
                throw new IllegalArgumentException("No keys found");
            }
            array = jsonObject.getJSONArray(KEYS);
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
/*
    public static JSONWebKey getJsonWebKey(JSONObject key) throws NoSuchAlgorithmException, InvalidKeySpecException {

        JSONWebKey entry = new JSONWebKey();
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

        if (key.containsKey(KEY_TYPE)) {
            entry.type = key.getString(KEY_TYPE);
            // Note that OA4MP only supports RSA keys at this time, since these are by far the most widely used.
            if (entry.type.toLowerCase().startsWith("ec")) {
                return getECJsonWebKey(key, entry);

            }
            return getRSAJsonWebKey(key, entry, !entry.type.toLowerCase().startsWith("rsa"));
        } else {
            throw new IllegalStateException("Error: missing key type");
        }


    }
*/

    /*
    secp256r1
    secp384r1
    secp521r1 	The NIST elliptic curves as specified in RFC 8422.
    x25519
    x448
    	The elliptic curves as specified in RFC 8446 and RFC 8442.
    ffdhe2048
    ffdhe3072
    ffdhe4096
    ffdhe6144
    ffdhe8192
     */
    // Fix https://github.com/ncsa/oa4mp/issues/131
   /* protected static JSONWebKey getECJsonWebKey(JSONObject key, JSONWebKey entry) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger x = getBI(key, EC_COEFFICIENT_X);
        BigInteger y = getBI(key, EC_COEFFICIENT_Y);
        ECPoint z = new ECPoint(x, y);
         ECField ecField = new ECField() {
             @Override
             public int getFieldSize() {
                 return 162;
             }
         };

        EllipticCurve curve = new EllipticCurve(ecField, BigInteger.ONE, BigInteger.TEN);
        if (key.containsKey(EC_CURVE)) {
            switch (key.getString(EC_CURVE)) {
                case "secp256r1":
                    //  curve = new EllipticCurve()

            }
        }
        ECGenParameterSpec gps = new ECGenParameterSpec("secp256r1"); // NIST P-256
                               X509EncodedKeySpec x509EncodedKeySpec;

        ECParameterSpec parameters = new ECParameterSpec(curve, z, BigInteger.ONE, 1);
        entry.publicKey = KeyFactory.getInstance("EC").generatePublic(new ECPublicKeySpec(z, parameters));
        if (key.containsKey(EC_COEFFICIENT_D)) {
            BigInteger d = getBI(key, EC_COEFFICIENT_D); // in the Java class, this is s.

            // Then we have a private key too.
            entry.privateKey = KeyFactory.getInstance("EC").generatePrivate(new ECPrivateKeySpec(d, parameters));
        }

        return entry;

        //ECPublicKeySpec spec = new ECPublicKeySpec(modulus, publicExp);

    }*/

    /*
    final BigInteger x = new BigInteger(1, Base64.getUrlDecoder().decode(json.getString("x")));
    final BigInteger y = new BigInteger(1, Base64.getUrlDecoder().decode(json.getString("y")));
    publicKey = KeyFactory.getInstance("EC").generatePublic(new ECPublicKeySpec(new ECPoint(x, y), parameters.getParameterSpec(ECParameterSpec.class)));

    final BigInteger x = new BigInteger(1, Base64.getUrlDecoder().decode(json.getString("x")));
    final BigInteger y = new BigInteger(1, Base64.getUrlDecoder().decode(json.getString("y")));
    final BigInteger d = new BigInteger(1, Base64.getUrlDecoder().decode(json.getString("d")));
    privateKey = KeyFactory.getInstance("EC").generatePrivate(new ECPrivateKeySpec(d, parameters.getParameterSpec(ECParameterSpec.class)));
     */
  /*  protected static JSONWebKey getRSAJsonWebKey(JSONObject key, JSONWebKey entry, boolean skipIt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Key type is required
        if (skipIt) {
            DebugUtil.trace(JSONWebKeyUtil.class, "loading JSON webkeys and ignoring key of type " + entry.type);
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
    }*/

    /**
     * Get the {@link BigInteger} from the component.
     *
     * @return
     */
  /*  public static BigInteger getBI(JSONObject key, String component) {
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
/*
    protected static JSONObject initToJSON(JSONWebKey webKey) {
        JSONObject jsonKey = new JSONObject();
        jsonKey.put(ALGORITHM, webKey.algorithm);
        jsonKey.put(KEY_ID, webKey.id);
        jsonKey.put(USE, webKey.use);
        jsonKey.put(KEY_TYPE, webKey.type);
        return jsonKey;
    }
*/

/*
    protected static JSONObject toJSONEC(JSONWebKey webKey) {
        JSONObject jsonKey = initToJSON(webKey);
        ECPublicKey ecPublicKey = (ECPublicKey) webKey.publicKey;
        ECPoint w = ecPublicKey.getW();
        jsonKey.put(EC_COEFFICIENT_X, bigIntToString(w.getAffineX()));
        jsonKey.put(EC_COEFFICIENT_Y, bigIntToString(w.getAffineY()));
        jsonKey.put(EC_CURVE, ecPublicKey.getParams().getCurve().toString());
        if (webKey.privateKey != null) {
            ECPrivateKey ecPrivateKey = (ECPrivateKey) webKey.privateKey;
            jsonKey.put(EC_COEFFICIENT_D, bigIntToString(ecPrivateKey.getS()));
        }
        return jsonKey;
    }
*/

/*    protected static JSONObject toJSONRSA(JSONWebKey webKey) {

        JSONObject jsonKey = initToJSON(webKey);
        RSAPublicKey rsaPub = (RSAPublicKey) webKey.publicKey;
        jsonKey.put(MODULUS, bigIntToString(rsaPub.getModulus()));
        jsonKey.put(PUBLIC_EXPONENT, bigIntToString(rsaPub.getPublicExponent()));
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
    }*/


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


 /*   public static void main(String[] args) throws Throwable {
        //testPython();
    }*/

/*    protected static void testPython() throws Throwable {
        JSONWebKeys jsonWebKeys;

        File f = new File("/home/ncsa/temp/zzz/python-keys.jwk");
        //  File f = new File("/home/ncsa/temp/zzz/jwks.jwk");
        JSONWebKeys jwks = fromJSON(f);
        JSONWebKey key = jwks.getDefault();
        System.out.println("keys valid? " + MyKeyUtil.validateKeyPair(key.publicKey, key.privateKey));
        System.out.println("full key=" + getJwkUtil2().toJSON(key).toString(2));
        System.out.println();
        System.out.println("public keys = " + toJSON(makePublic(jwks)).toString(2));

        KeyPair kp = MyKeyUtil.generateKeyPair();
        System.out.println(toJSON(create(kp)).toString(2));

    }*/





/*   public elliptic curve key

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
