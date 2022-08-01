package edu.uiuc.ncsa.security.core.inheritance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A list for managing order-dependant inheritance. Each element is unique. Appending
 * or prepending removed any current entry.
 * The order of inheritance is most to least important.
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/21 at  6:32 AM
 */
public class InheritanceList implements Iterable<String> {
    @Override
    public Iterator<String> iterator() {
        return elements.iterator();
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        elements.forEach(action);
    }

    @Override
    public Spliterator<String> spliterator() {
        return elements.spliterator();
    }

    public ArrayList<String> getElements() {
        return elements;
    }

    ArrayList<String> elements = new ArrayList<>();

    public void append(String name) {
        if (!elements.contains(name)) {
            elements.add(name);
        }
    }

    public void append(List<String> names) {
        for (String name : names) {
            append(name);
        }
    }

    public void append(InheritanceList inheritanceList){
        for(String x : inheritanceList){
            append(x);
        }
    }
    public void prepend(InheritanceList inheritanceList) {
        prepend(inheritanceList.getElements());

    }
    public void prepend(String name) {
        if (elements.contains(name)) {
          //  elements.remove(name);
            return;   // do not change order of list!
        }
        elements.add(0, name);
    }

    public void prepend(List<String> names) {
        // Have to do these in reverse order or just inserting the first element reverses it
        for (int i = names.size() - 1; 0 <= i; i--) {
            prepend(names.get(i));
        }
    }


    public boolean contains(String name) {
        return elements.contains(name);
    }

    public void remove(String name) {
        elements.remove(name);
    }

    public void remove(List<String> names) {
        for (String name : names) {
            elements.remove(name);
        }
    }

    public int size() {
        return 0;
    }

    public void insertAt(int index, String name) {

    }

    public void insertAt(int index, List<String> name) {
    }

    public void clear() {
        elements = new ArrayList<>();
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    public static void main(String[] args) {
        InheritanceList il = new InheritanceList();
        il.append("A");
        il.append("B");
        il.append("C");
        ArrayList<String> pqr = new ArrayList<>();
        pqr.add("P");
        pqr.add("Q");
        pqr.add("R");
        il.append(pqr);
        System.out.println(il); // A B C P Q R

        il.clear();
        il.append("A");
        il.append("B");
        il.append("C");
        il.prepend(pqr);
        System.out.println(il); //P Q R A B C
        il.append("A");
        System.out.println(il); // no change: P Q R A B C
        // Check it iterates
        for(String x : il){
            System.out.print(x);
        }
    }
}
