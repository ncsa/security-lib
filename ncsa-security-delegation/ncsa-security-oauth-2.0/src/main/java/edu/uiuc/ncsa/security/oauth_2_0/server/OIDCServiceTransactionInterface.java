package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.oauth_2_0.jwt.FlowStates;
import net.sf.json.JSONObject;

import java.util.Collection;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/20 at  5:46 PM
 */
public interface OIDCServiceTransactionInterface {
    FlowStates getFlowStates();

    void setFlowStates(FlowStates flowStates);

    Collection<String> getScopes();

    void setScopes(Collection<String> scopes);

    List<String> getAudience();

    void setAudience(List<String> audience);

    JSONObject getExtendedAttributes();

    void setExtendedAttributes(JSONObject xas);
}

