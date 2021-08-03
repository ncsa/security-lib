package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * A utility for sending messages via SMTP or SMTPS using Java mail.
 * A MailUtil object contains a complete configuration for sending messages.
 * <p>Created by Jeff Gaynor<br>
 * on 10/5/11 at  1:18 PM
 */
public class MailUtil extends TemplateUtil implements Logable {
    public Session getSession(Properties props) throws NamingException {
        return Session.getDefaultInstance(props);
    }

    public MailUtil(MyLoggingFacade myLogger) {
        this.myLogger = myLogger;
    }

    /**
     * The left-hand delimiter for replacements in templates. Must make a valid regex when combined with the
     * key and right-hand delimiter.
     */
    public static String LEFT_DELIMITER = "${";
    /**
     * The right-hand delimiter for replacements in templates. Must make a valid regex when combined with the
     * left-hand delimiter and key.
     */
    public static String RIGHT_DELIMITER = "}";
    /**
     * The separator between email addresses. The default is a semi-colon.
     */
    public static String ADDRESS_SEPARATOR = ";";


    public MailUtil() {
        try {
            mailEnvironment = new MailEnvironment(false, null, -1, null, null, null, null, null, false, false);
        } catch (Throwable x) {
            warn("Error: could not create mail environment:" + x);
        }
    }

    public MailUtil(MailEnvironment me) {
        mailEnvironment = me;
    }

    public boolean isEnabled() {
        return getMailEnvironment().mailEnabled;
    }

    public static class MailEnvironment extends AbstractEnvironment implements  MailConfigurationTags{
        public MailEnvironment(boolean mailEnabled) {
            this.mailEnabled = mailEnabled;
        }

        boolean starttls = false;

        /**
         * Populate from a map
         * @param map
         */
        public MailEnvironment(Map<String, Object> map) {
             if(map.containsKey(MAIL_ENABLED)) this.mailEnabled = (boolean) map.get(MAIL_ENABLED);
             if(map.containsKey(MAIL_MESSAGE_TEMPLATE))this.messageTemplate = (String) map.get(MAIL_MESSAGE_TEMPLATE);
        }

        public MailEnvironment(
                boolean mailEnabled,
                String server,
                int port,
                String password,
                String from,
                String recipients,
                String messageTemplate,
                String subjectTemplate,
                boolean useSSL,
                boolean starttls) throws Exception {
            if (from != null) {
                this.from = new InternetAddress(from);
            }
            this.mailEnabled = mailEnabled;
            this.messageTemplate = messageTemplate;
            this.password = password;
            this.port = port;
            this.recipients = parseRecipients(recipients);
            this.server = server;
            this.subjectTemplate = subjectTemplate;
            this.useSSL = useSSL;
            this.starttls = starttls;
        }

        /**
         * Take
         * @param otherME
         */
        public void update(MailEnvironment otherME){

        }

/*
        protected void init(boolean mailEnabled,
                            String server,
                            int port,
                            String password,
                            String from,
                            String recipients,
                            String messageTemplate,
                            String subjectTemplate,
                            boolean useSSL,
                            boolean starttls) throws AddressException {
            this.mailEnabled = mailEnabled;
            info("Mail notifications " + (mailEnabled ? "en" : "dis") + "abled");
            this.useSSL = useSSL;
            this.starttls = starttls;
            if (from != null) {
                this.from = new InternetAddress(from);
            }

            if (subjectTemplate == null) {
                warn("Email: No subject template found, using default");
                subjectTemplate = "Something happened on the server.";
            } else {
                this.subjectTemplate = subjectTemplate;
            }

            this.server = server;
            if (messageTemplate == null) {
                warn("Email: No message template found, using default");
                messageTemplate = "Something happened on the server.";
            } else {
                this.messageTemplate = messageTemplate;
            }

            this.port = port;
            this.password = password;
            this.recipients = parseRecipients(recipients);
            debug("Email uses ssl: " + (useSSL ? "y" : "n") + ", server=" + server + ", sender=" + from);

        }
*/


        boolean mailEnabled;
        boolean useSSL;


        InternetAddress from = null;
        String password;
        String server;
        int port = -1;
        String messageTemplate;
        String subjectTemplate;
        public InternetAddress[] recipients;

