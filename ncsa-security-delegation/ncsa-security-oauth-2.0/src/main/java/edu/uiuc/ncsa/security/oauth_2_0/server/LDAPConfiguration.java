package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;

import javax.naming.Name;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;
import java.util.HashMap;
import java.util.Map;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkEquals;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/3/16 at  11:17 AM
 */
public class LDAPConfiguration {
    String server;
    int port = -1;
    SSLConfiguration sslConfiguration;

    public String getSearchNameKey() {
        return searchNameKey;
    }

    public void setSearchNameKey(String searchNameKey) {
        this.searchNameKey = searchNameKey;
    }

    String searchNameKey;
    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    String securityPrincipal;

    public String getSearchBase() {
        return searchBase;
    }

    public void setSearchBase(String searchBase) {
        this.searchBase = searchBase;
    }

    String searchBase;

    Map<String,LDAPConfigurationUtil.AttributeEntry> searchAttributes = new HashMap<>();

    /**
     * Search attributes are recorded as a map. The key  is the search term in the LDAP query. The value
     * is the name that should be returned for this attribute in the claim.
     * @return
     */
    public Map<String,LDAPConfigurationUtil.AttributeEntry> getSearchAttributes() {
        return searchAttributes;
    }

    public void setSearchAttributes(Map<String,LDAPConfigurationUtil.AttributeEntry> searchAttributes) {
        this.searchAttributes = searchAttributes;
    }

    /**
     * If this is disabled (or there is no configuration for one) then the LDAP scope handler should
     * not be created, just a basic one.
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    boolean enabled = false;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String password;

    public int getPort() {
        return port;
    }

    public void setPort(int  port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public SSLConfiguration getSslConfiguration() {
        return sslConfiguration;
    }

    public void setSslConfiguration(SSLConfiguration sslConfiguration) {
        this.sslConfiguration = sslConfiguration;
    }

    /**
     * This will return the corresponding number for the security authorization (see constants in {@link LDAPConfigurationUtil})
     * which can be used for switch statements.
     * @return
     */
    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    int authType = LDAPConfigurationUtil.LDAP_AUTH_UNSPECIFIED_KEY;
    String contextName;

    /**
     * The name of the context for the JNDI {@link LdapContext#search(Name, Attributes)} function. If this is omitted
     * in the configuration, then it is set to the empty string.
     * @return
     */
    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    boolean failOnError = false;
    boolean notifyOnFail = false;

    public boolean isFailOnError() {
        return failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public boolean isNotifyOnFail() {
        return notifyOnFail;
    }

    public void setNotifyOnFail(boolean notifyOnFail) {
        this.notifyOnFail = notifyOnFail;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LDAPConfiguration)) return false;
        LDAPConfiguration ldap = (LDAPConfiguration) obj;
        if(!checkEquals(ldap.getContextName(), getContextName())) return false;
        if(!checkEquals(ldap.getPassword(), getPassword())) return false;
        if(!checkEquals(ldap.getSecurityPrincipal(), getSecurityPrincipal())) return false;
        if(!checkEquals(ldap.getSearchBase(), getSearchBase())) return false;
        if(!checkEquals(ldap.getServer(), getServer())) return false;
        if(ldap.getPort() != getPort()) return false;
        if(ldap.getAuthType() != getAuthType()) return false;
        if(!ldap.getSslConfiguration().equals(getSslConfiguration())) return false;
        return true;
    }

    @Override
    public String toString() {
        return "LDAPConfiguration{" +
                "authType=" + authType +
                ", server='" + server + '\'' +
                ", port=" + port +
                ", sslConfiguration=" + sslConfiguration +
                ", searchNameKey='" + searchNameKey + '\'' +
                ", securityPrincipal='" + securityPrincipal + '\'' +
                ", searchBase='" + searchBase + '\'' +
                ", searchAttributes=" + searchAttributes +
                ", enabled=" + enabled +
                ", password='" + password + '\'' +
                ", contextName='" + contextName + '\'' +
                ", failOnError=" + failOnError +
                ", notifyOnFail=" + notifyOnFail +
                '}';
    }
}
