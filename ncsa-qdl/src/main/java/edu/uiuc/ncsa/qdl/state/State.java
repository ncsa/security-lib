package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;

/**
 * /**
 * represents the internal state of the system. It has the {@link NamespaceResolver},
 * {@link SymbolTable}, {@link edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator} and such.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  7:25 AM
 */
public class State {
    public State(NamespaceResolver resolver,
                 SymbolStack symbolStack,
                 OpEvaluator opEvaluator,
                 MetaEvaluator metaEvaluator,
                 FunctionTable functionTable,
                 ModuleMap moduleMap) {
        this.resolver = resolver;
        this.symbolStack = symbolStack;
        this.metaEvaluator = metaEvaluator;
        this.opEvaluator = opEvaluator;
        this.moduleMap = moduleMap;
        this.functionTable = functionTable;
        metaEvaluator.addFunctionTable(functionTable);
    }

    public FunctionTable getFunctionTable() {
        return functionTable;
    }

    public void setFunctionTable(FunctionTable functionTable) {
        this.functionTable = functionTable;
    }

    FunctionTable functionTable;
    public ModuleMap getModuleMap() {
        return moduleMap;
    }

    public void setModuleMap(ModuleMap moduleMap) {
        this.moduleMap = moduleMap;
    }

    protected ModuleMap moduleMap;
    NamespaceResolver resolver;

    public NamespaceResolver getResolver() {
        return resolver;
    }

    public void setResolver(NamespaceResolver resolver) {
        this.resolver = resolver;
    }

    public SymbolStack getSymbolStack() {
        return symbolStack;

    }

    public void setSymbolStack(SymbolStack symbolStack) {
        this.symbolStack = symbolStack;
    }

    public OpEvaluator getOpEvaluator() {
        return opEvaluator;
    }

    public void setOpEvaluator(OpEvaluator opEvaluator) {
        this.opEvaluator = opEvaluator;
    }

    public MetaEvaluator getMetaEvaluator() {

        return metaEvaluator;
    }

    public void setMetaEvaluator(MetaEvaluator metaEvaluator) {
        this.metaEvaluator = metaEvaluator;
    }

    SymbolStack symbolStack;
    MetaEvaluator metaEvaluator;
    OpEvaluator opEvaluator;
    public int getOperatorType(String name){
        return getOpEvaluator().getType(name);
    }

    public int getFunctionType(String name){
        return getMetaEvaluator().getType(name);
    }

    /**
     * Takes this state object and sets up a new local environment. This is passed to things
     * like loops, conditionals etc. The lifecycle of these is that they are basically abandoned
     * when done then garbage collected.
     *
     * @return State
     *
     */
    public State newLocalState() {
        //System.out.println("** State, creating new local state **");
        SymbolStack newStack = new SymbolStack(resolver, symbolStack.getParentTables());
        State newState = new State(resolver,
                newStack,
                getOpEvaluator(),
                getMetaEvaluator(),
                getFunctionTable(),
                getModuleMap());
        return newState;
    }
}
