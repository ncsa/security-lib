package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.HierarchicalConfigProvider;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * Provider for the {@link edu.uiuc.ncsa.security.util.mail.MailUtil} from an XML configuration file.
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  4:56 PM
 */
public class MailUtilProvider extends HierarchicalConfigProvider<MailUtil> implements MailConfigurationTags {

    public MailUtilProvider() {
    }

    public MailUtilProvider(ConfigurationNode config) {
        super(config);
    }

    @Override
    protected boolean checkEvent(CfgEvent cfgEvent) {
        if (cfgEvent.getConfiguration().getName().equals(MAIL)) {
            setConfig(cfgEvent.getConfiguration());
            return true;
        }
        return false;
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        if (checkEvent(configurationEvent)) {
            return get();
        }
        return null;
    }

    MailUtil.MailEnvironment me = null;
    protected MailUtil.MailEnvironment getME() {
        if(me != null){
            return me;
        }
        try {
            String x = getAttribute(MAIL_PORT, "-1");
            int port = Integer.parseInt(x); // if this bombs, catch it.
            me = new MailUtil.MailEnvironment(
                    Boolean.parseBoolean(getAttribute(MAIL_ENABLED, "false")), //enabled
                    getAttribute(MAIL_SERVER, "none"), //server
                    port, //port
                    getAttribute(MAIL_PASSWORD, "changeme"), //password
                    getAttribute(MAIL_USERNAME, null), //from
                    getAttribute(MAIL_RECIPIENTS), //recipients
                    Configurations.getNodeValue(getConfig(), MAIL_MESSAGE_TEMPLATE),// message template
                    Configurations.getNodeValue(getConfig(), MAIL_SUBJECT_TEMPLATE), // subject template
                    Boolean.parseBoolean(getAttribute(MAIL_USE_SSL, "false")), //use ssl
                    Boolean.parseBoolean(getAttribute(MAIL_START_TLS, "false"))); //use start tls (mostly for gmail)

            return me;

        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new MyConfigurationException("Error: Could not create mail environment.", t);
        }

    }

    @Override
    public MailUtil get() {
        if (getConfig() == null) {
            return new MailUtil();
        }
        return new MailUtil(getME());
    }
}
