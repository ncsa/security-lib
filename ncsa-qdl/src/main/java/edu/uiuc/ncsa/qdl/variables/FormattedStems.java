package edu.uiuc.ncsa.qdl.variables;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/16/21 at  7:38 AM
 */
public class FormattedStems<V> {
    List<List<V>>  entries = new ArrayList<>();
    protected V formatEntry(Object x){
      try{
          return (V)x;
          // fugly solution, but Java won't let us do anything else.
      }catch(ClassCastException classCastException){

      }
        return null;
    }
    protected V getPlaceholder(){
        return null;
    }

    /**
     * returns what the formatted object is.
     * @param rowNumber
     * @param colNumber
     * @param object
     * @return
     */
    public V put(int rowNumber, int colNumber, Object object){

        V entry = formatEntry(object);
         List<V> row = getRow(rowNumber);
         for(int i = row.size(); i < colNumber+1; i++){
             row.add(colNumber, getPlaceholder());
         }
         row.set(colNumber, entry);
         return entry;
    }
    public String get(int row, int column){
        return null;
    }
     public boolean hasRow(int rowNumber){
        return rowNumber < entries.size();
     }
    protected List<V> getRow(int n){
        if(n < entries.size()){
            return entries.get(n);
        }
        List<V> row = null;
        // n+1 is new size if it doesn't have enough rows.
        for(int i = entries.size(); i<n+1; i++){
             row = new ArrayList<>();
             entries.add(row);
        }
        return row;
    }
}
