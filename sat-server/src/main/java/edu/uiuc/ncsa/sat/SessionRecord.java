package edu.uiuc.ncsa.sat;

import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.security.util.cli.Message;

import java.util.Date;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/20/22 at  7:16 AM
 */
public class SessionRecord {

    public SessionRecord(SATClient client, Executable executable) {
        this.client = client;
        this.executable = executable;
    }

    public SATClient client;
    public Executable executable;
    public Date createTS = new Date();
    public Date lastAccessed = new Date();
    public Message message = new Message();
    public byte[] sKey;
    public UUID sessionID;
}
