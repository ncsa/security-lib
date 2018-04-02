package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.ServiceTransaction;
import edu.uiuc.ncsa.security.oauth_2_0.UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * This is charged with modelling the source ofor sets of claims. Note that the contract
 * of the standard implementation is to
 * have a no argument constructor that has the scopes you set in the configuration file injected.
 * Scopes are then used internally to determine which claims are returned.
 * <p>Created by Jeff Gaynor<br>
 * on 8/17/15 at  2:28 PM
 */
public interface ClaimSource {
    /**
     * A {@link UserInfo} object and the current service transaction are supplied. The contract is that
     * this handler will receive a UserInfo object with standard information in place for
     * the request, but may then populate a UserInfo object and return it. Whatever is returned will be
     * serialized in JSON and returned as the response from the user info request.
     * @param userInfo
     * @param transaction
     * @return
     * @throws UnsupportedScopeException
     */
    public UserInfo process(UserInfo userInfo, ServiceTransaction transaction) throws UnsupportedScopeException;

    // Resolves OAUTH-199, pass in servlet request to scope handler.

    public UserInfo process(UserInfo userInfo, HttpServletRequest request, ServiceTransaction transaction) throws UnsupportedScopeException;
    public void setScopes(Collection<String> scopes);

    /**
     * A list of scopes that this handler supports. Any scope that is not recognized by this handler should
     * be rejected.
     * @return
     */
    public Collection<String> getScopes();

    /**
     * in order to support server discovery, every plugin must enumerate whatever claims it may
     * serve. This is not a guarantee that all of these claims will be delivered, just that they
     * might be.
     * @return
     */
    public Collection<String> getClaims();

    public boolean isEnabled();
}
