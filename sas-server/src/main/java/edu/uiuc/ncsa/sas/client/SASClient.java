package edu.uiuc.ncsa.sas.client;

import edu.uiuc.ncsa.security.core.DateComparable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;
import net.sf.json.JSONObject;

import java.security.PublicKey;
import java.util.Date;

/**
 * Contains the information for a client of the system -- such as keys, name, etc.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:43 AM
 */
public class SASClient extends IdentifiableImpl implements DateComparable {
    public SASClient(Identifier identifier) {
        super(identifier);
    }

    @Override
    public Date getCreationTS() {
        return creationTS;
    }
    PublicKey publicKey;
    String name;
    Date creationTS = new Date();
    JSONObject cfg;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreationTS(Date creationTS) {
        this.creationTS = creationTS;
    }

    public JSONObject getCfg() {
        return cfg;
    }

    public void setCfg(JSONObject cfg) {
        this.cfg = cfg;
    }
}
