package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.client.ClientProvider;
import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.sas.example.EchoExecutable;
import edu.uiuc.ncsa.sas.loader.SASConfigurationLoader;
import edu.uiuc.ncsa.sas.thing.action.*;
import edu.uiuc.ncsa.sas.thing.response.*;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.exceptions.UnknownClientException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.servlet.*;
import edu.uiuc.ncsa.security.util.cli.ExitException;
import edu.uiuc.ncsa.security.util.crypto.KeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeys;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import static edu.uiuc.ncsa.security.servlet.AbstractServlet.getEnvironment;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:29 AM
 */
public class SASServlet extends HttpServlet implements Logable {
    JSONWebKeys jsonWebKeys;

    /*
      A lot of what comes next is boiler plated from the AbstractServlet. This is because
      we need to have it peacefully co-exist as part of the deployment with, say OA4MP
      and the monolithic static nature of the Environment (OA2SE) precluded that. Since this
      is a largely self-contained class (so really not much cause to extend it) this should
      work. Until, of course, it does not.
     */
    SASConfigurationLoader<? extends SASEnvironment> configurationLoader;

    public Initialization getInitialization() {
        return initialization;
    }

    public void setInitialization(Initialization initialization) {
        this.initialization = initialization;
    }

    protected Initialization initialization;

    public SASConfigurationLoader<? extends SASEnvironment> getConfigurationLoader() {
        return configurationLoader;
    }

    public void setConfigurationLoader(SASConfigurationLoader<? extends SASEnvironment> b) {
        configurationLoader = b;
    }


    public void setSASE(SASEnvironment env) {
        sase = env;
    }

    protected SASEnvironment sase;


    public void loadEnvironment() throws IOException {
        setSASE(getConfigurationLoader().load());
    }

    protected SASEnvironment getSASE() {
        return (SASEnvironment) getEnvironment();
    }


    public boolean isDebugOn() {
        return getMyLogger().isDebugOn();
    }

    public void setDebugOn(boolean setOn) {
        getMyLogger().setDebugOn(setOn);
    }

    protected MyLoggingFacade getMyLogger() {
        if (sase != null) {
            return sase.getMyLogger();
        }
        // always return one so even if things blow up some record remains...
        return new MyLoggingFacade("oa4mp");
    }

    public void debug(String x) {
        getMyLogger().debug(x);
    }

    public void error(String x) {
        getMyLogger().error(x);
    }

    public void error(String x, Throwable t) {
        getMyLogger().error(x, t);
    }

    public void info(String x) {
        getMyLogger().info(x);
    }

    public void warn(String x) {
        getMyLogger().warn(x);
    }

    String ACTION_TAG = "action=";
    String SESSION_TAG = "session_id=";
    String ID_TAG = "id=";

