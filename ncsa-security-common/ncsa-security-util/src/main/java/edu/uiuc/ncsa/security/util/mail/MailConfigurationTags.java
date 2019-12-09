package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.configuration.ConfigurationTags;

/**
 * Tags for the XML element in a configuration file that governs mail messages.
 * The assumed format for the mail configuration is (as of new version in Dec. 4, 2019):
 * <pre>
 *   &lt;mail useSSL="true"
 *       server="a.b.c"&gt;
 *    &lt;entry name="foo" recipents="bert@sesame.edu"&gt;
 *      &lt;messageTemplate&gt;/path/to/message_template&lt;/messageTemplate&gt;
 *    &lt;/entry&gt;
 *    &lt;entry name="bar" parent="foo" recipents="ernie@sesame.edu"/&gt;
 *    &lt;subjectTemplate&gt;/path/to/subject_template&lt;/subjectTemplate&gt;
 * &lt;/mail&gt;
 * </pre>
 * What does this mean?
 * <ul>
 *     <li>The top-level mail tag sets global defaults -- every entry inherits these.</li>
 *     <li>A single subject template for all entries is specified</li>
 *     <li>The entry named foo sets a messsage template and specifies a recipient list</li>
 *     <li>The entry bar inherits everything from bar and sets its own set of recipients</li>
 * </ul>                                                                                                     l
 *
 * These are access in the mail util by passing in the name of the configuration you want the component to use.
 * It is possible then to have several different systems of email firing off mail as needed, for instance, in some
 * cases a component might email "help" in others it might send emails to "admin" or perhaps "notify" .
 * <h3>Backwards compatibility</h3>
 * <p>This <it>should</it> require no changes to an existing system if you are happy with it. If a component specifies
 * to failOnMissingConfig then if a named config is not found, an exception is thrown. Otherwise whatever is
 * the default config is used and a message is logged. The default is to resolve missing configurations to the default.
 * Specifying everything at the top level
 * means that all components will use the single configuration. </p>
 * <p>It is suggested that you may want to have several standard tags such as help, notify, admin.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 4/3/12 at  1:01 PM
 */
public interface MailConfigurationTags extends ConfigurationTags {
    public static final String MAIL_COMPONENT = "entry";
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
    public static final String MAIL_FAIL_ON_MISSING_CONFIG = "failOnMissingConfig";
    public static final String MAIL_NAME = "name";
    public static final String MAIL_PARENT = "parent";

    public static final String MAIL_CONFIG_DEFAULT_NAME = "default";
    public static final String MAIL_CONFIG_ROOT="root";
    public static final String MAIL_CONFIG_ADMIN="admin";
    public static final String MAIL_CONFIG_HELP="help"; // For notifications that require actions
    public static final String MAIL_CONFIG_ALERTS="alerts"; // For notifications that do not require action.
}
