package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.configuration.ConfigurationTags;

/**
 * Tags for the XML element in a configuration file that governs mail messages.
 * <p>Created by Jeff Gaynor<br>
 * on 4/3/12 at  1:01 PM
 */
public interface MailConfigurationTags extends ConfigurationTags {
    public static final String MAIL = "mail";


    public static final String MAIL_ENABLED = "enabled";
    public static final String MAIL_USE_SSL = "useSSL";
    public static final String MAIL_START_TLS = "starttls";
    public static final String MAIL_SERVER = "server";
    public static final String MAIL_USERNAME = "username";
    public static final String MAIL_PASSWORD = "password";
    public static final String MAIL_PORT = "port";
    public static final String MAIL_RECIPIENTS = "recipients";
    public static final String MAIL_MESSAGE_TEMPLATE = "messageTemplate";
    public static final String MAIL_SUBJECT_TEMPLATE = "subjectTemplate";
}
