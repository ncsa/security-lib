package edu.uiuc.ncsa.qdl.extensions.sets;

import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.extensions.QDLModuleMetaClass;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.qdl.variables.Constant.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/1/22 at  12:24 PM
 * @deprecated - this functionality has been improved and added to the base system. 
 */
public class QDLSets implements QDLModuleMetaClass {
    public static String TO_SET_COMMAND = "to_set";

    public class ToSet implements QDLFunction {
        @Override
        public String getName() {
            return TO_SET_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (objects.length != 1) {
                throw new IllegalArgumentException(TO_SET_COMMAND + " requires a single argument.");
            }
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException("argument to " + TO_SET_COMMAND + " must be a stem variable");
            }
            StemVariable stemVariable = (StemVariable) objects[0];
            if (!stemVariable.isList()) {
                throw new IllegalArgumentException("argument to " + TO_SET_COMMAND + " must be list");
            }
            StemVariable result = new StemVariable();
            for (Object key : stemVariable.keySet()) {
                Object value = stemVariable.get(key);
                int type = Constant.getType(value);
                switch (type) {
                    case NULL_TYPE:
                    case BOOLEAN_TYPE:
                    case DECIMAL_TYPE:
                    case STRING_TYPE:
                        result.put(value.toString(), stemVariable.get(key));
                        break;
                    case LONG_TYPE:
                        result.put((Long) value, stemVariable.get(key));
                        break;
                    default:
                        throw new IllegalArgumentException("unsupported type for " + TO_SET_COMMAND);

                }
            }
            return result;
        }

