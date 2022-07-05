package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.QDLExceptionWithTrace;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLList;
import edu.uiuc.ncsa.qdl.variables.QDLSet;
import edu.uiuc.ncsa.qdl.variables.QDLStem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/1/22 at  4:50 PM
 */
public class IndexArgs extends ArrayList<IndexArg> {
    /**
     * Does one of the index args have a wildcard?
     *
     * @return
     */
    public boolean hasWildcard() {
        for (IndexArg indexArg : this) {
            if (indexArg.isWildcard()) return true;
        }
        return false;
    }

    /**
     * If one (except the zero-th element, which is the actual stem) is all wildcards.
     *
     * @return
     */
    public boolean isAllWildcards() {
        for (int i = 1; i < size(); i++) {
            if (!get(i).isWildcard()) return false;
        }
        return true;

    }

    /**
     * Only works if there are no wildcards! This counts  the number of indices that will be
     * computed.
     *
     * @return
     */
    public int count() {
        int s = 1;
        for (int i = 1; i < size(); i++) { // zero-th element is left most
            s = s * argSize(get(i));
        }
        return s;
    }

    public int argSize(IndexArg indexArg) {
        Object obj = indexArg.swri.getResult();
        if (Constant.isScalar(obj)) {
            return 1;
        }
        if (obj instanceof AllIndices) {
            throw new IllegalStateException("cannot compute size for wildcards");
        }
        if (obj instanceof QDLStem) {
            return ((QDLStem) obj).size();
        }
        if (obj instanceof QDLSet) {
            return ((QDLSet) obj).size();
        }
        throw new IllegalStateException("cannot compute size for unknown type");

    }


      /*
   In an expression like a\x1.\x2.\...\xk. where the xk are lists or scalars
   list sizes are n1,n2,...nk.
   ν = total size = n1*n2*...*nk
   An example shows this.
   a\[;2]\[;3]\[;4]    generates indices

 - [0,    - 0,    k- 0]       And the relationship is (general = here)
|  [0,   g| 0,    e| 1]       n1, n2, n3, n4, ... = 2, 3, 4 given
|  [0,   r| 0,    y| 2]       p1, p2, p3, p4, ... = 12 4  1
p  |  [0,   o| 0,    s- 3]       g1, g2, g3, g4, ... = 1  2  3
e  |  [0,   u| 1,       0]
r  |  [0,   p| 1,       1]      ν = 24
i  |  [0,    | 1,       2]      p1 = ν/n1, p_k = p_k-1/nk, 1<k
o  |  [0,    - 1,       3]      gk = ν/(nk*pk)
d  |  [0,      2,       0]
|  [0,      2,       1]
|  [0,      2,       2]
-  [0,      2,       3]
   [1,      0,       0]
   [1,      0,       1]
   [1,      0,       2]
   [1,      0,       3]
   [1,      1,       0]
   [1,      1,       1]
   [1,      1,       2]
   [1,      1,       3]
   [1,      2,       0]
   [1,      2,       1]
   [1,      2,       2]
   [1,      2,       3]

       */

    /**
     * <pre>
     * indexArgs are [a1,a2,a3,a4,a5,...,ak] with sizes [n1,n2,n3,n4,n5,...,nk]
     * nu = n1*n2*...*nk
     * Each iteration has nu passes.
     * each period is nu1= nu/n1, nu2 = nu/(nu1*n2), nu3 = nu/(nu2*n3),...
     * </pre>
     *
     * @return
     */
    public ArrayList<IndexList> createSourceIndices() {

        if (hasWildcard()) {
            throw new IllegalStateException("cannot use this for wildcards");
        }

        int ν = count();
        int ϖ = ν;// period size

        ArrayList<IndexList> sources = new ArrayList<>(ν); // allocate space early or this gets slow.

        for (int i = 0; i < ν; i++) {
            sources.add(new IndexList());
        }// initialize it now, since it is a lot less complicated later.
        for (int i = 1; i < size(); i++) {
            IndexArg indexArg = get(i);
            Collection passedKeys = indexArg.createKeySet();
            int nk = argSize(indexArg);
            ϖ = ϖ / nk;
            int γ = ν / (nk * ϖ); // group size
            int sourcePointer = 0;
            for (int j = 0; j < γ; j++) {
                // number of groups.
                for (Object key : passedKeys) {
                    for (int k = 0; k < ϖ; k++) {
                        sources.get(sourcePointer++).add(key);
                    }
                }
            }
        }
        return sources;
    }


    /**
     * These are the indices for the result.
     *
     * @return
     */
    public ArrayList<IndexList> createTargetIndices() {
        if (hasWildcard()) {
            throw new IllegalStateException("cannot use this for wildcards");
        }

        int ν = count();
        int ϖ = ν;// period size

        ArrayList<IndexList> sources = new ArrayList<>(ν); // allocate space early or this gets slow.
        for (int i = 0; i < ν; i++) {
            sources.add(new IndexList());
        }// initialize it now, since it is a lot less complicated later.
        for (int i = 1; i < size(); i++) {
            IndexArg indexArg = get(i);

            if (Constant.isScalar(indexArg.swri.getResult())) {
                continue;
            }
            Collection passedKeys;
            if (indexArg.strictOrder) {
                passedKeys = indexArg.createKeySet();
            } else {
                passedKeys = new QDLList(argSize(indexArg));
            }
            int nk = argSize(indexArg);
            ϖ = ϖ / nk;
            int γ = ν / (nk * ϖ); // group size
            int sourcePointer = 0;
            for (int j = 0; j < γ; j++) {
                // number of groups.
                for (Object key : passedKeys) {
                    for (int k = 0; k < ϖ; k++) {
                        sources.get(sourcePointer++).add(key);
                        //     System.out.println(sources.get(sourcePointer - 1));
                    }
                }
            }
        }
        return sources;
    }

    public boolean add(IndexArg indexArg, State state) {
        return super.addAll(checkIfList(indexArg, state));
    }

    protected IndexArgs checkIfList(IndexArg indexArg, State state) {
        IndexArgs indexArgs = new IndexArgs();
        if (indexArg.interpretListArg) {
            indexArg.swri.evaluate(state);
            if (!(indexArg.swri.getResult() instanceof QDLStem)) {
                throw new QDLExceptionWithTrace("argument for stem extraction must be a list", indexArg.swri);
            }
            QDLStem args = (QDLStem) indexArg.swri.getResult();
            if (!args.isList()) {
                throw new QDLExceptionWithTrace("argument for stem extraction must be a list", indexArg.swri);
            }
            for (Object key : args.keySet()) {
                IndexArg indexArg1 = new IndexArg();
                indexArg1.strictOrder = indexArg.strictOrder;
                if(args.get(key) instanceof AllIndices){
                    indexArg1.swri = (AllIndices) args.get(key);

                }else{
                    ConstantNode constantNode = new ConstantNode(args.get(key));
                    constantNode.setEvaluated(true);
                    indexArg1.swri = constantNode;
                }
                indexArgs.add(indexArg1);

            }
        } else {
            if((indexArg.swri instanceof Polyad)){
                if(((Polyad)indexArg.swri).getName().equals(StemEvaluator.STAR)){
                    indexArg.swri = new AllIndices();
                }
            }
            indexArgs.add(indexArg);
        }
        return indexArgs;
    }
}
