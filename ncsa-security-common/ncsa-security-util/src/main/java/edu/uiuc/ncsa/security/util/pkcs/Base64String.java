package edu.uiuc.ncsa.security.util.pkcs;

import org.apache.commons.codec.binary.Base64;

/**
 * Wrapper for Base 64 encoded strings. The assumption is that this is created around a string
 * that is a base 64 encoding of something. If you use the byte constructor,{@link Base64String#Base64String(byte[])}
 * this will
 * do the base 64 encoding for you. This allows typing so we don't pass b64 encoded strings
 * around by accident. calling <code>toString</code> returns the <b>decoded</b> value.
 * <p>Created by Jeff Gaynor<br>
 * on 10/16/13 at  11:10 AM
 */
public class Base64String {
    String b64String;

    public Base64String(String b64String) {
        this.b64String = b64String;
    }
    public Base64String(byte[] rawbytes) {
           this(Base64.encodeBase64URLSafeString(rawbytes));
       }

    @Override
    public String toString() {
        return b64String;
    }

    /**
     * Encode the binary into a base 64 string. You may undo this with {@link #decodeValue()};
     * @param bytes
     * @return
     */
    public String encodeValue(byte[] bytes){
        b64String = Base64.encodeBase64String(bytes);
        return b64String;
    }

    public byte[] decodeValue(){
        if(b64String == null){
            throw new IllegalStateException("Error: the base 64 string is null");
        }
        return Base64.decodeBase64(b64String);
    }

    /**
     * Equivalent to <code>toString</code>.Returns the base 64 encoded string itself.
     * @return
     */
    public String getValue(){
        return toString();
    }
}
