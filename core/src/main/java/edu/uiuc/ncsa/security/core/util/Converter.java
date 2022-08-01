package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Identifiable;

import java.util.List;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/3/18 at  5:47 PM
 */
public interface Converter<V extends Identifiable> {

    V fromMap(Map<String, Object> data);

    V createIfNeeded(V v);

    V fromMap(Map<String, Object> map, V v);

    void toMap(V value, Map<String, Object> data);

    V subset(V v, List<String> attributes);
}
