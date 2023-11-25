package edu.uiuc.ncsa.security.util.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.*;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.*;
import java.security.spec.ECPoint;
import java.security.spec.RSAKeyGenParameterSpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import static edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil.*;

/**
 * This is a utility that does all of the work for JSON webkeys. This is part of a conversion internally
 * to JOSE.
 * <p>Created by Jeff Gaynor<br>
 * on 10/26/23 at  8:41 AM
 */
// Fixes https://github.com/ncsa/security-lib/issues/24
public class JWKUtil2 implements Serializable {
    public static final String KEYS = "keys";
    public static final String KEY_ID = "kid";
    public static final String USE = "use";
    public static final String KEY_TYPE = "kty";
    public static final String EC_CURVE_KEY = "crv";
    public static final String ISSUED_AT = "iat";
    public static final String NOT_VALID_BEFORE = "nbf";
    public static final String EXPIRES_AT = "exp";
    /**
     * RSA 256 key algorithm
     */
    public static final String RS_256 = "RS256";
    /**
     * RSA 384 key algorithm
     */
    public static final String RS_384 = "RS384";
    /**
     * RSA 512 key algorithm
     */
    public static final String RS_512 = "RS512";
    /**
     * Elliptic curve ES 256 key algorithm
     */
    public static final String ES_256 = "ES256";
    /**
     * Elliptic curve ES 256K key algorithm
     */
    public static final String ES_256K = "ES256k";
    /**
     * Elliptic curve ES 384 key algorithm
     */
    public static final String ES_384 = "ES384";
    /**
     * Elliptic curve ES 512 key algorithm
     */
    public static final String ES_512 = "ES512";
    public static final String EC_CURVE_P_256 = Curve.P_256.getName();
    public static final String EC_CURVE_P_256K = Curve.P_256K.getName();
    public static final String EC_CURVE_P_384 = Curve.P_384.getName();
    public static final String EC_CURVE_P_521 = Curve.P_521.getName();
    public static final String EC_CURVE_SEC_256K1 = Curve.SECP256K1.getName();
    public static final String EC_CURVE_ED_25519 = Curve.Ed25519.getName();
    public static final String EC_CURVE_ED_448 = Curve.Ed448.getName();
    public static final String EC_CURVE_X_25519 = Curve.X25519.getName();
    public static final String EC_CURVE_X_448 = Curve.X448.getName();

    public static final String[] ALL_EC_CURVES = new String[]{EC_CURVE_P_256,
            EC_CURVE_P_384, EC_CURVE_P_521, EC_CURVE_SEC_256K1,
            EC_CURVE_ED_25519, EC_CURVE_ED_448, EC_CURVE_X_448,
            EC_CURVE_X_25519, EC_CURVE_P_256K
    };
    public static final String[] ALL_EC_ALGORITHMS = new String[]{
            ES_256, ES_384, ES_512, ES_256K
    };
    public static final String[] ALL_RSA_ALGORITHMS = new String[]{
            RS_256, RS_384, RS_512
    };

    /*
        public static final Curve P_256 = new Curve("P-256", "secp256r1", "1.2.840.10045.3.1.7");
    public static final Curve SECP256K1 = new Curve("secp256k1", "secp256k1", "1.3.132.0.10");
    public static final Curve P_256K = new Curve("P-256K", "secp256k1", "1.3.132.0.10");
    public static final Curve P_384 = new Curve("P-384", "secp384r1", "1.3.132.0.34");
    public static final Curve P_521 = new Curve("P-521", "secp521r1", "1.3.132.0.35");
    public static final Curve Ed25519 = new Curve("Ed25519", "Ed25519", (String)null);
    public static final Curve Ed448 = new Curve("Ed448", "Ed448", (String)null);
    public static final Curve X25519 = new Curve("X25519", "X25519", (String)null);
    public static final Curve X448 = new Curve("X448", "X448", (String)null);
     */
    public JSONWebKeys fromJSON(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String x = br.readLine();
        String out = "";
        while (x != null) {
            out = out + x;
            x = br.readLine();

        }
        br.close();
        return fromJSON(out);
    }

    public JSONWebKeys fromJSON(String x) {
        return fromJSON(JSONSerializer.toJSON(x));
    }

