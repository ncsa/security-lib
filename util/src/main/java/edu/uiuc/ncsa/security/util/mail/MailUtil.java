package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.events.NotificationEvent;

import javax.mail.Session;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A utility for sending messages via SMTP or SMTPS using Java mail.
 * A MailUtil object contains a complete configuration for sending messages.
 * <p>Created by Jeff Gaynor<br>
 * on 10/5/11 at  1:18 PM
 */
public class MailUtil implements MailUtilInterface {
    public Session getSession(Properties props) throws NamingException {
        return Session.getDefaultInstance(props);
    }

    public MailUtil(MyLoggingFacade myLogger) {
        this.myLogger = myLogger;
    }




    public MailUtil() {
        try {
            mailEnvironment = new MailEnvironment(false, null, -1, null, null, null, null, null, false, false, -1L);
        } catch (Throwable x) {
            warn("Error: could not create mail environment:" + x);
        }
    }

    public MailUtil(MailEnvironment me) {
        mailEnvironment = me;
    }

    @Override
    public boolean isEnabled() {
        return getMailEnvironment().mailEnabled;
    }

    @Override
    public MailEnvironment getMailEnvironment() {
        return mailEnvironment;
    }

    MailEnvironment mailEnvironment;

    @Override
    synchronized public boolean sendMessage(NotificationEvent notificationEvent,
                                            String subjectTemplate,
                                            String messageTemplate,
                                            Map replacements) {
        return sendMessage(notificationEvent,
                subjectTemplate,
                messageTemplate,
                replacements,
                null);
    }



    /**
     * This allows for sending with a specific subject and message template. This is useful for
     * internally generated messages that may need a lot of customization on the fly. Remember that
     * a template has string delimited with ${KEY} which the replacements map (KEY, VALUE pairs) will
     * render into its VALUE.
     *
     * @param subjectTemplate
     * @param messageTemplate
     * @param replacements
     * @return
     */
    @Override
    synchronized public boolean sendMessage(NotificationEvent notificationEvent,
                                            String subjectTemplate,
                                            String messageTemplate,
                                            Map replacements,
                                            String newRecipients) {
        if (!isEnabled()) {
            return true;
        }
        // Fix https://github.com/ncsa/security-lib/issues/65
        if(notificationEvent!=null && getMailEnvironment().isThrottleEnabled()){
            // Note that the source of the event has to be a stable object like a service since
            // intervals are keyed off  of it.
            if(sentMessageTimestamps.containsKey(notificationEvent.getSource().hashCode())){
               if(sentMessageTimestamps.get(notificationEvent.getSource().hashCode())<System.currentTimeMillis()-getMailEnvironment().getThrottleInterval()) {
                   if(DebugUtil.isTraceEnabled()) {
                       DebugUtil.trace(this, "Throttling message for " + notificationEvent.getSource().getClass().getSimpleName());
                   }
                   return false;
               }
            }
            sentMessageTimestamps.put(notificationEvent.getSource().hashCode(), System.currentTimeMillis());
        }
        MailSenderThread mst = new MailSenderThread(this, subjectTemplate, messageTemplate, replacements, newRecipients);
        mst.start();
        return true;
    }

    HashMap<Integer, Long> sentMessageTimestamps = new HashMap<>();


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
    @Override
    synchronized public boolean sendMessage(NotificationEvent notificationEvent,
                                            Map replacements) {

        try {
            return sendMessage(notificationEvent,
                    getSubjectTemplate(),
                    getMessageTemplate(),
                    replacements);
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
