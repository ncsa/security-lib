package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.util.Date;

/**
 * A set of commands for debugging the {@link CLIDriver} only.
 */
public class TestCommands extends CommonCommands{
    @Override
    public String getPrompt() {
        return getName() + ">";
    }
    CLIDriver cliDriver;
    @Override
    public CLIDriver getDriver() {
        return cliDriver;
    }
    public void setDriver(CLIDriver cliDriver) {
        this.cliDriver = cliDriver;
    }

    @Override
    public String getName() {
        return "test";
    }

    public TestCommands(MyLoggingFacade logger) throws Throwable {
        super(logger);
    }
    public void size(InputLine inputLine) throws Exception{
        say("42");
    }
    public void time(InputLine inputLine) throws Exception{
        say(Iso8601.date2String(new Date()));
    }

    public static void main(String[] args) {
        CLIDriver cli = new CLIDriver();
        cli.bootstrap(args);
        try {
            TestCommands testCommands =new TestCommands(cli.getLogger());
            cli.addCommands(testCommands);
            cli.start();
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
/*
  For batch file testing
  -batchFile /home/ncsa/dev/ncsa-git/security-lib/util/src/main/resources/cli-test/test0.cmd

  For testing run mode
  -run size

 */