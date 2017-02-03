package edu.uiuc.ncsa.security.util.jwk;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/6/17 at  2:46 PM
 */
public class JSONWebKey {
    public String id;
    public String algorithm;
    public String use;
    public String type;
    public PublicKey publicKey;
    public PrivateKey privateKey;

    @Override
    protected JSONWebKey clone() throws CloneNotSupportedException {
        JSONWebKey newKey = new JSONWebKey();
        newKey.id = id;
        newKey.algorithm = algorithm;
        newKey.use = use;
        newKey.type = type;
        newKey.publicKey = publicKey;
        newKey.privateKey = privateKey;
        return newKey;
    }
}
