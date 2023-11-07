package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import jakarta.mail.Session;

import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * A utility for sending messages via SMTP or SMTPS using Java mail.
 * A MailUtil object contains a complete configuration for sending messages.
 * <p>Created by Jeff Gaynor<br>
 * on 10/5/11 at  1:18 PM
 */
public class JakartaMailUtil implements MailUtilInterface {
    public Session getSession(Properties props) throws NamingException {
        return Session.getDefaultInstance(props);
    }

    public JakartaMailUtil(MyLoggingFacade myLogger) {
        this.myLogger = myLogger;
    }


    public JakartaMailUtil() {
        try {
            mailEnvironment = new MailEnvironment(false, null, -1, null, null, null, null, null, false, false);
        } catch (Throwable x) {
            warn("Error: could not create mail environment:" + x);
        }
    }

    public JakartaMailUtil(MailEnvironment me) {
        mailEnvironment = me;
    }

    public boolean isEnabled() {
        return getMailEnvironment().mailEnabled;
    }

    public MailEnvironment getMailEnvironment() {
        return mailEnvironment;
    }

    MailEnvironment mailEnvironment;

    synchronized public boolean sendMessage(String subjectTemplate, String messageTemplate, Map replacements) {
        return sendMessage(subjectTemplate, messageTemplate, replacements, null);
    }

    /**
     * This allows for sending with a specific subject and message template. This is useful for
     * internally generated messages that may need a lot of customization on the fly. Remember that
     * a template has string delimited with ${KEY} which the replacements map (KEY, VALUE pairs) will
     * render into VALUES.
     *
     * @param subjectTemplate
     * @param messageTemplate
     * @param replacements
     * @return
     */
    synchronized public boolean sendMessage(String subjectTemplate,
                                            String messageTemplate,
                                            Map replacements,
                                            String newRecipients) {
        if (!isEnabled()) {
            return true;
        }

        // return OLDsendMessage(subjectTemplate, messageTemplate, replacements, newRecipients);
        return NEWsendMessage(subjectTemplate, messageTemplate, replacements, newRecipients);
    }

    synchronized public boolean NEWsendMessage(String subjectTemplate,
                                               String messageTemplate,
                                               Map replacements,
                                               String newRecipients) {
        JakartaMailSenderThread mst = new JakartaMailSenderThread(this, subjectTemplate, messageTemplate, replacements, newRecipients);
        mst.start();
        return true;
    }



    /**
     * This takes a map of replacements for the templates and sends the message.
     * <p>This will return a true if the message succeeded and a false otherwise.
     * It will <b>not</b> cause a failure outright, since a failed notification should not
     * bring down your server.</p>
     *
     * @param replacements
     * @return
     */
    // Probable fix for CIL-324: a sudden attempt to send many messages causes strange failures.
    // This looks like a synchronization issue, so this method is now synchronized.
    synchronized public boolean sendMessage(Map replacements) {

        try {
            return sendMessage(getSubjectTemplate(), getMessageTemplate(), replacements);
        } catch (IOException e) {
            info("got exception sending message:");
            for (Object key : replacements.keySet()) {
                info("(" + key + "," + replacements.get(key.toString()) + ")");
            }
            e.printStackTrace();
            error("Sending mail failed. Continuing & message reads \"" + e.getMessage() + "\"");
            return false;
        }
    }

    public String getMessageTemplate() throws IOException {
        if (messageTemplate == null) {
            messageTemplate = readTemplate(getMailEnvironment().messageTemplate);
        }
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

    String messageTemplate;

    public String getSubjectTemplate() throws IOException {
        if (subjectTemplate == null) {
            subjectTemplate = readTemplate(getMailEnvironment().subjectTemplate);
        }
        return subjectTemplate;
    }

    String subjectTemplate;

    protected String readTemplate(String fileName) throws IOException {
        debug("reading in template file \"" + fileName + "\"");
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String out = null;
        String inLine = br.readLine();
        boolean isFirst = true;
        while (inLine != null) {
            if (!inLine.startsWith("#")) {
                if (isFirst) {
                    isFirst = false;
                    out = inLine;
                } else {
                    out = out + "\n" + inLine;
                }
            }
            inLine = br.readLine();
        }
        br.close();
        return out;
    }

    public MyLoggingFacade getMyLogger() {
        if (myLogger == null) {
            myLogger = new MyLoggingFacade(getClass().getName());
        }
        return myLogger;
    }

    public void setMyLogger(MyLoggingFacade myLogger) {
        this.myLogger = myLogger;
    }

    MyLoggingFacade myLogger;

    public void debug(String x) {
        getMyLogger().debug(x);
    }

    public void setDebugOn(boolean debugOn) {
        getMyLogger().setDebugOn(debugOn);
    }


    public boolean isDebugOn() {
        return getMyLogger().isDebugOn();
    }


    public void info(String x) {
        getMyLogger().info(x);

    }

    public void warn(String x) {
        getMyLogger().warn(x);
    }

    public void error(String x) {
        getMyLogger().error(x);
    }

    public void error(String x, Throwable t) {
        getMyLogger().error(x, t);
    }
}
