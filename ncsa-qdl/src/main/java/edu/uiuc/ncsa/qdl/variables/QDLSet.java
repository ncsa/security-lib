package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import net.sf.json.JSONArray;

import java.math.BigDecimal;
import java.util.HashSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/6/22 at  4:02 PM
 */
public class QDLSet extends HashSet {
    /**
     * JSON does not have sets, so this is a bit klugy: It will take an array and
     * stick the values into a set. This is lossy.
     *
     * @param array
     */
    public void fromJSON(JSONArray array) {
        StemVariable stemVariable = new StemVariable();
        stemVariable.fromJSON(array);
        clear();
        addAll(stemVariable.getStemList().values());
    }

    public JSONArray toJSON() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(this);
        return jsonArray;
    }

    public String inputForm() {
        String out = "{";
        boolean isFirst = true;
        for (Object obj : this) {
            out = out + (isFirst ? "" : ",") + InputFormUtil.inputForm(obj);
            isFirst = false;
        }
        return out + "}";
    }

    @Override
    public String toString() {
        String out = "{";
        boolean isFirst = true;
        for (Object obj : this) {
            String value;
            if (obj instanceof BigDecimal) {
                value = InputFormUtil.inputForm((BigDecimal) obj);
            } else {
                value = obj.toString();
            }
            out = out + (isFirst ? "" : ",") + value;
            isFirst = false;
        }
        return out + "}";
    }

    public QDLSet intersection(QDLSet arg) {
        QDLSet outSet = new QDLSet();
        for (Object key : this) {
            if (arg.contains(key)) {
                outSet.add(key);
            }
        }
        return outSet;
    }

    public QDLSet difference(QDLSet arg) {
        QDLSet outSet = new QDLSet();
        for (Object key : this) {
            if (!arg.contains(key)) {
                outSet.add(key);
            }
        }
        return outSet;
    }

    public QDLSet union(QDLSet arg) {
        QDLSet outSet = new QDLSet();
        outSet.addAll(this);
        outSet.addAll(arg);
        return outSet;
    }

    public boolean isSubsetOf(QDLSet arg) {
        for (Object key : this) {
            if (!arg.contains(key)) {
                return false;
            }
        }
        return true;
    }

    public boolean isEqualTo(QDLSet arg) {
        return isSubsetOf(arg) && (size() == arg.size());
    }

    public QDLSet symmetricDifference(QDLSet arg) {
        return difference(arg).union(arg.difference(this));
    }

/*    @Override
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof QDLSet)) {
            return false;
        }
        QDLSet arg = (QDLSet) o;
        if (size() != arg.size()) {
            return false;
        }
        for (Object element : this) {
            if (!arg.contains(element)) {
                return false;
            }
        }
        return true;
    }*/

    public static void main(String[] args) {
        QDLSet set1 = new QDLSet();
        set1.add(2);
        set1.add(3);
        set1.add(5);
        set1.add("p");
        set1.add("a");

        QDLSet set2 = new QDLSet();
        set2.add(2);
        set2.add(4);
        set2.add(5);
        set2.add("a");
        set2.add("b");
        set2.add("q");


        System.out.println("s1= " + set1);
        System.out.println("s2= " + set2);
        System.out.println("s1 + s2 = " + set1.union(set2));
        System.out.println("s1 - s2= " + set1.difference(set2));
        System.out.println("s2 - s1= " + set2.difference(set1));
        System.out.println("s1 subset s2= " + set1.isSubsetOf(set2));
        System.out.println("s1 sDiff s2= " + set1.symmetricDifference(set2));
        System.out.println("s1 == s1? " + set1.isEqualTo(set1));

    }
}
