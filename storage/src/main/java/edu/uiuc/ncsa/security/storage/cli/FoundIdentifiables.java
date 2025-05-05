package edu.uiuc.ncsa.security.storage.cli;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.util.cli.InputLine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The list of fidentifier found from the {@link StoreCommands#findItem(InputLine, boolean)} method.
 * It contains the list of {@link Identifiable}s as well as forensic information about where it
 * came from (so better error and informational messages can be written).
 */
public class FoundIdentifiables extends ArrayList<Identifiable> {
    public FoundIdentifiables(boolean isRS, int initialCapacity) {
        super(initialCapacity);
        this.isRS = isRS;
    }

    public FoundIdentifiables(boolean isRS) {
        this.isRS = isRS;
    }

    public FoundIdentifiables(List<Identifiable> allEntries) {
        super(allEntries);
    }

    /**
     * Is does this list have a single element?
     * @return
     */
    public boolean isSingleton(){
        return size() == 1;
    };

    public FoundIdentifiables(boolean isRS, Collection<? extends Identifiable> c) {
        super(c);
        this.isRS = isRS;
    }

    public boolean isRS() {
        return isRS;
    }

    public void setRS(boolean RS) {
        isRS = RS;
    }

    protected boolean isRS;

    /**
     * If this was found to be set locally with the {@link StoreCommands#set_id(InputLine) call.}
     * @return
     */
    public boolean isLocalID() {
        return localID;
    }

    public void setLocalID(boolean localID) {
        this.localID = localID;
    }

    boolean localID = false;

    /** If this was given as the numeric index
     *
      * @return
     */
    public boolean isNumericIndex() {
        return isNumericIndex;
    }

    public void setNumericIndex(boolean numericIndex) {
        this.isNumericIndex = numericIndex;
    }

    boolean isNumericIndex = false;

    /**
     * If {@link #isNumericIndex} is true, this is the index that was given.
     * @return
     */
    public int getNumericIndex() {
        return numericIndex;
    }

    public void setNumericIndex(int numericIndex) {
        this.numericIndex = numericIndex;
    }

    int numericIndex = -1;

    /**
     * If {@link #isRS() retuns true, this is the name of the result set used.}
     * @return
     */
    public String getRSName() {
        return rsName;
    }

    public void setRSName(String rsName) {
        this.rsName = rsName;
    }

    String rsName = null;

    public boolean hasRSIndexList(){
        return rsIndexList != null;
    }
    /**
     * If {@link #isRS()} is true and an index list was specified, this will be
     * non-null.
     * @return
     */
    public List getRsIndexList() {
        return rsIndexList;
    }

    public void setRsIndexList(List rsIndexList) {
        this.rsIndexList = rsIndexList;
    }

    List rsIndexList = null;

    /**
     * If the id was explicitly set on the command line as an argument. The id
     * itself will be in the identifier and in such cases there is exactly one.
     * @return
     */
    public boolean isGivenID() {
        return givenID;
    }

    public void setGivenID(boolean givenID) {
        this.givenID = givenID;
    }

    boolean givenID = false;
}
