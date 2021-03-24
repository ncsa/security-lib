package edu.uiuc.ncsa.qdl.util;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.extensions.JavaModule;
import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.extensions.QDLFunctionRecord;
import edu.uiuc.ncsa.qdl.functions.FR_WithState;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.QDLModule;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.math.BigDecimal;
import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/10/21 at  8:38 AM
 */
public class InputFormUtil {
    public static String JAVA_CLASS_MARKER = "java:";

    public static String inputForm(Object obj) {
        if (obj instanceof Boolean) return inputForm((Boolean) obj);
        if (obj instanceof Long) return inputForm((Long) obj);
        if (obj instanceof BigDecimal) return inputForm((BigDecimal) obj);
        if (obj instanceof String) return inputForm((String) obj);
        if (obj instanceof StemVariable) return inputForm((StemVariable) obj);
        if (obj instanceof QDLNull) return inputForm((QDLNull) obj);
        return "";
    }

    public static String inputForm(Boolean b) {
        return b ? "true" : "false";
    }

    public static String inputForm(Long myLong) {
        return myLong.toString();
    }

    public static String inputForm(BigDecimal d) {
        d = d.stripTrailingZeros();
        String s = d.toString();
        if(OpEvaluator.getNumericDigits() < s.length()){
            s = d.toEngineeringString();
        }
/*
        String s = d.toString();
        if(s.indexOf("E")!=-1){
            s = s.replace("E", "*10^(");
            s = s + ")";
        }
*/
/*      This fails. Must take into account large integers first as well as sign.
        Just plain string works fine, so this is mostly cosmetic.
        int dIndex = s.indexOf(".");
        if(OpEvaluator.getNumericDigits() < s.length()-dIndex){
            s =  s.substring(dIndex, 1+dIndex + OpEvaluator.getNumericDigits());
        }*/
        return s; // Or it spits out scientific noation like .123E-15 which bombs in the parser
    }

    public static String inputForm(String s) {
        s = s.replace("'", "\\'");
        return "'" + s + "'";
    }

    /**
     * Look up a function for the given state by arg count. If there is no such function,
     * then an null string is returned.
     *
     * @param fName
     * @param argCount
     * @param state
     * @return
     */
    public static String inputForm(String fName, int argCount, State state) {
        FR_WithState fr_withState = state.resolveFunction(fName, argCount);
        String output = null;

        if (fr_withState == null) {
            // no such critter
            return output;
        }
        FunctionRecord fr = fr_withState.functionRecord;
        if (fr != null) {
            if (fr instanceof QDLFunctionRecord) {
                QDLFunction qf = ((QDLFunctionRecord) fr).qdlFunction;
                if (qf != null) {
                    output = JAVA_CLASS_MARKER + qf.getClass().getCanonicalName();
                }
            } else {
                output = StringUtils.listToString(fr.sourceCode);
            }
        }
        return output;
    }

    public static String inputForm(QDLNull qdlNull) {
        return "null";
    }

    public static String inputForm(StemVariable stemVariable) {
        return stemVariable.inputForm();
    }

    public static String inputForm(StemVariable stemVariable, int indentFactor) {
        return stemVariable.inputForm(indentFactor);
    }

    /**
     * Finds teh input form for a module. Note that the name is either
     * an alias, like acl or acl# (trailing # is optional) or the namespace
     * (like oa4mp:/util/acl)
     *
     * @param moduleNS
     * @param state
     * @return
     */
    public static String inputFormModule(URI moduleNS, State state) {
        // Cases:

        Module m = state.getModuleMap().get(moduleNS);
        if (m == null) {
            return null;
        }
        if (m instanceof JavaModule) {
            return JAVA_CLASS_MARKER + ((JavaModule) m).getClassname();
        }
        if (m instanceof QDLModule) {
            return StringUtils.listToString(((QDLModule) m).getSource());
        }
        return null;
    }

    public static String inputFormModule(String moduleName, State state) {
        URI moduleNS;

        if (moduleName.endsWith(ImportManager.NS_DELIMITER)) {
            moduleName = moduleName.substring(0, moduleName.length() - 1);
        }
        moduleNS = state.getImportManager().getByAlias(moduleName);
        if (moduleNS == null) {
            try {
                moduleNS = URI.create(moduleName);
            } catch (Throwable t) {
                // not a uri, not an alias, not a module.
                return null;
            }
        }
        return inputFormModule(moduleNS, state);
    }

    /**
     * Finds the input form for a variable from the state.
     *
     * @param varName
     * @param state
     * @return
     */
    public static String inputFormVar(String varName, int indentFactor, State state) {
        Object object = state.getValue(varName);
        if (object == null) return null;
        if (object instanceof StemVariable && 0 <= indentFactor) {
            return inputForm((StemVariable) object, indentFactor);
        }
        return inputForm(object);
    }

    public static String inputFormVar(String varName, State state) {
        return inputFormVar(varName, -1, state);
    }

    public static void main(String[] args) {
        StemVariable stemVariable = new StemVariable();
        stemVariable.put(0L, "foo0");
        stemVariable.put(1L, "foo1");
        stemVariable.put(2L, "foo2");
        stemVariable.put("long", 123L);
        stemVariable.put("string", "abc'def'786%$%$#");
        stemVariable.put("boolean", Boolean.TRUE);
        stemVariable.put("decimal", new BigDecimal("-123.3456"));
        stemVariable.put("null", QDLNull.getInstance());
        System.out.println(stemVariable.inputForm());
    }
}