    @Override
    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        throw new UnsupportedOperationException("This server does not support GET");
    }


    protected LogonResponse doLogon(LogonAction logonAction, SessionRecord sessionRecord) throws IOException {

        // check they are a user
        UUID sessionID = UUID.randomUUID();
        // have to figure out the public key size.
        int totalBytes = ((RSAPublicKey) sessionRecord.client.getPublicKey()).getModulus().bitLength() / 8;
        LogonResponse logonResponse = new LogonResponse(logonAction, sessionID, new byte[]{32, 64});
        // Take the total bytes (limited by RSA key size), remove the response size without key,
        // estimate key. The 3/4 accounts for the amount of space lost to base 64 encoding.
        //    System.out.println(getClass().getCanonicalName() + " stats:")
        //    System.out.println(getClass().getCanonicalName() + " total bytes:" + totalBytes);
        //    System.out.println(getClass().getCanonicalName() + " JSON =" + logonResponse.serialize().toString(1));
        //    System.out.println(getClass().getCanonicalName() + " JSON size=" + logonResponse.serialize().toString(1).length());

        int keySize = (totalBytes - logonResponse.serialize().toString(1).length()) * 3 / 4;
        //   System.out.println(getClass().getCanonicalName() + " key size:" + keySize);
        //   System.out.println(getClass().getSimpleName() + ": s key size =" + keySize);
        byte[] sKey = KeyUtil.generateSKey(keySize); //1024 bits, probably.
        logonResponse = new LogonResponse(logonAction, sessionID, sKey);
        sessionRecord.executable = createExecutable(logonAction.getExecutableType());
        sessionRecord.sKey = sKey;
        sessionRecord.sessionID = sessionID;
        sessions.put(sessionID, sessionRecord);
        return logonResponse;
    }

    /**
     * Create the appropriate executable.
     *
     * @param executableName name to allow you to choose which executable to create
     * @return
     */
    public Executable createExecutable(String executableName) {
        return new EchoExecutable();   // Just to demo this. Write your own and override.
    }

    protected LogoffResponse doLogoff(SASClient client, LogoffAction logoffAction, HttpServletResponse response, SessionRecord sessionRecord, String message) throws IOException {
        LogoffResponse logoffResponse = new LogoffResponse(logoffAction, message);
        //getSATE().getResponseSerializer().serialize(logoffResponse, response, sessionRecord);
        sessions.remove(sessionRecord.sessionID);
        return logoffResponse;
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        throw new UnsupportedOperationException("This server does not support DELETE");
    }

    @Override
    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        try {
            doIt(httpServletRequest, httpServletResponse);
        } catch (Throwable e) {
            e.printStackTrace();
            handleException(new SASExceptionHandlerThingie(e, httpServletRequest, httpServletResponse, null));
        }
    }

    protected void doIt(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Throwable {
        // Either there is basic auth (to do logon) or there is a session id. The payload is always a blob.
        if (getSASE().hasAccessList()) {
            String ipAddress = httpServletRequest.getRemoteAddr();
            if (!getSASE().getAccessList().contains(ipAddress)) {
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        String rawSessionID = httpServletRequest.getHeader(SASConstants.HEADER_SESSION_ID);
        Identifier clientID = null;
        UUID sessionID = null;
        boolean isLogon = false;
        if (rawSessionID == null) {
            clientID = HeaderUtils.getIDFromHeaders(httpServletRequest);
            isLogon = true;

        } else {
            sessionID = UUID.fromString(rawSessionID);
        }


        SASClient client = null;
        if (clientID != null) {
            if (!ClientProvider.isClientID(clientID.getUri().toString())) {
                throw new UnknownClientException("unknown client \"" + clientID + "\"");
            }
            if (!getSASE().getClientStore().containsKey(clientID)) {
                throw new UnknownClientException("unknown client \"" + clientID + "\"");
            }
            client = getSASE().getClientStore().get(clientID);
  /*          doLogon(client, httpServletResponse);
            return;*/
        }

        // If it gets to here, then this is a pending session.
        SessionRecord sessionRecord = null;
        if (sessionID != null) {
            sessionRecord = sessions.get(sessionID);
        } else {
            sessionRecord = new SessionRecord(client, null);
        }
        // From this point forward, we can do secure error responses. Up until here we don't
        // have the key, etc.
        try {
            List<Action> actions;
            if (isLogon) {
                actions = getSASE().getRequestDeserializer().rsaDeserialize(sessionRecord, httpServletRequest);
            } else {
                actions = getSASE().getRequestDeserializer().sDeserialize(sessionRecord, httpServletRequest);
            }
            List<Response> responses = new ArrayList<>();

            for (int i = 0; i < actions.size(); i++) {
                Response response = null;
                switch (actions.get(i).getType()) {
                    case SASConstants.ACTION_LOGON:
                        response = doLogon((LogonAction) actions.get(i), sessionRecord);
                        break;
                    case SASConstants.ACTION_LOGOFF:
                        response = doLogoff(client, (LogoffAction) actions.get(i), httpServletResponse, sessionRecord, "log off");
                        break;
                    case SASConstants.ACTION_NEW_KEY:
                        response = doNewKey(client, (NewKeyAction) actions.get(i), httpServletResponse, sessionRecord);
                        break;
                    default:
                    case SASConstants.ACTION_EXECUTE:
                        try {
                            response = doExecute(sessionRecord, actions.get(i));
                        } catch (ExitException xx) {
                            // Shutdown hook. An application can throw this to signal logging off. In this way
                            // a user does not need to know about the SAS, but can simply exit their application
                            // normally and the effect is the same.
                            response = doLogoff(client, (LogoffAction) actions.get(i), httpServletResponse, sessionRecord, "log off");
                        }
                        break;
                }
                responses.add(response);
            }
            sessionRecord.lastAccessed = new Date();
            getSASE().getResponseSerializer().serialize(responses, httpServletResponse, sessionRecord);

        } catch (Throwable t) {
            handleException(new SASExceptionHandlerThingie(t, httpServletRequest, httpServletResponse, sessionRecord));
        }

    }

    protected Response doNewKey(SASClient client, NewKeyAction newKeyAction, HttpServletResponse httpServletResponse, SessionRecord sessionRecord) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[newKeyAction.getSize()];
        secureRandom.nextBytes(key);
        return new NewKeyResponse(newKeyAction, key);
    }

    /**
     * Invoke a specific method in the {@link Executable}
     *
     * @param sessionRecord
     * @param invokeAction
     * @return
     */
    protected OutputResponse invoke(SessionRecord sessionRecord, InvokeAction invokeAction) {
        Executable exe = sessionRecord.executable;
        exe.getIO().flush();
        sessionRecord.executable.execute(invokeAction);
        sessionRecord.lastAccessed = new Date();
        StringBuilder output = ((StringIO) exe.getIO()).getOutput();
        return new OutputResponse(invokeAction, output.toString());
    }

    Map<UUID, SessionRecord> sessions = new HashMap<>();

    protected Response doExecute(SessionRecord sessionRecord, Action action) {
        Executable exe = sessionRecord.executable;
        if (exe == null) {
            throw new GeneralException("Session with id " + sessionRecord.sessionID + " not found");
        }
        exe.getIO().flush();
        Response r = exe.execute(action);
        sessionRecord.lastAccessed = new Date();
        return r;
    }


    protected void handleException(ExceptionHandlerThingie xh) throws IOException, ServletException {
        Throwable t = xh.throwable;
        if (t instanceof NFWException) {
            error("INTERNAL ERROR: " + (t.getMessage() == null ? "(no message)" : t.getMessage()), t); // log it appropriately
        }
        // ok, if it is a strange error, print a stack if you need to.

        getExceptionHandler().handleException((SASExceptionHandlerThingie) xh);
    }

    public static class SASExceptionHandlerThingie extends ExceptionHandlerThingie {
        public SASExceptionHandlerThingie(Throwable throwable,
                                          HttpServletRequest request,
                                          HttpServletResponse response,
                                          SessionRecord sessionRecord) {
            super(throwable, request, response);
            this.sessionRecord = sessionRecord;
        }

        public boolean hasSessionRecord() {
            return sessionRecord != null;
        }

        public SessionRecord sessionRecord;
    }

    ExceptionHandler exceptionHandler = null;

    public ExceptionHandler getExceptionHandler() {
        if (exceptionHandler == null) {
            exceptionHandler = new BasicExceptionHandler(getMyLogger());
        }
        return exceptionHandler;
    }
}