package edu.uiuc.ncsa.security.util.pkcs;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A utility for doing certain security-related operations, such as creating key pairs, serializing them and deserializing them
 * to PEM formats.<br><br>. In a nutshell you can
 * <ul>
 * <li>Generate key pairs</li>
 * <Li>Read and write PKCS 8 format private keys</li>
 * <li>Read and write X509 encoded public keys</li>
 * <li>Write PKCS 1 private keys</li>
 * </ul>
 * There is no call to read in a PKCS 1 format pem; these are private keys that start with
 * <code>-----BEGIN RSA PRIVATE KEY-----</code>. This requires laborious parsing of ASN 1 objects and so
 * far there is not much of a need for it. Java much prefers the newer and more secure PKCS 8 format which you should
 * use if possible.
 * <p>All methods are static and if you need something other than the defaults, set them before first use.
 * <p>Created by Jeff Gaynor<br>
 * on Jun 15, 2010 at  4:51:25 PM
 */
public class KeyUtil {

    public static final String BEGIN_RSA_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String END_RSA_PRIVATE_KEY = "-----END RSA PRIVATE KEY-----";

    public static final String BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";
    public static final String END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    public static final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    public static final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";

    /**
     * Write a PEM format PKCS1 private using a writer.
     *
     * @param privateKey
     * @param writer
     * @throws IOException
     */

    public static void toPKCS1PEM(PrivateKey privateKey, Writer writer) throws IOException {
        String pem = toPKCS1PEM(privateKey);
        writer.write(pem);
        writer.flush();
    }

    public static void toPKCS1PEM(PrivateKey privateKey, OutputStream out) throws IOException {
        PrintStream printStream = new PrintStream(out);
        printStream.print(toPKCS1PEM(privateKey));
        printStream.flush();

    }


    /**
     * Take a private key and put it into PKCS 1 format. These are used, e.g., by OpenSSL.
     *
     * @return
     * @throws IOException
     */
    public static String toPKCS1PEM(PrivateKey privateKey) throws IOException {
        byte[] bytes = privateKey.getEncoded();
        return PEMFormatUtil.delimitBody(PEMFormatUtil.bytesToChunkedString(bytes), BEGIN_RSA_PRIVATE_KEY, END_RSA_PRIVATE_KEY);
    }

    public static void main(String[] args) throws Exception {
        //Reader r = new FileReader("/home/ncsa/temp/zzz/private.pem");
        Reader privateFR = new FileReader("/home/ncsa/temp/zzz/openssl-private.pem");
        Reader publicFR = new FileReader("/home/ncsa/temp/zzz/openssl-public.pem");
        PrivateKey privateKey = fromPKCS1PEM(privateFR);
        PublicKey publicKey = fromX509PEM(publicFR);
        //KeyPair keyPair = keyPairFromPKCS1(publicKey, privateKey);
        System.out.println("keypair valid = " + validateKeyPair(publicKey, privateKey));
        KeyPair kp = generateKeyPair();
        System.out.println("generated keypair:" + kp);
    }

    public static PrivateKey fromPKCS1PEM(Reader reader) throws Exception {
        return fromPKCS1PEM(PEMFormatUtil.readerToString(reader));
    }


    /**
     * Read a PKCS 1 format pem and return the private key.  Read the <a href="https://www.rfc-editor.org/rfc/rfc3447#page-44">RSA spec</a>
     *
     * @param pem
     * @return
     * @throws Exception
     */
    public static PrivateKey fromPKCS1PEM(String pem) throws Exception {
        byte[] bytes = PEMFormatUtil.getBodyBytes(pem, BEGIN_RSA_PRIVATE_KEY, END_RSA_PRIVATE_KEY);

        DerInputStream derReader = new DerInputStream(bytes);
        DerValue[] sequence = derReader.getSequence(0);
        // skip the version at index 0
        //  Note that getting the big integers this way automatically corrects so that the result is always positive.
        // We have do this manually in the JSONWebKeyUtil.
        BigInteger modulus = sequence[1].getBigInteger();
        BigInteger publicExp = sequence[2].getBigInteger();
        BigInteger privateExp = sequence[3].getBigInteger();
        BigInteger prime1 = sequence[4].getBigInteger();
        BigInteger prime2 = sequence[5].getBigInteger();
        BigInteger exp1 = sequence[6].getBigInteger();
        BigInteger exp2 = sequence[7].getBigInteger();
        BigInteger crtCoef = sequence[8].getBigInteger();

        RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec =
                new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(rsaPrivateCrtKeySpec);
    }

