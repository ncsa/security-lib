package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.SASEnvironment;
import edu.uiuc.ncsa.sas.client.ClientConverter;
import edu.uiuc.ncsa.sas.client.ClientKeys;
import edu.uiuc.ncsa.sas.client.ClientProvider;
import edu.uiuc.ncsa.sas.client.SASClient;
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
public class SASServletInitializer implements Initialization {
    static boolean isInitRun = false;

    @Override
    public void init() throws ServletException {
        if (isInitRun) {
            return;
        }
        isInitRun = true;
        //setupDebug();
    }

    public static Identifier testClientID = BasicIdentifier.newID(SASConstants.CLIENT_ID_HEAD + "debug_client");
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
        Store<SASClient> clientStore = new MemoryStore<SASClient>(new ClientProvider()) {
            ClientConverter clientConverter;

            @Override
            public XMLConverter<SASClient> getXMLConverter() {
                if (clientConverter == null) {
                    clientConverter = new ClientConverter(new ClientKeys(), new ClientProvider());
                }
                return clientConverter;
            }

            @Override
            public List<SASClient> getMostRecent(int n, List<String> attributes) {
                return null;
            }
        };
        SASClient client = new SASClient(testClientID);
        client.setPublicKey(jsonWebKeys.getDefault().publicKey);
        client.setName("Debug client for SAS");
        clientStore.put(client.getIdentifier(), client);
        getEnvironment().setClientStore(clientStore);
    }

    @Override
    public void setEnvironment(AbstractEnvironment environment) {
        this.SASEnvironment = (SASEnvironment) environment;
    }

    SASEnvironment SASEnvironment;

    @Override
    public SASEnvironment getEnvironment() {
        return SASEnvironment;
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

    SASExceptionHandler SASExceptionHandler = null;

    @Override
    public ExceptionHandler getExceptionHandler() {
        if (SASExceptionHandler == null) {
            SASExceptionHandler = new SASExceptionHandler(getEnvironment());
        }
        return SASExceptionHandler;
    }
}
