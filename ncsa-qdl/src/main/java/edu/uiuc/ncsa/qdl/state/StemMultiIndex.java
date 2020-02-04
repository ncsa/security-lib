package edu.uiuc.ncsa.qdl.state;

/**
 * If we get a stem variable, this has the components to it
 */
public class StemMultiIndex {
    /**
     * Given a stem wrapper where a stem reference has been found at newNameIndex,
     * make a new object suitable for handing to a stem variable for resolution.
     * The original index, w, should have its {@link #realLength} adjusted accordingly
     *
     * @param w
     * @param newNameIndex
     */
    public StemMultiIndex(StemMultiIndex w, int newNameIndex) {
        name = w.components[newNameIndex] + "."; // make sure it has the period
        components = new String[w.getRealLength() - newNameIndex - 1]; // starts at name of stem
        System.arraycopy(w.components, newNameIndex+1 , components, 0, components.length);
        realLength = components.length;
    }

    /**
     * So no indices.
     * @return
     */
      public boolean isEmpty(){
         return getRealLength() == 0 || (getRealLength() == 1 && (getComponents()[0].isEmpty()));
      }
    public StemMultiIndex(String variable) {
        int first_index = variable.indexOf(".");
        int last_index = variable.length();

        name = variable.substring(0, first_index) + ".";
        if (variable.endsWith(".")) {
            // account for trailing "."
            isStem = true;
            last_index = last_index - 1;
        }

        this.components = variable.substring(first_index + 1).split("\\.");
        realLength = components.length;
    }

    public int getRealLength() {
        return realLength;
    }

    public void setRealLength(int realLength) {
        this.realLength = realLength;
    }

    int realLength = -1; // As stems resolve, the length of this changes. This sets the actual length

    public String[] getComponents() {
        return components;
    }
    public String getLastComponent(){
        return components[components.length - 1];
    }
    public String getName() {
        return name;
    }

    public boolean isStem() {
        return isStem;
    }

    String[] components;
    String name;
    boolean isStem = false;

    protected StemMultiIndex(){

    }
    /**
     * Returns a new Stem that is adjusted to the realLength
     * @return
     */
    public StemMultiIndex truncate(){
        if(realLength == components.length) return this;
        StemMultiIndex stemMultiIndex = new StemMultiIndex();
        stemMultiIndex.components = new String[realLength];
        System.arraycopy(components, 0, stemMultiIndex.components, 0, this.realLength);
        stemMultiIndex.name = name;
        stemMultiIndex.realLength = this.realLength;
        return stemMultiIndex;

    }
}