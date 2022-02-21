package edu.uiuc.ncsa.qdl.module;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XStack;
import edu.uiuc.ncsa.qdl.state.XTable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/29/22 at  7:25 AM
 */
public class MIStack<V extends MITable2<? extends XKey, ? extends MIWrapper>> extends XStack<V> implements Serializable {
    public MIStack() {
        pushNewTable();
    }

    @Override
    public XStack newInstance() {
        return new MIStack();
    }

    @Override
    public XTable newTableInstance() {
        return new MITable2();
    }

    @Override
    public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException {
        throw new NotImplementedException("implement version 1 serialization for new instance stack");
    }


    /**
     * Convenience method that casts
     * @param xKey
     * @return
     */
   public Module getModule(XKey xKey){
       MIWrapper wrapper = (MIWrapper) get(xKey);
       if(wrapper == null){
           return null;
       }
         return (wrapper).getModule();
   }


    @Override
    public String getXMLStackTag() {
        return XMLConstants.INSTANCE_STACK;
    }

    @Override
    public String getXMLTableTag() {
        return XMLConstants.MODULES_TAG;
    }

    /**
     * Gets the current set of keys for a given alias. This takes overrides into account
     * so must be computed afresh each time. Note that this returns {@link MTKey}s.
     *
     * @param xKey
     * @return
     */
    public Set<MTKey> getByAlias(XKey xKey) {
        HashSet<MTKey> keys = new HashSet<>();
        Set<XKey> allKeys = keySet();
        for (XKey tempKey : allKeys) {
            // Remember that the key of the module is its alias, not its
            // namespace (which is used to key templates).
            Module m = (Module) get(tempKey);
            keys.add(new MTKey(m.getNamespace()));
        }
        return keys;
    }

    /**
     * For a given module namespace, return all current aliases. Note that this means
     * current overrides, so if there are multiple instances in the stack, only
     * the active one is returned.
     * @param key
     * @return
     */
    public List<XKey> getAliases(MTKey key) {
        ArrayList<XKey> foundKeys = new ArrayList<>();
        for (XKey tempKey : keySet()) {
            Module m = ((MIWrapper) get(tempKey)).getModule();
            if (m.getNamespace().equals(key.getUriKey())) {
                foundKeys.add(tempKey);
            }
        }
        return foundKeys;
    }

    /**
     * Same as {@link #getAliases(MTKey)} except that the result is not a set of keys
     * but a simple list of the aliases as strings. Mostly this is used in the workspace
     * to display lists of aliases.
     * @param key
     * @return
     */
    public List<String> getAliasesAsString(MTKey key){
        List<XKey> originalAliases = getAliases(key);
        ArrayList<String> aliases = new ArrayList<>();
        for(XKey xKey : originalAliases){
            aliases.add(xKey.getKey());
        }
        return aliases;
    }

    @Override
    public void setStateStack(State state, XStack xStack) {
           state.setMInstances((MIStack) xStack);
    }

    @Override
    public XStack getStateStack(State state) {
        return state.getMInstances();
    }
}
