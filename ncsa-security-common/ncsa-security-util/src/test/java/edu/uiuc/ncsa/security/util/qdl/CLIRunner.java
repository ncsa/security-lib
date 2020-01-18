package edu.uiuc.ncsa.security.util.qdl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  10:35 AM
 */
public class CLIRunner {
    // This just exists to run a few tests from the command line
    public static void main(String[] args){
          try{
              IOFunctionTest test = new IOFunctionTest();
              test.scanExample();
          }catch(Exception e
          ){
              e.printStackTrace();
          }
      }
}
