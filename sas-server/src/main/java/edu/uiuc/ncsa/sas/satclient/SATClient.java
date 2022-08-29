package edu.uiuc.ncsa.sas.satclient;

import edu.uiuc.ncsa.security.core.DateComparable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;
import net.sf.json.JSONObject;

import java.security.PublicKey;
import java.util.Date;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:43 AM
 */
public class SATClient extends IdentifiableImpl implements DateComparable {
    public SATClient(Identifier identifier) {
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
