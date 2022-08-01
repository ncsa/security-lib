package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A map of all functors that have resulted in the execution of a functor. This lets you recover them in toto
 * and pass them around.
 * <p>Created by Jeff Gaynor<br>
 * on 3/22/18 at  2:16 PM
 */
public class FunctorMap extends HashMap<String, List<JFunctor>> {
    public void put(JFunctor functor) {
        if(functor == null){
            return;
        }
        if (containsKey(functor.getName())) {
            get(functor.getName()).add(functor);
        } else {
            ArrayList<JFunctor> x = new ArrayList<>();
            x.add(functor);
            put(functor.getName(), x);
        }
    }


    public boolean containsKey(JFunctor jFunctor) {
        return super.containsKey(jFunctor.getName());
    }

    /**
     * Add all of the functors in the argument to this map.
     *
     * @param functorMap
     * @return
     */
    public void addAll(FunctorMap functorMap) {
        if(functorMap == null){
            return;
        }
        for (String key : functorMap.keySet()) {
            List<JFunctor> functors = functorMap.get(key);
            if (containsKey(key)) {
                get(key).addAll(functors);
            } else {
                put(key, functors);
            }
        }
    }
}
