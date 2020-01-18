package edu.uiuc.ncsa.security.util.qdl.expressions;

import edu.uiuc.ncsa.security.util.qdl.variables.Constant;

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
    public static final int SAY_TYPE = 1 + IO_FUNCTION_BASE_VALUE;
    public static String SCAN_FUNCTION = "scan";
    public static final int SCAN_TYPE = 2 + IO_FUNCTION_BASE_VALUE;


    @Override
    public int getType(String name) {
        int out = super.getType(name);
        if(out != UNKNOWN_VALUE) return out;
        if (name.equals(SAY_FUNCTION)) return SAY_TYPE;
        if (name.equals(SCAN_FUNCTION)) return SCAN_TYPE;
        return UNKNOWN_VALUE;
    }



    @Override
    public void evaluate(Polyad polyad) {
        switch(polyad.getOperatorType()){
            case SAY_TYPE:
                String result = "";
                if(polyad.getArgumments().size() != 0) {
                    result = polyad.getArgumments().get(0).evaluate().toString();
                }
                System.out.println(result);
                polyad.setResult(null);
                polyad.setResultType(Constant.NULL_TYPE);
                polyad.setEvaluated(true);
                return;
            case SCAN_TYPE:
                if(polyad.getArgumments().size() != 0){
                    System.out.print(polyad.getArgumments().get(0).evaluate());
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
                return;
        }
        super.evaluate(polyad);
    }
}
