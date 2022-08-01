package edu.uiuc.ncsa.security.core;

import java.util.Map;

/**
 * This is an interface that classes which convert to or from XML should implement.
 * <p>Created by Jeff Gaynor<br>
 * on 7/4/18 at  3:07 PM
 */
public interface XMLConverter<V extends Identifiable> {
    V fromMap(Map<String, Object> map, V v);

    void toMap(V value, Map<String, Object> data);
}
