package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:27 AM
 */
public class ModuleTest extends TestBase {
    @Test
    public void createModule() throws Exception{
        ModuleMap moduleMap = new ModuleMap();
        NamespaceResolver namespaceResolver = NamespaceResolver.getResolver();
        SymbolTableImpl symbolTable = new SymbolTableImpl(namespaceResolver);

        //Module module = new Module(symbolTable,namespaceResolver,);
        //module.
    }
}
