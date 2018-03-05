package edu.uiuc.ncsa.security.util.functor.logic;

/**
 * A functor for checking if one string starts with another. <br/>
 * $starstWith[target, head]<br/>
 * The target string is checked for beginning with the head. This is equivalent to
 * invoking target.startsWith(head)
 *
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  11:50 AM
 */
public class jStartsWith extends jStringComparisons {
    public jStartsWith() {
        super("$startWith");
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
        String head = processArg(args.get(1));
        boolean rc = target.startsWith(head);
        result = new Boolean(rc);
        executed = true;
        return result;
    }
}
