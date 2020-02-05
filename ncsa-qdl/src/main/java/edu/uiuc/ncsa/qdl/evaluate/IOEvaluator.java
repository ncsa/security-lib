package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.QDLServerModeException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.FileUtil;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:18 AM
 */
public class IOEvaluator extends MathEvaluator {
    public static final int IO_FUNCTION_BASE_VALUE = 4000;
    public static String SAY_FUNCTION = "say";
    public static String PRINT_FUNCTION = "print";
    public static final int SAY_TYPE = 1 + IO_FUNCTION_BASE_VALUE;
    public static String SCAN_FUNCTION = "scan";
    public static final int SCAN_TYPE = 2 + IO_FUNCTION_BASE_VALUE;

    public static String READ_FILE = "read_file";
    public static final int READ_FILE_TYPE = 3 + IO_FUNCTION_BASE_VALUE;

    public static String WRITE_FILE = "write_file";
    public static final int WRITE_FILE_TYPE = 4 + IO_FUNCTION_BASE_VALUE;


    @Override
    public int getType(String name) {
        if (name.equals(SAY_FUNCTION)) return SAY_TYPE;
        if (name.equals(PRINT_FUNCTION)) return SAY_TYPE;
        if (name.equals(SCAN_FUNCTION)) return SCAN_TYPE;
        if (name.equals(READ_FILE)) return READ_FILE_TYPE;
        if (name.equals(WRITE_FILE)) return WRITE_FILE_TYPE;
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getOperatorType()) {
            case SAY_TYPE:
                if (state.isServerMode()) return true;
                String result = "";
                if (polyad.getArgumments().size() != 0) {
                    Object temp = polyad.evalArg(0, state);
                    ;
                    if (temp == null) {
                        result = "null";

                    } else {
                        result = temp.toString();
                    }
                }
                System.out.println(result);
                if(polyad.getArgumments().size() == 0){
                    polyad.setResult(null);
                    polyad.setResultType(Constant.NULL_TYPE);

                }  else{
                    polyad.setResult(polyad.getArgumments().get(0).getResult());
                    polyad.setResultType(polyad.getArgumments().get(0).getResultType());

                }
                polyad.setEvaluated(true);
                return true;
            case SCAN_TYPE:
                if (state.isServerMode()) return true;

                if (polyad.getArgumments().size() != 0) {
                    // This is the prompt.
                    System.out.print(polyad.evalArg(0, state));
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                result = "";
                try {
                    result = bufferedReader.readLine().trim();
                } catch (IOException e) {
                    result = "(error)";
                }
                polyad.setResult(result);
                polyad.setResultType(Constant.STRING_TYPE);
                polyad.setEvaluated(true);
                return true;
            case READ_FILE_TYPE:
                doReadFile(polyad, state);
                return true;
            case WRITE_FILE_TYPE:
                doWriteFile(polyad, state);
                return true;
        }
        return false;
    }

    protected void doWriteFile(Polyad polyad, State state) {
        if(state.isServerMode()){
            throw new QDLServerModeException("File operations are not permitted in server mode");
        }

        if ( polyad.getArgumments().size() < 2) {
            throw new IllegalArgumentException("Error: " + WRITE_FILE + " requires a two arguments.");
        }
        Object obj = polyad.evalArg(0, state);
        Object obj2 = polyad.evalArg(1, state);
        if(obj == null || !isString(obj)){
            throw new IllegalArgumentException("Error: The first argument to \"" + WRITE_FILE + "\" must be a string that is the file name." );
        }
        String fileName = obj.toString();
        if(obj2 == null){
            throw new IllegalArgumentException("Error: The second argument to \"" + WRITE_FILE + "\" must be a string or a stem list." );
        }
        boolean isBase64 = false;
        if(polyad.getArgumments().size() == 3){
            Object obj3 = polyad.evalArg(2, state);
            if(!isBoolean(obj3)){
                throw new IllegalArgumentException("Error: The third argument to \"" + WRITE_FILE + "\" must be a boolean." );
            }
            isBase64 = (Boolean)obj3;
        }
        try {
            boolean didIt = false;
            if (isStem(obj2)) {
                FileUtil.writeStemToFile(fileName, (StemVariable) obj2);
                didIt = true;
            }
            if(isString(obj2)){
                FileUtil.writeStringToFile(fileName, (String) obj2);
                didIt = true;
            }
            if(isBase64){
                FileUtil.writeFileAsBinary(fileName, (String) obj2);
                didIt = true;
            }
            if(!didIt){
                throw new IllegalArgumentException("Error: The argument to " + WRITE_FILE + " is an unecognized type");
            }
            polyad.setResult(Boolean.TRUE);
            polyad.setResultType(Constant.BOOLEAN_TYPE);
            polyad.setEvaluated(true);
        }catch(Throwable t){
            if(t instanceof QDLException){
                throw (RuntimeException)t;
            }
            throw new QDLException("Error: could not write file \"" + fileName + "\":" + t.getMessage());
        }

    }

    protected void doReadFile(Polyad polyad, State state) {
        if (0 == polyad.getArgumments().size()) {
            throw new IllegalArgumentException("Error: " + READ_FILE + " requires a file name to read.");
        }
        Object obj = polyad.evalArg(0, state);
        ;
        if (obj == null || !isString(obj)) {
            throw new IllegalArgumentException("Error: The " + READ_FILE + " command requires a string for its first argument.");
        }
        String fileName = obj.toString();
        int op = -1; // default
        if (polyad.getArgumments().size() == 2) {
            Object obj2 = polyad.evalArg(1, state);
            if (!isLong(obj2)) {
                throw new IllegalArgumentException("Error: The " + READ_FILE + " command's second argument must be an integer.");
            }
            op = ((Long) obj2).intValue();
            if (op != 0 && op != 1) {
                throw new IllegalArgumentException("Error: The " + READ_FILE + " command's second argument must have value of 1 or 0.");
            }
        }
        try {
            switch (op) {
                case 1:
                    // binary file. Read it and base64 encode it
                    polyad.setResult(FileUtil.readFileAsBinary(fileName));
                    polyad.setResultType(Constant.STRING_TYPE);
                    polyad.setEvaluated(true);
                    return;
                case 0:
                    // Read as lines, put in a stem
                    polyad.setResult(FileUtil.readFileAsStem(fileName));
                    polyad.setResultType(Constant.STEM_TYPE);
                    polyad.setEvaluated(true);
                    return;
                default:
                case -1:
                    // read it as a long string.
                    polyad.setResult(FileUtil.readFileAsString(fileName));
                    polyad.setResultType(Constant.STRING_TYPE);
                    polyad.setEvaluated(true);
                    return;

            }
        } catch (Throwable t) {
            if (t instanceof QDLException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Error reading file \"" + fileName + "\"" + t.getMessage());
        }

    }
}
