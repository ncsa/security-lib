package edu.uiuc.ncsa.security.util.functor;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/6/18 at  9:02 AM
 */
public enum FunctorTypeImpl implements FunctorType {
    NONE("none"),
    IF("$if"),
    THEN("$then"),
    ELSE("$else"),
    OR("$or"),
    XOR("$xor"),
    TRUE("$true"),
    FALSE("$false"),
    AND("$and"),
    NOT("$not"),
    CONTAINS("$contains"),
    EXISTS("$exists"),
    MATCH("$match"),
    EQUALS("$equals"),
    STARTS_WITH("$startsWith"),
    REPLACE("$replace"),
    ENDS_WITH("$endsWith"),
    TO_LOWER_CASE("$toLowerCase"),
    TO_UPPER_CASE("$toUpperCase"),
    CONCAT("$concat"),
    DROP("$drop");

    private String value;

    FunctorTypeImpl(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
