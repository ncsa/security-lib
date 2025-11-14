package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;

import javax.mail.internet.AddressException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/23 at  12:31 AM
 */
public class MailEnvironment extends AbstractEnvironment implements MailConfigurationTags {
    /**
     * The separator between email addresses. The default is a semi-colon.
     */
    public static String ADDRESS_SEPARATOR = ";";
    public MailEnvironment() {
    }

    public MailEnvironment(boolean mailEnabled) {
        this.mailEnabled = mailEnabled;
    }



    /**
     * Populate from a map
     *
     * @param map
     */
    public MailEnvironment(Map<String, Object> map) {
        if (map.containsKey(MAIL_ENABLED)) this.mailEnabled = (boolean) map.get(MAIL_ENABLED);
        if (map.containsKey(MAIL_MESSAGE_TEMPLATE)) this.messageTemplate = (String) map.get(MAIL_MESSAGE_TEMPLATE);
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
            boolean starttls,
            long throttleInterval) throws Exception {
        this.from = from;
        this.mailEnabled = mailEnabled;
        this.messageTemplate = messageTemplate;
        this.password = password;
        this.port = port;
        this.recipients = parseRecipients(recipients);
        this.server = server;
        this.subjectTemplate = subjectTemplate;
        this.useSSL = useSSL;
        this.starttls = starttls;
        this.throttleInterval = throttleInterval;
    }


    public MailEnvironment setUsername(String username) {
        this.username = username;
        return this;
    }

    // For builder pattern
    public MailEnvironment setEnabled(boolean enabled) {
        this.mailEnabled = enabled;
        return this;
    }

    public MailEnvironment setFrom(String from) throws AddressException {
        this.from = from;
        return this;
    }

    public MailEnvironment setReplyTo(String replyTo) throws AddressException {
        this.replyTo = replyTo;
        return this;
    }


    public MailEnvironment setServer(String server) {
        this.server = server;
        return this;
    }

    public MailEnvironment setPassword(String password) {
        this.password = password;
        return this;
    }

    public MailEnvironment setPort(int port) {
        this.port = port;
        return this;
    }

    public MailEnvironment useSSL(boolean useSSL) {
        this.useSSL = useSSL;
        return this;
    }

    public MailEnvironment setDebug(boolean debugOn) {
        super.setDebugOn(debugOn);
        return this;
    }

    public MailEnvironment startTLS(boolean starttls) {
        this.starttls = starttls;
        return this;
    }

    public MailEnvironment setSubject(String subject) {
        this.subjectTemplate = subject;
        return this;
    }

    public MailEnvironment setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public MailEnvironment setMessage(String message) {
        this.messageTemplate = message;
        return this;
    }

    public MailEnvironment setRecipients(String recipients) throws AddressException {
        this.recipients = parseRecipients(recipients);
        return this;
    }
    public MailEnvironment setRecipients(List<String> recipients) throws AddressException {
        this.recipients = recipients;
        return this;
    }

    public MailEnvironment setCC(String cc) throws AddressException {
        if (cc != null) {
            this.carbonCopy = parseRecipients(cc);
        }
        return this;
    }

    public MailEnvironment setCC(List<String> cc) throws AddressException {
        if (cc != null) {
            this.carbonCopy = cc;
        }
        return this;
    }

    public MailEnvironment setBCC(String bcc) throws AddressException {
        if (bcc != null) {
            this.blindCarbonCopy = parseRecipients(bcc);
        }
        return this;
    }

    public MailEnvironment setBCC(List<String> bcc) throws AddressException {
        if (bcc != null) {
            this.blindCarbonCopy = bcc;
        }
        return this;
    }

    public static MailEnvironment create() {
        return new MailEnvironment();
    }

    @Override
    public String toString() {
        return "MailEnvironment{" +
                "blindCarbonCopy=" + blindCarbonCopy +
                ", carbonCopy=" + carbonCopy +
                ", contentType='" + contentType + '\'' +
                ", from=" + from +
                ", mailEnabled=" + mailEnabled +
                ", messageTemplate='" + messageTemplate + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", recipients=" + recipients +
                ", server='" + server + '\'' +
                ", starttls=" + starttls +
                ", subjectTemplate='" + subjectTemplate + '\'' +
                ", username='" + username + '\'' +
                ", useSSL=" + useSSL +
                '}';
    }

    /**
     * Take
     *
     * @param otherME
     */
    public void update(MailEnvironment otherME) {

    }



    public List<String> blindCarbonCopy = null;
    public List<String> carbonCopy = null;
    public List<String> recipients;
    public String contentType = null;
    public String from = null;
    public String messageTemplate;
    public String password;
    public String replyTo;
    public String server;
    public String subjectTemplate;
    public boolean mailEnabled;
    public boolean useSSL;
    public int port = -1;
    public  boolean starttls = false;
    public String username;

    public boolean isThrottleEnabled(){
        return 0 < throttleInterval;
    }
    public long getThrottleInterval() {
        return throttleInterval;
    }

    public MailEnvironment setThrottleInterval(long throttleInterval) {
        this.throttleInterval = throttleInterval;
        return this;
    }

    long throttleInterval = -1L;


    public List<String> parseRecipients(String x) throws AddressException {
        List<String> z = new ArrayList<>();
        if (x != null) {
            String[] addresses = x.split(ADDRESS_SEPARATOR);
            for (String y : addresses) {
                z.add(y);
            }
        }
        return z;
    }

    /**
     * Configure a {@link Properties} object using the current environment, returning the port.
     * This is for the option that {@link #useSSL} is true.
     * @param props
     * @return
     */
    public int setSSLProperties(Properties props) {
        if(props == null){props = new Properties();}
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.host", server);
        props.put("mail.smtps.auth", "true");
        int defaultPort = port == -1 ? 465 : port;
        props.put("mail.smtps.port", Integer.toString(port));
        props.put("mail.smtps.ssl.trust", server);
        props.put("mail.smtps.ssl.enable", "true");
        props.put("mail.smtps.user", username == null ? "" : username);

        props.setProperty("mail.smtps.ssl.protocols", "TLSv1.3");
        return defaultPort;
    }

    /**
     * Configure a {@link Properties} object using the current environment, returning the port.
     * This is for the option that {@link #starttls} is true.
     * @param props
     * @return
     */
    public int setTLSProperties(Properties props) {
        setSMTPProperties(props);
        props.put("mail.smtp.starttls.enable", "true");
        int defaultPort = port == -1 ? 587 : port;
        props.put("mail.smtp.port", Integer.toString(defaultPort));
        props.put("mail.smtp. user", username == null ? "" : username);
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.1 TLSv1.2");
        return port;
    }

    /**
     * Configure a {@link Properties} object using the current environment, returning the port.
     * In this case, neither SSL nor TLS are used.
     * @param props
     */
    public int setSMTPProperties(Properties props) {
        if(props == null){props = new Properties();}
        props.put("mail.smtp.auth", Boolean.toString(username != null));
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.host", server);
        int defaultPort = port == -1 ? 25 : port;
        props.put("mail.smtp.user", username == null ? "" : username);
        props.put("mail.smtp.port", Integer.toString(defaultPort));
        return defaultPort;
    }
}
