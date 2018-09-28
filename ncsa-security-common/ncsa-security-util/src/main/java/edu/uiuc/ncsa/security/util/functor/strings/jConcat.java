package edu.uiuc.ncsa.security.util.functor.strings;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * concat(A,B,C,...) <br/>
 * A functor that accepts a list of string and simply concatenates them into a single string.
 * <p>Created by Jeff Gaynor<br>
 * on 4/5/18 at  4:16 PM
 */
public class jConcat extends JFunctorImpl {
    public jConcat() {
        super(FunctorTypeImpl.CONCAT);
    }

    @Override
    public Object execute() {
        if(executed){
            return result;
        }
        if(getArgs().size() == 0){
            executed = true;
            result = "";
            return result;
        }
        String rc = "";
        for(int i = 0; i < getArgs().size(); i++){
            Object obj = getArgs().get(i);
            if(obj instanceof JFunctor){
                JFunctor jf = (JFunctor)obj;
                jf.execute();
                rc = rc + jf.getResult();
            }else{
                rc = rc + obj;
            }
        }
        executed = true;
        result = rc;
        return rc;
    }
}
