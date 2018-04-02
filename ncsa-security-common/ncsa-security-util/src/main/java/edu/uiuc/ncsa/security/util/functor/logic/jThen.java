package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import java.util.ArrayList;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.THEN;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  10:38 AM
 */
public class jThen extends JFunctorImpl {
    public jThen() {
        super(THEN);
    }
    protected jThen(FunctorTypeImpl type){
        super(type);
    }

    /**
     * There is no actual result from this functor. What happens is that each element in its argument list is executed.
     *
     * @return
     */
    @Override
    public Object execute() {
        if (executed) {
               return result;
           }
        checkArgs();
        ArrayList<Object> results = new ArrayList<>();
        for (int i = 0; i < args.size(); i++) {
            JFunctorImpl ff = (JFunctorImpl) args.get(i);
            getFunctorMap().put(ff);
            ff.execute();
            results.add(ff.getResult());
        }
        result = results;
        executed = true;
        return result;
    }

    /**
     * A map of every functor that is associated at the top level with this block. Note that the value is list of
     * functors in case any of them are repeated (e.g. the set functor for claims). This allows you to look up specific functors
     * that have been executed directly.
     * @return
     */
    public FunctorMap getFunctorMap() {
        if(functorMap == null){
            functorMap = new FunctorMap();
        }
        return functorMap;
    }

    FunctorMap functorMap;

}
