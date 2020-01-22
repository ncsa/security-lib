package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:03 AM
 */
public class Module {
    public Module(SymbolTableImpl symbols, NamespaceResolver namespaceResolver, String name) {
        this.symbols = symbols;
        this.namespaceResolver = namespaceResolver;
        this.name = name;
    }

    public Module(SymbolTableImpl symbols, NamespaceResolver namespaceResolver, String name, String alias) {
        this(symbols,namespaceResolver,name);
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    String alias;


    public SymbolTableImpl getSymbols() {
        return symbols;
    }

    public NamespaceResolver getNamespaceResolver() {
        return namespaceResolver;
    }

    public String getName() {
        return name;
    }

    SymbolTableImpl symbols;
    NamespaceResolver namespaceResolver;
    String name;
}
