package edu.uiuc.ncsa.security.oauth_2_0;

import java.util.Collection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/15 at  2:04 PM
 */
public interface OA2Scopes {
    public static final String SCOPE_ADDRESS = "address";
    public static final String SCOPE_EMAIL = "email";
    public static final String SCOPE_MYPROXY = "edu.uiuc.ncsa.myproxy.getcert";
    public static final String SCOPE_OFFLINE_ACCESS = "offline_access";
    public static final String SCOPE_OPENID = "openid";
    public static final String SCOPE_PHONE = "phone";
    public static final String SCOPE_PROFILE = "profile";
    public static String SCOPE_CILOGON_INFO = "org.cilogon.userinfo";
    public static String EDU_PERSON_ORC_ID = "eduPersonOrcid";


    /**
     * These are the basic scopes supported by the OA4MP OIDC protocol.
     */
    public static String[] basicScopes = {SCOPE_EMAIL,SCOPE_MYPROXY, SCOPE_CILOGON_INFO, SCOPE_OPENID, SCOPE_PROFILE};


    /**
     * Utility that checks if a given scope is allowed by the protocol. The scopes in this interface
     * are all potentially supported by a server. Basic support only requires that the open id scope be
     * present.
     */
    public static class ScopeUtil {
        public static Collection<String> getScopes() {
            return scopes;
        }

        public static void setScopes(Collection<String> scopes) {
            ScopeUtil.scopes = scopes;
        }

        static Collection<String> scopes;

        public static boolean hasScope(String targetScope){
            for(String x : getScopes()){
                if(x.equals(targetScope)) return true;
            }
            return false;
        }

    }
}
