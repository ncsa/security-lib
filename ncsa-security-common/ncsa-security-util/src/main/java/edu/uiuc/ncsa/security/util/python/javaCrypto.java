package edu.uiuc.ncsa.security.util.python;

import edu.uiuc.ncsa.security.util.pkcs.KeyUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;

/**
 * A wrapper for the Java cryptography classes. This is a replacement for the python module M2Crypto
 * and simply wraps the java API calls. Import this as M2Crypto in your code and you can effectively use
 * this in place of M2Crypto.
 * <p>Created by Jeff Gaynor<br>
 * on 7/8/13 at  11:01 AM
 */
public class javaCrypto {
    public static void main(String[] args){
        try{
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
              String key = "aaaaaaaaaaaaaaaa";
              String textToEncryptpt = "1234567890123456";

              SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
              IvParameterSpec ivspec = new IvParameterSpec(key.getBytes());
              cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
              byte[] encrypted = cipher.doFinal(textToEncryptpt.getBytes());
              System.out.println(Base64.encodeBase64String(encrypted));

        }catch(Throwable t){
            t.printStackTrace();
        }
    }
    public static class Rand{
        static SecureRandom random;

        /**
         * Takes a string of random bytes
         * @param seed
         */
        public static void rand_seed(String seed){
            random = new SecureRandom(seed.getBytes());

        }
    }
    public static class RSA{
        KeyPair keyPair;
        public RSA gen_key(int keyLength, int exponent, Object callback) throws NoSuchProviderException, NoSuchAlgorithmException {
            RSA rsa = new RSA();
            keyPair = KeyUtil.generateKeyPair();
            return rsa;
        }
        public void save_key(String fullPath, String cipher, Object callback) throws IOException {
            KeyUtil.toPKCS1PEM(keyPair.getPrivate(), new FileOutputStream(fullPath));
        }
        public void save_pub_key(String fullPath) throws IOException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
            KeyUtil.toX509PEM(keyPair.getPublic(), new FileWriter(fullPath));
        }
    }
}
