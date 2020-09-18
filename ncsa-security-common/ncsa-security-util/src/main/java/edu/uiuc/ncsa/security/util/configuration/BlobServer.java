package edu.uiuc.ncsa.security.util.configuration;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/3/20 at  10:24 AM
 * @experimental
 */
public class BlobServer {
    public BlobServer(MyLoggingFacade myLoggingFacade) {
        logger = myLoggingFacade;
    }

    MyLoggingFacade logger;
    int port = 9999;
    static String host = "localhost";

    protected void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        server.createContext("/config", new MyHttpHandler());
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        server.setExecutor(threadPoolExecutor);
        server.start();
        if (logger == null) {
            System.out.println("Server started on port " + port);
        } else {
            logger.info("Server started on port " + port);
        }

    }

    private static class MyHttpHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Got request.");
            System.out.println("    Method:" + httpExchange.getRequestMethod());
            System.out.println("   Req uri:" + httpExchange.getRequestURI());
            System.out.println("    params:" + httpExchange.getRequestURI().getQuery());
            System.out.println("raw params:" + httpExchange.getRequestURI().getRawQuery());
            System.out.println(" local add:" + httpExchange.getLocalAddress().getHostName());
            System.out.println("  protocol:" + httpExchange.getProtocol());
            if (!host.equals(httpExchange.getLocalAddress().getHostName())) {
                throw new GeneralException("Error: Request rejected");
            }
            String requestParamValue = null;

            if ("GET".equals(httpExchange.getRequestMethod())) {
                requestParamValue = handleGetRequest(httpExchange);
                System.out.println("param = " + requestParamValue);

            } else if ("POST".equals(httpExchange)) {
                throw new GeneralException("Error: Post not supported");
            }
            handleResponse(httpExchange, requestParamValue);
        }

        private void handleResponse(HttpExchange httpExchange, String requestParamValue) throws IOException {
            System.out.println("handling response");

            OutputStream outputStream = httpExchange.getResponseBody();
            // content
            String response = "marizy doats. You sent  " + requestParamValue;
            Headers responseHeaders = httpExchange.getResponseHeaders();
            responseHeaders.set("Content-Type", "text/plain");
            httpExchange.sendResponseHeaders(200, response.length());

            System.out.println("writing response");

            outputStream.write(response.getBytes());
            outputStream.flush();
            outputStream.close();
            System.out.println("done!");

        }

        private String handleGetRequest(HttpExchange httpExchange) {
            String q = httpExchange.getRequestURI().getRawQuery();
            if(StringUtils.isTrivial(q)){
                System.out.println("empty query");
                return "";
            }
            String[] params = q.split("&");
            for(String p : params){
                String[] x = p.split("=");
                    System.out.println("key="+ x[0] + ", value = " + x[1]);
            }
            return q;
        }


    }

    public static void main(String[] args) throws IOException {
        BlobServer blobServer = new BlobServer(new MyLoggingFacade("blob"));
        blobServer.start();
    }
}
