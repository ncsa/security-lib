package edu.uiuc.ncsa.sat.thing;

/**
 * Top-level "thing" (analog of Java Object) for the SAT server.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:34 AM
 */
public  class Thing {
      String value;

      public Thing(String value) {
          this.value = value;
      }

      public String getValue() {
          return value;
      }

      @Override
      public boolean equals(Object obj) {
          if(!(obj instanceof Thing)) return false;
          String targetValue = ((Thing) obj).getValue();
          if(getValue() == null && targetValue == null) return true;
          if(getValue() == null && targetValue != null) return false;
          if(getValue() != null && targetValue == null) return false;
          return getValue().equals(targetValue);
      }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
                "value='" + value + '\'' +
                ']';
    }
}