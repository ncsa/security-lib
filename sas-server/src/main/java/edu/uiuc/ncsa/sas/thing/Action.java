package edu.uiuc.ncsa.sas.thing;

/**
 * Models an actions, such as logon, execute, logoff, etc.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  10:39 AM
 */
public class Action extends Thing {
    public Action(String value) {
        super(value);
    }

    /**
     * An (optional) unique identifier that the client sends. This allows the client to
     * disambiguate like-named calls and dispatch them however it pleases. <br/><br/>
     * This is passed back unaltered if present.
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    /**
     * An optional state string that the client sends.<br/><br/> It is passed back unaltered.
     * @return
     */
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    String state;

    /**
     * the client may send a comment. This is passed along, and while it might be recorded in the logs,
     * is ignored. This is because JSON does not allow for comments, hence the client can have comments for
     * its own purposes.
     * @return
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    String comment;

    @Override
    public String toString() {
        return "Action{" +
                "id='" + id + '\'' +
                ", state='" + state + '\'' +
                ", comment='" + comment + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
