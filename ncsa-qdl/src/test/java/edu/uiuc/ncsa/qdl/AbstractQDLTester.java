package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.variables.QDLCodec;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.VThing;
import edu.uiuc.ncsa.qdl.workspace.WorkspaceCommands;
import edu.uiuc.ncsa.qdl.xml.XMLUtils;
import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.*;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  1:15 PM
 */
public class AbstractQDLTester extends TestBase {

   protected TestUtils testUtils = TestUtils.newInstance();

    /*
    Convenience getters for testing
     */
    protected BigDecimal getBDValue(String variable, State state) {
        return (BigDecimal) state.getValue(variable);
    }

    protected Long getLongValue(String variable, State state) {
        return (Long) state.getValue(variable);
    }

    protected String getStringValue(String variable, State state) {
        return (String) state.getValue(variable);
    }

    protected Boolean getBooleanValue(String variable, State state) {
        return (Boolean) state.getValue(variable);
    }

    protected StemVariable getStemValue(String variable, State state) {
        return (StemVariable) state.getValue(variable);
    }

    protected BigDecimal comparisonTolerance = new BigDecimal(".000000000001");

    /**
     * Compares two {@link BigDecimal}s. Tells if their difference is less than the
     * comparison tolerance. This effectively means they are equal.
     *
     * @param x
     * @param y
     * @param comparisonTolerance
     * @return
     */
    protected boolean areEqual(BigDecimal x, BigDecimal y, BigDecimal comparisonTolerance) {
        return x.subtract(y).abs().compareTo(comparisonTolerance) < 0;
    }

    protected boolean areEqual(BigDecimal x, BigDecimal y) {
        return areEqual(x, y, comparisonTolerance);
    }

    /**
     * Shallow check that two stems are equal. This is messy to write since Java and QDL
     * have very different typing systems.
     *
     * @param stem1
     * @param stem2
     * @return
     */
    protected boolean areEqual(StemVariable stem1, StemVariable stem2) {
        if (stem1.size() != stem2.size()) return false;
        for (String key1 : stem1.keySet()) {
            Object v1 = null;
            Object v2  = null;
            if (stem1.isLongIndex(key1)) {
                Long k1 = Long.parseLong(key1);
                if (!stem2.containsKey(k1)) return false;
                v1 = stem1.get(k1);
                v2 = stem2.get(k1);
            } else {
                if (!stem2.containsKey(key1)) return false;
                v1 = stem1.get(key1);
                v2 = stem2.get(key1);
            }
            if(v1 == null){
                if(v2 != null) return false;
            }else{
                if(v2 == null){
                    return false;
                }else{
                    if(!v1.equals(v2)) return false;
                }
            }
        }
        return true;
    }

    /**
     * For use in conjunction with {@link #areEqual(StemVariable, StemVariable)}.
     * <br/>
     * Note: These will be exact decimals and later no comparison tolerance is used.
     *
     * @param array
     * @return
     */
    protected StemVariable arrayToStem(double[] array) {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (double dd : array) {
            // Have to convert to a string then back since otherwise the BigD adds rounding cruft
            // and these cannot be check for equality
            arrayList.add(new BigDecimal(Double.toString(dd)));
        }
        StemVariable stemVariable = new StemVariable();
        stemVariable.addList(arrayList);
        return stemVariable;
    }

    protected StemVariable arrayToStem(long[] array) {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (long dd : array) {
            arrayList.add(dd);
        }
        StemVariable stemVariable = new StemVariable();
        stemVariable.addList(arrayList);
        return stemVariable;

    }

    protected StemVariable arrayToStem(int[] array) {
        ArrayList<Object> arrayList = new ArrayList<>();
        for (int dd : array) {
            arrayList.add(new Long(dd));
        }
        StemVariable stemVariable = new StemVariable();
        stemVariable.addList(arrayList);
        return stemVariable;

    }

    // get an encoded random string
    protected String geter() {
        return enc(getRandomString());
    }

    QDLCodec codec = new QDLCodec();

    protected String enc(String x) {
        return codec.encode(x);
    }

    protected String dec(String x) {
        return codec.decode(x);
    }

