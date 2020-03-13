package edu.uiuc.ncsa.security.delegation.admin;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;

import java.math.BigInteger;
import java.net.URI;
import java.util.BitSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 11/14/16 at  9:29 AM
 */
public class CMTester {
    public static void main(String[] args){
        BigInteger bigInteger = new BigInteger("1101",2);
        System.out.println(bigInteger);
        int n = 28;
        BitSet bs = BitSet.valueOf(new long[]{n});
        BitSet bs1 = BitSet.valueOf(new long[]{n});
        System.out.println(bs);
        System.out.println(bs.equals(bs1));


        String var_regex = "^[a-zA-Z0-9_$]+[a-zA-Z0-9_$\\.]*";
        System.out.println("-abc.ds$s.asd.".matches(var_regex));
        System.out.println("_$2E$4Fabc.ds$s.asd.".matches(var_regex));
        System.out.println("0".matches(var_regex));
       String x = "_8c3ca7828fd90b76d574ac8c210a2cb1";
       URI uri = URI.create(x);
        Identifier xx = BasicIdentifier.newID(x);
        System.out.println(xx);
        System.out.println(uri);
    }

}