    public JSONWebKeys fromJSON(JSON json) {
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

    public JSONWebKey getJsonWebKey(JSONObject json) {
        try {
            /* Here's the rub. In https://www.rfc-editor.org/rfc/rfc7518.html#section-3.4 it specifies what
               the default are and in practice, people do not include the crv for the JWK, just the
               algorithm. JOSE will not deserialize without the right crv, so add it:
            +-------------------+-------------------------------+
            | "alg" Param Value | Digital Signature Algorithm   |
            +-------------------+-------------------------------+
            | ES256             | ECDSA using P-256 and SHA-256 |
            | ES384             | ECDSA using P-384 and SHA-384 |
            | ES512             | ECDSA using P-521 and SHA-512 |
            +-------------------+-------------------------------+
            */

            if (!json.containsKey(EC_CURVE_KEY)) {
                switch (json.getString(ALGORITHM)) {
                    case ES_256K:
                    case ES_256:
                        json.put(EC_CURVE_KEY, EC_CURVE_P_256);
                        break;
                    case ES_384:
                        json.put(EC_CURVE_KEY, EC_CURVE_P_384);
                        break;
                    case ES_512:
                        json.put(EC_CURVE_KEY, EC_CURVE_P_521);
                        break;
                    // default is do nothing, just in case it is not an elliptic curve.https://test.cilogon.org/authorize?scope=openid&response_type=code&redirect_uri=https%3A%2F%2Flocalhost%3A9443%2Fnot-ready&state=tlStIUF5d59xv4ImkRF_KH-04UfOo3iSSgq-GU0BTwA&nonce=dvWmeva9HEHo-u4w1wLYS1B_3z55mRTQGBs_5QeFMug&prompt=login&client_id=test%3Atest%2Fucsd
                    // }
                }
            }
            JWK jwk = JWK.parse(json.toString());
            JSONWebKey jsonWebKey = new JSONWebKey(jwk);
            return jsonWebKey;
        } catch (ParseException parseException) {
            throw new GeneralException("error parsing json:" + parseException.getMessage(), parseException);
        }
    }

    public JSONWebKey getJsonWebKey(String rawJSON) {
        try {
            JWK jwk = JWK.parse(JSONObject.fromObject(rawJSON));
            JSONWebKey jsonWebKey = new JSONWebKey(jwk);
            return jsonWebKey;
        } catch (ParseException parseException) {
            throw new GeneralException("error parsing json:" + parseException.getMessage(), parseException);
        }
    }

    public JSONWebKey makePublic(JSONWebKey key) {
        JSONWebKey newKey = null;
        try {
            newKey = key.clone();
            newKey.privateKey = null;
            return newKey;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new NFWException("cloning not supporting on JSON web keys");
        }
    }

    public JSONWebKeys makePublic(JSONWebKeys keys) {
        JSONWebKeys newKeys = new JSONWebKeys(keys.getDefaultKeyID());
        for (String key : keys.keySet()) {
            newKeys.put(makePublic(keys.get(key)));
        }
        // now we have a clone with no private keys, we need to c
        return newKeys;
    }

    public JSONObject toJSON(JSONWebKeys webKeys) {
        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        for (String key : webKeys.keySet()) {
            array.add(toJSON(webKeys.get(key)));
        }
        json.put("keys", array);
        return json;
    }

    public JSONObject toJSON(JSONWebKey webKey) {
        if (webKey.hasJOSEJWK()) {
            return JSONObject.fromObject(webKey.getJOSEJWK().toString());
        }
        if (webKey.type.equals("RSA")) {
            return toJSONRSA(webKey);
        }
        if (webKey.type.equals("EC")) {
            return toJSONEC(webKey);
        }
        throw new IllegalArgumentException("unsupported key type \"" + webKey.type + "\"");
    }

    /**
     * This creats a JSON object from the {@link JSONWebKey} that serializes it in a spec compliant way, hence
     * can be turned back into a JOSE {@link JWK}.
     *
     * @param webKey
     * @return
     */
    protected static JSONObject initToJSON(JSONWebKey webKey) {
        JSONObject jsonKey = new JSONObject();
        jsonKey.put(ALGORITHM, webKey.algorithm);
        jsonKey.put(KEY_ID, webKey.id);
        jsonKey.put(USE, webKey.use);
        jsonKey.put(KEY_TYPE, webKey.type);
        if (webKey.issuedAt != null) {
            jsonKey.put(ISSUED_AT, webKey.issuedAt.getTime() / 1000);
        }
        if (webKey.notValidBefore != null) {
            jsonKey.put(NOT_VALID_BEFORE, webKey.notValidBefore.getTime() / 1000);
        }
        if (webKey.expiresAt != null) {
            jsonKey.put(EXPIRES_AT, webKey.expiresAt.getTime() / 1000);
        }
        return jsonKey;
    }

    protected JSONObject toJSONEC(JSONWebKey webKey) {
        JSONObject jsonKey = initToJSON(webKey);
        ECPublicKey ecPublicKey = (ECPublicKey) webKey.publicKey;
        ECPoint w = ecPublicKey.getW();
        jsonKey.put("x", bigIntToString(w.getAffineX()));
        jsonKey.put("y", bigIntToString(w.getAffineY()));
        jsonKey.put("crv", webKey.curve); //as per rfc7518.
        if (webKey.privateKey != null) {
            ECPrivateKey ecPrivateKey = (ECPrivateKey) webKey.privateKey;
            jsonKey.put("d", bigIntToString(ecPrivateKey.getS()));
        }
        return jsonKey;
    }

    protected JSONObject toJSONRSA(JSONWebKey webKey) {

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
    }


    /**
     * Create a webkey using the given named curve. Currently supported names are
     * <ul>
     *     <li>Ed25519</li>
     *     <li>Ed448</li>
     *     <li>P-256</li>
     *     <li>P-256K (deprecated, may be removed)</li>
     *     <li>P-384</li>
     *     <li>P-521</li>
     *     <li>X25519</li>
     *     <li>X448</li>
     *     <li>secp256k1</li>
     * </ul>
     *
     * @return
     */

    public JSONWebKey createECKey() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return createECKey(EC_CURVE_P_256, "ES256");
    }

