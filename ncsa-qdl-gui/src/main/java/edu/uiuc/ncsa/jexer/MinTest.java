package edu.uiuc.ncsa.jexer;

import jexer.TApplication;

/**
 * NOTE this is set up as a swing application.
 * Nifty quick demo.
 * <p>Created by Jeff Gaynor<br>
 * on 5/29/20 at  9:48 AM
 */
public class MinTest {
    public static void main(String[] args) throws Exception {
        // To run in the IDE, use Swing:
        TApplication app = new TApplication(TApplication.BackendType.SWING);
        // To run at the command line, specify XTERM:
     //   TApplication app = new TApplication(TApplication.BackendType.XTERM);
        
        // now, change to top dir for this module and issue

        // mvn compile exec:java -Dexec.mainClass="edu.uiuc.ncsa.jexer.MinTest"

        // NOTE that if you run this as XTERM in the IDE it will ALWAYS crash the entire IDE!
        // ALSO running this anyplace but a bona fide full screen text window, you will get another mouse pointer.
        app.addToolMenu();
        app.addFileMenu();
        app.addWindowMenu();
        app.run();

    }
}
