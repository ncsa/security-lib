package edu.uiuc.ncsa.qdl.module.legacy;

import edu.uiuc.ncsa.qdl.evaluate.*;
import edu.uiuc.ncsa.qdl.exceptions.ImportException;
import edu.uiuc.ncsa.qdl.state.Surjection;

import java.io.Serializable;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Keeps imported namespaces and their aliases. It is assumed that both of these are unique.
 * Modules are kept elsewhere and a module must be imported before it ends up here.
 * This relates the imports of a module (identified by its uri) to its imported aliases
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:13 AM
 */
public class MAliases implements Serializable {
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

    public void addImport(URI moduleName, String alias) {
        checkAlias(alias);
        // If there is already an entry, overwrite it in case the alias changed.
        surjection.put(alias,moduleName);
    }

    public MAliases() {
    }

    /**
     * In the current architecture, there should only ever be one of this for the entire
     * running instance of the system.
     *
     * @return
     */
    public static MAliases newMInstances() {
        if (mAliases == null) {
            mAliases = new MAliases();
        }
        return mAliases;
    }

    public static void setmInstances(MAliases mAliases) {
        MAliases.mAliases = mAliases;
    }

    static MAliases mAliases;

    public boolean hasImports() {
        return !surjection.isEmpty();
    }

    protected Surjection<String,URI> surjection = new Surjection<>();


    public URI getByAlias(String alias) {
        return surjection.get(alias);
    }

    public List<String> getAliases(URI namespace) {
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

    public void removeAlias(Object alias) {
        if(surjection.isEmpty()){
            return;
        }
        surjection.remove(alias);
    }
}