    public JSONWebKey createECKey(String curveName, String alg) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Algorithm algorithm;
        switch (alg) {
            default:
            case ES_256:
                algorithm = JWSAlgorithm.ES256;
                break;
            case ES_384:
                algorithm = JWSAlgorithm.ES384;
                break;
            case ES_512:
                algorithm = JWSAlgorithm.ES512;
                break;
            case ES_256K:
                algorithm = JWSAlgorithm.ES256K;
                break;
        }
        Curve curve = Curve.parse(curveName);
        KeyPairGenerator gen = KeyPairGenerator.getInstance("EC");
        gen.initialize(curve.toECParameterSpec());
        KeyPair keyPair = gen.generateKeyPair();
        JWK jwk = new ECKey.Builder(curve, (ECPublicKey) keyPair.getPublic())
                .privateKey((ECPrivateKey) keyPair.getPrivate())
                .keyID(createID())
                .issueTime(new Date())
                .algorithm(algorithm)
                .keyUse(new KeyUse("sig"))
                .build();
        JSONWebKey jsonWebKey = new JSONWebKey(jwk);
        return jsonWebKey;
    }


    public JSONWebKey createRSAKey(int length, String alg) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        Algorithm algorithm;
        switch (alg) {
            case RS_512:
                algorithm = JWSAlgorithm.RS512;
                break;
            case RS_384:
                algorithm = JWSAlgorithm.RS384;
                break;
            default:
            case RS_256:
                algorithm = JWSAlgorithm.RS256;

        }
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        RSAKeyGenParameterSpec rsaKeyGenParameterSpec = new RSAKeyGenParameterSpec(length, RSAKeyGenParameterSpec.F4);
        gen.initialize(rsaKeyGenParameterSpec);
        KeyPair keyPair = gen.generateKeyPair();
        JWK jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(createID())
                .issueTime(new Date())
                .algorithm(algorithm)
                .keyUse(new KeyUse("sig"))
                .build();

        ;

        return new JSONWebKey(jwk);

    }

    public int getIdLength() {
        return idLength;
    }

    public void setIdLength(int idLength) {
        this.idLength = idLength;
    }

    int idLength = 8;

    /**
     * Create and RSA key with the default bit size (2048) and RS256 algorithm.
     *
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidAlgorithmParameterException
     */
    public JSONWebKey createRSAKey() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return createRSAKey(2048, RS_256);

    }

    /**
     * Create id with standard 8 byte length
     *
     * @return
     */
    protected String createID() {
        return createID(getIdLength());
    }

    protected String createID(int count) {
        Random random = new Random();
        byte[] bytes = new byte[count];
        random.nextBytes(bytes);
        return Hex.encodeHexString(bytes).toUpperCase();

    }

    protected String bigIntToString(BigInteger bigInteger) {
        return Base64.encodeBase64URLSafeString(bigInteger.toByteArray());
    }
}
