package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.util.functor.FunctorType;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.event.DelimiterEvent;

import java.io.Serializable;

/**
 * Top-level handler class. This has the routine bookkeeping in it for any handler, such as tracking the number
 * of delimiters, whether it has been used and some other handy utilities.
 * <p>Created by Jeff Gaynor<br>
 * on 7/15/18 at  5:21 PM
 */
public abstract class AbstractHandler implements Serializable {
    public static final int SWITCH_TYPE = 0;
    public static final int CONDITIONAL_TYPE = 1;
    public static final int FUNCTOR_TYPE = 2;

    /**
     * Allows for determining the type of handler without resorting to a lot of java class operations.
     * @return
     */
    abstract public int getHandlerType();
    public AbstractHandler(JFunctorFactory functorFactory) {
        this.functorFactory = functorFactory;
    }

    JFunctorFactory functorFactory;

    public JFunctorFactory getFunctorFactory() {
        return functorFactory;
    }

    protected String convertType(FunctorType type) {
        return type.getValue().substring(1); // remove dollar sign
    }

    protected boolean equals(String name, FunctorType type) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return name.equals(convertType(type));
    }

    protected int openDelimiterCount = 0;
    protected int closeDelimiterCount = 0;

    public boolean isUsed() {
        return used;
    }

    protected boolean used = false; // flag if this handler has been invoked since last reset.

    public void reset() {
        openDelimiterCount = 0;
        closeDelimiterCount = 0;
        used = false;
    }

    public boolean areDelimitersBalanced() {
        return openDelimiterCount == closeDelimiterCount;
    }

    public void openDelimiter(DelimiterEvent delimeterEvent) {
        openDelimiterCount++;
        used = true;
    }

    public void closeDelimiter(DelimiterEvent delimeterEvent) {
        closeDelimiterCount++;
    }

    @Override
    public String toString() {
        return "AbstractHandler{" +
                "functorFactory=" + functorFactory +
                ", used=" + used +
                ", closeDelimiterCount=" + closeDelimiterCount +
                ", openDelimiterCount=" + openDelimiterCount +
                '}';
    }
}
