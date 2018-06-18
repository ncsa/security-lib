package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;
import edu.uiuc.ncsa.security.util.functor.JMetaFunctor;

/**
 * This is the exclusive OR operator. This means that if there is a list of conditionals,
 * it will evaluate them in turn until the first of them is true and skip the rest.
 * The result is therefore true if exactly one of these has evaluated to true and false otherwise
 * if none do. This is very useful in creating statements that might be nested and require
 * elseif statements.
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/18 at  3:45 PM
 */
public class jXOr extends JFunctorImpl {
    public jXOr() {
        super(FunctorTypeImpl.XOR);
    }

    /**
     * The index of the functor that evaluated to true. Use this to get it from the arg list.
     * @return
     */
    public int getIndex() {
        return index;
    }

    protected int index = -1;


    @Override
       public Object execute() {
           if (isExecuted()) {
                     return result;
                 }
         //  checkArgs();
           boolean rc = false;
           for (int i = 0; i < args.size(); i++) {
               JMetaFunctor ff = (JMetaFunctor) args.get(i);
               ff.execute();
               if((Boolean) ff.getResult()){
                   index = i;
                   // then we are done.
                   rc = true;
                   break;
               }
           }
           result = rc;
           executed = rc;
           return rc;
       }
}
