package edu.uiuc.ncsa.demo;

import com.typesafe.config.*;
import net.sf.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Command line hacking to evaluate HOCON.
 * <p>Created by Jeff Gaynor<br>
 * on 8/31/20 at  8:57 AM
 */
public class HOCONDemo {

    public static void main(String[] args) {
        ConfigRenderOptions renderOpts = ConfigRenderOptions.defaults().setOriginComments(false).setComments(false).setJson(false);
        Config conf = ConfigFactory.parseString(test3a);
        System.out.println(conf.root().render(renderOpts));

        doToken();
        doBasic();
    }
       /*
       How to turn a configuration object back in to HOCON (poorly documented). This is from a Scala
       tutorial (which uses the typesafe Java libraries). Need to try this at some point.
       
       val dishConfig = … // as in creating Conf example
       val renderOpts = ConfigRenderOptions.defaults().setOriginComments(false).setComments(false).setJson(false);
       dishConfig.root().render(renderOpts)

        */
    protected static void doBasic() {
        File file = new File("/home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl-gui/src/main/resources/hocon.config");
        Config conf = ConfigFactory.parseFile(file);
        ConfigResolveOptions configResolveOptions = null;
        //Config conf = ConfigFactory.parseResources("hocon.config");
        System.out.println(conf);
        System.out.println(conf.getInt("foo.bar"));
        System.out.println(conf.getInt("foo.baz"));
        System.out.println(conf.getList("array"));
        System.out.println(conf.getList("qdl"));
        System.out.println(conf.getInt("token.id.blarf"));

    }

    protected static void doToken() {
        Config conf = ConfigFactory.parseResources("tokken.config");
        System.out.println(conf);
        List<? extends ConfigObject> templates = conf.getObjectList("id_token.templates");
        System.out.println(templates);
        System.out.println(conf.getLong("id_token.create_ts"));
        String rawJSON = conf.root().render(ConfigRenderOptions.concise());
        JSONObject jsonObject = JSONObject.fromObject(rawJSON);
        System.out.println(jsonObject.toString(1));
    }

    static String test2a = "{\"qdl\":\n" +
             "   {\n" +
             "     \"load\":\"y.qdl\",\n" +
             "     \"xmd\":{\"phase\":\"pre_auth\",\"token_type\":\"id\"},\n" +
             "     \"args\":{\"port\":9443,\"verbose\":true,\"x0\":-47.5, \"ssl\":[3.5,true]},\n" +
             "     \"arg_name\":\"oa2\"\n" +
             "   }\n" +
             "}\n";

    /*
    {"id_token": {
                          "id": "test0",
                          "qdl":  {
                           "arg_name": "oa2",
                           "args":   [
                            1,
                            true,
                            "foo",
                            23.4,
                               {
                             "x": 2,
                             "y": "fnord"
                            }
                           ],
                           "load": "vfs#/scripts/test0.qdl",
                           "xmd": {"exec_phase": "pre_auth"}
                          },
                          "type": "wlcg"
                         }}
     */
    public static String test1 = "{\"id_token\": {\n" +
            "                      \"id\": \"test0\",\n" +
            "                      \"qdl\":  {\n" +
            "                       \"arg_name\": \"oa2\",\n" +
            "                       \"args\":   [\n" +
            "                        1,\n" +
            "                        true,\n" +
            "                        \"foo\",\n" +
            "                        23.4,\n" +
            "                           {\n" +
            "                         \"x\": 2,\n" +
            "                         \"y\": \"fnord\"\n" +
            "                        }\n" +
            "                       ],\n" +
            "                       \"load\": \"vfs#/scripts/test0.qdl\",\n" +
            "                       \"xmd\": {\"exec_phase\": \"pre_auth\"}\n" +
            "                      },\n" +
            "                      \"type\": \"wlcg\"\n" +
            "                     }}";

    public static String test3 = "tokens  {\n" +
            "  identity{\n" +
            "      type = wlcg\n" +
            "      id=q12345\n" +
            "      qdl{\n" +
            "         arg_name=oa2\n" +
            "         load = vfs#/scripts/test0.qdl\n" +
            "         xmd={\n" +
            "           exec_phase=pre_auth\n" +
            "           } // end extended metadata \n" +
            "         args=[\n" +
            "            1, true,foo,23.4,{x=2,y=fnord}\n" +
            "         ] //end args\n" +
            "      }// end qdl  \n" +
            "  } //end id token\n" +
            "} //end token";

    public static String test3a = "tokens  {\n" +
            "  identity{\n" +
            "      type = wlcg\n" +
            "      id=q12345\n" +
            "      qdl{\n" +
            "         arg_name=oa2\n" +
            "         load = vfs#/scripts/test0.qdl\n" +
            "         xmd={exec_phase=pre_auth} // end extended metadata \n" +
            "         args=[1,true,foo,23.4,{x=2,y=fnord}] //end args\n" +
            "      }// end qdl  \n" +
            "  } //end id token\n" +
            "} //end token";
}
