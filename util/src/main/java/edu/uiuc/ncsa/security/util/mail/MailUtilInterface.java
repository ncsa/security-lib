package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.util.events.NotificationEvent;

import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/7/23 at  6:20 AM
 */
public interface MailUtilInterface extends Logable {
    boolean isEnabled();

    MailEnvironment getMailEnvironment();

    boolean sendMessage(NotificationEvent notificationEvent,
                        String subjectTemplate,
                        String messageTemplate,
                        Map replacements);

    boolean sendMessage(NotificationEvent notificationEvent,
                        String subjectTemplate,
                        String messageTemplate,
                        Map replacements,
                        String newRecipients);

    // Probable fix for CIL-324: a sudden attempt to send many messages causes strange failures.
    // This looks like a synchronization issue, so this method is now synchronized.
    boolean sendMessage(NotificationEvent notificationEvent,
                        Map replacements);
}
