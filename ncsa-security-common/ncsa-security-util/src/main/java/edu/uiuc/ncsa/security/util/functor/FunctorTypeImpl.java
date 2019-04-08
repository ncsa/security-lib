package edu.uiuc.ncsa.security.util.functor;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/6/18 at  9:02 AM
 */
public enum FunctorTypeImpl implements FunctorType {
    NONE("none"),
    AND("$and"),
    IF("$if"),
    ELSE("$else"),
    FALSE("$false"),
    NOT("$not"),
    OR("$or"),
    THEN("$then"),
    TRUE("$true"),
    XOR("$xor"),
    CONCAT("$concat"),
    CONTAINS("$contains"),
    DROP("$drop"),
    ECHO("$echo"),
    ENDS_WITH("$endsWith"),
    EQUALS("$equals"),
    EXISTS("$exists"),
    MATCH("$match"),
    REPLACE("$replace"),
    STARTS_WITH("$startsWith"),
    TO_LOWER_CASE("$toLowerCase"),
    TO_UPPER_CASE("$toUpperCase"),
    TO_ARRAY("$toArray"),
    SET_ENV("$setEnv"),
    GET_ENV("$getEnv"),
    CLEAR_ENV("$clearEnv"),
    RAISE_ERROR("$raiseError");

    private String value;

    FunctorTypeImpl(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
