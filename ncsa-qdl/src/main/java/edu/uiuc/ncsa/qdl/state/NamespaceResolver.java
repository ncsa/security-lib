package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.security.core.util.DoubleHashMap;

import java.net.URI;
import java.util.Set;

/**
 * Keeps namepsaces and their aliases. It is assumed that both of these are unique.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:13 AM
 */
public class NamespaceResolver {
    /**
     * Delimiter for namespaces
     */
    public static final String NS_DELIMITER = "#";

    public void addImport(URI moduleName, String alias) {
        // If there is already an entry, overwrite it in case the alias changed.
        map.put(moduleName, alias);


    }

    public NamespaceResolver() {
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
    public boolean hasImports(){
        return !map.isEmpty();
    }

    DoubleHashMap<URI, String> map = new DoubleHashMap<>();


    public URI getByAlias(String alias) {
        return map.getByValue(alias);
    }

    public String getAlias(URI namespace) {
        return map.get(namespace);
    }

    public boolean hasAlias(String alias) {
        return map.getByValue(alias) != null;
    }

    public void remove(URI namespace) {
        map.remove(namespace);
    }

    public Set<URI> keySet(){
        return map.keySet();
    }
}
