package edu.uiuc.ncsa.security.util.functor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  8:52 AM
 */
public interface JFunctor {
    Object execute();
    Object getResult();
    ArrayList<Object> getArgs();
    String getName();
    void addArg(String x);
    void addArg(Integer x);
    void addArg(JFunctor x);
    void addArg(List<JFunctor> functors);
}
