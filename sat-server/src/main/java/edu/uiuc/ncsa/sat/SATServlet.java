package edu.uiuc.ncsa.sat;

import edu.uiuc.ncsa.sat.client.ClientProvider;
import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.sat.thing.LogoffResponse;
import edu.uiuc.ncsa.sat.thing.LogonResponse;
import edu.uiuc.ncsa.sat.thing.OutputResponse;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.UnknownClientException;
import edu.uiuc.ncsa.security.servlet.AbstractServlet;
import edu.uiuc.ncsa.security.servlet.HeaderUtils;
import edu.uiuc.ncsa.security.util.cli.Executable;
import edu.uiuc.ncsa.security.util.cli.IOInterface;
import edu.uiuc.ncsa.security.util.cli.Message;
import edu.uiuc.ncsa.security.util.crypto.KeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeys;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:29 AM
 */
public class SATServlet extends AbstractServlet {
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

    protected SATEnvironment getSATE() {
        return (SATEnvironment) getEnvironment();
    }

    String ACTION_TAG = "action=";
    String SESSION_TAG = "session_id=";
    String ID_TAG = "id=";

    @Override
    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        throw new UnsupportedOperationException("This server does not support GET");
    }


    protected void doLogon(SATClient client, HttpServletResponse response) throws IOException {
        // check they are a user
        UUID sessionID = UUID.randomUUID();
        // have to figure out the public key size.
        int pkeySize = ((RSAPublicKey) client.getPublicKey()).getModulus().bitLength();
        // Take the minimum. This way if the RSA key is smaller (E.g. 1024) the symmetric key
        // still fits in the response.
        int sKeySize = Math.min(pkeySize / 4, 1024) / 8;
        System.out.println(getClass().getSimpleName() + ": s key size =" + sKeySize);
        byte[] sKey = KeyUtil.generateSKey(sKeySize); //1024 bits, probably.
        LogonResponse logonResponse = new LogonResponse(sessionID);
        SessionRecord sessionRecord = new SessionRecord(client, createExecutable());
        sessionRecord.sKey = sKey;
        sessionRecord.sessionID = sessionID;
        sessions.put(sessionID, sessionRecord);
        getSATE().getResponseSerializer().serialize(logonResponse, response, sessionRecord);
    }

    protected Executable createExecutable() {
        return new Executable() {
            @Override
            public void execute(String x) {
                System.out.println("test exec, got:" + x);
                getIO().println("test exec, got:" + x);
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

    protected void doLogoff(SATClient client, HttpServletResponse response, SessionRecord sessionRecord, String message) throws IOException {
        LogoffResponse logoffResponse = new LogoffResponse(message);
        getSATE().getResponseSerializer().serialize(logoffResponse, response, sessionRecord);
        sessions.remove(sessionRecord.sessionID);
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
            handleException(e, httpServletRequest, httpServletResponse);
        }
    }

    @Override
    protected void doIt(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Throwable {
        // Either there is basic auth (to do logon) or there is a session id. The payload is always a blob.
        String rawSessionID = httpServletRequest.getHeader(SATConstants.HEADER_SESSION_ID);
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
            doLogon(client, httpServletResponse);
            return;
        }

        // If it gets to here, then this is a pending session.
        SessionRecord sessionRecord = sessions.get(sessionID);
        JSONObject payload;
        if (sessionRecord == null) {
            throw new UnknownSessionException("No session for uuid \"" + sessionID + "\"");
        }
        client = sessionRecord.client;
        if (isLogon) {
            payload = getSATE().getRequestDeserializer().rsaDeserialize(sessionRecord, httpServletRequest);
        } else {
            payload = getSATE().getRequestDeserializer().sDeserialize(sessionRecord, httpServletRequest);
        }

        switch (getSATE().getRequestDeserializer().getAction(payload)) {
            case SATConstants.ACTION_LOGON:
                doLogon(client, httpServletResponse);
                break;
            case SATConstants.ACTION_LOGOFF:
                doLogoff(client, httpServletResponse, sessionRecord, "log off");
                break;
            case SATConstants.ACTION_EXECUTE:
                OutputResponse outputResponse = new OutputResponse();
                outputResponse.content = execute(sessionRecord, getSATE().getRequestDeserializer().getContent(payload));
                getSATE().getResponseSerializer().serialize(outputResponse, httpServletResponse, sessionRecord);
                sessionRecord.lastAccessed = new Date();
                break;
        }

    }

    Map<UUID, SessionRecord> sessions = new HashMap<>();

    public static class SessionRecord {

        public SessionRecord(SATClient client, Executable executable) {
            this.client = client;
            this.executable = executable;
        }

        public SATClient client;
        public Executable executable;
        public Date createTS = new Date();
        public Date lastAccessed = new Date();
        public Message message = new Message();
        public byte[] sKey;
        public UUID sessionID;
    }

    protected String execute(SessionRecord sessionRecord, String content) {
        Executable exe = sessionRecord.executable;
        if (exe == null) {
            throw new GeneralException("Session with id " + sessionRecord.sessionID + " not found");
        }
        exe.getIO().flush();
        exe.execute(content);
        sessionRecord.lastAccessed = new Date();
        StringBuilder output = ((StringIO) exe.getIO()).getOutput();
        if (output == null) {
            return "";
        }
        return output.toString();

    }

}
