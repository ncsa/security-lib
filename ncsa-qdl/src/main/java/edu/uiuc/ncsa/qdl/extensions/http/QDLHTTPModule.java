package edu.uiuc.ncsa.qdl.extensions.http;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/5/21 at  8:36 AM
 */
public class QDLHTTPModule extends JavaModule {
    public QDLHTTPModule() {
    }

    public QDLHTTPModule(URI namespace, String alias) {
        super(namespace, alias);
    }

    @Override
    public Module newInstance(State state) {
        QDLHTTPModule qdlhttp = new QDLHTTPModule(URI.create("qdl:/tools/http_client"), "http");
        HTTPClient httpModule = new HTTPClient();
        funcs.add(httpModule.new Host());
        funcs.add(httpModule.new Get());
        funcs.add(httpModule.new Headers());
        funcs.add(httpModule.new Open());
        funcs.add(httpModule.new Close());
        funcs.add(httpModule.new IsOpen());
        qdlhttp.addFunctions(funcs);
        if(state != null){
            qdlhttp.init(state);
        }
        return qdlhttp;
    }
    @Override
    public List<String> getDescription() {
        if (descr.isEmpty()) {
            descr.add("Module for the HTTP operations. This allows you to do HTTP GET, etc from QDL.");
        }
        return descr;
    }

    List<String> descr = new ArrayList<>();
}
