package edu.uiuc.ncsa.security.util.ssl;

import java.security.cert.X509Certificate;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkEquals;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Jun 12, 2010 at  9:27:22 AM
 */
public class SSLConfiguration extends SSLKeystoreConfiguration {

    public SSLConfiguration() {
    }

    public void setTrustRootPath(String trustRootPath) {
        this.trustRootPath = trustRootPath;
    }

    String trustRootPassword;

    public boolean isStrictHostnames() {
        return strictHostnames;
    }

    public void setStrictHostnames(boolean hostNameVerificationOff) {
        this.strictHostnames = hostNameVerificationOff;
    }

    boolean strictHostnames = true;

    public boolean isUseDefaultTrustManager() {
        return useDefaultTrustManager;
    }

    public void setUseDefaultTrustManager(boolean useDefaultTrustManager) {
        this.useDefaultTrustManager = useDefaultTrustManager;
    }

    boolean useDefaultTrustManager = true;

    /**
     * This is used in the trust root manager to check against the certificate DN. This is useful if there are
     * self-signed certs, since when the TrustManager invokes {@link javax.net.ssl.X509TrustManager#checkServerTrusted(X509Certificate[], String)}
     * it invokes checkServerDN to verify that the name on the certificate matches the lookup. Normally you do
     * not have to set this BUT in cases of self-signed certs, it may need to be manually set since the lookup for the
     * hostname (especially localhost) might not work quite as expected without a lot of hacking of the DNS.
     * Most common use case is this is in the client's SSL configuration and is set to "CN=localhost" for a self-signed cert.
     *
     * @return
     */
    public String getTrustRootCertDN() {
        return trustRootCertDN;
    }

    public void setTrustRootCertDN(String trustRootCertDN) {
        this.trustRootCertDN = trustRootCertDN;
    }

    public boolean hasCertDN() {
        return trustRootCertDN != null;
    }

    String trustRootCertDN;

    public String getTrustRootType() {
        return trustRootType;
    }

    public void setTrustRootType(String trustRootType) {
        this.trustRootType = trustRootType;
    }

    public String getTrustRootPassword() {
        return trustRootPassword;
    }

    public void setTrustRootPassword(String trustRootPassword) {
        this.trustRootPassword = trustRootPassword;
    }

    String trustRootType;

    String trustRootPath; // = "/etc/grid-security/certificates";

    public String getTlsVersion() {
        return tlsVersion;
    }

    public void setTlsVersion(String tlsVersion) {
        this.tlsVersion = tlsVersion;
    }

    String tlsVersion = "TLS"; // This uses the default in Java.


    public String getTrustrootPath() {
        return trustRootPath;
    }

    public String toString() {
        String x = super.toString();
        x = x + "[trust root path=" + getTrustrootPath() + "]";
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof SSLConfiguration)) return false;
        SSLConfiguration ssl = (SSLConfiguration) obj;
        if (!checkEquals(ssl.getTlsVersion(), getTlsVersion())) return false;
        if (!checkEquals(ssl.getTrustRootPassword(), getTrustRootPassword())) return false;
        if (!checkEquals(ssl.getTrustrootPath(), getTrustrootPath())) return false;
        if (!checkEquals(ssl.getTrustRootType(), getTrustRootType())) return false;
        return super.equals(obj);
    }
}
