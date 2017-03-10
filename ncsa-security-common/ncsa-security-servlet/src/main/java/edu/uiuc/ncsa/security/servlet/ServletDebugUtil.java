package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.util.DebugUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/10/17 at  11:25 AM
 */
public class ServletDebugUtil extends DebugUtil {
    public static void printAllParameters(Class callingClass, HttpServletRequest request) {
        if(!isEnabled) return;
           String reqUrl = request.getRequestURL().toString();
           String queryString = request.getQueryString();   // d=789
           if (queryString != null) {
               reqUrl += "?" + queryString;
           }
           System.err.println("\n" + callingClass.getSimpleName() + " at " + (new Date()));
           System.err.println("Request parameters for " + reqUrl + "");

           if (request.getParameterMap() == null || request.getParameterMap().isEmpty()) {
               System.err.println("  (none)");
           } else {
               for (Object key : request.getParameterMap().keySet()) {
                   String[] values = request.getParameterValues(key.toString());
                   System.err.println(" " + key + ":");
                   if (values == null || values.length == 0) {
                       System.err.println("   (no values)");
                   } else {
                       for (String x : values) {
                           System.err.println("   " + x);
                       }
                   }
               }
           }
           System.err.println("Cookies:");
           if (request.getCookies() == null) {
               System.err.println(" (none)");
           } else {
               for (javax.servlet.http.Cookie c : request.getCookies()) {
                   System.err.println(" " + c.getName() + "=" + c.getValue());
               }
           }
           System.err.println("Headers:");
           Enumeration e = request.getHeaderNames();
           if (!e.hasMoreElements()) {
               System.err.println(" (none)");
           } else {
               while (e.hasMoreElements()) {
                   String name = e.nextElement().toString();
                   System.err.println(" " + name);
                   System.err.println("   " + request.getHeader(name));
               }
           }
       }
}
