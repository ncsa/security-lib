package edu.uiuc.ncsa.security.util.ssl;

/**
 * Trust manager formerly part of MyProxy, but universally useful.
 */


import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.pkcs.CertUtil;

import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.*;
import java.util.*;

public class MyTrustManager implements X509TrustManager {
    public SSLConfiguration getSslConfiguration() {
        return sslConfiguration;
    }

    SSLConfiguration sslConfiguration = null;

    boolean debugOn = DebugUtil.isEnabled(); // set to false for release
    boolean stackTracesOn = false; // set to false for use as a library, true for standalone release

    public MyTrustManager(MyLoggingFacade logger,  SSLConfiguration sslConfiguration) {
        this.logger = logger;
        this.sslConfiguration = sslConfiguration;
        this.serverDN = sslConfiguration.getTrustRootCertDN();
        this.trustRootPath = sslConfiguration.getTrustrootPath();
    }

    public MyTrustManager(MyLoggingFacade logger, String trustRootPath, String serverDN) {
        this.logger = logger;
        this.serverDN = serverDN;
        this.trustRootPath = trustRootPath;
    }

    public boolean hasServerDN() {
        return serverDN != null;
    }

    public String getServerDN() {
        return serverDN;
    }

    public void setServerDN(String serverDN) {
        this.serverDN = serverDN;
    }

    String serverDN = null;

    MyLoggingFacade logger;

    public MyLoggingFacade getLogger() {
        if (logger == null) {
            logger = new MyLoggingFacade(MyTrustManager.class.getName());
            logger.setDebugOn(debugOn);
        }
        return logger;
    }


    protected boolean hasSSLConfiguration(){
        return sslConfiguration != null;
    }
    public final String DEFAULT_TRUST_ROOT_PATH = "/etc/grid-security/certificates";

    String trustRootPath = DEFAULT_TRUST_ROOT_PATH;

    public String getTrustRootPath() {
        return trustRootPath;
    }


    public void setTrustRootPath(String trustRootPath) {
        this.trustRootPath = trustRootPath;
    }


    public boolean isRequestTrustRoots() {
        return requestTrustRoots;
    }

    public void setRequestTrustRoots(boolean requestTrustRoots) {
        this.requestTrustRoots = requestTrustRoots;
    }

    boolean requestTrustRoots;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    void dbg(String x) {
      DebugUtil.dbg(this, x);
    }

    String host;

    /**
     * Try to read the key store certs from a directory of certificates.
     *
     * @return
     */
    protected X509Certificate[] getIssuersFromDirectory(File dir) {
        X509Certificate[] issuers = null;
        String certDirPath = getTrustRootPath();

        // Comment: This might fail for any number of reasons, such as having the
        // trust root path not exist. Throwing exceptions will not work here since
        // they will be intercepted by the SSL layer and not propagated to the caller...

        String[] certFilenames = dir.list();
        String[] certData = new String[certFilenames.length];
        for (int i = 0; i < certFilenames.length; i++) {
            try {
                FileInputStream fileStream = new FileInputStream(
                        certDirPath + File.separator + certFilenames[i]);
                byte[] buffer = new byte[fileStream.available()];
                fileStream.read(buffer);
                certData[i] = new String(buffer);
                fileStream.close();
            } catch (Exception e) {
                dbg("Exception Reading issues " + e.getMessage());
                // ignore
            }
        }
        try {
            issuers = CertUtil.getX509CertsFromStringList(certData, certFilenames);
            dbg("Got " + issuers.length + " issuers.");
        } catch (Exception e) {
            if (stackTracesOn) e.printStackTrace();
            dbg("Exception getting issuers. Returning null. " + e.getMessage());
        }
        return issuers;
    }

