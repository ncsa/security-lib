package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

/**
* <p>Created by Jeff Gaynor<br>
* on 4/25/12 at  3:07 PM
*/
public class ClientApprovalKeys extends SerializationKeys {
    public ClientApprovalKeys() {
        identifier("oauth_consumer_key");
    }

    String approved = "approved";
    String approvalTS = "approval_ts";
    String approver = "approver";

    public String approved(String... x) {
        if (0 < x.length) approved = x[0];
        return approved;
    }

    public String approvalTS(String... x) {
        if (0 < x.length) approvalTS = x[0];
        return approvalTS;
    }

    public String approver(String... x) {
        if (0 < x.length) approver = x[0];
        return approver;
    }
}