    public static KeyPair keyPairFromPKCS1(Reader r) throws Exception {
        return keyPairFromPKCS1(PEMFormatUtil.readerToString(r));
    }

    /**
     * Verifies that a given keypair is correct.
     * A positive result means they are <i>most likely</i> correct. A failure
     * means they most certainly do not match.
     *
     * @param keyPair
     * @return
     */
    public static boolean validateKeyPair(KeyPair keyPair) throws Exception {
        return validateKeyPair(keyPair.getPublic(), keyPair.getPrivate());
    }

    /**
     * See {@link #validateKeyPair(KeyPair)}
     * @param publicKey
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static boolean validateKeyPair(PublicKey publicKey, PrivateKey privateKey) throws Exception {
        byte[] challenge = new byte[10000];
        ThreadLocalRandom.current().nextBytes(challenge); // Get really random bytes

        // sign using the private key
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(challenge);
        byte[] signature = sig.sign();

        // verify signature using the public key
        sig.initVerify(publicKey);
        sig.update(challenge);

        return sig.verify(signature);
    }

    /**
     * Read a PKCS 1 key in and generate the keypair from it.
     *
     * @param pem
     * @return
     * @throws Exception
     */
    public static KeyPair keyPairFromPKCS1(String pem) throws Exception {
        byte[] bytes = PEMFormatUtil.getBodyBytes(pem, BEGIN_RSA_PRIVATE_KEY, END_RSA_PRIVATE_KEY);

        DerInputStream derReader = new DerInputStream(bytes);
        DerValue[] seq = derReader.getSequence(0);
        // skip version seq[0];
        BigInteger modulus = seq[1].getBigInteger();
        BigInteger publicExp = seq[2].getBigInteger();
        BigInteger privateExp = seq[3].getBigInteger();
        BigInteger prime1 = seq[4].getBigInteger();
        BigInteger prime2 = seq[5].getBigInteger();
        BigInteger exp1 = seq[6].getBigInteger();
        BigInteger exp2 = seq[7].getBigInteger();
        BigInteger crtCoef = seq[8].getBigInteger();

        RSAPrivateCrtKeySpec keySpec =
                new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, publicExp);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return new KeyPair(publicKey, privateKey);
    }

    public static void toX509PEM(PublicKey publicKey, Writer writer) throws IOException {
        writer.write(toX509PEM(publicKey));
        writer.flush();
    }

    public static String toX509PEM(PublicKey publicKey) {
        byte[] bytes = publicKey.getEncoded();
        return PEMFormatUtil.delimitBody(PEMFormatUtil.bytesToChunkedString(bytes), BEGIN_PUBLIC_KEY, END_PUBLIC_KEY);
    }

    /**
     * DER encoding for the private key.
     *
     * @param privateKey
     * @return
     */
    public static byte[] toDER(PrivateKey privateKey) {
        return privateKey.getEncoded();
    }

    public static byte[] toDER(PublicKey publicKey) {
        return publicKey.getEncoded();
    }


    public static byte[] privateToDER(KeyPair keyPair) {
        return toDER(keyPair.getPrivate());
    }


    public static byte[] publicToDER(KeyPair keyPair) {
        return toDER(keyPair.getPublic());
    }

    /**
     * Decode a PKCS #8 encoded private key. OpenSSL, for instance, does not put out this format
     * automatically. The standard command will generate a PEM file, e.g.,
     * <code>
     * openssl genrsa -out privkey.pem 2048
     * </code>
     * so you must convert it e.g., with the following command:<br><br>
     * <code>
     * openssl pkcs8 -topk8 -nocrypt -in privkey.pem -inform PEM -out privkey.der -outform DER
     * </code><br><br>
     * The result is that you have two copies of the private key. The one ending with extension .der
     * (which is binary) can be imported using this method. Doing this conversion in Java is well past the scope of this
     * utility. If you use this on a key in the wrong format you will get an exception.
     *
     * @param encodedPrivate
     * @return
     * @throws edu.uiuc.ncsa.security.core.exceptions.GeneralException
     */
    public static PrivateKey fromPKCS8DER(byte[] encodedPrivate) {
        try {
            PKCS8EncodedKeySpec encodedPrivatePKCS8 = new PKCS8EncodedKeySpec(encodedPrivate);
            return getKeyFactory().generatePrivate(encodedPrivatePKCS8);
        } catch (Exception e) {
            throw new GeneralException("Could not decode private key", e);
        }
    }


    public static String toPKCS8PEM(PrivateKey privateKey) {
        return PEMFormatUtil.delimitBody(privateKey.getEncoded(), BEGIN_PRIVATE_KEY, END_PRIVATE_KEY);
    }

    public static void toPKCS8PEM(PrivateKey privateKey, Writer writer) throws IOException {
        writer.write(toPKCS8PEM(privateKey));
        writer.flush();
    }

    /**
     * This takes the PEM encoding of a PKCS 8 format private key, strips the header and footer, converts
     * to bytes then invokes {@link #fromPKCS8DER(byte[])}.
     * You can get a PKCS #8 private key that is PEM encoded from open ssl e.g. with
     * <code>
     * openssl pkcs8 -topk8 -nocrypt -in privkey.pem -inform PEM -out privkey-pkcs8.pem -outform PEM
     * </code><br><br>
     *
     * @param pem
     * @return
     * @throws edu.uiuc.ncsa.security.core.exceptions.GeneralException
     */
    public static PrivateKey fromPKCS8PEM(String pem) throws GeneralException {
        return fromPKCS8DER(PEMFormatUtil.getBodyBytes(pem, BEGIN_PRIVATE_KEY, END_PRIVATE_KEY));
    }

    /**
     * Public keys are encoded with the X509 public key spec.
     *
     * @param encodedPublic
     * @return
     */
    public static PublicKey fromX509PEM(String encodedPublic) {
        return fromX509DER(PEMFormatUtil.getBodyBytes(encodedPublic, BEGIN_PUBLIC_KEY, END_PUBLIC_KEY));
    }


    public static PublicKey fromX509DER(byte[] encodedPublic) {
        X509EncodedKeySpec x = new X509EncodedKeySpec(encodedPublic);
        try {
            return getKeyFactory().generatePublic(x);
        } catch (Exception e) {
            throw new GeneralException("Could not decode public key", e);
        }
    }


    public static int getKeyLength() {
        return keyLength;
    }

    public static void setKeyLength(int length) {
        keyLength = length;
    }

    static int keyLength = 2048;

    public static KeyPairGenerator getKeyPairGenerator() throws NoSuchProviderException, NoSuchAlgorithmException {
        if (keyPairGenerator == null) {
            keyPairGenerator = KeyPairGenerator.getInstance(getKeyAlgorithm());
            keyPairGenerator.initialize(getKeyLength());
        }
        return keyPairGenerator;
    }

    public static void setKeyPairGenerator(KeyPairGenerator generator) {
        keyPairGenerator = generator;
    }


    static KeyPairGenerator keyPairGenerator;


    public static KeyPair generateKeyPair() throws NoSuchProviderException, NoSuchAlgorithmException {
        return getKeyPairGenerator().generateKeyPair();
    }

    public static String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public static void setKeyAlgorithm(String algorithm) {
        keyAlgorithm = algorithm;
    }

    protected static String keyAlgorithm = "RSA";
    protected static KeyFactory keyFactory;

    public static KeyFactory getKeyFactory() throws NoSuchAlgorithmException {
        if (keyFactory == null) {
            keyFactory = KeyFactory.getInstance(getKeyAlgorithm());
        }
        return keyFactory;
    }

    public static PrivateKey fromPKCS8PEM(Reader reader) throws IOException {
        return fromPKCS8PEM(PEMFormatUtil.readerToString(reader));
    }

    public static PublicKey fromX509PEM(Reader reader) throws IOException {
        return fromX509PEM(PEMFormatUtil.readerToString(reader));
    }

}
