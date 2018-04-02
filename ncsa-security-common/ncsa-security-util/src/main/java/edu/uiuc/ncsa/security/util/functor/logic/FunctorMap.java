package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/22/18 at  2:16 PM
 */
public class FunctorMap extends HashMap<String, List<JFunctor>> {
    public void put(JFunctor functor){
         if(containsKey(functor.getName())){
             get(functor.getName()).add(functor);
         }else{
             ArrayList<JFunctor> x = new ArrayList<>();
             x.add(functor);
             put(functor.getName(), x);
         }
    }
}
