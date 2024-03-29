package edu.uiuc.ncsa.sas.thing;

/**
 * Top-level "thing" (analog of Java Object) for the SAS server.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:34 AM
 */
public  class Thing {
    public void setType(String type) {
        this.type = type;
    }

    protected String type;

      public Thing(String type) {
          this.type = type;
      }

      public String getType() {
          return type;
      }

      @Override
      public boolean equals(Object obj) {
          if(!(obj instanceof Thing)) return false;
          String targetValue = ((Thing) obj).getType();
          if(getType() == null && targetValue == null) return true;
          if(getType() == null && targetValue != null) return false;
          if(getType() != null && targetValue == null) return false;
          return getType().equals(targetValue);
      }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" +
                "value='" + type + '\'' +
                ']';
    }
}