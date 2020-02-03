package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;

import java.io.Serializable;

/**
 * This helps us organize the functionality of the state object. There are
 * sublclasses of this that do specific tasks. The inheritance hierarchy is
 * <pre>
 * namespaces --> variables --> functions --. total state.
 * </pre>
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/20 at  6:37 AM
 */
public abstract class AbstractState implements Serializable {
    private static final long serialversionUID = 129348937L;

    public AbstractState(NamespaceResolver resolver,
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
        //metaEvaluator.addFunctionTable(functionTable);
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

    public int getOperatorType(String name) {
        return getOpEvaluator().getType(name);
    }

    public int getFunctionType(String name) {
        return getMetaEvaluator().getType(name);
    }

    public abstract State newModuleState();

    public abstract State newStateWithImports();

    public abstract State newStateNoImports();


}
