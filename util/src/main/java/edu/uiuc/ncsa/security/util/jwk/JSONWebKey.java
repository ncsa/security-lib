package edu.uiuc.ncsa.security.util.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

/**
 * An object containing a <a href="https://tools.ietf.org/html/rfc7517">JSON web key</a>. This contains and allows access
 * to all the components of the key. Typically these are not modified once the key has been created since changing them
 * is apt to result in a bad key.
 * <p>Created by Jeff Gaynor<br>
 * on 1/6/17 at  2:46 PM
 */
public class JSONWebKey implements Serializable {
    public JSONWebKey() {
    }

    boolean rsaKey = true;
    boolean ecKey = false;
    /**
     * Is this an RSA key?
     * @return
     */
    public boolean isRSAKey() {
return rsaKey;    }

    public boolean isECKey(){
return ecKey;
    }

    public JSONWebKey(JWK jwk) {
        id = jwk.getKeyID();
        algorithm = jwk.getAlgorithm().getName();
        use = jwk.getKeyUse().getValue();
        type = jwk.getKeyType().getValue();
        // Note that times are to the nearest second, not millisecond since JOSE does
        // that. This is the standard, but we usually like to record the exact time.
        issuedAt = jwk.getIssueTime();
        notValidBefore = jwk.getNotBeforeTime();
        expiresAt = jwk.getExpirationTime();

        if (jwk instanceof ECKey) {
            curve = ((ECKey) jwk).getCurve().getName();
        }
        this.JOSEJWK = jwk;
        try {
            if (jwk.getKeyType().getValue().equals("RSA")) {
                rsaKey = true; ecKey=false;
                privateKey = jwk.toRSAKey().toPrivateKey();
                publicKey = jwk.toRSAKey().toPublicKey();
                return;
            }
            if (jwk.getKeyType().getValue().equals("EC")) {
                rsaKey = false; ecKey = true;
                privateKey = jwk.toECKey().toPrivateKey();
                publicKey = jwk.toECKey().toPublicKey();
                return;
            }
            throw new GeneralException("unknown key type \"" + jwk.getKeyType().getValue() + "\"");
        } catch (JOSEException joseException) {
            throw new GeneralException("error creating key: " + joseException.getMessage(), joseException);
        }
    }

    public JWK getJOSEJWK() {
        return JOSEJWK;
    }

    public void setJOSEJWK(JWK JOSEJWK) {
        this.JOSEJWK = JOSEJWK;
    }

    public JWK JOSEJWK;

    public boolean hasJOSEJWK() {
        return JOSEJWK != null;
    }

    /**
     * This is used for elliptic curves and is the name of the curve used.
     */
    public String curve;

    public String id;
    public String algorithm;
    public String use;
    public String type;
    public PublicKey publicKey;
    public PrivateKey privateKey;
    public Date issuedAt = null;
    public Date notValidBefore = null;
    public Date expiresAt = null;


    @Override
    protected JSONWebKey clone() throws CloneNotSupportedException {
        JSONWebKey newKey = new JSONWebKey();
        newKey.id = id;
        newKey.algorithm = algorithm;
        newKey.use = use;
        newKey.type = type;
        newKey.publicKey = publicKey;
        newKey.privateKey = privateKey;
        newKey.expiresAt = expiresAt;
        newKey.issuedAt = issuedAt;
        newKey.notValidBefore = notValidBefore;
        return newKey;
    }

    @Override
    public String toString() {
        return "JSONWebKey{" +
                "id='" + id + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", use='" + use + '\'' +
                ", type='" + type + '\'' +
                ", expires at='" + (expiresAt == null ? "not set" : expiresAt) + '\'' +
                ", issued at='" + (issuedAt == null ? "not set" : issuedAt) + '\'' +
                ", not valid before='" + (notValidBefore == null ? "not set" : notValidBefore) + '\'' +
                ", publicKey=" + publicKey +
                ", privateKey=" + privateKey +
                '}';
    }
}
