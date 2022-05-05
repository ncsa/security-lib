package edu.uiuc.ncsa.qdl.extensions.database;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/5/22 at  12:02 PM
 */
public class QDLDBModule extends JavaModule {
    public QDLDBModule() {
    }

    public QDLDBModule(URI namespace, String alias) {
        super(namespace, alias);
    }

    @Override
    public Module newInstance(State state) {
        QDLDBModule module = new QDLDBModule(URI.create("qdl:/tools/db"), "db");

        QDLDB qdldb = new QDLDB();
        funcs.add(qdldb.new Connect());
        funcs.add(qdldb.new Read());
        funcs.add(qdldb.new Update());
        funcs.add(qdldb.new Execute());

        vars.add(qdldb.new SQLTypes());
        module.addFunctions(funcs);
        module.addVariables(vars);
        if (state != null) {
            module.init(state);
        }
        setupModule(module);
        return module;
    }
    @Override
    public List<String> getDescription() {
        if(doxx == null){
            doxx = new ArrayList<>();
            doxx.add("Module for database operations in QDL.");
            doxx.add("This supports CRUD (Create, Read, Update and Delete) operations in");
            doxx.add("mysql, maridb, postgresql and derby");
            doxx.add("There are 4 basic functions available");
            doxx.add(QDLDB.QUERY_COMMAND + " allows for statements that return a result, such as select or count");
            doxx.add(QDLDB.UPDATE_COMMAND + " allows for updating an existing database. This does not return a result");
            doxx.add(QDLDB.EXECUTE_COMMAND + " allows for executing other commands that do not return a result, such as insert or delete");
            doxx.add(QDLDB.CONNECT_COMMAND + " command that connects to a database. You must run this first before access the database");
            doxx.add("    or an error will occur.");
            doxx.add("This is not intended to be a full fledged database access module, it is designed to provide the tools");
            doxx.add("to write one with all basic access patterns and data conversions taken care of");
            doxx.add("Database administration (such as creating them and setting permissions, etc.) are outside of this");
            doxx.add("module. This module is for access.");
       }
        return doxx;
    }
    List<String> doxx = null;
}
