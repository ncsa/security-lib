package edu.uiuc.ncsa.security.util.functor;

import java.util.ArrayList;
import java.util.List;

/**
 * The interface for functors that return an explicit result and have a name (e.g. "$exists") in addition to arguments
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  8:52 AM
 */
public interface JFunctor extends JMetaFunctor{

    String getName();

    ArrayList<Object> getArgs();
    void addArg(String x);
    void addArg(Integer x);
    void addArg(JFunctor x);
    void addArg(JMetaFunctor x);
    void addArg(List<JFunctor> functors);
}
