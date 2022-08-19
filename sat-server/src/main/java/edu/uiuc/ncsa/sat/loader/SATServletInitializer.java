package edu.uiuc.ncsa.sat.loader;

import edu.uiuc.ncsa.sat.SATConstants;
import edu.uiuc.ncsa.sat.SATEnvironment;
import edu.uiuc.ncsa.sat.client.ClientConverter;
import edu.uiuc.ncsa.sat.client.ClientKeys;
import edu.uiuc.ncsa.sat.client.ClientProvider;
import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.servlet.AbstractServlet;
import edu.uiuc.ncsa.security.servlet.ExceptionHandler;
import edu.uiuc.ncsa.security.servlet.Initialization;
import edu.uiuc.ncsa.security.storage.MemoryStore;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeys;

import javax.servlet.ServletException;
import java.io.File;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  4:00 PM
 */
public class SATServletInitializer implements Initialization {
    static boolean isInitRun = false;

    @Override
    public void init() throws ServletException {
        if (isInitRun) {
            return;
        }
        isInitRun = true;
        setupDebug();
    }

    public static Identifier testClientID = BasicIdentifier.newID(SATConstants.CLIENT_ID_HEAD + "debug_client");
    // sat:client/debugClient
    /**
     * Sets up a client store with exactly one element and uses canned keys. This is only for development
     * purposes!
      */
    protected void setupDebug() {
        JSONWebKeys jsonWebKeys = null;
        String keyPath = "/home/ncsa/dev/ncsa-git/security-lib/crypto/src/main/resources/keys.jwk";
        String keyID = "2D700DF531E09B455B9E74D018F9A133";
        try {
            jsonWebKeys = JSONWebKeyUtil.fromJSON(new File(keyPath));
            jsonWebKeys.setDefaultKeyID(keyID);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        Store<SATClient> clientStore = new MemoryStore<SATClient>(new ClientProvider()) {
            ClientConverter clientConverter;

            @Override
            public XMLConverter<SATClient> getXMLConverter() {
                if (clientConverter == null) {
                    clientConverter = new ClientConverter(new ClientKeys(), new ClientProvider());
                }
                return clientConverter;
            }

            @Override
            public List<SATClient> getMostRecent(int n, List<String> attributes) {
                return null;
            }
        };
        SATClient client = new SATClient(testClientID);
        client.setPublicKey(jsonWebKeys.getDefault().publicKey);
        client.setName("Debug client for SAT");
        clientStore.put(client.getIdentifier(), client);
        getEnvironment().setClientStore(clientStore);
    }

    @Override
    public void setEnvironment(AbstractEnvironment environment) {
        this.satEnvironment = (SATEnvironment) environment;
    }

    SATEnvironment satEnvironment;

    @Override
    public SATEnvironment getEnvironment() {
        return satEnvironment;
    }

    AbstractServlet abstractServlet;

    @Override
    public AbstractServlet getServlet() {
        return abstractServlet;
    }

    @Override
    public void setServlet(AbstractServlet servlet) {
        this.abstractServlet = servlet;
    }

    SATExceptionHandler satExceptionHandler = null;

    @Override
    public ExceptionHandler getExceptionHandler() {
        if (satExceptionHandler == null) {
            satExceptionHandler = new SATExceptionHandler();
        }
        return satExceptionHandler;
    }
}
