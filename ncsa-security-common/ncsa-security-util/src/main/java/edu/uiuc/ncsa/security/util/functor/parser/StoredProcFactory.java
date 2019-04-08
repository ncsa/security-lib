package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.JMetaMetaFunctor;
import edu.uiuc.ncsa.security.util.json.JSONEntry;
import edu.uiuc.ncsa.security.util.json.JSONStore;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/11/19 at  11:21 AM
 */
public class StoredProcFactory extends JFunctorFactory {
    JSONStore<? extends JSONEntry> store;

    public StoredProcFactory(JSONStore store) {
        this.store = store;
    }

    public StoredProcFactory(JSONStore store, boolean verboseOn) {
        super(verboseOn);
        this.store = store;
    }

    @Override
    public JMetaMetaFunctor lookUpFunctor(String name) {
        JMetaMetaFunctor ff =  super.lookUpFunctor(name);
              if(ff != null){
                  return ff;
              }
        Identifier id = BasicIdentifier.newID(name);
        // assumes FQ name
        if(store.containsKey(id)){
            JSONEntry entry  = store.get(id);
            List<String> rawProc  = entry.getProcedure();
            Procedure procedure  = new Procedure(rawProc, this);
            return procedure;
        }
        return null;
    }
}