        List<String> doxx;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(TO_SET_COMMAND + "(x.) = convert a list of values to a set.");
                doxx.add("A set is a stem whose keys and values are the same, i.e.");
                doxx.add("stem.'x' == 'x'");
                doxx.add("E.g. convert a list of string to a set");
                doxx.add("   " + TO_SET_COMMAND + "(['a','b','c']");
                doxx.add("{a:a, b:b, c:c}");
                doxx.add("E.g. for a list of numbers");
                doxx.add("   " + TO_SET_COMMAND + "([;5]*3+2)");
                doxx.add("{2:2, 5:5, 8:8, 11:11, 14:14}");
                doxx.add("See also: " + IS_MEMBER_OF_COMMAND + ", " + INTERSECTION_COMMAND);
            }
            return doxx;
        }
    }

    public static String IS_MEMBER_OF_COMMAND = "is_member";

    public class IsMember implements QDLFunction {
        @Override
        public String getName() {
            return IS_MEMBER_OF_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (!(objects[1] instanceof StemVariable)) {
                throw new IllegalArgumentException(IS_MEMBER_OF_COMMAND + " requires a set as its second argument");
            }
            StemVariable stem1 = (StemVariable) objects[1];
            if (!(objects[0] instanceof StemVariable)) {
                if (stem1.containsKey(objects[0])) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
            StemVariable stem0 = (StemVariable) objects[0];

            StemVariable result = new StemVariable();
            for (Object key : stem0.keySet()) {
                result.putLongOrString(key, stem1.containsKey(key));
            }
            return result;
        }

        List<String> doxx;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(IS_MEMBER_OF_COMMAND + "(x.,y.) = check if each element of set x. is the set y.");
                doxx.add("The result is a left conformable list of booleans.");
                doxx.add("Note that the result is a stem for each element.");
                doxx.add("See also: " + SUBSET_COMMAND + ", " + EQUALS_COMMAND);
            }
            return doxx;
        }
    }

    public static String INTERSECTION_COMMAND = "intersection";

    public class Intersection implements QDLFunction {
        @Override
        public String getName() {
            return INTERSECTION_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException("first argument of " + INTERSECTION_COMMAND + " must be a set");
            }
            StemVariable stem0 = (StemVariable) objects[0];
            if (!(objects[1] instanceof StemVariable)) {
                throw new IllegalArgumentException("second argument of " + INTERSECTION_COMMAND + " must be a set");
            }
            StemVariable stem1 = (StemVariable) objects[1];
            StemVariable result = new StemVariable();
            for (Object key : stem0.keySet()) {
                if (stem1.containsKey(key)) {
                    result.putLongOrString(key, key);
                }
            }
            return result;
        }

        List<String> doxx = null;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(INTERSECTION_COMMAND + "(x.,y.) - find the intersection of two sets.");
                doxx.add("the result is set that contains the elements common to both of the ");
                doxx.add("arguments.");
                doxx.add("See also: " + DIFFERENCE_COMMAND);
            }
            return doxx;
        }
    }

    public static String PEEK_COMMAND = "peek";

    public class Peek implements QDLFunction {
        @Override
        public String getName() {
            return PEEK_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException(PEEK_COMMAND + " requires a set as its first argument");
            }
            long defaultCount = 5L;
            if (objects.length == 2) {
                if (!(objects[1] instanceof Long)) {
                    throw new IllegalArgumentException(PEEK_COMMAND + " requires an integer as its second argument");
                }
                defaultCount = (Long) objects[1];
            }
            StemVariable arg = (StemVariable) objects[0];
            defaultCount = Math.min(defaultCount, arg.keySet().size());
            StemVariable result = new StemVariable();
            int i = 0;
            for (Object key : arg.keySet()) {
                if (i == defaultCount) {
                    break;
                }
                result.put(i++, key);
            }
            return result;
        }

        List<String> doxx = null;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(PEEK_COMMAND + "(x. {,count}) - peek inside a set.");
                doxx.add("x. - the set");
                doxx.add("count - (optional) the number of elements to return. Default is 5");
                doxx.add("Since sets have no canonical ordering, there is no easy way");
                doxx.add("(like a list) to look at the first say 5 elements.");
                doxx.add("This command allows you to look at a random number of elements");
                doxx.add("The result is a list (not set) of elements. ");
                doxx.add("Why isn't this a set? Because the major use of this is to have a");
                doxx.add("quick look at a few members and it is easier to list things and format");
                doxx.add("them if they are a list.");
                doxx.add("See also:" + TO_LIST_COMMAND);
            }
            return doxx;
        }
    }

    public static String CARDINALITY_COMMAND = "card";

    public class Cardinality implements QDLFunction {
        @Override
        public String getName() {
            return CARDINALITY_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (objects[0] instanceof StemVariable) {
                return (long) ((StemVariable) objects[0]).keySet().size();

            }
            return 1;
        }

        List<String> doxx = new ArrayList<>();

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx.add(CARDINALITY_COMMAND + "(set.) - quick count of the elements of the set");
                doxx.add("This is faster than the standard size command which assumes possible recursive");
                doxx.add("structures in the stem.");
            }
            return doxx;
        }
    }

    public static String TO_LIST_COMMAND = "to_list";

    public class ToList implements QDLFunction {
        @Override
        public String getName() {
            return TO_LIST_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException(TO_LIST_COMMAND + " requires a set as its argument");
            }
            StemVariable arg = (StemVariable) objects[0];
            long i = 0L;
            StemVariable result = new StemVariable();
            for (Object key : arg.keySet()) {
                result.put(i++, key);
            }
            return result;
        }

        List<String> doxx = null;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(TO_LIST_COMMAND + "(set.) - converts a set to a list.");
                doxx.add("Note that there is no canonical ordering of elements in a set");
                doxx.add("hence none is assured.");
                doxx.add("See also:" + TO_SET_COMMAND);
            }
            return doxx;
        }
    }

    public static String DIFFERENCE_COMMAND = "diff";

    public class Difference implements QDLFunction {
        @Override
        public String getName() {
            return DIFFERENCE_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException("first argument of " + DIFFERENCE_COMMAND + " must be a set");
            }
            StemVariable stem0 = (StemVariable) objects[0];
            if (!(objects[1] instanceof StemVariable)) {
                throw new IllegalArgumentException("second argument of " + DIFFERENCE_COMMAND + " must be a set");
            }
            StemVariable stem1 = (StemVariable) objects[1];
            StemVariable result = new StemVariable();
            for (Object key : stem0.keySet()) {
                if (stem0.containsKey(key) && !stem1.containsKey(key)) {
                    result.putLongOrString(key, key);
                }
            }
            return result;
        }

        List<String> doxx = null;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(DIFFERENCE_COMMAND + "(x., y.) - find the difference of two sets.");
                doxx.add("The difference of two sets, A and B is defined as");
                doxx.add("A - B := A ∩ B' = {x:x∈ A ∧ x ∉ B}");
                doxx.add("See also: " + StemEvaluator.UNION);
            }
            return doxx;
        }
    }

    public static String SUBSET_COMMAND = "subset_of";

    public class Subset implements QDLFunction {
        @Override
        public String getName() {
            return SUBSET_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException(SUBSET_COMMAND + " requires a set as its first argument");
            }

            if (!(objects[1] instanceof StemVariable)) {
                throw new IllegalArgumentException(SUBSET_COMMAND + " requires a set as its second argument");
            }
            StemVariable stem1 = (StemVariable) objects[1];
            StemVariable stem0 = (StemVariable) objects[0];

            for (Object key : stem0.keySet()) {
                if (!stem1.containsKey(key)) {
                    return Boolean.FALSE;
                }
            }
            return Boolean.TRUE;
        }

        List<String> doxx = null;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(SUBSET_COMMAND + "(x., y.) - returns true if every element in x. is in y.");
                doxx.add("This returns a scalar, unlike " + IS_MEMBER_OF_COMMAND + " which tests");
                doxx.add("each element and returns membership.");
                doxx.add("See also:" + EQUALS_COMMAND + ", " + IS_MEMBER_OF_COMMAND);
            }
            return doxx;
        }
    }

    public static String EQUALS_COMMAND = "equals";

    public class Equals implements QDLFunction {
        @Override
        public String getName() {
            return EQUALS_COMMAND;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            if (!(objects[0] instanceof StemVariable)) {
                throw new IllegalArgumentException(SUBSET_COMMAND + " requires a set as its first argument");
            }

            if (!(objects[1] instanceof StemVariable)) {
                throw new IllegalArgumentException(SUBSET_COMMAND + " requires a set as its second argument");
            }
            Subset subset = new Subset();
            Boolean result = (Boolean) subset.evaluate(objects, state);
            if (!result) {
                return Boolean.FALSE;
            }
            // only test for equality if stem0 is a subset of stem1.
            StemVariable stem0 = (StemVariable) objects[0];
            StemVariable stem1 = (StemVariable) objects[1];

            if (stem1.size() != stem0.size()) {
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }

        List<String> doxx = null;

        @Override
        public List<String> getDocumentation(int argCount) {
            if (doxx == null) {
                doxx = new ArrayList<>();
                doxx.add(EQUALS_COMMAND + "(x.,y.) returns true if and only if every ");
                doxx.add("   element of x. is in y. and conversely.");
                doxx.add("See also: " + SUBSET_COMMAND);
                doxx.add("");
            }
            return doxx;
        }
    }
}

/*
    tf.'old' :='/home/ncsa/dev/temp/old_transactions.txt';
tf.'new' := '/home/ncsa/dev/temp/2022-04-01-delete_transactions.sql';
old. := file_read(tf.'old', 1);
old. := substring(old., 51) - '\'';
old := to_set(old.);

  new. := file_read(tf.'new', 1);
new. := substring(new., 51) - '\'';
new := to_set(new.);
  common. := intersection(new., old.)

  size(a,b,c) -> a+b+c
  size(2,3,4)
 */
