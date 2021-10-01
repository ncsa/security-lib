package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.exceptions.ParsingException;
import edu.uiuc.ncsa.qdl.statements.Element;
import edu.uiuc.ncsa.qdl.statements.Statement;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/20 at  6:19 AM
 */
public class ParsingMap extends HashMap<String, ParseRecord> {
    // Marking additions means that the map monitors whatis added from the time the mark is started
    // The new additions then can be removed at once with the rollback command.
    boolean markAdditions = false;
    List<String> addedIds = null;

    public void startMark() {
            startMark(true);
    }

    public void startMark(boolean clearAdded) {
        if(addedIds == null || clearAdded){
            addedIds = new ArrayList<>();
        }
        markAdditions = true;
    }

    public void endMark() {
        markAdditions = false;
    }

    public void rollback() {
        for (String key : addedIds) {
            remove(key);
        }
        markAdditions = false;
    }

    public void clearMark(){
        addedIds.clear();
    }

    public ParseRecord getRoot() {
        return root;
    }

    public void setRoot(ParseRecord root) {
        this.root = root;
    }

    ParseRecord root;

    /**
     * At the end of parsing, this has all of the executable elements in it. Get this, grab the included
     * statement(it's just a wrapper class) and you are off.
     *
     * @return
     */
    public ArrayList<Element> getElements() {
        return elements;
    }

    ArrayList<Element> elements = new ArrayList<>();

    public void put(ElementRecord record) {
        if (markAdditions) {
            addedIds.add(record.identifier);
        }
        if (size() == 0) {
            setRoot(record);
        }
        if (!containsKey(record.identifier)) {
            getElements().add(record.element);
        }
        super.put(record.identifier, record);

    }


    public List<String> getAddedIds() {
        return addedIds;
    }

    public void setAddedIds(List<String> addedIds) {
        this.addedIds = addedIds;
    }

    public void put(StatementRecord record) {
        if (markAdditions) {
            addedIds.add(record.identifier);
        }

        if (size() == 0) {
            setRoot(record);
        }
        super.put(record.identifier, record);

    }


    public Statement getStatementFromContext(ParseTree context) {
        ParseRecord parseRecord = super.get(IDUtils.createIdentifier(context));
        if (parseRecord instanceof StatementRecord) {
            return ((StatementRecord) parseRecord).statement;
        }
        return null;
    }

    /**
     * Find the parent or grandparent etc. of the node in this map.
     * A common pattern is, e.g. having a heavily parenthesized expression
     * in the source like <br/><br/>
     * <code>(((a<b) &&...</code><br/><br/>
     * which may have arbitrarily many association contexts wrapped around it.
     * Our creation of the standard tree omits such parsing cruft so this will trace back
     * to the ancestor.
     *
     * @param parseTree
     * @return
     */
    public ParseRecord findAncestor(ParseTree parseTree) {
        ParseTree parent = parseTree.getParent();
        String id = IDUtils.createIdentifier(parseTree);
        while (!containsKey(id)) {
            parent = parent.getParent();
            id = IDUtils.createIdentifier(parent);
        }
        return get(id);
    }

    /**
     * This will find all the child nodes in this map and remove the entire tree. This is useful if, for instance
     * you have defined a bunch of statements in a function definition or some such.
     *
     * @param parseTree
     */
    public void nukeIt(ParseTree parseTree) {
        String id = IDUtils.createIdentifier(parseTree);
        if (!containsKey(id)) return;
        ParseRecord p = get(id);
        List<String> idsToRemove = new ArrayList<>();
        idsToRemove.add(id);
        List<String> expungeList = new ArrayList<>();

        List<String> kids = getKidIds(id);
        List<String> nextList = new ArrayList<>();
        nextList.addAll(kids);
        expungeList.addAll(kids);
        while (!nextList.isEmpty()) {
            nextList = new ArrayList<>();
            for (String key : kids) {
                nextList.addAll(getKidIds(key));
            }
            expungeList.addAll(nextList);
        }

    }

    protected List<String> getKidIds(String id) {
        List<String> kids = new ArrayList<>();
        for (String key : keySet()) {
            if (get(key).parentIdentifier.equals(id)) {
                kids.add(key);
            }
        }
        return kids;
    }

    /**
     * In this case we have the id of a node that is <b>not</b> in this map
     * because there are many types of intermediate node created by the parser we just don't
     * record. This will take an id then look at the parent child relations and return the first child it finds
     * in this map. E.g.,
     * <pre>
     *     A --> B --> C --> D
     * </pre>
     * In this case, A is in the map, B and C are artifacts of parsing that are not in the map
     * and we need D. So we take the ParseTree for A, look at the childen for B and continue
     * until D is found. Use this sparingly since it may end up snooping through a lot of nodes,
     * however, this is about the best option for finding this.
     *
     * @param parseTree
     * @return
     */
    public ParseRecord findFirstChild(ParseTree parseTree) {
        return findFirstChild(parseTree,false);
    }
    public ParseRecord findFirstChild(ParseTree parseTree, boolean returnNullOK) {
        List<ParseTree> kids = getKids(parseTree);
        for (int i = 0; i < maxSearchDepth; i++) {
            List<ParseTree> grandKids = new ArrayList<>();
            for (ParseTree kid : kids) {
                String id = IDUtils.createIdentifier(kid);
                if (containsKey(id)) {
                    return get(id);
                }
                grandKids.addAll(getKids(kid));
            }
            kids = grandKids;
        }
        if(returnNullOK){
            return null;
        }
        throw new ParsingException("possible decimal without leading digit or missing/extra: parenthesis | bracket | terminal ';' | embedded ',' | embedded ':'");

    }

    int maxSearchDepth = 10; // how far down the tree's children we are willing to go before bailing.

    protected List<ParseTree> getKids(ParseTree parseTree) {
        // Basically need this since we are using the interface to do everything.
        List<ParseTree> kids = new ArrayList<>();
        for (int i = 0; i < parseTree.getChildCount(); i++) {
            kids.add(parseTree.getChild(i));
        }
        return kids;
    }
}
