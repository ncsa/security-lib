package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/30/20 at  6:13 AM
 */
public class StemConverter {
    /**
      * Does the grunt work of taking an entry from a JSON object and converting it to something QDL can
      * understand. Used mostly in the toString methods.
      *
      * @param obj
      * @return
      */
     public static Object convert(Object obj) {
         if (obj == null) return null;

         if (obj instanceof Integer) return new Long(obj.toString());
         if (obj instanceof Double) return new BigDecimal(obj.toString(), OpEvaluator.getMathContext());
         if (obj instanceof Boolean) return obj;
         if (obj instanceof Long) return obj;
         if (obj instanceof Date) return Iso8601.date2String((Date) obj);
         if (obj instanceof String) return obj;
         if (obj instanceof JSONArray) return convert((JSONArray) obj);
         if (obj instanceof JSONObject) return convert((JSONObject) obj);
         if(obj instanceof BigDecimal) return InputFormUtil.inputForm((BigDecimal) obj);
         return obj.toString();
     }
    public static StemVariable convert(JSONArray array) {
        StemVariable out = new StemVariable();
        QDLList qdlList = new QDLList();
        qdlList.addAll(array);
        out.setQDLList(qdlList);
        return out;

/*
        QDLList<SparseEntry> qdlList = new QDLList<>();

        for (int i = 0; i < array.size(); i++) {
            qdlList.append(new SparseEntry(i, convert(array.get(i))));
        }
        out.setStemList(qdlList);
        return out;
*/
    }
    public static StemVariable convert(JSONObject object) {

          StemVariable out = new StemVariable();
          for (Object key : object.keySet()) {
              Object obj = object.get(key);
              out.put(key.toString(), convert(obj));
          }
          return out;
      }
}
