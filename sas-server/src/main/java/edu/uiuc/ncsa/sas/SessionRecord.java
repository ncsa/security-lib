package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.security.util.cli.Message;

import java.util.Date;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/20/22 at  7:16 AM
 */
public class SessionRecord {

    public SessionRecord(SASClient client, Executable executable) {
        this.client = client;
        this.executable = executable;
    }

    public SASClient client;
    public Executable executable;
    public Date createTS = new Date();
    public Date lastAccessed = new Date();
    public Message message = new Message();
    public byte[] sKey;
    public UUID sessionID;
}
