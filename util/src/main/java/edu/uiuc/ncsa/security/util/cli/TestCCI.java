package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.configuration.XMLConfigUtil;
import edu.uiuc.ncsa.security.util.testing.TestConfigLoader;
import edu.uiuc.ncsa.security.util.testing.TestEnvironment;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.HashMap;

public class TestCCI extends ConfigurableCommandsImpl2 {
    public TestCCI(CLIDriver driver) {
        super(driver);
    }

    @Override
    public String getComponentName() {
        return TestConfigLoader.TEST_TAG;
    }

    public TestEnvironment getTestEnvironment() throws Exception {
        return (TestEnvironment) getEnvironment();
    }


    TCL loader = null;

    @Override
    public TCL getLoader() {
        if (loader == null) {
            try {
                loader = figureOutLoader(getConfigFile(), getConfigName());
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return loader;
    }

    public static class TCL extends TestConfigLoader<TestEnvironment> {
        public TCL(ConfigurationNode node) {
            super(node);
        }

        public TCL(ConfigurationNode node, MyLoggingFacade logger) {
            super(node, logger);
        }

        @Override
        public String getVersionString() {
            return "1.0";
        }

        @Override
        public HashMap<String, String> getConstants() {
            throw new NotImplementedException("getConstants not implemented");
        }
    }

    @Override
    public void load(InputLine inputLine) throws Throwable {
        super.load(inputLine);
        getEnvironment(); // loads it and sets it.
    //    setTestEnvironment(getLoader().load());
    }

    @Override
    public void setLoader(ConfigurationLoader<? extends AbstractEnvironment> loader) {
        this.loader = (TCL) loader;
    }

    @Override
    protected TCL figureOutLoader(String fileName, String configName) throws Throwable {
        ConfigurationNode node =
                XMLConfigUtil.findConfiguration(fileName, configName, getComponentName());
        return new TCL(node);
    }

    @Override
    public void useHelp() {
        say("Choose the component you wish to use.");
        say("you specify the component as use + name. Supported components are");
        say("my_test - really simple  test component");
    }

    @Override
    public String getPrompt() {
        return getName() + ">";
    }

    @Override
    public String getName() {
        return "test_cli";
    }

    @Override
    public void about(boolean showBanner, boolean showHeader) {
        say("╔════════════╗");
        say("║  Test CLI  ║");
        say("╚════════════╝");

    }

    @Override
    public boolean use(InputLine inputLine) throws Throwable {

        CommonCommands2 commands = null;

        if (inputLine.hasArg("my_test")) {
            commands = new CCIComponent(getDriver());
        }
        if (commands != null) {
            return switchOrRun(inputLine, commands);
        }

        if (super.use(inputLine)) {
            return true;
        }
        say("could not find the component named \"" + inputLine.getArg(1) + "\". Type 'use --help' for help");

        return false;
    }

    public class CCIComponent extends CommonCommands2 {
        public CCIComponent(CLIDriver driver) {
            super(driver);
        }

        @Override
        public void about(boolean showBanner, boolean showHeader) {

        }

        @Override
        public void initialize() throws Throwable {

        }

        @Override
        public void load(InputLine inputLine) throws Throwable {

        }

        @Override
        public String getPrompt() {
            return getName() + ">";
        }

        @Override
        public String getName() {
            return "my_test";
        }

        public void get(InputLine inputLine) throws Throwable {
            if(showHelp(inputLine)){
                say("get name = show the value of an entry. To see entries, issue show_all");
                return;
            }
            if(inputLine.getArgCount() != 1){
                say("incorrect number of arguments");
                return;
            }
            String key = inputLine.getArg(1);
            if(getTestEnvironment().isEmpty() || !getTestEnvironment().getTestData().containsKey(key)){
                say("sorry but \"" + key + "\" is not a valid key");
                return;
            }
            say(getTestEnvironment().getTestData().get(key).toString());
        }
        public void set(InputLine inputLine) throws Throwable {
            if(showHelp(inputLine)){
                say("set key value - set a value for a given key.");
                return;
            }
            String key = inputLine.getArg(1);
            String value = inputLine.getArg(2);
            //getTestEnvironment().getTestMap().put(key, value);
        }
        public void show_all(InputLine inputLine) throws Exception {
            if (showHelp(inputLine)) {
                say("print the known variables in the environment");
                return;
            }
            if (getTestEnvironment() == null) {
                say("no environment set");
                return;
            }
            for (String key : getTestEnvironment().getTestData().keySet()) {
                say(key + ": " + getTestEnvironment().getTestData().get(key));
            }
        }
    }

    public static void main(String[] args) {
        try {
            CLIDriver driver = new CLIDriver();

            //InputLine inputLine = driver.bootstrap(args);
            TestCCI testCCI = new TestCCI(driver);
            driver.addCommands(testCCI); // zero-th is always the CCI
            testCCI.bootstrap(args);
            driver.start();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
