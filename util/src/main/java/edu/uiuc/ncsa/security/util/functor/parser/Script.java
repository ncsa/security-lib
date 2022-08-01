package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import net.sf.json.JSONObject;

/**
 * A wrapper for a couple of type of scripting objects. This is to determine which is which and execute the script.
 * Either this has a JSONObject (like a {@link edu.uiuc.ncsa.security.util.functor.LogicBlock} or it has a JSONArray
 * of strings that are to be executed sequentially. <br>
 * Note that the factory and parser contain the environment of the execution. If an older set of logic blocks,
 * these are available in the {@link #getLogicBlocks()} call. If this is from a newer script, the complete set
 * of returned handlers from the parser is available in the {@link #getHandlers()} call.
 * <br>
 * The two formats for the JSON object are either
 * <pre>
 *      {"script":[
 *         "line 1",
 *         "line 2",
 *         ...
 *      ],
 *      "version":"1.0"}
 *  </pre>
 * where each line is a separate command (version is optional) or as described in the comment
 * to {@link JFunctorFactory#createLogicBlock(JSONObject)}
 * <br>Note this this has several convenience methods, such as {@link #getFunctorMap()}.<br>
 * Also, you may either create this and execute is against arguments <b><i>OR</i></b>
 * initialize it with JSON and execute it later when needed in your control flow. This
 * allows you to configure everything the way you need it up front.
 * <p>Created by Jeff Gaynor<br>
 * on 9/24/18 at  1:49 PM
 */
public class Script extends FunctorScript {



    public Script(JFunctorFactory functorFactory) {
        super(functorFactory);
    }


}
