package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.security.core.util.DoubleHashMap;

/**
 * Keeps namepsaces and their aliases. It is assumed that both of these are unique.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:13 AM
 */
public class NamespaceResolver {
    public String getActiveNamespace() {
        return activeNamespace;
    }

    public void setActiveNamespace(String activeNamespace) {
        this.activeNamespace = activeNamespace;
    }

    public String getDefaultUserNamespace() {
        return QDL_DEFAULT_USER_NAMESPACE;
    }

    public boolean hasNamespace(String namespace){
        return map.containsKey(namespace);
    }

    public boolean isDefaultNamespaceActive(){
        return getActiveNamespace().equals(getDefaultUserNamespace());
    }

    public String getDefaultSystemNamespace() {
        return QDL_DEFAULT_SYSTEM_NAMESPACE;
    }

    public String getDefaultSystemAlias() {
        return QDL_DEFAULT_SYSTEM_ALIAS;
    }

    public String getDefaultUserAlias() {
        return QDL_DEFAULT_USER_ALIAS;
    }

    public String activeNamespace = QDL_DEFAULT_USER_NAMESPACE;// default
    /**
     * The default system namespace is for all og the built-in functions.
     */
    public static String QDL_DEFAULT_SYSTEM_NAMESPACE = "qdl:/system";
    public static String QDL_DEFAULT_SYSTEM_ALIAS = "sys";
    /**
     * All variables and functions a user defines are in this namespace unless
     * a new namespace is set.
     */
    public static String QDL_DEFAULT_USER_NAMESPACE = "qdl:/user";
    public static String QDL_DEFAULT_USER_ALIAS = "user";

    public NamespaceResolver() {
        // always pre-populate.
        map.put(QDL_DEFAULT_SYSTEM_NAMESPACE, QDL_DEFAULT_SYSTEM_ALIAS);
        map.put(QDL_DEFAULT_USER_NAMESPACE, QDL_DEFAULT_USER_ALIAS);
    }

    /**
     * In the current architecture, there should only ever be one of this for the entire
     * running instance of the system.
     *
     * @return
     */
    public static NamespaceResolver getResolver() {
        if (resolver == null) {
            resolver = new NamespaceResolver();
        }
        return resolver;
    }

    public static void setResolver(NamespaceResolver resolver) {
        NamespaceResolver.resolver = resolver;
    }

    static NamespaceResolver resolver;

    DoubleHashMap<String, String> map = new DoubleHashMap<>();

    public void createNS(String namespace, String alias) {
        map.put(namespace, alias);
    }

    public String getByAlias(String alias) {
        return map.getByValue(alias);
    }

    public String getAlias(String namespace) {
        return map.get(namespace);
    }

    public boolean hasAlias(String alias) {
        return map.getByValue(alias) != null;
    }

    public void remove(String namespace) {
        map.remove(namespace);
    }
}
