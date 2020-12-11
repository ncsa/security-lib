package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifierProvider;
import edu.uiuc.ncsa.security.core.util.Identifiers;
import net.sf.json.JSONObject;

import java.net.URI;
import java.util.Base64;
import java.util.Date;

/**
 * Utilities for working with tokens.
 * <h2>version 1 tokens</h2>
 * these are of the form:
 * <pre>
 *     scheme + scheme specific part + //host / + component/  + uid/ + create timestamp
 * </pre>
 * E.g.
 * https://cilogon.org/oauth2/refreshToken/77e2b3dcc7934ffa455d7f49629bad61/1605827034976
 * <h2>Version 2</h2>
 * These are of the form:
 * scheme + scheme specific part + // + host / + version/ + component/  + uid/ + create timestamp/ + lifetime
 * <pre>
 * </pre>
 * E.g.
 * https://cilogon.org/oauth2/v2.0/refreshToken/77e2b3dcc7934ffa455d7f49629bad61/1605827034976/1000000
 * <p>Created by Jeff Gaynor<br>
 * on 11/24/20 at  9:37 AM
 */
public class TokenUtils {
    public static void main(String[] args) {
        IdentifierProvider ip = new IdentifierProvider<Identifier>(URI.create("https://cilogon.org/oauth2"), "my_token", true) {
        };
        Date now = new Date();
        String version = "2.0.0";
        long lifetime = 11 * 24 * 3600 * 1000L; // 11 days
        //Identifiers.setByteCount(8);
        String uid = Identifiers.getHexString();
        String host = "https://cilogon.org/oauth2/";
        String token = "my_token";
        System.out.println(ip.get());
        JSONObject json = new JSONObject();
        json.put("version", version);
        json.put("uid", uid);
        json.put("timestamp", now.getTime());
        json.put("lifetime", lifetime);
        System.out.println(json.toString(1));
        String jj = Base64.getEncoder().encodeToString(json.toString().getBytes());
        URI urij = URI.create(host + token + "/" + jj);
        System.out.println(urij);
        System.out.println("length = " + urij.toString().length());
        String q = host + token + "?version=" + version + "&uid=" + uid + "&ts=" + now.getTime() + "&lifetime=" + lifetime;
        URI uriQ = URI.create(q);
        System.out.println(uriQ);
        System.out.println("length = " + uriQ.toString().length());

        q = host + uid + "?version=" + version + "&token_type=" + token + "&ts=" + now.getTime() + "&lifetime=" + lifetime;
        uriQ = URI.create(q);
        System.out.println(uriQ);
        System.out.println("length = " + uriQ.toString().length());

        q = host + token + "/" + uid + "/v" + version + "/" + now.getTime() + "/" + lifetime;
        uriQ = URI.create(q);
        System.out.println(uriQ);
        System.out.println("length = " + uriQ.toString().length());

        q = host + token + "/" + uid + "/" + now.getTime() + "?version=" + version + "&lifetime=" + lifetime;
        uriQ = URI.create(q);
        System.out.println(uriQ);
        System.out.println("length = " + uriQ.toString().length());
    }
}
