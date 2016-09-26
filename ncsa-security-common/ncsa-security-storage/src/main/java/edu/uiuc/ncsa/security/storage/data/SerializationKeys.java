package edu.uiuc.ncsa.security.storage.data;

/**
 * A class containing keys for data serialization, e.g. by databases or to XML.
 * <p>Note that you might have to override the {@link #identifier} method if you want to specify
 * a key for the identifier different than the default. It is suggested you do this by
 * overriding the default constructor and setting the value there</p>
 * <h3>And another thing</h3>
 * The setters and getters are done using variable arguments. Invoking {@link #identifier} with
 * no arguments returns the current value. Passing any arguments sets its value to the first.
 * All extensions to this class should follow this pattern since it is very concise and simple to use.
 * Generally setting a value is only done by classes that override this. Having the usual accessor pattern
 * for these (get/set) leaves the impression that there is some state that apps can manipulate. In point of fact
 * these values are used in places such as database tables for column names which are immutable.
 * <p>Created by Jeff Gaynor<br>
 * on 4/13/12 at  3:03 PM
 */
public class SerializationKeys {
    String identifier = "identifier";

    /**
     * Identifier for an {@link edu.uiuc.ncsa.security.core.Identifiable} object. This is used as the
     * primary identifier (primary key for SQL databases) for stored objects. Override at creation time
     * if you have a different primary key. Every Store implementation will automatically use this
     * for storing and retrieving objects.
     * @param x
     * @return
     */
    public String identifier(String... x) {
        if (0 < x.length) identifier = x[0];
        return identifier;
    }

}
