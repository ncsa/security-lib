package edu.uiuc.ncsa.security.delegation.server.storage;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;

import java.util.Date;

/**
 * A client approval. Note that this has the same id as the client record! Changing the identifier will
 * effectively remove this approval.
 * <p>Created by Jeff Gaynor<br>
 * on May 26, 2011 at  9:40:02 AM
 */
public class ClientApproval extends IdentifiableImpl  {

    public ClientApproval(Identifier identifier) {
        super(identifier);
    }

    static final long serialVersionUID = 1714880068599897702L;
    boolean approved;
    String approver;
    Date approvalTimestamp;

    public Date getApprovalTimestamp() {
        return approvalTimestamp;
    }

    public void setApprovalTimestamp(Date approvalTimestamp) {
        this.approvalTimestamp = approvalTimestamp;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }


    @Override
    public boolean equals(Object obj) {
        if(!super.equals(obj)) return false;
        if(!(obj instanceof ClientApproval)) return false;
        ClientApproval ca = (ClientApproval) obj;
        if (!getIdentifierString().equals(ca.getIdentifierString())) return false;
        if (!(isApproved() == ca.isApproved())) return false;
        if(getApprover() == null && ca.getApprover() == null) return true;
        if(getApprover() != null && ca.getApprover() == null) return false;
        if(getApprover() == null && ca.getApprover() != null) return false;
        if (!getApprover().equals(ca.getApprover())) return false;
        return true;
    }

    @Override
    public String toString() {
        String x = getClass().getSimpleName() + "[approved=" + isApproved() +
                ", approver=" + getApprover() + ", id=" + getIdentifierString() + ", on " + getApprovalTimestamp() + "]";
        return x;
    }
}
