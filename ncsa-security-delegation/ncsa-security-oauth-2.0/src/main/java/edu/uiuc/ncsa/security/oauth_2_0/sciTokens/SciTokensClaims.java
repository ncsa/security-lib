package edu.uiuc.ncsa.security.oauth_2_0.sciTokens;

import edu.uiuc.ncsa.security.oauth_2_0.server.OA2Claims;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/31/17 at  2:07 PM
 */
public interface SciTokensClaims extends OA2Claims {
    public static String READ = "https://scitokens.org/v1/scope/read";
    public static String WRITE = "https://scitokens.org/v1/scope/write";
    public static String QUEUE = "https://scitokens.org/v1/scope/queue";
    public static String EXECUTE = "https://scitokens.org/v1/scope/execute";
}
