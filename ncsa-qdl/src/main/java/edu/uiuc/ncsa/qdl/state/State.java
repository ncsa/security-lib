package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.scripting.QDLScript;
import edu.uiuc.ncsa.qdl.scripting.ScriptProvider;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;

import java.util.HashMap;

/**
 * This is a facade for the various stateful components we have to track.
 * Represents the internal state of the system. It has the {@link ImportManager},
 * {@link SymbolTable}, {@link edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator} and such.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:25 AM
 */
public class State extends FunctionState {

    public State(ImportManager resolver,
                 SymbolStack symbolStack,
                 OpEvaluator opEvaluator,
                 MetaEvaluator metaEvaluator,
                 FunctionTable functionTable,
                 ModuleMap moduleMap,
                 boolean isServerMode) {
        super(resolver,
                symbolStack,
                opEvaluator,
                metaEvaluator,
                functionTable,
                moduleMap);
        this.serverMode = isServerMode;
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public void setServerMode(boolean serverMode) {
        this.serverMode = serverMode;
    }

    boolean serverMode = false;

    HashMap<String, ScriptProvider> scriptProviders = new HashMap<>();

    public void addScriptProvider(ScriptProvider scriptProvider) {
        scriptProviders.put(scriptProvider.getScheme(), scriptProvider);
    }

    public void removeScriptProvider(String scheme) {
        scriptProviders.remove(scheme);
    }

    /**
     * Take a name, peel off any scheme and then check if there is
     *
     * @param fqName
     * @return
     */
    public QDLScript getScriptFromProvider(String fqName) {
        if (scriptProviders.isEmpty()) return null;
        String scheme = fqName.substring(0, fqName.indexOf(":") + 1);
        if (scheme == null || scheme.isEmpty()) {
            return null;
        }
        return scriptProviders.get(scheme).get(fqName);

    }

    public boolean hasScriptProviders() {
        return !scriptProviders.isEmpty();
    }

    /*public QDLScript getScript(String fqPath){
        scriptProviders.get
    }*/
    public State newStateNoImports() {
        ImportManager nr = new ImportManager();
        SymbolStack newStack = new SymbolStack(symbolStack.getParentTables());
        State newState = new State(nr,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap(),
                isServerMode());
        return newState;

    }

    /**
     * Takes this state object and sets up a new local environment. This is passed to things
     * like loops, functions, conditionals etc. The lifecycle of these is that they are basically abandoned
     * when done then garbage collected.
     *
     * @return State
     */
    public State newStateWithImports() {
        //System.out.println("** State, creating new local state **");
        SymbolStack newStack = new SymbolStack(symbolStack.getParentTables());
        State newState = new State(importedModules,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap(),
                isServerMode());
        return newState;
    }

    /**
     * For modules only. This copies the state except that no functions are inherited. The
     * contract is that modules only internal state that may be imported.
     *
     * @return
     */
    public State newModuleState() {
        // NOTE this has no parents. Modules have completely clear state when starting!
        ImportManager r = new ImportManager();
        SymbolStack newStack = new SymbolStack();
        newStack.addParent(new SymbolTableImpl());
        State newState = new State(r,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                new FunctionTable(),
                getModuleMap(),
                isServerMode());
        return newState;
    }


    public void addModule(Module module) {
        if (module instanceof JavaModule) {
            ((JavaModule) module).init(this.newModuleState());
        }
        getModuleMap().put(module);

    }

    public int getStackSize(){
      return getSymbolStack().getSymbolCount();
    }


}
