package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/23 at  12:31 AM
 */ // CIL-1069 fix. Create thread to send message in the background since
// latency is high and makes the server sluggish.
// To test: start OA4MP and register a client. An email should be set to personal account.
public class JakartaMailSenderThread extends Thread {
    private final JakartaMailUtil mailUtil;
    String subjectTemplate;
    String messageTemplate;
    Map replacements;
    String newRecipients;


    public JakartaMailSenderThread(JakartaMailUtil mailUtil, String subjectTemplate,
                                   String messageTemplate,
                                   Map replacements,
                                   String newRecipients) {
        this.mailUtil = mailUtil;
        this.subjectTemplate = subjectTemplate;
        this.messageTemplate = messageTemplate;
        this.replacements = replacements;
        this.newRecipients = newRecipients;
    }

    InternetAddress[] toInternetAddress(List<String> raw) throws AddressException {
        InternetAddress[] internetAddresses = new InternetAddress[raw.size()];
        int i = 0;
        for (String x : raw) {
            internetAddresses[i++] = new InternetAddress(x);
        }
        return internetAddresses;
    }

    @Override
    public void run() {
        Transport tr = null; // so it's outside the try-catch block.
        List<String> actualRecipients = mailUtil.getMailEnvironment().recipients;
        try {
            if (!StringUtils.isTrivial(newRecipients)) {
            actualRecipients =    mailUtil.getMailEnvironment().parseRecipients(newRecipients);
            }

            InternetAddress[] recipients = toInternetAddress(actualRecipients);

            mailUtil.info("Preparing to send mail notification");

            Properties props = new Properties();
            props.put("mail.debug", mailUtil.getMailEnvironment().isDebugOn() ? "true" : "false");

            String protocol = null;
            int defaultPort = -1;
            // https://stackoverflow.com/questions/60654561/java-mail-cannot-connect-to-smtp-using-tls-or-ssl
            if (mailUtil.getMailEnvironment().useSSL) {
                defaultPort = mailUtil.getMailEnvironment().setSSLProperties(props);
                protocol = "smtp";
            } else {
                if (mailUtil.getMailEnvironment().starttls) {
                    defaultPort = mailUtil.getMailEnvironment().setTLSProperties(props);
                    protocol = "smtps";
                } else {
                    defaultPort = mailUtil.getMailEnvironment().setSMTPProperties(props);
                    protocol = "smtp";
                }
            }
            mailUtil.debug("Using authorization for user " + mailUtil.getMailEnvironment().from);
            Session session = mailUtil.getSession(props);


       /*     if (getMailEnvironment().useSSL) {
                session.setProtocolForAddress("rfc822", "smtps");
            }*/

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUtil.getMailEnvironment().from));

            message.setRecipients(Message.RecipientType.TO, recipients);
            String type = "text/plain";
            if (!StringUtils.isTrivial(mailUtil.getMailEnvironment().contentType)) {
                type = mailUtil.getMailEnvironment().contentType;
            }
            if (replacements == null) {
                message.setContent(messageTemplate, type);
            } else {
                message.setSubject(TemplateUtil.replaceAll(subjectTemplate, replacements));
                message.setContent(TemplateUtil.replaceAll(messageTemplate, replacements), type);
                if (replacements.containsKey("reply-to")) {
                    InternetAddress address = new InternetAddress((String) replacements.get("reply-to"));
                    message.setReplyTo(new Address[]{address});
                }
            }
            if (mailUtil.getMailEnvironment().carbonCopy != null) {
                message.setRecipients(Message.RecipientType.CC, toInternetAddress(mailUtil.getMailEnvironment().carbonCopy));
            }
            if (mailUtil.getMailEnvironment().blindCarbonCopy != null) {
                message.setRecipients(Message.RecipientType.BCC, toInternetAddress(mailUtil.getMailEnvironment().blindCarbonCopy));
            }
            message.setSentDate(new Date());
            /* There is an issue using javax and jakarta classes because of a class loader
            problem. On suggestion on the net was to seriously hot rod the loaders, but
            that seems to be a bad idea.
            This does not quite work though... */
/*
            Thread t =  Thread.currentThread();
            ClassLoader ccl = t.getContextClassLoader();
            t.setContextClassLoader(session.getClass().getClassLoader());
            try {

                Transport.send(message, new Address[]{new InternetAddress(mailUtil.getMailEnvironment().server)},
                                    mailUtil.getMailEnvironment().username == null ? mailUtil.getMailEnvironment().from.toString() : mailUtil.getMailEnvironment().username,
                                    mailUtil.getMailEnvironment().password);
            } finally {
                t.setContextClassLoader(ccl);
            }
*/

            tr = session.getTransport(protocol);
            tr.connect(mailUtil.getMailEnvironment().server,
                    defaultPort,
                    mailUtil.getMailEnvironment().username == null ? mailUtil.getMailEnvironment().from.toString() : mailUtil.getMailEnvironment().username,
                    mailUtil.getMailEnvironment().password);

            tr.sendMessage(message, recipients);

            mailUtil.info("Mail notification sent to " + Arrays.toString(recipients));
        } catch (Throwable throwable) {
            String message = "Sending mail failed, message reads \"" + throwable.getMessage() + "\"";
            if (throwable instanceof MessagingException) {
                MessagingException messagingException = (MessagingException) throwable;
                if (messagingException.getNextException() != null) {
                    message = message + ", next message reads \"" + messagingException.getNextException().getMessage() + "\"";
                }
            }
            mailUtil.error(message, throwable);
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
}
