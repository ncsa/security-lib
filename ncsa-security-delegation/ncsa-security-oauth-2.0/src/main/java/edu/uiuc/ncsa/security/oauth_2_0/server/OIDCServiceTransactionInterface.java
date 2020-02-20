package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.oauth_2_0.jwt.FlowStates;

import java.util.Collection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/20 at  5:46 PM
 */
public interface OIDCServiceTransactionInterface {
    FlowStates getFlowStates();

 //   void setState(JSONObject state);

    // This is used to store the flow states, claim sources AND the claims in between calls.
  //  JSONObject getState();

    void setFlowStates(FlowStates flowStates);

 //   void setClaimsSources(List<ClaimSource> sources) throws IOException;

 //   List<ClaimSource> getClaimSources() throws IOException, ClassNotFoundException;


//    JSONObject getClaims();

  //  void setClaims(JSONObject claims);

    Collection<String> getScopes();

    void setScopes(Collection<String> scopes);
}

