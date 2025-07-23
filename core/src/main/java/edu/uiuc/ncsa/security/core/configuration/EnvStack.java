package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.state.SKey;
import edu.uiuc.ncsa.security.core.state.SStack;
import edu.uiuc.ncsa.security.core.state.STable;

public class EnvStack<T extends EnvTable<? extends SKey, ? extends EnvEntry>> extends SStack<T> {
    @Override
    public SStack newInstance() {
        return new EnvStack<>();
    }

    @Override
    public STable newTableInstance() {
        return new EnvTable<>();
    }
}
