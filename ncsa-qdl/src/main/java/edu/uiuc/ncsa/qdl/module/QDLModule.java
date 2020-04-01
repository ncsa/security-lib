package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.statements.ModuleStatement;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/1/20 at  11:30 AM
 */
public class QDLModule extends Module {

    public ModuleStatement getModuleStatement() {
        return moduleStatement;
    }

    public void setModuleStatement(ModuleStatement moduleStatement) {
        this.moduleStatement = moduleStatement;
    }

    ModuleStatement moduleStatement;
}
