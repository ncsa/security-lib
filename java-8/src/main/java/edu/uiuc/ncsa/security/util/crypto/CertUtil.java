package edu.uiuc.ncsa.security.util.crypto;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Feb 23, 2011 at  11:45:07 AM
 */

import org.apache.commons.codec.binary.Base64;
import sun.security.pkcs10.PKCS10;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.X500Name;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utilities for formatting certs and other things. Cert requests are handled in PKCS10 format.
 * Certificates are encoded in PEM as PKCS12 (default) or as PKCS7 if so requested.
 * <p>Created by Jeff Gaynor<br>
 * on Jun 15, 2010 at  2:45:47 PM
 */
public class CertUtil {

    public static final String UTF_8 = "UTF-8"; // encoding for all of this.
    public static final String BEGIN_PKCS7 = "-----BEGIN PKCS7-----";
    public static final String END_PKCS7 = "-----END PKCS7-----";

    static Logger logger;
    /**
     * This required by the specification
     */
    public static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERTIFICATE = "-----END CERTIFICATE-----";
    public static final String DEFAULT_PKCS10_SIGNATURE_ALGORITHM = "SHA1withRSA";
    public static final String DEFAULT_PKCS10_PROVIDER = "SunRsaSign";
    public static final String DEFAULT_PKCS10_DISTINGUISHED_NAME = "ignore";


