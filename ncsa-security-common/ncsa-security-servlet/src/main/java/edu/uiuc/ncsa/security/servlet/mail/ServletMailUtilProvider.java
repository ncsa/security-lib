package edu.uiuc.ncsa.security.servlet.mail;

import edu.uiuc.ncsa.security.util.mail.MailUtil;
import edu.uiuc.ncsa.security.util.mail.MailUtilProvider;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/25/13 at  11:01 AM
 */
public class ServletMailUtilProvider extends MailUtilProvider {
    public ServletMailUtilProvider() {
    }

    public ServletMailUtilProvider(ConfigurationNode config) {
        super(config);
    }

    @Override
    public MailUtil get() {
        if (getConfig() == null) {
            return new ServletMailUtil();
        }
        return new ServletMailUtil(getME());
    }
}
