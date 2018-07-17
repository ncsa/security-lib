package edu.uiuc.ncsa.security.util.functor.parser.event;

import java.util.EventListener;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:25 AM
 */
public interface DelimiterListener extends EventListener {
    void openDelimiter(DelimiterEvent delimeterEvent);

    void closeDelimiter(DelimiterEvent delimeterEvent);

    void reset();
}
