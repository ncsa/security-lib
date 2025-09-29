package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.InvalidURIException;

import java.net.URI;
import java.util.UUID;

/**
 * Simple implementation of an identifier. This also has a couple of factory methods for
 * creating identifiers.
 * <p>Created by Jeff Gaynor<br>
 * on 4/3/12 at  3:41 PM
 */
public class BasicIdentifier implements Identifier {
    static final long serialVersionUID = 0xcafebeefL;

    final URI id;
    final String idString;   // Set the ID string to speed this up, since toString for URIs is actually pretty complex.
    @Override
    public String toString() {
        if (idString == null) return null;
        return idString;
    }

    @Override
    public boolean isTrivial() {
        return StringUtils.isTrivial(idString);
    }

    @Override
    public URI getUri() {
        return id;
    }

    public BasicIdentifier(URI id) {
        if(id == null) {
            throw new IllegalArgumentException("null cannot be used as an identifier");
        }
        this.id = id;
        idString =id.toString();
    }

    public BasicIdentifier(String id) {
        if(id == null){
            throw new IllegalArgumentException("null cannot be used as an identifier");
        }
        try {
            this.idString = id;
            this.id = URI.create(id);
        }catch(Throwable t){
            throw new InvalidURIException("Error parsing URI \"" + id + "\"", t);
        }
    }

    /**
     * Create a new identifier from a given string.
     * @param id
     * @return
     */
    public static Identifier newID(String id) {
        if (id == null) return null;
        return new BasicIdentifier(id);
    }

    /**
     * Create a new identifier from a given URI.
     * @param uri
     * @return
     */
    public static Identifier newID(URI uri) {
        if (uri == null) return null;
        return new BasicIdentifier(uri);
    }

    /**
     * Create a random id. Never use random IDs in for things that grant security access, since these
     * are random which does not imply they are unguessable. Random IDs are useful for internal identifiers
     * (such as for objects) or for testing.
     *
     * @return
     */
    public static Identifier randomID() {
        return newID("urn:id:" + UUID.randomUUID());
    }


    @Override
    public boolean equals(Object obj) {
        boolean rc = false;
        if (obj instanceof BasicIdentifier) {
            BasicIdentifier x = (BasicIdentifier) obj;
            if (getUri() == null) {
                if (x.getUri() == null) rc = true;
            } else {
                rc = getUri().equals(x.getUri());
            }
        }
        return rc;
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return 41; // some prime number...
        }
        return id.hashCode();
    }

    public int compareTo(Object o) {
        if (!(o instanceof Identifier)) {
            throw new ClassCastException("The given object is of type " + o.getClass().getName() + " and cannot be compared to an Identifier");
        }
        if (id == null) {
            if (o.toString() == null) return 0;
            throw new NullPointerException("Error: Attempt to compare a null identifier to a non-null one \"" + o.toString() + "\"");
        }
        return toString().compareTo(o.toString());
    }
    public static void main(String[] args){
       System.out.println( BasicIdentifier.newID("*"));
    }
}