    public static Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(CertUtil.class.getName());
        }
        return logger;
    }


    /**
     * A utility that will take a list of Base64 encoded X509 certificates and convert them into
     * an array of java.security.cert.X509Certificate objects.
     *
     * @param certList
     * @param nameList The array of file names corresponding to each cert. This is only used to generate error messages
     *                 and if it is omitted will not otherwise effect function.
     * @return
     * @throws CertificateException
     */
    public static X509Certificate[] getX509CertsFromStringList(
            String[] certList, String[] nameList)  {
        Collection<X509Certificate> c = new ArrayList<X509Certificate>(
                certList.length);
        for (int i = 0; i < certList.length; i++) {
            int index = -1;
            String certData = certList[i];
            if (certData != null) {
                index = certData.indexOf(BEGIN_CERTIFICATE);
            }
            if (index >= 0) {
                certData = certData.substring(index);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(
                        certData.getBytes());
                try {
                    X509Certificate cert = (X509Certificate) getCertFactory()
                            .generateCertificate(inputStream);
                    c.add(cert);
                } catch (Exception e) {
                    if (nameList != null) {
                        getLogger().warning(nameList[i]
                                + " can not be parsed as an X509Certificate.");
                    } else {
                        getLogger().warning("failed to parse an X509Certificate");
                    }
                }
            }
        }
        if (c.isEmpty())
            return null;
        return c.toArray(new X509Certificate[0]);
    }

    /**
     * Take an X509 cert and encode it correctly in PEM format, writing the result to the
     * output stream. This will check for a known condition in various security suites (e.g. Open SSL)
     * in which an extra line feed will cause the entire result to be rejected. This extra line feed
     * occurs in the odd case that the encoded cert's length is a multiple of the line length. The only
     * way to catch this is to encode it as a string, then check if the last character is a line feed.
     *
     * @param x509Certificate
     * @param outputStream
     * @throws CertificateEncodingException
     */
    public static void toPEM(X509Certificate x509Certificate, OutputStream outputStream) throws CertificateEncodingException {
        PEMFormatUtil.delimitBody(x509Certificate.getEncoded(), BEGIN_CERTIFICATE, END_CERTIFICATE, outputStream);
    }


    /**
     * Take a string that is an base64-encoded, DER-encoded PKCS10 certificate request,. This is, for instance
     * stored in a database and not PEM format.
     */

    public static MyPKCS10CertRequest fromStringToCertReq(String x) {
        return new MySunPKCS_CR(Base64.decodeBase64(x));
    }

    public static String fromCertReqToString(MyPKCS10CertRequest certReq) {
        if (certReq == null) return null;
        return PEMFormatUtil.bytesToChunkedString(certReq.getEncoded());
    }

    /**
     * Convert a string containing the PEM-format cert to a certificate. Useful if the cert has been
     * persisted, e.g., with a database as a string.
     *
     * @param certString
     * @return
     * @throws CertificateException
     */
    public static X509Certificate[] fromX509PEM(String certString) throws CertificateException {
        try {
            ByteArrayInputStream bs = new ByteArrayInputStream(certString.getBytes(UTF_8));
            return fromPEM(bs);
        } catch (Exception e) {
            throw new CryptoException("Error converting cert string", e);
        }

    }


    /**
     * @param inputStream
     * @return
     * @throws CertificateException
     */
    public static X509Certificate[] fromPEM(InputStream inputStream) throws CertificateException {
        /*
          This strips off the header and tail automatically, so we don't really have to do anything.
         */
        return getCertFactory().generateCertificates(inputStream).toArray(new X509Certificate[0]);
    }

    public static CertificateFactory getCertFactory() throws CertificateException {
        if (certFactory == null) {
            certFactory = CertificateFactory.getInstance("X.509");
        }
        return certFactory;
    }


    static CertificateFactory certFactory;

    /**
     * Encode to PEM.
     *
     * @param x509Certificate
     * @return
     */
    public static String toPEM(X509Certificate x509Certificate) {
        try {
            // The issue is that getEncoded returns a byte array in DER format, but
            // the inverse operation from the cert factory expects the header and strips it off.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            toPEM(x509Certificate, baos);
            return new String(baos.toByteArray(), UTF_8);
        } catch (Exception e) {
            throw new CryptoException("Error encoding cert", e);
        }

    }

    /**
     * Encode the certificates with the the default (PKCS 12).
     * @param x509Certificates
     * @param out
     */
    public static void toPEM(Collection<X509Certificate> x509Certificates, OutputStream out) {
        X509Certificate[] certs = new X509Certificate[x509Certificates.size()];
        certs = x509Certificates.toArray(certs);
        toPEM(certs, out);
    }

    public static String toPEM(Collection<X509Certificate> x509Certificates) {
        X509Certificate[] certs = new X509Certificate[x509Certificates.size()];
        certs = x509Certificates.toArray(certs);
        return toPEM(certs);
    }

    public static void toPEM(X509Certificate[] x509Certificates, OutputStream out) {
        PrintStream printStream = new PrintStream(out);
        printStream.print(toPEM(x509Certificates));
        printStream.flush();
    }


    /**
     * Encode a certificate in PKCS 7 format.
     * @param certs
     * @param out
     * @throws CertificateException
     */
    public static void toPKCS7(X509Certificate[] certs, OutputStream out) throws CertificateException {
        ArrayList<X509Certificate> certArrayList = new ArrayList<X509Certificate>(Arrays.asList(certs));

        CertPath cp = getCertFactory().generateCertPath(certArrayList);
        // Encode certificate in PKCS7 format
        Base64 encoder = new Base64(64); //chunk at 64 char
        String pkcsCert = new String(encoder.encode(cp.getEncoded("PKCS7"))).trim();
        pkcsCert = pkcsCert.replaceAll("\\r", "");// removes extra linefeed.
        PrintStream printStream = new PrintStream(out);
        printStream.println(BEGIN_PKCS7);
        printStream.println(pkcsCert);
        printStream.println(END_PKCS7);
        printStream.flush();
        printStream.close();
    }

    public static String toPEM(X509Certificate[] x509Certificates) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean firstPass = true;
        for (X509Certificate x509Certificate : x509Certificates) {
            if (firstPass) {
                stringBuffer.append(toPEM(x509Certificate));
                firstPass = false;
            } else {
                stringBuffer.append("\n" + toPEM(x509Certificate));
            }
        }
        return stringBuffer.toString();
    }

    /**
     * Create a certification request with a supplied distinguished name. This should be exactly
     * what you want it to be, e.g. the username.
     *
     * @param keypair
     * @param dn
     * @return
     * @throws SignatureException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static MyPKCS10CertRequest createCertRequest(KeyPair keypair, String dn) throws InvalidKeyException, NoSuchAlgorithmException {
        return createCertRequest(keypair, DEFAULT_PKCS10_SIGNATURE_ALGORITHM, dn, DEFAULT_PKCS10_PROVIDER);
    }

    /**
     * Create a certification request with the default distinguished name
     *
     * @param keypair
     * @return
     * @throws SignatureException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static MyPKCS10CertRequest createCertRequest(KeyPair keypair) throws SignatureException,
            NoSuchProviderException, InvalidKeyException, NoSuchAlgorithmException, IOException {
        return createCertRequest(keypair, DEFAULT_PKCS10_DISTINGUISHED_NAME);
    }

    /**
     * This is merely public in case you want to use it. Generally use the {@link #createCertRequest(KeyPair)}
     *
     * @param keypair
     * @param sigAlgName
     * @param provider
     * @param dn
     * @return
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchProviderException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static MyPKCS10CertRequest createCertRequest(KeyPair keypair,
                                                        String sigAlgName,
                                                        String dn,
                                                        String provider) throws
            InvalidKeyException,  NoSuchAlgorithmException {
        //String sigAlg = "SHA512WithRSA";
        PKCS10 pkcs10 = new PKCS10(keypair.getPublic());

        Signature signature = Signature.getInstance(sigAlgName);
        signature.initSign(keypair.getPrivate());
        try {
            X500Name x500Name = null;
            if (dn == null) {
                x500Name = new X500Name(DEFAULT_PKCS10_DISTINGUISHED_NAME, "OU", "OU", "USA");
            } else {
                x500Name = new X500Name(dn, "OU", "OU", "USA");
            }
            pkcs10.encodeAndSign(x500Name, signature);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bs);
            pkcs10.print(ps);
            byte[] c = bs.toByteArray();
            if (ps != null) {
                ps.close();
            }
            if (bs != null) {
                bs.close();
            }
        } catch (CryptoException rx) {
            throw rx;
        } catch (Throwable th) {
            throw new CryptoException("Error creating cert request", th);
        }
        return new MySunPKCS_CR(pkcs10);
    }

    public static class MySunPKCS_CR extends MyPKCS10CertRequest {
        public MySunPKCS_CR(byte[] derEncoded) {
            try {
                checkVersion(derEncoded);
                pkcs10 = new PKCS10(derEncoded);
            } catch (CryptoException re) {
                throw re;
            } catch (Exception e) {
                e.printStackTrace();
                throw new InvalidCertRequestException("Error creating cert request from byte array", e);
            }
        }


        /**
         * Fix for OAUTH-96, sort of. Some python clients send and invalid cert request
         * because the programmer does not set the version (to zero). Python then sends a
         * zero-length integer. Now, as this violates the PKCS10 spec., and should be rejected.
         * Bouncy Castle will ignore it but the Sun libraries will throw an extremely
         * unhelpful IOException. The method does the check and throws a much better exception.
         *
         * @param derEncoded
         */
        protected void checkVersion(byte[] derEncoded) {
            try {
                DerInputStream derInputStream = new DerInputStream(derEncoded);
                DerValue[] seq = derInputStream.getSequence(3); //try and get the first three elements.
                seq[0].data.getBigInteger();
            } catch (IOException iox) {
                throw new InvalidCertRequestException("Invalid Certification Request. Be sure that the version number " +
                        "of the (PCKS10) request is set to zero.", iox);
            }
        }

        @Override
        public String toString() {
            if (pkcs10 == null) return "null";
            return pkcs10.toString();
        }

        PKCS10 pkcs10;

        public MySunPKCS_CR(PKCS10 pkcs10) {
            this.pkcs10 = pkcs10;
        }

        @Override
        public String getCN() {
            if (pkcs10 == null) {
                return null;
            }
            try {
                return pkcs10.getSubjectName().getCommonName();
            } catch (IOException e) {
                throw new CryptoException("Could not get common name", e);
            }
        }

        @Override
        public PublicKey getPublicKey() {
            return pkcs10.getSubjectPublicKeyInfo();
        }

        @Override
        public byte[] getEncoded() {
            return pkcs10.getEncoded();
        }
    }

    public static String getDN(X509Certificate x509Certificate) {
        return x509Certificate.getSubjectDN().getName();
    }

    /**
     * <b>IF</b> the user has an EPPN or EPTID, it will be in a specific extension to
     * the cert. Since that requires a bit of Voodoo (the extension value) to get, this call will perform that,
     * returning either a null if no such item exists or the value.
     *
     * @param x509Certificate
     * @return
     */
    public static String getEPPN(X509Certificate x509Certificate) {
        byte[] rawEPPN = x509Certificate.getExtensionValue("1.3.6.1.4.1.5923.1.1.1.6");
        String eppn = null;
        if (rawEPPN != null) {
            eppn = (new String(rawEPPN)).substring(4);
        }
        return eppn;

    }

    public static String getEmail(X509Certificate x509Certificate) {
        Collection<List<?>> collection = null;
        try {
            collection = x509Certificate.getSubjectAlternativeNames();
        } catch (CertificateParsingException e) {
            throw new CryptoException("Error parsing cert", e);
        }
        if (collection == null || collection.isEmpty()) return null;
        String email = null;
        for (List object : collection) {
            email = object.get(1).toString();
        }
        return email;
    }

    /**
     * Command line utility to generate a keypair with a given DN. This is intended for low level
     * debugging, not public consumption. This is a very stupid utility but much more convenient
     * than using openSSL or some other such command line utility.
     * @param args
     */
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Usage: This accepts a single argument that is the DN for a cert request. It returns the pem encoded " +
                    "cert request (but not the private key)");
            return;
        }
        try{
            KeyPair keyPair = KeyUtil.generateKeyPair();
            MyPKCS10CertRequest cr = CertUtil.createCertRequest(keyPair, args[0]);
            System.out.println(CertUtil.fromCertReqToString(cr));
        }catch(Throwable t){

            t.printStackTrace();
        }
    }
}
