package edu.uiuc.ncsa.security.util.ssl;

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
        if(obj == null) return false;
        if(!(obj instanceof SSLConfiguration)) return false;
        SSLConfiguration ssl = (SSLConfiguration) obj;
        if(!checkEquals(ssl.getTlsVersion(), getTlsVersion())) return false;
        if(!checkEquals(ssl.getTrustRootPassword(), getTrustRootPassword())) return false;
        if(!checkEquals(ssl.getTrustrootPath(), getTrustrootPath())) return false;
        if(!checkEquals(ssl.getTrustRootType(), getTrustRootType())) return false;
        return super.equals(obj);
    }
}