        public InternetAddress[] parseRecipients(String x) throws AddressException {
            InternetAddress[] z = null;
            if (x != null) {
                String[] addresses = x.split(ADDRESS_SEPARATOR);
                z = new InternetAddress[addresses.length];
                int i = 0;
                for (String y : addresses) {
                    z[i++] = new InternetAddress(y);
                }
            }
            if (z == null) {
                warn("Warning: No recipients found for email notification");
                z = new InternetAddress[0];
            }
            return z;
        }
    }

    public MailEnvironment getMailEnvironment() {
        return mailEnvironment;
    }

    MailEnvironment mailEnvironment;
    synchronized public boolean sendMessage(String subjectTemplate, String messageTemplate, Map replacements)  {
        return sendMessage(subjectTemplate, messageTemplate, replacements, null);
    }


    /**
     * This allows for sending with a specific subject and message template. This is useful for
     * internally generated messages that may need a lot of customization on the fly. Remember that
     * a template has string delimited with ${KEY} which the replacements map (KEY, VALUE pairs) will
     * render into VALUES.
     * @param subjectTemplate
     * @param messageTemplate
     * @param replacements
     * @return
     */
    synchronized public boolean sendMessage(String subjectTemplate,
                                            String messageTemplate,
                                            Map replacements,
                                            String newRecipients) {
        Transport tr = null;

        InternetAddress[] recipients = getMailEnvironment().recipients;
        
        if(!StringUtils.isTrivial(newRecipients)){
            try {
                getMailEnvironment().parseRecipients(newRecipients);
            }catch(AddressException addressException){
                if(DebugUtil.isEnabled()){
                    addressException.printStackTrace();
                }
                warn( "The requested list of recipients \"" + newRecipients + "\" could not be parsed.\n" +
                        "error message reads \""+ addressException.getMessage() + "\"\n"+
                        "Did you use the right separator \"" + MailUtil.ADDRESS_SEPARATOR + "\" between addresses?\n" +
                        "Using default addresses.");
            }
        }
        try {

            info("Preparing to send mail notification");

            Properties props = new Properties();
            if (getMailEnvironment().isDebugOn()) {
                props.put("mail.debug", "true");
            }
            String protocol;
            int defaultPort = getMailEnvironment().port;
            if (getMailEnvironment().useSSL) {
                protocol = "smtps";
                if (getMailEnvironment().starttls) {
                    props.put("mail.smtp.starttls.enable", "true"); // fix for gmail, mostly. .
                }
                defaultPort = defaultPort == -1 ? 465 : defaultPort;
            } else {
                protocol = "smtp";
                defaultPort = defaultPort == -1 ? 25 : defaultPort;
            }
            debug("preparing message with protocol=" + protocol + " on server=" + getMailEnvironment().server);
            props.put("mail.transport.protocol", protocol);
            props.put("mail." + protocol + ".host", getMailEnvironment().server);
            if (protocol.equals("smtp") && getMailEnvironment().from == null) {
                props.put("mail." + protocol + ".auth", "false");

            } else {
                debug("Using authorization for user " + getMailEnvironment().from);
                props.put("mail." + protocol + ".auth", "true");
            }
            Session session = getSession(props);
            tr = session.getTransport(protocol);
            tr.connect(getMailEnvironment().server,
                    defaultPort,
                    getMailEnvironment().from == null ? null : getMailEnvironment().from.toString(),
                    getMailEnvironment().password);
            Message message = new MimeMessage(session);
            message.setFrom(getMailEnvironment().from);

            message.setRecipients(Message.RecipientType.TO, recipients);
            message.setSubject(replaceAll(subjectTemplate, replacements));
            message.setContent(replaceAll(messageTemplate, replacements), "text/plain");
            if(replacements.containsKey("reply-to")){
                InternetAddress address = new InternetAddress((String) replacements.get("reply-to"));
                message.setReplyTo(new Address[]{address});
            }
            tr.sendMessage(message, recipients);
            info("Mail notification sent to " + Arrays.toString(recipients));
            return true;
        } catch (Throwable throwable) {
            info("got exception sending message:");
            for (Object key : replacements.keySet()) {
                info("(" + key + "," + replacements.get(key.toString()) + ")");
            }
            throwable.printStackTrace();
            error("Sending mail failed. Continuing & message reads \"" + throwable.getMessage() + "\"");
            return false;
            //throw new GeneralException("Error", throwable);
        } finally {
            if (tr != null) {
                try {
                    tr.close();
                } catch (MessagingException e) {
                    throw new GeneralException("Error: could not close java mail transport", e);
                }
            }
        }
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
}
