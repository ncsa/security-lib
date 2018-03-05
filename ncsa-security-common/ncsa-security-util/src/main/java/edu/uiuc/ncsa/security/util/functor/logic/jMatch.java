package edu.uiuc.ncsa.security.util.functor.logic;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  11:40 AM
 */
public class jMatch extends jStringComparisons {
    public jMatch() {
        super("$match");
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
        String regex = processArg(args.get(1));
        boolean rc = target.matches(regex);
        result = new Boolean(rc);
        executed = true;
        return result;
    }
}
