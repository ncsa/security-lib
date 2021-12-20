package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.state.XThing;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module template table.
 * <p>Created by Jeff Gaynor<br>
 * on 12/14/21 at  12:22 PM
 */
public class MTTable<K extends MTKey, V extends Module>  extends HashMap<K, V> implements  XTable<K, V> {
    @Override
    public V put(XThing value) {
        return null;
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {

    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {

    }

    public void clearChangeList(){
        changeList = new ArrayList<>();
    }
    // On updates, the change list will track additions or replacements.
    // clear it before updates, read it it after, then clear it again.
    public List<URI> getChangeList() {
        return changeList;
    }

    public void setChangeList(List<URI> changeList) {
        this.changeList = changeList;
    }

    List<URI> changeList = new ArrayList<>();

    public V put(URI key, V value) {
        changeList.add(key);
        return super.put((K) new MTKey(key), value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(K key : m.keySet()){
            getChangeList().add(key.getUriKey());
        }
        super.putAll(m);
    }
}
