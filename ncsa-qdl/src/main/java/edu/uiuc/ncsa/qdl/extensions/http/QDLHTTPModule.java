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
        funcs.add(httpModule.new Close());
        funcs.add(httpModule.new CreateCredentials());
        funcs.add(httpModule.new Delete());
        funcs.add(httpModule.new Get());
        funcs.add(httpModule.new Headers());
        funcs.add(httpModule.new Host());
        funcs.add(httpModule.new IsOpen());
        funcs.add(httpModule.new Open());
        funcs.add(httpModule.new Post());
        funcs.add(httpModule.new Put());
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
            descr.add("Supported HTTP methods are");
            descr.add("   get - get from a server");
            descr.add("  post - post to a server");
            descr.add("   put - update a server");
            descr.add("delete - remove something from a server");
            descr.add("Other functions are");
            descr.add("open(host, is_secure) - open a connection. You may make it insecure if needed (not recommended)");
            descr.add("is_open() - is the connection open");
            descr.add("close() - close the current connection");
            descr.add("headers(arg.) - set the headers for subsequent operations");

            descr.add("\nThere are many ways of sending such requests. Generally here you may");
            descr.add("supply a uri path, which is appened to the host and parameters (in get or delete)");
            descr.add("You should read the individual requirements for the methods.");
        }
        return descr;
    }

    List<String> descr = new ArrayList<>();
}
