package edu.uiuc.ncsa.security.storage.monitored.upkeep;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  10:04 AM
 */
public interface UpkeepConstants {
    String ACTION_DELETE = "delete";
    String ACTION_ARCHIVE = "archive";
    String ACTION_TEST = "test";
    String ACTION_RETAIN = "retain";
    /**
     * This means nothing applied, take no action.
     */
    String ACTION_NONE = "none";

    String WHEN_AFTER = "after";
    String WHEN_BEFORE = "before";
    String WHEN_NEVER = "never";
    String TYPE_ACCESSED = "accessed";
    String TYPE_CREATED = "created";
    String TYPE_MODIFIED = "modified";

}
