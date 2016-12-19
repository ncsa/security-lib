package edu.uiuc.ncsa.security.oauth_2_0.server;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 12/19/16 at  11:26 AM
 */
public abstract class ScopeHandlerFactory {
    public ScopeHandlerFactory() {
    }

    public static ScopeHandlerFactory getFactory() {
        return factory;
    }

    public static void setFactory(ScopeHandlerFactory factory) {
        ScopeHandlerFactory.factory = factory;
    }

    static ScopeHandlerFactory factory;

    public  abstract ScopeHandler create(ScopeHandlerFactoryRequest request);

    public static ScopeHandler newInstance(ScopeHandlerFactoryRequest request){
        return getFactory().create(request);
    }

    public static boolean isFactorySet(){
        return factory != null;
    }
}