    /**
     * Read the certs for the key store from a JKS file
     *
     * @return
     */
    protected X509Certificate[] getIssuersFromFile(File certFile) {
        ArrayList<X509Certificate> issuerList = new ArrayList<>();
        try {

            FileInputStream fis = new FileInputStream(certFile);
            KeyStore keystore = KeyStore.getInstance(getSslConfiguration().getTrustRootType());
            keystore.load(fis, getSslConfiguration().getTrustRootPassword().toCharArray());
            Enumeration e = keystore.aliases();
            while (e.hasMoreElements()) {
                Certificate cert = keystore.getCertificate((String) e.nextElement());
                    if (cert instanceof X509Certificate) {
                        issuerList.add((X509Certificate) cert);
                }
            }
            fis.close();
            return issuerList.toArray(new X509Certificate[]{});
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public X509Certificate[] getAcceptedIssuers() {
        String certDirPath = getTrustRootPath();

        if (certDirPath == null) {
              dbg("cert dir path null. Aborting");
              return null;
          }
          File dir = new File(certDirPath);
          if (dir.isDirectory()) {
              dbg(" cert dir path is not a directory. Getting from file.");
              return getIssuersFromDirectory(dir);
          }
        return getIssuersFromFile(dir);
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType)
            throws CertificateException {
        throw new CertificateException(
                "checkClientTrusted not implemented by " + getClass().getName());
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType)
            throws CertificateException {
        checkServerCertPath(certs);
        checkServerDN(certs[0]);
    }

    protected void checkServerCertPath(X509Certificate[] certs)
            throws CertificateException {
        try {
            CertPathValidator validator = CertPathValidator
                    .getInstance(CertPathValidator.getDefaultType());
            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            CertPath certPath = certFactory.generateCertPath(Arrays
                    .asList(certs));
            X509Certificate[] acceptedIssuers = getAcceptedIssuers();
            if (acceptedIssuers == null) {
                String certDir = getTrustRootPath();
                if (certDir != null) {
                    throw new CertificateException(
                            "no CA certificates found in " + certDir);
                } else if (!isRequestTrustRoots()) {
                    throw new CertificateException(
                            "no CA certificates directory found");
                }
                getLogger()
                        .info("no trusted CAs configured -- bootstrapping trust from MyProxy server");
                acceptedIssuers = new X509Certificate[1];
                acceptedIssuers[0] = certs[certs.length - 1];
            }
            Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>(
                    acceptedIssuers.length);
            for (int i = 0; i < acceptedIssuers.length; i++) {
                TrustAnchor ta = new TrustAnchor(acceptedIssuers[i], null);
                trustAnchors.add(ta);
            }
            PKIXParameters pkixParameters = new PKIXParameters(trustAnchors);
            pkixParameters.setRevocationEnabled(false);
            validator.validate(certPath, pkixParameters);
        } catch (CertificateException e) {
            if (stackTracesOn) e.printStackTrace();
            throw e;
        } catch (GeneralSecurityException e) {
            if (stackTracesOn) e.printStackTrace();
            throw new CertificateException(e);
        } catch (Throwable t) {
            if (stackTracesOn) t.printStackTrace();
        }
    }

    /**
     * Parses the server DN for the CN and does a few sanity checks on the result before returning.
     *
     * @param subject
     * @return
     * @throws CertificateException
     */
    private String getCommonName(String subject) throws CertificateException {
        int index = subject.indexOf("CN=");
        if (index == -1) {
            throw new CertificateException("Server certificate subject ("
                    + subject + "does not contain a CN component.");
        }
        String CN = subject.substring(index + 3);
        index = CN.indexOf(',');
        if (index >= 0) {
            CN = CN.substring(0, index);
        }
        if ((index = CN.indexOf('/')) >= 0) {
            String service = CN.substring(0, index);
            CN = CN.substring(index + 1);
            if (!service.equals("host") && !service.equals("myproxy")) {
                dbg("common name =\"" + CN + "\" has unknown server element \"" + subject + "\"");

                throw new CertificateException(
                        "Server certificate subject CN contains unknown server element: "
                                + subject);
            }
        }

        return CN;
    }

    // Note that System.err is used for debugging because that will go to catalina.out whereas the current logging will send
    // these to a  central log that is very hard to sift through.
    private void checkServerDN(X509Certificate cert)
            throws CertificateException {
        // Essentially, we have to check that the CN and the host match,
        MyLoggingFacade ll = getLogger();
        String CN = getCommonName(cert.getSubjectX500Principal().getName());
        
        if (hasServerDN()) {
            // Fixes OAUTH-176: server DN can be injected.
            /*
             Note that since it is getting hard to track down the older OAUTH Jira tickets, I'll quote
             the original for OAUTH-176 here:

             -----
             Allow specification of subjectDN in the MyProxy configuration
             (http://grid.ncsa.illinois.edu/myproxy/oauth/server/configuration/server-myproxy.xhtml).
             This will give the OA4MP server functionality equivalent to MYPROXY_SERVER_DN in
             http://grid.ncsa.illinois.edu/myproxy/man/myproxy-logon.1.html and the -subject option
             in https://github.com/jglobus/JGlobus/blob/master/myproxy/src/main/java/org/globus/myproxy/MyProxyCLI.java
             to support the case where the hostname of the myproxy-server does not match the DN of the myproxy-server's
             host certificate. This is useful for testing and in DNS load-balancing setups.
             -----

             However, this means allowing injecting it in to the trust manager generally, which is also a useful thing
             to do for, say, clients with self-signed certs. 
             */
            String configuredCN = getCommonName(getServerDN());
            dbg(".checkServerDN: A server DN has been configured.");
            dbg(".checkServerDN: Configured serverDN has CN = " + configuredCN);

            if (CN.equals(configuredCN)) {
                return;
            }
            dbg(".checkServerDN: Configured serverDN check failed.");


        }
        dbg(".checkServerDN: Checking cert CN against hostname");

        // So if the server DN and the returned hostname do NOT match, check the cert name against the hostname
        if (getHost().equals("localhost")) {
            try {
                setHost(InetAddress.getLocalHost().getHostName());
            } catch (Exception e) {
                // ignore
            }
        }
        dbg(".checkServerDN: Configured host = " + getHost());
        dbg(".checkServerDN: host=CN? " + CN.equals(getHost()));

        if (!CN.equals(getHost())) {
            dbg("common name =\"" + CN + "\" does not match host from reverse lookup = \"" + host + "\"");
            throw new CertificateException(
                    "Server certificate subject CN (" + CN
                            + ") does not match server hostname (" + host
                            + ").");
        }
        dbg("Success! common name =\"" + CN + "\" matches host = \"" + host + "\"");

    }

}
