package edu.uiuc.ncsa.qdl.variables;

import java.util.ArrayList;

import static edu.uiuc.ncsa.qdl.state.QDLConstants.STEM_PATH_MARKER;
import static edu.uiuc.ncsa.qdl.state.QDLConstants.STEM_PATH_MARKER2;

/**
 * A path in a stem. Unlike the . (child of operator) these have been resolved
 * and can be passed around. They are of the form
 * <pre>
 *     (` | · ) vpath0 (` | · ) vpath1 (` | · ) ...
 * </pre>
 * where each component is vencoded. E.g.
 * <pre>
 *     `0`foo$2Ebar`back$60tick
 * </pre>
 * or
 * <pre>
 *     ·0·foo$2Ebar·back$60tick
 * </pre>
 *
 * <i>I.e.</i> a raised dot or backtick means that no tail resolution should take place.
 * <h2>Contract</h2>
 * This contains only v-encoded elements. There are setters to optional encode if needed,
 * but it is up to the programmer to make sure of the integrity of the entries.
 * <p>Created by Jeff Gaynor<br>
 * on 6/7/21 at  8:51 AM
 */
public class StemPath<V extends StemPathEntry> extends ArrayList<V> {
   static String regex = "[" + STEM_PATH_MARKER + STEM_PATH_MARKER2 + "]";

    public static boolean isPath(String path){
        return path.startsWith(STEM_PATH_MARKER) || path.startsWith(STEM_PATH_MARKER2);
    }

    public void parsePath(String path) {
        if(!isPath(path)){
            V stemPathEntry = (V) new StemPathEntry(path);
            add(stemPathEntry);
            return;
        }

        String[] components = path.split(regex);
        for(int i = 1; i < components.length; i++){

        }
    }
    public String toPath(boolean useUnicode){
        StringBuilder stringBuilder = new StringBuilder();
        for(StemPathEntry spe : this){
           stringBuilder.append((useUnicode?STEM_PATH_MARKER2:STEM_PATH_MARKER) + ((spe.isString()?spe.key:spe.index)));
        }
        return stringBuilder.toString();
    }
   public static void main(String[] args){
       StemPath stemPath = new StemPath();

       stemPath.add(new StemPathEntry("a.b", true));
       stemPath.add(new StemPathEntry("foo", true));
       stemPath.add(new StemPathEntry("mairzy doats", true));
       stemPath.add(new StemPathEntry("long#%$path", true));
       System.out.println(stemPath.toPath(true));
       System.out.println(stemPath.toPath(false));
       StemVariable stemVariable = new StemVariable();
       stemVariable.put("a.b", "blarg");
       stemVariable.put("foo", "woof");
       stemVariable.put("mairzy doats", "fnord");
       stemVariable.put("long#%$path", "blarf");
       System.out.println(stemVariable.get(stemPath));

   }
}
