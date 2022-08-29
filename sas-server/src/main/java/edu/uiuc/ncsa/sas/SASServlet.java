package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.satclient.ClientProvider;
import edu.uiuc.ncsa.sas.satclient.SATClient;
import edu.uiuc.ncsa.sas.loader.SASExceptionHandler;
import edu.uiuc.ncsa.sas.thing.action.*;
import edu.uiuc.ncsa.sas.thing.response.*;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.exceptions.UnknownClientException;
import edu.uiuc.ncsa.security.servlet.AbstractServlet;
import edu.uiuc.ncsa.security.servlet.HeaderUtils;
import edu.uiuc.ncsa.security.util.cli.IOInterface;
import edu.uiuc.ncsa.security.util.crypto.KeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeys;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:29 AM
 */
public class SASServlet extends AbstractServlet {
    JSONWebKeys jsonWebKeys;

    @Override
    public void loadEnvironment() throws IOException {
        setEnvironment(getConfigurationLoader().load());
        try {
            JSONWebKeys jsonWebKeys = JSONWebKeyUtil.fromJSON(new File("/home/ncsa/dev/ncsa-git/security-lib/crypto/src/main/resources/keys.jwk"));
            jsonWebKeys.setDefaultKeyID("2D700DF531E09B455B9E74D018F9A133"); // for testing!
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    protected SASEnvironment getSATE() {
        return (SASEnvironment) getEnvironment();
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
        // Take the total bytes (limited by RSA key size), remove the response size withoout key,
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
        sessionRecord.executable = createExecutable();
        sessionRecord.sKey = sKey;
        sessionRecord.sessionID = sessionID;
        sessions.put(sessionID, sessionRecord);
        return logonResponse;
    }

    protected Executable createExecutable() {
        return new Executable() {
            @Override
            public Response execute(Action action) {
                StringBuilder output;
                switch (action.getType()) {
                    case SASConstants.ACTION_EXECUTE:
                        ExecuteAction executeAction = (ExecuteAction) action;
                        getIO().println("test: execute(" + executeAction.getArg() + ")");
                        break;
                    case SASConstants.ACTION_INVOKE:
                        InvokeAction invokeAction = (InvokeAction) action;
                        getIO().println("test: " + invokeAction.getName() + "(" + invokeAction.getArgs() + ")");
                        break;
                    default:
                        getIO().println("test exec, got action:" + action.getType());
                }
                output = ((StringIO) getIO()).getOutput();
                return new OutputResponse(action, output.toString());

            }

            IOInterface ioInterface = new StringIO("");

            @Override
            public IOInterface getIO() {
                return ioInterface;
            }

            @Override
            public void setIO(IOInterface io) {
                ioInterface = io;
            }
        };
    }

    protected LogoffResponse doLogoff(SATClient client, LogoffAction logoffAction, HttpServletResponse response, SessionRecord sessionRecord, String message) throws IOException {
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
            handleException(e, httpServletRequest, httpServletResponse);
        }
    }

    @Override
    protected void doIt(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Throwable {
        // Either there is basic auth (to do logon) or there is a session id. The payload is always a blob.

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


        SATClient client = null;
        if (clientID != null) {
            if (!ClientProvider.isClientID(clientID.getUri().toString())) {
                throw new UnknownClientException("unknown client \"" + clientID + "\"");
            }
            if (!getSATE().getClientStore().containsKey(clientID)) {
                throw new UnknownClientException("unknown client \"" + clientID + "\"");
            }
            client = getSATE().getClientStore().get(clientID);
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
                actions = getSATE().getRequestDeserializer().rsaDeserialize(sessionRecord, httpServletRequest);
            } else {
                actions = getSATE().getRequestDeserializer().sDeserialize(sessionRecord, httpServletRequest);
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
                        response = doExecute(sessionRecord, actions.get(i));
                        break;
                }
                responses.add(response);
            }
            sessionRecord.lastAccessed = new Date();
            getSATE().getResponseSerializer().serialize(responses, httpServletResponse, sessionRecord);

        } catch (Throwable t) {
            handleException(t, httpServletRequest, httpServletResponse, sessionRecord);
        }

    }

    protected Response doNewKey(SATClient client, NewKeyAction newKeyAction, HttpServletResponse httpServletResponse, SessionRecord sessionRecord) {
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


    protected void handleException(Throwable t,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   SessionRecord sessionRecord) throws IOException, ServletException {
        if (t instanceof NFWException) {
            error("INTERNAL ERROR: " + (t.getMessage() == null ? "(no message)" : t.getMessage()), t); // log it appropriately
        }
        // ok, if it is a strange error, print a stack if you need to.
        if (sessionRecord == null) {
            getExceptionHandler().handleException(t, request, response);
        }
        getExceptionHandler().handleException(t, request, response, sessionRecord);
    }

    @Override
    public SASExceptionHandler getExceptionHandler() {
        return (SASExceptionHandler) super.getExceptionHandler();
    }
}
