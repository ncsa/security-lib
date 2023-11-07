package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

import static edu.uiuc.ncsa.security.util.mail.MailEnvironment.ADDRESS_SEPARATOR;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/23 at  12:31 AM
 */ // CIL-1069 fix. Create thread to send message in the background since
// latency is high and makes the server sluggish.
// To test: start OA4MP and register a client. An email should be set to personal account.
public class MailSenderThread extends Thread {
    private final MailUtil mailUtil;
    String subjectTemplate;
    String messageTemplate;
    Map replacements;
    String newRecipients;


    public MailSenderThread(MailUtil mailUtil, String subjectTemplate,
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

        if (!StringUtils.isTrivial(newRecipients)) {
            try {
                mailUtil.getMailEnvironment().parseRecipients(newRecipients);
            } catch (AddressException addressException) {
                if (DebugUtil.isEnabled()) {
                    addressException.printStackTrace();
                }
                mailUtil.warn("The requested list of recipients \"" + newRecipients + "\" could not be parsed.\n" +
                        "error message reads \"" + addressException.getMessage() + "\"\n" +
                        "Did you use the right separator \"" + ADDRESS_SEPARATOR + "\" between addresses?\n" +
                        "Using default addresses.");
            }
        }
        try {
            List<String> actualRecipients = mailUtil.getMailEnvironment().recipients;
            if (!StringUtils.isTrivial(newRecipients)) {
                actualRecipients = mailUtil.getMailEnvironment().parseRecipients(newRecipients);
            }
            InternetAddress[] recipients = toInternetAddress(actualRecipients);

            mailUtil.info("Preparing to send mail notification");

            Properties props = new Properties();
            props.put("mail.debug", mailUtil.getMailEnvironment().isDebugOn() ? "true" : "false");

            int defaultPort = -1;
            String protocol = "smtp"; // do not get smpts since that will fail.

            // https://stackoverflow.com/questions/60654561/java-mail-cannot-connect-to-smtp-using-tls-or-ssl
            if (mailUtil.getMailEnvironment().useSSL) {
                defaultPort = mailUtil.getMailEnvironment().setSSLProperties(props);
            } else {
                if (mailUtil.getMailEnvironment().starttls) {
                    defaultPort = mailUtil.getMailEnvironment().setTLSProperties(props);
                } else {
                    defaultPort = mailUtil.getMailEnvironment().setSMTPProperties(props);
                }
            }
            mailUtil.debug("Using authorization for user " + mailUtil.getMailEnvironment().from);
            Session session = mailUtil.getSession(props);

            tr = session.getTransport(protocol);

            tr.connect(mailUtil.getMailEnvironment().server,
                    defaultPort,
                    mailUtil.getMailEnvironment().username == null ? mailUtil.getMailEnvironment().from.toString() : mailUtil.getMailEnvironment().username,
                    mailUtil.getMailEnvironment().password);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mailUtil.getMailEnvironment().from));
            message.setRecipients(Message.RecipientType.TO, recipients);
            String type = "text/plain";
            if (!StringUtils.isTrivial(mailUtil.getMailEnvironment().contentType)) {
                type = mailUtil.getMailEnvironment().contentType;
            }
            String replyTo = null;
            if (replacements!=null && replacements.containsKey("reply-to") && replacements.get("reply-to") != null) {
                replyTo = (String) replacements.get("reply-to");
            } else {
                if (mailUtil.getMailEnvironment().replyTo != null) {
                    replyTo = mailUtil.getMailEnvironment().replyTo;
                }
            }
            if (replacements == null || replacements.isEmpty()) {
                message.setContent(messageTemplate, type);
                message.setSubject(subjectTemplate);
                if(replyTo!=null) {
                    message.setReplyTo(new Address[]{new InternetAddress(replyTo)});
                }
            } else {
                message.setSubject(TemplateUtil.replaceAll(subjectTemplate, replacements));
                message.setContent(TemplateUtil.replaceAll(messageTemplate, replacements), type);
                if (replyTo!= null) {
                    InternetAddress address = new InternetAddress(TemplateUtil.replaceAll(replyTo, replacements));
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
