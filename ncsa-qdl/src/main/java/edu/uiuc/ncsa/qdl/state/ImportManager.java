package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.*;
import edu.uiuc.ncsa.qdl.exceptions.ImportException;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Keeps imported namespaces and their aliases. It is assumed that both of these are unique.
 * Modules are kept elsewhere and a module must be imported before it ends up here.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:13 AM
 */
public class ImportManager implements Serializable {
    public static String[] RESERVED_ALIAS = new String[]{
            MathEvaluator.MATH_NAMESPACE,
            TMathEvaluator.TMATH_NAMESPACE,
            SystemEvaluator.SYS_NAMESPACE,
            IOEvaluator.IO_NAMESPACE,
            StemEvaluator.STEM_NAMESPACE,
            StringEvaluator.STRING_NAMESPACE,
            ListEvaluator.LIST_NAMESPACE
    };

    protected void checkAlias(String alias) {
        if (alias == null || alias.isEmpty()) {
            throw new ImportException("the alias is empty or null.");
        }
        for (String x : RESERVED_ALIAS) {
            if (alias.equals(x)) {
                throw new ImportException("the alias \"" + alias + "\" is reserved for system use.");
            }
        }
    }

    private static final long serialVersionUID = 0xcafed00d5L;
    /**
     * Delimiter for namespaces. Change this and you will probably break then entire system...
     */
    public static final String NS_DELIMITER = "#";

    public void addImport(URI moduleName, String alias) {
        checkAlias(alias);
        // If there is already an entry, overwrite it in case the alias changed.
        surjection.put(alias,moduleName);
    }

    public ImportManager() {
    }

    /**
     * In the current architecture, there should only ever be one of this for the entire
     * running instance of the system.
     *
     * @return
     */
    public static ImportManager getResolver() {
        if (resolver == null) {
            resolver = new ImportManager();
        }
        return resolver;
    }

    public static void setResolver(ImportManager resolver) {
        ImportManager.resolver = resolver;
    }

    static ImportManager resolver;

    public boolean hasImports() {
        return !surjection.isEmpty();
    }

    protected Surjection<String,URI> surjection = new Surjection<>();


    public URI getByAlias(String alias) {
        return surjection.get(alias);
    }

    public List<String> getAlias(URI namespace) {
        return surjection.getByURI(namespace);
    }

    public boolean hasAlias(String alias) {
        return surjection.containsKey(alias);
    }

    public void remove(URI namespace) {
        surjection.remove(namespace);
    }

    public Collection<URI> keySet() {
        return surjection.values();
    }
}
