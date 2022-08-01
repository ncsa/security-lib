package edu.uiuc.ncsa.security.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class with a bunch of very useful JSP utilities in it.
 * <p>Created by Jeff Gaynor<br>
 * on 10/12/11 at  3:11 PM
 */
public class JSPUtil {
    /**
     * Forwards a request
     *
     * @param req
     * @param res
     * @param resource
     * @throws IOException
     * @throws ServletException
     */
    public synchronized static void fwd(HttpServletRequest req, HttpServletResponse res, String resource) throws IOException, ServletException {
        RequestDispatcher dispatcher = req.getRequestDispatcher(resource);
        dispatcher.forward(req, res);
    }

    /**
     * Returns if a string is null or of zero length.
     *
     * @param x
     * @return
     */
    public static boolean isEmpty(String x) {
        return x == null || x.length() == 0;
    }

    /**
     * Gets a parameter from the request and invokes the objects toString method. This
     * is very useful if you have URIs or URLs and need to check the request for
     * values.
     *
     * @param request
     * @param key
     * @return
     */
    public static String getParameter(HttpServletRequest request, Object key) {
        // using a key lets us pass in Resources and URIs too.
        return request.getParameter(key.toString());
    }


    public static void handleException(Throwable t, HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        request.setAttribute("exception", t);
        fwd(request, response, page);
    }
}
