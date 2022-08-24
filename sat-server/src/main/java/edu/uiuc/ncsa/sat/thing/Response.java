package edu.uiuc.ncsa.sat.thing;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:29 AM
 */
public class Response {


    public Response() {
    }

    public Response(Action action) {
        init(action);
    }

    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    String state;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    String comment;

    public void init(Action action){
        setId(action.getId());
        setState(getState());
        setComment(getComment());
    }
}
