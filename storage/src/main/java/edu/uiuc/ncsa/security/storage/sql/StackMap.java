package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * This will block until the capacity drops below max.
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/21 at  8:49 AM
 */
public class StackMap<V extends Identifiable>  extends LinkedBlockingQueue<V> {
   /**
    * Default capacity is used which is the maximum integer for Java, effectively making
    * this unbounded.
    */
   public StackMap() {
   }

   public StackMap(int capacity) {
      super(capacity);
   }

   public HashMap<Identifier, V> getMap() {
      return map;
   }

   HashMap<Identifier, V> map = new HashMap<>();

   @Override
   public boolean remove(Object o) {
      if(!(o instanceof Identifiable)){
         // Could silently return a true if its not a queued object, but far better to fail early
         // since this normally should not happen -- it implies something is broken elsewhere.
         throw new IllegalArgumentException("Error: attempt to remove non queued object from queue");
      }
      boolean qChanged =  super.remove(o);

      map.remove(((V)o).getIdentifier());
      return qChanged;
   }

   @Override
   public V take() throws InterruptedException {
      V found =  super.take();
      map.remove(found.getIdentifier());
      return found;
   }

   @Override
   public V poll() {
      V found = super.poll();
      if(found != null){
            map.remove(found.getIdentifier());
      }
      return found;
   }

   @Override
   public V poll(long timeout, TimeUnit unit) throws InterruptedException {
      V found = super.poll(timeout, unit);
      if(found != null){
             map.remove(found.getIdentifier());
       }
       return found;
   }

   @Override
   public void put(V v) throws InterruptedException {
      super.put(v);
      map.put(v.getIdentifier(), v);

   }

   @Override
   public void clear() {
      super.clear();
      map = new HashMap<>();
   }
}
