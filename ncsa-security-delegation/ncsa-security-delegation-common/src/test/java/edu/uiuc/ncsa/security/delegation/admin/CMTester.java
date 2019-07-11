package edu.uiuc.ncsa.security.delegation.admin;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;

import java.net.URI;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/14/16 at  9:29 AM
 */
public class CMTester {
    public static void main(String[] args){
       String x = "_8c3ca7828fd90b76d574ac8c210a2cb1";
       URI uri = URI.create(x);
        Identifier xx = BasicIdentifier.newID(x);
        System.out.println(xx);
        System.out.println(uri);
    }

}
