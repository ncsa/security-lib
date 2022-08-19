package edu.uiuc.ncsa.sat;

import edu.uiuc.ncsa.security.core.Identifier;

import java.util.UUID;

/**
 * Identifies a session by the client id and the session uuid.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  11:03 AM
 */
public class Subject {
    public Identifier identifier;
    public UUID sessionID;
}
