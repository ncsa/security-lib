package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/20 at  1:33 PM
 */
public class StateUtils {
    /**
     * Serialize the Sate object to the given output stream.
     * @param state
     * @param outputStream
     * @throws IOException
     */
    public static void save(State state, OutputStream outputStream) throws IOException {
        GZIPOutputStream gos = new GZIPOutputStream(outputStream);
           ObjectOutputStream out = new ObjectOutputStream(gos);

           // Method for serialization of object
           out.writeObject(state);
           out.flush();
           out.close();
    }

    /**
     * Serialize the state to a base 64 encoded string.
     * @param state
     * @return
     * @throws IOException
     */
    public static String saveb64(State state) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        save(state, baos);
        return Base64.encodeBase64URLSafeString(baos.toByteArray());
    }

    /**
     * Deserialize the state from a base 64 encoded string.
     * @param encodedState
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static State loadb64(String encodedState) throws IOException, ClassNotFoundException {
        byte[] bytes = Base64.decodeBase64(encodedState);
        ByteArrayInputStream baos = new ByteArrayInputStream(bytes);
        return load(baos);
    }

    public static State load(InputStream inputStream) throws IOException, ClassNotFoundException {
        GZIPInputStream gis = new GZIPInputStream(inputStream);
        ObjectInputStream in = new ObjectInputStream(gis);

        // Method for deserialization of object
        State state = (State) in.readObject();
        in.close();
        return state;
    }

    public static void main(String[] args){
        try {
            // Just a quick test for this
            State state = new State(new ImportManager(),
                    new SymbolStack(),
                    new OpEvaluator(),
                    new MetaEvaluator(),
                    new FunctionTable(),
                    new ModuleMap(),
                    new MyLoggingFacade("foo"),
                    false);
            state.setValue("foo", 42L);
            String b = saveb64(state);
            System.out.println("b = " + b);
            System.out.println("size = " + b.length());
            state = loadb64(b);
            System.out.println("state ok? " + state.getValue("foo").equals(42L));
        }catch(Throwable t){
            t.printStackTrace();
        }
    }
}