    /**
     * Simple utility that appends the line and a return. Use this to make scripts for testing the parser itself.
     *
     * @param stringBuffer
     * @param x
     * @return
     */
    protected StringBuffer addLine(StringBuffer stringBuffer, String x) {
        return stringBuffer.append(x + "\n");
    }

    /*
    This is actually just a copy of the test base class in security-util, but it cuts out
    a dependency.
     */
    public static int randomStringLength = 8; // default length for random strings
    public int count = 5; // on tests with loops, this sets max reps.

    public static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    static Random random;

    public static String getRandomString(int length) {
        // so approximate how long the result will be and add in (at most) 2 characters.
        byte[] bytes = new byte[(int) (Math.round(Math.ceil(length * .75)) + 1)];
        getRandom().nextBytes(bytes);
        // Have to be careful to use only URL safe encoding or random errors can start occurring,
        // especially if using these to make other urls!
        return Base64.encodeBase64URLSafeString(bytes).substring(0, length);
    }

    public static String getRandomString() {
        return getRandomString(randomStringLength);
    }


    protected boolean testNumberEquals(Object arg1, Object arg2) {
        if ((arg1 instanceof Long) && (arg2 instanceof Long)) {
            return arg1.equals(arg2);
        }
        BigDecimal left;
        BigDecimal right;
        if (arg1 instanceof Long) {
            left = new BigDecimal((Long) arg1);
        } else {
            left = (BigDecimal) arg1;
        }
        if (arg2 instanceof Long) {
            right = new BigDecimal((Long) arg2);
        } else {
            right = (BigDecimal) arg2;
        }

        BigDecimal result = left.subtract(right);
        return result.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Tests that the variable has the given value in the {@link edu.uiuc.ncsa.qdl.variables.VStack}
      * @param variableName
     * @param newValue
     * @param state
     * @return
     */
    protected boolean checkVThing(String variableName, Object newValue, State state){
        return ((VThing)state.getVStack().get(new XKey(variableName))).getValue().equals(newValue);
    }
    /**
     * Takes the current state, serializes, deserializes it then returns a new workspace
     * @param state
     * @return
     * @throws Throwable
     */
    protected State pickleState(State state) throws Throwable {
        // Serialize the workspace
        StringWriter stringWriter = new StringWriter();
        XMLStreamWriter xsw = createXSW(stringWriter);
        WorkspaceCommands workspaceCommands = new WorkspaceCommands();
        workspaceCommands.setState(state);
        workspaceCommands.toXML(xsw);

        // Deserialize the workspace
        // Need pretty print. This takes the place or writing it to a file, then reading it.
        String pp = XMLUtils.prettyPrint(stringWriter.toString());
      //  System.out.println("XML:\n" + pp);
        StringReader reader = new StringReader(pp);
        XMLEventReader xer = createXER(reader);
        workspaceCommands.fromXML(xer);
        return workspaceCommands.getInterpreter().getState();
    }

    /**
     * One stop shopping for roundtripping serializing the state.
     * The script is interpreted and serialized then deserialized and the new state is
     * returned.
     * <h3>Usage</h3>
     * A typical use is to fork a test into a serialization test and nonserialization test
     * by passing in the doRountrip. Then before the tests you want to run inser
     * <pre>
     *     State = ...
     *     StringBuffer script; // <i>setup whatever state you need for your test</i>
     *     if(doRoundtrip){
     *         state = roundTripStateSerialization(state, script, true);
     *         script = new StringBuffer();
     *     }
     *     // ... <i>Put in any checks you would do on the previously created state</i>
     * </pre>
     * This replaces the state with its roundtripped version and restarts the script
     * @param oldState
     * @param script
     * @return
     * @throws Throwable
     */
    protected State roundTripStateSerialization(State oldState, StringBuffer script) throws Throwable{
            QDLInterpreter interpreter = new QDLInterpreter(null, oldState);
            interpreter.execute(script.toString());
            return pickleState(oldState);
    }

    /**
     * Create the {@link XMLStreamWriter}
     *
     * @param w
     * @return
     * @throws XMLStreamException
     */
    protected XMLStreamWriter createXSW(Writer w) throws XMLStreamException {
        return XMLOutputFactory.newInstance().createXMLStreamWriter(w);
    }

    protected XMLEventReader createXER(Reader reader) throws XMLStreamException {
        return XMLInputFactory.newInstance().createXMLEventReader(reader);
    }
}
