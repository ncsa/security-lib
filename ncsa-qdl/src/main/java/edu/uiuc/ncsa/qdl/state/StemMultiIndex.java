package edu.uiuc.ncsa.qdl.state;

import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

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
        name = w.components.get(newNameIndex) + STEM_INDEX_MARKER; // make sure it has the period
        components = new ArrayList<>();
        //components = new String[w.getRealLength() - newNameIndex - 1]; // starts at name of stem
        for(int i =0; i < w.getRealLength() - newNameIndex - 1; i++){
            components.add(i, w.components.get(newNameIndex+1+i));
        }
        //System.arraycopy(w.components, newNameIndex+1 , components, 0, components.length);
        realLength = components.size();
    }

    /**
     * So no indices.
     * @return
     */
      public boolean isEmpty(){
         return getRealLength() == 0 || (getRealLength() == 1 && (getComponents().get(0).isEmpty()));
      }

    /**
     * Creates a new instants. Note that the name is the entire stem name, so
     * a.i.j... and this parses it into components.  The very first component is
     * always the name of the stem.
     * @param variable
     */
    public StemMultiIndex(String variable) {
        int first_index = variable.indexOf(STEM_INDEX_MARKER);

        name = variable.substring(0, first_index) + STEM_INDEX_MARKER;
        if (variable.endsWith(STEM_INDEX_MARKER)) {
            // account for trailing "."
            isStem = true;
        }

        List<String> ccc = new ArrayList<>();
        for(String x: variable.substring(first_index + 1).split("\\" + STEM_INDEX_MARKER)){
            ccc.add(x);
        }
        this.components = ccc;
        realLength = components.size();
    }

    public int getRealLength() {
        return realLength;
    }

    public void setRealLength(int realLength) {
        this.realLength = realLength;
    }

    int realLength = -1; // As stems resolve, the length of this changes. This sets the actual length

    public List<String> getComponents() {
        return components;
    }
    public String getLastComponent(){
        return components.get(components.size() - 1);
    }
    public String getName() {
        return name;
    }

    public boolean isStem() {
        return isStem;
    }

    List<String> components;
    String name;
    boolean isStem = false;

    protected StemMultiIndex(){

    }
    /**
     * Returns a new Stem that is adjusted to the realLength
     * @return
     */
    public StemMultiIndex truncate(){
        if(realLength == components.size()) return this;
        StemMultiIndex stemMultiIndex = new StemMultiIndex();
        stemMultiIndex.isStem = isStem(); // don't lose if it's a stem!
        //stemMultiIndex.components = new String[realLength];
        stemMultiIndex.components = new ArrayList<>();
        for(int i = 0; i < realLength; i++){
            stemMultiIndex.components.add(i, components.get(i));
        }
        //System.arraycopy(components, 0, stemMultiIndex.components, 0, this.realLength);
        stemMultiIndex.name = name;
        stemMultiIndex.realLength = this.realLength;
        return stemMultiIndex;

    }
}
