package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.sas.thing.action.ActionDeserializer;
import edu.uiuc.ncsa.sas.thing.response.ResponseSerializer;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:33 AM
 */
public class SASEnvironment extends AbstractEnvironment {
    public SASEnvironment(MyLoggingFacade myLogger,
                          Store<? extends SASClient> clientStore,
                          ActionDeserializer actionDeserializer,
                          ResponseSerializer responseSerializer) {
        super(myLogger);
        this.clientStore = clientStore;
        this.actionDeserializer = actionDeserializer;
        this.responseSerializer = responseSerializer;
    }

    public Store<? extends SASClient> getClientStore(){
        return clientStore;
    }

    public void setClientStore(Store<? extends SASClient> clientStore) {
        this.clientStore = clientStore;
    }

    Store<? extends SASClient> clientStore;
    public ActionDeserializer getRequestDeserializer() {
        if(actionDeserializer == null){
            actionDeserializer = new ActionDeserializer();
        }
        return actionDeserializer;
    }

    ActionDeserializer actionDeserializer;

    public ResponseSerializer getResponseSerializer() {
        if(responseSerializer == null){
            responseSerializer = new ResponseSerializer();
        }
        return responseSerializer;
    }

    ResponseSerializer responseSerializer;
}
