package edu.uiuc.ncsa.security.oauth_2_0.server.claims;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * This centralizes the creation of claims. It is transitional at this point for migrating the creation to right
 * after the authorization has completed rather than as part of creating the access token
 * <p>Created by Jeff Gaynor<br>
 * on 4/24/18 at  10:00 AM
 */
public abstract class ClaimsUtil {
    public List<ClaimSource> getGetClaimSources() {
        return getClaimSources;
    }

    public void setGetClaimSources(List<ClaimSource> getClaimSources) {
        this.getClaimSources = getClaimSources;
    }

    List<ClaimSource> getClaimSources;

    public JSONObject createClaims(HttpServletRequest request) throws Throwable {
        JSONObject claims = new JSONObject();
        createBasicClaims(request, claims);
        createSpecialClaims(request, claims);
        return claims;
    }

    protected abstract JSONObject createBasicClaims(HttpServletRequest request, JSONObject claims) throws Throwable;

    /**
     * This allows subclasses to put their additional code to handle claims here. The initial method will just pull things off the standard state
     * and deliver a very basic set of claims. For instance, all {@link ClaimSource} calls hould be put here.
     *
     * @param httpServletRequest
     * @param claims
     * @return
     * @throws Throwable
     */
    protected abstract JSONObject createSpecialClaims(HttpServletRequest httpServletRequest, JSONObject claims) throws Throwable;
}
