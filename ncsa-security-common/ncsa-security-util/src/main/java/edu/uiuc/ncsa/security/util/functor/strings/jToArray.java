package edu.uiuc.ncsa.security.util.functor.strings;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;
import net.sf.json.JSONArray;

import java.util.StringTokenizer;

/**
 * A functor to take a string with an embedded delimiter and turn it into an array.
 * <pre>
 *     $toArray[list, delim]
 * </pre>
 * For instance,
 * <pre>
 *     $toArray["A;B;C;D",";"]
 * </pre>
 * would return a JSON array
 * <pre>
 *     ["A","B","C"."D"]
 * </pre>
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on 7/12/18 at  4:09 PM
 */
public class jToArray extends JFunctorImpl {
    public jToArray() {
        super(FunctorTypeImpl.TO_ARRAY);
    }

    @Override
    public Object execute() {
        if (getArgs().size() < 2) {
            throw new IllegalArgumentException("Error: This functor requires two arguments");

        }
        if (isExecuted()) {
            return result;
        }
        JSONArray array = new JSONArray();
        String arg = (String) getArgs().get(0);
        String delim = (String) getArgs().get(1);
        StringTokenizer st = new StringTokenizer(arg, delim);
        while (st.hasMoreTokens()) {
            array.add(st.nextToken());
        }
        executed = true;
        result = array;
        executed = true;
        return result;
    }
}
