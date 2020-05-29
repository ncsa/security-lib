package edu.uiuc.ncsa.jexer;

import jexer.TApplication;

/**
 * NOTE this is set up as a swing application.
 * Nifty quick demo.
 * <p>Created by Jeff Gaynor<br>
 * on 5/29/20 at  9:48 AM
 */
// Run this at the command line
public class MinTest {
    public static void main(String[] args) throws Exception{
            TApplication app = new TApplication(TApplication.BackendType.SWING);
            //TApplication app = new TApplication(TApplication.BackendType.XTERM);
            app.addToolMenu();
            app.addFileMenu();
            app.addWindowMenu();
            app.run();

    }
}
