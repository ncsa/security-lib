package edu.uiuc.ncsa.security.servlet.mail;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.mail.MailUtil;

import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Note this is used in OA4MP and CILogon.
 * <p>Created by Jeff Gaynor<br>
 * on 6/25/13 at  11:00 AM
 */
public class ServletMailUtil extends MailUtil {
    public ServletMailUtil() {
    }

    public ServletMailUtil(MailEnvironment me) {
        super(me);
    }

    public ServletMailUtil(MyLoggingFacade myLogger) {
        super(myLogger);
    }

    @Override
    public Session getSession(Properties props) throws NamingException {
        getMyLogger().info("Starting");
        DebugUtil.dbg(this, "Starting to get the mail session");
        // next bit gets the right session object from Tomcat and ensures it is set up
        // right in this environment.
        Context initCtx = new InitialContext();
        Object obj = null;
        try {
            DebugUtil.dbg(this, "Getting mail session");
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            obj = envCtx.lookup("mail/Session");
        } catch (Throwable t) {
            t.printStackTrace();
            DebugUtil.dbg(this, "Getting mail session #2, msg=" + t.getMessage());

            try {
                obj = initCtx.lookup("java:/comp/env/mail/Session");
            } catch (Throwable tt) {
                tt.printStackTrace();
                DebugUtil.dbg(this, "Completely failed to get mail session. msg=" + tt.getMessage());
            }

        }
            /*
            If you get an exception here like
            ClassCastException: cannot cast javax.mail.Session to javax.mail.Session
            i.e., same class to class, this is because they came from different class loaders.
            Tomcat should have a copy of whatever java mail jar in its $CATALINA_HOME/lib. This is
            available at runtime to this servlet. If you add this jar to the webapp and deploy
            is in the webapp's lib directory, you will get this Exception.

            Solution is to have only the single copy of the java-mail jar in the $CATALINA_HOME/lib directory.
            You will need to include in your compile classpath when compiling though. This is done in maven
            by specifying the java mail jar as a dependency with scope "provided"
             */
        Session session = (Session) obj; // cast to intercept nasty tomcat issue. Don't need to do this per se.
        return super.getSession(props);
    }

}
