package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.JMetaMetaFunctor;
import edu.uiuc.ncsa.security.util.json.JSONEntry;
import edu.uiuc.ncsa.security.util.json.JSONStore;
import net.sf.json.JSONArray;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * For a stored procedure by the user.  A {@link Script} may contain several of these, which should be invokable on their own.
 * The format for a procedure is
 * <pre>
 *     #namespace X;
 *     #def  name(arg0,arg1,...);
 *      ... commands
 *      #end_def;
 * </pre>
 *
 * <p>Created by Jeff Gaynor<br>
 * on 3/7/19 at  7:05 PM
 */
public class Procedure extends AbstractScript implements JMetaMetaFunctor {
    public static final String DIRECTIVE_MARKER = "#";
    protected String namespace;
    public static final String NAMESPACE = DIRECTIVE_MARKER + "namespace";
    protected static final String START_DEF = DIRECTIVE_MARKER + "def";
    protected static final String END_DEF = DIRECTIVE_MARKER + "end_def";
    protected static final String PROCEDURE_NAME = "name";
    String name;
    List<String> args;

    public String getFQName() {
        if (namespace == null || namespace.isEmpty()) {
            return ":" + getSignature();
        }
        return namespace + ":" + getSignature();
    }

    public String getSignature() {
        String sig = name;
        sig = sig + "_" + args.size();
        return sig;

    }

    List<String> buffer;

    protected void initialize() {
        buffer = new LinkedList<>();
        for (String arg : rawContent) {
            arg = arg.trim();
            if (arg.startsWith(NAMESPACE)) {
                initNamespace(arg);
                continue;
            }
            if (arg.startsWith(START_DEF)) {
                initArgs(arg);
                continue;
            }
            if (arg.startsWith(END_DEF)) {
                // nothing really to do except note it.
                continue;
            }
            buffer.add(arg);
        }
    }

    protected void initNamespace(String linein) {
        linein = linein.trim();
        namespace = linein.substring(NAMESPACE.length() + 1);
        if(namespace.endsWith(";")){
            namespace = namespace.substring(0,namespace.length()-1); // no final ;
        }

    }

    protected void initArgs(String linein) {

        linein = linein.substring(START_DEF.length());
        name = linein.substring(0, linein.indexOf("(")).trim();
        String rawArgs = linein.trim().substring(linein.indexOf("("), linein.indexOf(");") - 1);
        args = new LinkedList<>();

        StringTokenizer st = new StringTokenizer(rawArgs, ",");
        while (st.hasMoreTokens()) {
            args.add(st.nextToken());
        }
        System.out.println(name);
        System.out.println(args);
    }

    /**
     * The rawContent is the unprocessed lines of the procedure. Note that first line of the procedure is a definition statement
     * and lines until that point will be ignoed.
     *
     * @param rawContent
     * @param functorFactory
     */
    public Procedure(List<String> rawContent, JFunctorFactory functorFactory) {
        super(functorFactory);
        this.rawContent = rawContent;
        initialize();
    }

    List<String> rawContent;

    protected static List<String> createTest() {
        LinkedList<String> proc = new LinkedList<>();
        proc.add(NAMESPACE + ";");
        proc.add(START_DEF + " test(A,B);");
        proc.add("echo(getEnv('A'));");
        proc.add("echo(getEnv('B'));");
        proc.add(END_DEF + ";");
        return proc;
    }

    protected static List<String> createTest2() {
        LinkedList<String> proc = new LinkedList<>();
        proc.add(NAMESPACE + ";");
        proc.add(START_DEF + " test2();");
        proc.add("test('fnord','flarb');");
        proc.add(END_DEF + ";");
        return proc;
    }

    public java.lang.String getNamespace() {
        return namespace;
    }

    public List<String> getBuffer() {
        return buffer;
    }

    @Override
    public String toString() {
        String out = "Procedure[";
        out = out + "namespace=" + namespace + ",signature=" + getSignature() + ",fq name=" + getFQName();
        return out + "]";
    }

    public static class JM extends HashMap implements JSONStore {

        public JM() {
        }

        @Override
        public Identifiable create() {
            return null;
        }

        @Override
        public void update(Identifiable value) {

        }

        @Override
        public void register(Identifiable value) {

        }

        @Override
        public void save(Identifiable value) {

        }

        @Override
        public List getAll() {
            return null;
        }

        @Override
        public XMLConverter getXMLConverter() {
            return null;
        }

        @Override
        public List search(String key, String condition, boolean isRegEx) {
            return null;
        }
    }

   protected static Identifier ID_DEF = BasicIdentifier.newID("id:test_2");

    protected static JM populateStore() {
        JM jm = new JM();
        JSONArray jsonArray= new JSONArray();
            jsonArray.add("#namespace id;");
            jsonArray.add("#def test(A,B);");
            jsonArray.add("echo(getEnv('A'));");
            jsonArray.add("echo(getEnv('B'));");
            jsonArray.add("#end_def;");
            JSONEntry jec2 = new JSONEntry(ID_DEF);
            jec2.setType(JSONEntry.TYPE_PROCEDURE);
            jec2.setRawContent(jsonArray.toString());
            jm.put(ID_DEF, jec2);
        return jm;
    }

    public static void main(String[] args) {
        URI qqq = URI.create("http://[::1]:61023/oauth2redirect/example-provider");
        System.out.println("scheme = " + qqq.getScheme());
        System.out.println("host = " + qqq.getHost());
        System.out.println("is absolute = " + qqq.isAbsolute());
        List<String> testProcedure = createTest();
        StoredProcFactory ff = new StoredProcFactory(populateStore(), true);
        Procedure pp = (Procedure)ff.lookUpFunctor(ID_DEF.toString());
        System.out.println("Stored procedure is " + pp);
        ff.getEnvironment().put("A", "foo");
        ff.getEnvironment().put("B", "bar");
        Procedure procedure = new Procedure(testProcedure, ff);
        System.out.println(procedure);
        System.out.println("buffer = " + procedure.getBuffer());
        procedure.execute(procedure.getBuffer());
        List<String> testProcedure2 = createTest2();
        procedure = new Procedure(testProcedure2, ff);
        System.out.println(procedure);
        System.out.println("buffer = " + procedure.getBuffer());
        procedure.execute(procedure.getBuffer());


    }


    @Override
    public Object execute() {
        execute(getBuffer());
        return null;
    }

    @Override
    public Object getResult() {
        return null;
    }

    public JSONArray toJSON() {
        if (rawContent instanceof JSONArray) {
            return (JSONArray) rawContent;
        }
        JSONArray array = new JSONArray();
        for (String x : rawContent) {
            array.add(x);
        }
        return array;
    }
}
