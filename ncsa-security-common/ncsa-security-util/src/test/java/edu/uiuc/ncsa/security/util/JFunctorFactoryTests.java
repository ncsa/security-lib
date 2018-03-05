package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.logic.jAnd;
import net.sf.json.JSONObject;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  1:37 PM
 */
public class JFunctorFactoryTests extends TestBase {
    @Test
    public void testAnd() throws Exception{
        String rawJSON = "{\"$and\": [\n" +
                "  {\"$endsWith\":   [\n" +
                "    \"the quick brown fox\",\n" +
                "    \"fox\"\n" +
                "  ]},\n" +
                "  {\"$contains\":   [\n" +
                "    \"foo\",\n" +
                "    \"zfoo\"\n" +
                "  ]}\n" +
                "]}";

        JSONObject json = JSONObject.fromObject(rawJSON);
        JFunctor ff = JFunctorFactory.create(json);
        assert ff instanceof jAnd;
        ff.execute();
        assert ((jAnd) ff).getBooleanResult();
    }
}
