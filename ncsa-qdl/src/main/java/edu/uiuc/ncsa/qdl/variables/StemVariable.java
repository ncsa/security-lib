package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import net.sf.json.JSON;
import net.sf.json.JSONObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  12:09 PM
 */
public class StemVariable extends QDLStem {

    public StemVariable() {
        super();
    }

    public StemVariable(Long count, Object[] fillList) {
        super(count, fillList);
    }

    @Override
    public QDLStem newInstance() {
        return new StemVariable();
    }

    @Override
    public QDLStem newInstance(Long count, Object[] fillList) {
        return new StemVariable(count, fillList);
    }




    public static void main(String[] arg) {
        StemVariable s = new StemVariable();
        StemVariable s2 = new StemVariable();
        StemVariable s3 = new StemVariable();
        s.put("a", "b");
        s.put("s", "n");
        s.put("d", "m");
        s.put("0", "foo");
        s.put("1", "bar");
        s2.put(0L, "qwe");
        s2.put(1L, "eee");
        s2.put(2L, "rrr");
        s2.put("rty", "456");
        s2.put("tyu", "ftfgh");
        s2.put("ghjjh", "456456");
        s3.put("a3rty", "456222");
        s3.put("a3tyu", "ftf222gh");
        s3.put("a3ghjjh", "422256456");
        s2.put("woof.", s3);
        s.put("foo.", s2);
        System.out.println(InputFormUtil.inputForm(s));
        System.out.println(s.allKeys2());
        QDLStem allKeys = s.indices();
        System.out.println(allKeys);
        System.out.println(s.get(allKeys.get(10L)));
        System.out.println(s.indices(0L));
        System.out.println(s.indices(1L));
        System.out.println(s.indices(2L));
        System.out.println(s.indices(-1L));
        System.out.println(s.indices(4L));


        System.out.println(s.toJSON().toString());
        String rawJSON = "{\n" +
                "  \"isMemberOf\":   [" +
                "  {\n" +
                "      \"name\": \"all_users\",\n" +
                "      \"id\": 13002\n" +
                "    },\n" +
                "        {\n" +
                "      \"name\": \"staff_reporting\",\n" +
                "      \"id\": 16405\n" +
                "    },\n" +
                "        {\n" +
                "      \"name\": \"list_allbsu\",\n" +
                "      \"id\": 18942\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        StemVariable stemVariable = new StemVariable();
        stemVariable.fromJSON(JSONObject.fromObject(rawJSON));

        JSON j = stemVariable.toJSON();
        System.out.println(j.toString(2));


    }





    /*
   a.'foo' := 'abctest0';
   a.'bar' := 'abctest1';
   a.'baz' := 'deftest2';
   a.'fnord' := 'abdtest3';
   b.'z' := 'baz'
   b.w := 'foof';
   b.3 := 42
   a.www := b.
   unique(a.)

     unique(['a',2,4,true]~['a','b',0,3,true])
     unique(['a','b',0]~[['a',2,4,true]]~[[['a','b',0,3,true]]])
     unique(['a','b',0,3,true]~[['a','b',0,3,true]]~[[['a','b',0,3,true]]])

     */


















}
