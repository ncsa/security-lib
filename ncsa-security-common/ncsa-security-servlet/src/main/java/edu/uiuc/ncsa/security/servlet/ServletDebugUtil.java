package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.util.DebugUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/20/17 at  11:07 AM
 */
public class ServletDebugUtil extends DebugUtil {

    public static  void printAllParameters(Class klasse, HttpServletRequest request) {
        if(!isEnabled()) {return;}
          String reqUrl = request.getRequestURL().toString();
          String queryString = request.getQueryString();   // d=789
          if (queryString != null) {
              reqUrl += "?" + queryString;
          }
          printIt("\n" + klasse.getSimpleName() + " at " + (new Date()));
          printIt("HTTP method=" + request.getMethod());
          printIt("Request parameters for " + reqUrl + "");
          printIt("Query String:\"" + queryString + "\"");
          if (request.getParameterMap() == null || request.getParameterMap().isEmpty()) {
              printIt("  (none)");
          } else {
              for (Object key : request.getParameterMap().keySet()) {
                  String[] values = request.getParameterValues(key.toString());
                  printIt(" " + key + ":");
                  if (values == null || values.length == 0) {
                      printIt("   (no values)");
                  } else {
                      for (String x : values) {
                          printIt("   " + x);
                      }
                  }
              }
          }
          printIt("Cookies:");
          if (request.getCookies() == null) {
              printIt(" (none)");
          } else {
              for (javax.servlet.http.Cookie c : request.getCookies()) {
                  printIt(" " + c.getName() + "=" + c.getValue());
              }
          }
          printIt("Headers:");
          Enumeration e = request.getHeaderNames();
          if (!e.hasMoreElements()) {
              printIt(" (none)");
          } else {
              while (e.hasMoreElements()) {
                  String name = e.nextElement().toString();
                  printIt(" " + name);
                  printIt("   " + request.getHeader(name));
              }
          }
      }


}
