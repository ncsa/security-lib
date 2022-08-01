package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.Identifiable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * A very basic sorter. This will just sort  {@link Identifiable} objects on their
 * unique identifier.
 * <p>Created by Jeff Gaynor<br>
 * on 5/22/14 at  9:52 AM
 */
public class BasicSorter implements Sortable {
    @Override
    public ArrayList<Identifiable> sort(List<Identifiable> arg) {
        TreeMap<String, Identifiable> tm = new TreeMap<>();

        for(int i = 0; i < arg.size(); i++){
            Identifiable identifiable =  arg.get(i);
            tm.put(identifiable.getIdentifierString(), identifiable);
        }
        return new ArrayList(tm.values());
    }

    @Override
    public void setState(String args) {}
}
