package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.mail.MailUtil;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/14/12 at  5:23 PM
 */
public abstract class Notifier implements NotificationListener {
    public Notifier(MailUtil mailUtil, MyLoggingFacade loggingFacade) {
        this.mailUtil = mailUtil;
        this.loggingFacade = loggingFacade;
    }

    protected MailUtil mailUtil;
    protected MyLoggingFacade loggingFacade;


}
