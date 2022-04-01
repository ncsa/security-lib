package edu.uiuc.ncsa.qdl.extensions.sets;

import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/1/22 at  12:28 PM
 */
public class QDLSetsModule extends JavaModule {
    public QDLSetsModule() {
    }

    public QDLSetsModule(URI namespace, String alias) {
        super(namespace, alias);
    }

    @Override
    public Module newInstance(State state) {
        //        QDLHTTPModule qdlhttp = new QDLHTTPModule(URI.create("qdl:/tools/http_client"), "http");
        QDLSetsModule module = new QDLSetsModule(URI.create("qdl:/tools/sets"), "sets");
        QDLSets qdlSets = new QDLSets();
        funcs.add(qdlSets.new ToSet());
        funcs.add(qdlSets.new IsMember());
        funcs.add(qdlSets.new Intersection());
        funcs.add(qdlSets.new Peek());
        funcs.add(qdlSets.new ToList());
        module.addFunctions(funcs);
        if(state != null){
            module.init(state);
        }
        setupModule(module);
        return module;
    }
       /*
            module_load('edu.uiuc.ncsa.qdl.extensions.sets.QDLSetsLoader', 'java') =: q
  module_import(q)
    a. := sets#to_set(['a','b','c'])
  b. := sets#to_set(['p','q','b','a'])
  is_member(a., b.)
        */
    @Override
    public List<String> getDescription() {
        if(doxx == null){
            doxx = new ArrayList<>();
            doxx.add("Module for set operations in QDL.");
            doxx.add("A set is a stem whose keys and values are the same, i.e., every");
            doxx.add("entry for a. is of the form a.'x' := 'x';");
            doxx.add("It is often the case that you need to consider lists as sets");
            doxx.add("and compare them. While it is possible to do that with the ");
            doxx.add("for_each function, the scaling is bad. The quicker option is to");
            doxx.add("simply turn them into sets and use operations like " + QDLSets.INTERSECTION_COMMAND + ",");
            doxx.add(QDLSets.IS_MEMBER_OF_COMMAND + " or " + QDLSets.PEEK_COMMAND);
        }
        return doxx;
    }
    List<String> doxx = null;
}
