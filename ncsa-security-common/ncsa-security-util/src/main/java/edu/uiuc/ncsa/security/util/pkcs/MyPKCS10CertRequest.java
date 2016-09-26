package edu.uiuc.ncsa.security.util.pkcs;

import java.security.PublicKey;

/**
 * This fronts a PKCS 10 certification request. Since there are many implementations, some much
 * more finicky than others, this will let users choose which they should use.
 * <p>Created by Jeff Gaynor<br>
 * on 10/16/13 at  10:46 AM
 */
public abstract class MyPKCS10CertRequest {

    abstract public byte[] getEncoded();
    abstract public PublicKey getPublicKey();
    abstract public String getCN();
}
