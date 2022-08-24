package edu.uiuc.ncsa.sas.thing;

import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  7:22 AM
 */
public class LogonResponse extends Response{
    public LogonResponse(Action action, UUID uuid) {
        super(action);
        this.uuid = uuid;
    }

    public  UUID uuid;

}
