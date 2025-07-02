package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.util.Iso8601;

import java.util.Date;

/**
 * A set of commands for debugging the {@link CLIDriver} only.
 */
public class TestCommands extends CommonCommands2{
    public TestCommands(CLIDriver driver) {
        super(driver);
    }

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

    @Override
    public void about(boolean showBanner, boolean showHeader) {
        say("Test Commands");
    }

    @Override
    public void initialize() throws Throwable {

    }

    @Override
    public void load(InputLine inputLine) throws Throwable {

    }

    public void size(InputLine inputLine) throws Exception{
        say("42");
    }
    public void time(InputLine inputLine) throws Exception{
        say(Iso8601.date2String(new Date()));
    }

    public static void main(String[] args) {
        CLIDriver cli = new CLIDriver();
        InputLine inputLine = cli.bootstrap(args);
        try {
            TestCommands testCommands =new TestCommands(cli);
            cli.addCommands(testCommands);
            testCommands.bootstrap(inputLine);
            cli.start();
        }catch(Throwable t) {
            t.printStackTrace();
        }
    }
}
/*
  For batch file testing
  -in /home/ncsa/dev/ncsa-git/security-lib/util/src/main/resources/cli-test/test0.cmd

  For testing run mode
  -run size

 */