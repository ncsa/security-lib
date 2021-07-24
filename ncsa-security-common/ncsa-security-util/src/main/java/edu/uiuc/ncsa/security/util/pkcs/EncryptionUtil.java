package edu.uiuc.ncsa.security.util.pkcs;

import org.apache.commons.codec.digest.DigestUtils;
import org.jasypt.util.text.BasicTextEncryptor;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * test utility to password protect files.
 * <h2>Usage</h2>
 * encrypt - returns an encrypted text with header.
 * isEncrypted - checks if the argument has been encrypted with this utility
 *               Don't call decrypt without this
 * decrypt - returns unencrypted contents.
 * <h2>Note</h2>
 * This requires a password and a string to be encrypted.
 * The longer the password, the better the protection.
 * <p>Created by Jeff Gaynor<br>
 * on 7/19/21 at  9:52 AM
 */
public class EncryptionUtil {
    // idea is that since this enrypts text, have a header like
    // password_protected:(40 chars of SHA 1 hash)
    // encrypted text.
    // So first line has that it requires a password and there is a hash of
    // it. 
    public static String PASSWORD_HEADER = "password_protected:";

    public static void main(String[] args) throws IllegalAccessException {
       // System.out.print("enter password>");
        //char[] pwd = System.console().readPassword("");
        // String password = new String(pwd);

        String password = "my-password"; // cannot read password in IDE. Fake it
        byte[] bytes = new byte[200];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        String original = Base64.getEncoder().encodeToString(bytes);
        String encrypted = encrypt(original, password);
        System.out.println(encrypted);
        String decrypted = decrypt(encrypted, password);
        System.out.println("ok?" + decrypted.equals(original));


    }
    public static String encrypt(String text, String password){
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(password.toCharArray());

        String myEncryptedText = textEncryptor.encrypt(text);
        String hashed = DigestUtils.sha1Hex(password);
        return PASSWORD_HEADER + hashed + "\n" + myEncryptedText;
    }

    public static String decrypt(String encoded, String password) throws IllegalAccessException {
        int cr_index = encoded.indexOf("\n");
        String header= encoded.substring(0,cr_index);
        // assumes isEncrypted was called so this is a legit request.
        String storedHash = header.substring(PASSWORD_HEADER.length());
        String hashed = DigestUtils.sha1Hex(password);
        if(!hashed.equals(storedHash)){
            throw new IllegalAccessException("error: Incorrect password");
        }
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(password.toCharArray());

        String textToDecrypt =  encoded.substring(cr_index+1);
        String plainText = textEncryptor.decrypt(textToDecrypt);
        return plainText;
    }
    public static boolean isEncrypted(String text){
        return text.startsWith(PASSWORD_HEADER);
    }
}
