package edu.uiuc.ncsa.sat.admin;

import edu.uiuc.ncsa.sat.SATEnvironment;
import edu.uiuc.ncsa.sat.client.ClientKeys;
import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.sat.loader.SATConfigurationLoader;
import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.cli.StoreCommands;
import edu.uiuc.ncsa.security.util.cli.CLIDriver;
import edu.uiuc.ncsa.security.util.cli.InputLine;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import edu.uiuc.ncsa.security.util.crypto.KeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Vector;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  2:48 PM
 */
public class SATCommands extends StoreCommands {
    public SATEnvironment getSatEnvironment() {
        return satEnvironment;
    }

    public void setSatEnvironment(SATEnvironment satEnvironment) {
        this.satEnvironment = satEnvironment;
    }

    SATEnvironment satEnvironment;


    protected SATCommands(MyLoggingFacade logger, SATEnvironment satEnvironment) {
        super(logger == null ? satEnvironment.getMyLogger() : logger);
        this.satEnvironment = satEnvironment;
        setStore(satEnvironment.getClientStore());
    }

    @Override
    public String getPrompt() {
        return "sat>";
    }

    public SATCommands(MyLoggingFacade logger, Store store) {
        super(logger, store);
    }

    @Override
    public String getName() {
        return "sat";
    }


    @Override
    public void extraUpdates(Identifiable identifiable) throws IOException {
        ClientKeys keys = (ClientKeys) getMapConverter().getKeys();
        SATClient satClient = (SATClient) identifiable;
        String newName = getInput("name of this client?", satClient.getName());
        if (!StringUtils.isTrivial(newName)) {
            satClient.setName(newName);
        }
        if (satClient.getPublicKey() == null) {
            if (isOk(getInput("create new keys?", "n"))) {
                KeyPair keyPair = KeyUtil.generateKeyPair();
                say(KeyUtil.toPKCS1PEM(keyPair.getPrivate()));
                satClient.setPublicKey(keyPair.getPublic());
            } else {
                if (isOk(getInput("specify PKCS 8 file (y/n)?", "n"))) {
                    String f = getInput("enter file path", "");
                    if (StringUtils.isTrivial(f)) {
                        say("no file entered, aborted.");
                    } else {
                        try {
                            satClient.setPublicKey(KeyUtil.fromX509PEM(new FileReader(f)));
                        } catch (Exception e) {
                            say("uh-oh, that didn't work:" + e.getMessage());
                            if (isVerbose()) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        if (isOk(getInput("Enter a new public key? (y/n)", "y"))) {
            String oldKey = null;
            if (satClient.getPublicKey() == null) {
                oldKey = null;
            } else {
                oldKey = KeyUtil.toX509PEM(satClient.getPublicKey());
            }
            String newKey = multiLineInput(oldKey, keys.publicKey());
            if (newKey == null) {
                // no changes
            } else {
                if (newKey.length() == 1) {
                    // clear it
                    satClient.setPublicKey(null);
                } else {
                    if (-1 == newKey.indexOf("{")) {
                        satClient.setPublicKey(KeyUtil.fromX509PEM(newKey));
                    } else {
                        // JWK
                        try {
                            satClient.setPublicKey(JSONWebKeyUtil.getJsonWebKey(newKey).publicKey);
                        } catch (Throwable t) {
                            say("Could not process public key:" + t.getMessage());
                        }
                    }
                }
            }
        }

/*  At this point we don't use this field.r
        if(isOK(getInput("Enter a configuration? (y/n)", ""))){

        }
*/


    }


    @Override
    protected String format(Identifiable identifiable) {
        SATClient client = (SATClient) identifiable;
        String rc = "";

        String name = (client.getName() == null ? "no name" : client.getName());
        name = StringUtils.pad2(name, 20);
        rc = rc + name + " | " + StringUtils.pad2(identifiable.getIdentifierString(), 60);
        rc = rc + " | created on " + Iso8601.date2String(client.getCreationTS());
        return rc;
    }


    public static String CONFIG_NAME_FLAG = "-name";
    public static String CONFIG_FILE_FLAG = "-cfg";
    public static String CONFIG_TAG_NAME = "sat";

    public static void main(String[] args) {
        Vector<String> vector = new Vector<>();
        vector.add("dummy"); // Dummy zero-th arg.
        for (String arg : args) {
            vector.add(arg);
        }
        InputLine inputLine = new InputLine(vector); // now we have a bunch of utilities for this

        String cfgname = inputLine.hasArg(CONFIG_NAME_FLAG) ? inputLine.getNextArgFor(CONFIG_NAME_FLAG) : "default";
//      Old style -- single inheritance
        ConfigurationNode node = ConfigUtil.findConfiguration(
                inputLine.getNextArgFor(CONFIG_FILE_FLAG),
                cfgname, CONFIG_TAG_NAME);

        // New style -- multi-inheritance.
        //     ConfigurationNode node = ConfigUtil.findMultiNode(inputLine.getNextArgFor(CONFIG_FILE_FLAG), cfgname, CONFIG_TAG_NAME );
        SATConfigurationLoader loader = new SATConfigurationLoader(node);

        SATEnvironment satEnvironment1 = loader.load();
        SATCommands SATCommands = new SATCommands(null, satEnvironment1);
        CLIDriver driver = new CLIDriver(SATCommands);

        driver.start();
    }
}
