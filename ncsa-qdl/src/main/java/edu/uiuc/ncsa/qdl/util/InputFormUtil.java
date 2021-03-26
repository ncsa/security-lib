package edu.uiuc.ncsa.qdl.util;

import ch.obermuhlner.math.big.BigDecimalMath;
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
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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

    /*
      f(x)->x*cos(x)*exp(-x);
  g(x)->x^2*sin(x)*exp(1-x)
    y. := pplot(@f(),@g(), -2,2,1000)
    list_subset(y., 5, 10)

     */
    public static String inputForm(BigDecimal d) {
        return formatBD2(d);
    }

    protected static String formatBD(BigDecimal d) {
        d = d.stripTrailingZeros();
        //  int newScale =   d.scale() - d.precision();
        //  d.setScale(OpEvaluator.getNumericDigits(), RoundingMode.HALF_DOWN);
        //d.setScale(OpEvaluator.getNumericDigits(), RoundingMode.HALF_DOWN);
        BigDecimal frac = BigDecimalMath.fractionalPart(d);

        String s = d.toString();
        int p = s.indexOf(".");
        if (s.toUpperCase().indexOf("E") != -1) {
            return s;
        }
        if (0 <= p) {
            if (s.length() - p - 1 <= OpEvaluator.getNumericDigits()) {
                // nix
            } else {
                s = s.substring(0, p + 1 + OpEvaluator.getNumericDigits());
            }
        }
        return s; // Or it spits out scientific noation like .123E-15 which bombs in the parser
    }

    public static BigDecimal top = new BigDecimal("1000000000");
    public static BigDecimal bottom = new BigDecimal("-1000000000");
    protected static boolean isDBInRange(BigDecimal d){
        int bb = bottom.compareTo(d);
        int tt = d.compareTo(top);
        return (bb <= 0 ) && (  tt <= 0);
    }
    protected static String formatBD2(BigDecimal d) {
        try{
            return Long.toString(d.longValueExact());
        }catch(ArithmeticException arithmeticException) {
            // rock on
        }
/*
            NumberFormat formatter = new DecimalFormat("#####.########");
            formatter.setRoundingMode(RoundingMode.HALF_UP);
            formatter.setMinimumFractionDigits(1);
            formatter.setMaximumFractionDigits(OpEvaluator.getNumericDigits());
            return formatter.format(d);
*/

        NumberFormat formatter = new DecimalFormat("0.0E0");
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        //formatter.setMinimumFractionDigits((d.scale() > 0) ? d.precision() : d.scale());
        formatter.setMinimumFractionDigits(1);
        formatter.setMaximumFractionDigits(OpEvaluator.getNumericDigits());
        return formatter.format(d);

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
    /*
      y. := [n(4), n(4)+4, 8+n(4)]
      yy. := [y., 100+y.]
      yyy.:=[yy., 200 + yy., 400 + yy.]
      yyy_reduce0. := reduce(@+, yyy.)
      yyy_reduce1.0. :=  yyy.0.0 + yyy.0.1
      yyy_reduce1.1. :=  yyy.1.0 + yyy.1.1
      yyy_reduce1.2. :=  yyy.2.0 + yyy.2.1
      yyy_reduce2.0. := yyy.0.0.0 + yyy.0.0.1 + yyy.0.0.2
      yyy_reduce2.1. := yyy.0.1.0 + yyy.0.1.1 + yyy.0.1.2
         yyy.
      [
       [[[0,1,2,3],[4,5,6,7],[8,9,10,11]],[[100,101,102,103],[104,105,106,107],[108,109,110,111]]],
       [[[200,201,202,203],[204,205,206,207],[208,209,210,211]],[[300,301,302,303],[304,305,306,307],[308,309,310,311]]],
       [[[400,401,402,403],[404,405,406,407],[408,409,410,411]],[[500,501,502,503],[504,505,506,507],[508,509,510,511]]]
      ]
         yyy_reduce0.
      [
       [[600,603,606,609],[612,615,618,621],[624,627,630,633]],
       [[900,903,906,909],[912,915,918,921],[924,927,930,933]]
      ]
         yyy_reduce1.
     [
      [[100,102,104,106],[108,110,112,114],[116,118,120,122]],
      [[500,502,504,506],[508,510,512,514],[516,518,520,522]],
      [[900,902,904,906],[908,910,912,914],[916,918,920,922]]
     ]
        yyy.reduce2.
     [
      [12,15,18,21],
      [312,315,318,321]
     ]
     */
}
