package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.QDLCodec;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.Random;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  1:15 PM
 */
public class AbstractQDLTester extends TestBase {
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

    protected BigDecimal comparisonTolerance = new BigDecimal(".0000000000001");

    /**
     * Compares two {@link BigDecimal}s. Tells if their difference is less than the
     * comparison tolerance. This effectively means they are equal.
     * @param x
     * @param y
     * @param comparisonTolerance
     * @return
     */
    protected boolean areEqual(BigDecimal x, BigDecimal y, BigDecimal comparisonTolerance) {
        return x.subtract(y).abs().compareTo(comparisonTolerance) < 0;
    }
    protected boolean areEqual(BigDecimal x, BigDecimal y) {
        return areEqual(x,y,comparisonTolerance);
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
       * @param stringBuffer
       * @param x
       * @return
       */
      protected StringBuffer addLine(StringBuffer stringBuffer, String x){
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


      protected boolean testNumberEquals(Object arg1, Object arg2){
          if((arg1 instanceof Long) && (arg2 instanceof Long)){
              return arg1.equals(arg2);
          }
          BigDecimal left;
          BigDecimal right;
          if(arg1 instanceof Long){
               left = new BigDecimal((Long)arg1);
          }else{
              left = (BigDecimal)arg1;
          }
          if(arg2 instanceof Long){
               right = new BigDecimal((Long)arg2);
          }else{
              right = (BigDecimal)arg2;
          }

          BigDecimal result = left.subtract(right);
          return result.compareTo(BigDecimal.ZERO) == 0;
      }
}
