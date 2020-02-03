package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;

/**
 * This is a facade for the various stateful components we have to track. 
 * Represents the internal state of the system. It has the {@link NamespaceResolver},
 * {@link SymbolTable}, {@link edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator} and such.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:25 AM
 */
public class State extends FunctionState {

    public State(NamespaceResolver resolver,
                 SymbolStack symbolStack,
                 OpEvaluator opEvaluator,
                 MetaEvaluator metaEvaluator,
                 FunctionTable functionTable,
                 ModuleMap moduleMap) {
        super(resolver,
                symbolStack,
                opEvaluator,
                metaEvaluator,
                functionTable,
                moduleMap);
    }


    public State newStateNoImports() {
        NamespaceResolver nr = new NamespaceResolver();
        SymbolStack newStack = new SymbolStack(symbolStack.getParentTables());
        State newState = new State(nr,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap());
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
        SymbolStack newStack = new SymbolStack( symbolStack.getParentTables());
        State newState = new State(resolver,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap());
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
        NamespaceResolver r = new NamespaceResolver();
        SymbolStack newStack = new SymbolStack();
        newStack.addParent(new SymbolTableImpl());
        State newState = new State(r,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                new FunctionTable(),
                getModuleMap());
        return newState;
    }


    public void addModule(Module module) {
        if (module instanceof JavaModule) {
            ((JavaModule) module).init(this.newModuleState());
        }
        getModuleMap().put(module);

    }


}
