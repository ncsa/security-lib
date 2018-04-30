package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.LogicBlock;
import edu.uiuc.ncsa.security.util.functor.LogicBlocks;
import edu.uiuc.ncsa.security.util.functor.logic.jAnd;
import edu.uiuc.ncsa.security.util.functor.logic.jTrue;
import net.sf.json.JSONArray;
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
        JFunctorFactory functorFactory = new JFunctorFactory();
        JFunctor ff = functorFactory.create(json);
        assert ff instanceof jAnd;
        ff.execute();
        assert ((jAnd) ff).getBooleanResult();
    }
    @Test
    public void testConstants() throws Exception{
        String rawJSON = "{\"$and\": [\n" +
                "  {\"$endsWith\":   [\n" +
                "    \"the quick brown fox\",\n" +
                "    \"fox\"\n" +
                "  ]},\n" +
                "  \"$true\"\n" +
                "]}";
        JSONObject json = JSONObject.fromObject(rawJSON);
        JFunctorFactory functorFactory = new JFunctorFactory();
        JFunctor ff = functorFactory.create(json);
        assert ff instanceof jAnd;
        ff.execute();
        assert ((jAnd) ff).getBooleanResult();
        // And again with a logical value of false.

        rawJSON = "{\"$and\": [\n" +
                       "  {\"$endsWith\":   [\n" +
                       "    \"the quick brown fox\",\n" +
                       "    \"fox\"\n" +
                       "  ]},\n" +
                       "  \"$false\"\n" +
                       "]}";
        json = JSONObject.fromObject(rawJSON);
        ff = functorFactory.create(json);
        assert ff instanceof jAnd;
        ff.execute();
        assert !((jAnd) ff).getBooleanResult();
    }

    /**
     * The argument is a list of commands (in this case the trivial $true functor). The point of the
     * test is that this is converted internally to a logic block that has a conditional of true and that
     *  the resulting executable then block of commands can be queried for the functor. This permits
     *  for instance, introducing variables (as functors that are true or false) into the runtime and checking if they are
     *  set.
     * @throws Exception
     */
    @Test
    public void testCommndsOnlyLogicBlock() throws Exception{
        JSONArray array = new JSONArray();
        jTrue jt = new jTrue();
                array.add(jt.toJSON());
        JFunctorFactory ff = new JFunctorFactory();
        LogicBlocks<? extends LogicBlock> lbs = ff.createLogicBlock(array);
        assert lbs.size() == 1;
        lbs.execute();
        System.out.println(lbs.get(0).toString());
        LogicBlock lb = lbs.get(0);
        assert lb.isIfTrue();
        assert lb.getThenBlock().getFunctorMap().containsKey(jt);
    }
}
