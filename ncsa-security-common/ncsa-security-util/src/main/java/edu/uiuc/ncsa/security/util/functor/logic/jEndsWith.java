package edu.uiuc.ncsa.security.util.functor.logic;

/**
 * A functor for checking if one string ends with another. <br/>
 * $endsWith[target, tail]<br/>
 * The target string is checked for ending with the head. This is equivalent to
 * invoking target.endsWith(tail)
 *
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  11:50 AM
 */
public class jEndsWith extends jStringComparisons {
    public jEndsWith() {
        super("$endsWith");
    }

    @Override
    public Object execute() {
        if (executed) {
            return result;
        }
        if (args.size() != 2) {
            throw new IllegalStateException("Error: this functor requires two arguments. It currently has " + args.size() + " arguments.");
        }

        String target = processArg(args.get(0));
        String tail = processArg(args.get(1));
        boolean rc = target.endsWith(tail);
        result = new Boolean(rc);
        executed = true;
        return result;
    }
}
