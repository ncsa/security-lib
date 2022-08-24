package edu.uiuc.ncsa.sat;

import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.sat.thing.ResponseSerializer;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:33 AM
 */
public class SATEnvironment extends AbstractEnvironment {
    public SATEnvironment(MyLoggingFacade myLogger,
                          Store<? extends SATClient> clientStore,
                          RequestDeserializer requestDeserializer,
                          ResponseSerializer responseSerializer) {
        super(myLogger);
        this.clientStore = clientStore;
        this.requestDeserializer = requestDeserializer;
        this.responseSerializer = responseSerializer;
    }

    public Store<? extends SATClient> getClientStore(){
        return clientStore;
    }

    public void setClientStore(Store<? extends SATClient> clientStore) {
        this.clientStore = clientStore;
    }

    Store<? extends SATClient> clientStore;
    public RequestDeserializer getRequestDeserializer() {
        if(requestDeserializer == null){
            requestDeserializer = new RequestDeserializer();
        }
        return requestDeserializer;
    }

    RequestDeserializer requestDeserializer;

    public ResponseSerializer getResponseSerializer() {
        if(responseSerializer == null){
            responseSerializer = new ResponseSerializer();
        }
        return responseSerializer;
    }

    ResponseSerializer responseSerializer;
}
