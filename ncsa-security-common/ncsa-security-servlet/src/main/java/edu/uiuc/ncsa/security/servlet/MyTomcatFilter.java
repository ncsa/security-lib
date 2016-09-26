package edu.uiuc.ncsa.security.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A really basic filter. Any path that starts with a "/pages" or a "/static" is intercepted and passed
 * to the default java servlet that Tomcat runs. Your static content goes into the directory named
 * static and your other content is served from the directory called pages. This fixes a feature of
 * Tomcat 6 that allowed the default servlet to serve up static content from the top-level directory
 * including possibly sensitive information. Tomcat 7 prohibits this, so we have to route static
 * requests ourselves.
 * <p>Note that this (or some other similar filter) must be specified in your deployment descriptor
 * (web.xml file) in order to serve up static pages.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 3/27/12 at  11:11 AM
 */
public class MyTomcatFilter implements Filter {
    FilterConfig filterConfig;

    @Override
    public void destroy() {
        // no op at this point.
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String ru = req.getRequestURI();
        String path = ru.substring(req.getContextPath().length());
        if (path.startsWith("/static") || path.startsWith("/pages")) {
            request.getRequestDispatcher(path).forward(request, response);
        } else {
            if (path.equals("/")) {
                // any path that points to the context directly is directed to the static pages.
                String uri = "/static" + path;
                request.getRequestDispatcher(uri).forward(request, response);
            } else {
                //When in doubt, pass it to the path
                request.getRequestDispatcher(path).forward(request, response);
            }
        }
    }
}
