package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.io.Serializable;

/**
 * this represents a QDL variable.  At load time, the name will be read and the value will be assigned in the
 * symbol table. Once the variable is in the system,, this is ignored since the user then has access to it and
 * can modify it.
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  12:02 PM
 */
public interface QDLVariable extends Serializable {
    /**
     * The name of the variable. This may be a simple name for a scalar, like "a",
     * or it may represent a stem, like "a.". You may even set specific stem values
     * by passing in the indexed stem, e.g. "a,3" or "a.j" (the index for the latter is
     * "j" unless it resolves to another value.
     * @return
     */
    public String getName();

    /**
     * The value. The basic Java types that QDL knows are {@link Boolean},
     * {@link String}, {@link Long}, {@link java.math.BigDecimal},
     * and {@link StemVariable}.
     *
     * @return
     */
    public Object getValue();
}
