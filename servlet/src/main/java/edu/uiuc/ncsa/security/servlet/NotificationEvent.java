package edu.uiuc.ncsa.security.servlet;

import java.util.EventObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/14/12 at  5:21 PM
 */
public class NotificationEvent extends EventObject {
    public NotificationEvent(Object source) {
        super(source);
    }
}
