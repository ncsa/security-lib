package edu.uiuc.ncsa.security.storage.cli;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.util.cli.CLITool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Set;

/**
 * A storage utility that will allow for server administrators to do basic store operations
 * such as delete, read, create or update from the command line. This follows the general pattern
 * of listing all the elements and having the admin select the one to operate on.
 * <p>Created by Jeff Gaynor<br>
 * on 5/15/13 at  11:51 AM
 */
public abstract class StoreUtil extends CLITool {
    public static final int ACTION_CREATE = 100;
    public static final int ACTION_READ = 200;
    public static final int ACTION_UPDATE = 300;
    public static final int ACTION_DELETE = 400;
    public static final int ACTION_EXIT = 0;
    public static final int ACTION_UNKNOWN = -1; // if not recognized command

    public static final String ACTION_KEY_CREATE = "C"; // for create
    public static final String ACTION_KEY_READ = "L"; // for list
    public static final String ACTION_KEY_UPDATE = "U"; // for update
    public static final String ACTION_KEY_DELETE = "D"; // for delete
    public static final String ACTION_KEY_EXIT = "X"; // for ending this program

    /**
     * Get the store this operates on.
     *
     * @return
     */
    protected abstract Store getStore() throws Exception;

    @Override
    public void doIt() throws Exception {
        boolean keepAsking = true;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String inString = null;
        // main event loop
        while (keepAsking) {
            say("Enter the operation you want," +
                    " create(" + ACTION_KEY_CREATE +
                    "), list(" + ACTION_KEY_READ +
                    "), update(" + ACTION_KEY_UPDATE +
                    ") delete(" + ACTION_KEY_DELETE +
                    ") or exit(" + ACTION_KEY_EXIT + ")");
            inString = readline();
            switch (getAction(inString)) {
                case ACTION_CREATE:
                    create();
                    break;
                case ACTION_READ:
                    show();
                    break;
                case ACTION_UPDATE:
                    update();
                    break;
                case ACTION_DELETE:
                    delete();
                    break;
                case ACTION_EXIT:
                    say("exiting...");
                    keepAsking = false;
                    break;
                case ACTION_UNKNOWN:
                    say("Unknown command, try again");
            }//end switch
        }//end main event loop

    }

    protected void show() throws Exception {
        say("Listing entries, hit " + ACTION_KEY_EXIT + " to return to main menu, " + ACTION_KEY_READ + " to show entries again.");
        boolean keepAsking = true;
        boolean listEntries = true;
        LinkedList<Identifiable> linkedList = new LinkedList<Identifiable>();
        while (keepAsking) {
            if (listEntries) {
                linkedList = listAll();
                listEntries = false;
            }
            if (linkedList.isEmpty()) {
                keepAsking = false;
                continue;
            }
            String inLine = readline();
            if (inLine != null && inLine.toUpperCase().equals(ACTION_KEY_EXIT)) {
                keepAsking = false;
                say("returning to main menu");
                continue;
            }
            int choice = -1;
            try {
                choice = Integer.parseInt(inLine);
            } catch (NumberFormatException nfx) {
                // do nothing
            }
            if (choice == -1) {

                switch (getAction(inLine)) {
                    case ACTION_READ:
                        listEntries = true;
                        break;
                    case ACTION_EXIT:
                        keepAsking = false;
                        break;
                    default:
                        say("unknown option, try again (might need to exit this submenu first)");
                }

            } else {
                say(linkedList.get(choice).toString());
            }
        }//end event loop
    }

    protected LinkedList<Identifiable> listAll() throws Exception {
        say("select the number of the item below:");
        Set keys = getStore().keySet();
        LinkedList<Identifiable> linkedList = new LinkedList<Identifiable>();
        int i = 0;
        for (Object key : keys) {
            Identifiable x = (Identifiable) getStore().get(key);
            linkedList.add(x);
            say((i++) + "." + x.getIdentifierString());
        }

        if (linkedList.isEmpty()) {
            say("(no entries found)");
        }
        return linkedList;
    }

    protected void delete() throws Exception {
        LinkedList<Identifiable> linkedList = listAll();
        String inLine = readline();
        int choice = -1;
        try {
            choice = Integer.parseInt(inLine);
        } catch (NumberFormatException nfx) {
        }
        if (choice < 0  || linkedList.size() <= choice) {
            say("unknown choice");
            return;
        }
        Identifiable x = linkedList.get(choice);
        getStore().remove(x.getIdentifier());
        say("Done. object with id = " + x.getIdentifierString() + " has been removed from the store");
    }

    public abstract void update() throws Exception;

    public abstract void create() throws Exception;

    @Override
    public void help() {
        say("A command line tool to manage stores");
        say("usage: " + getClass().getSimpleName() + " options");
        defaultHelp(true);
        say("Where the options are given as -x (fnord) = short option, (long option), and [] = optional. Other options: ");
        say("  [-" + CONFIG_NAME_OPTION + " (-" + CONFIG_NAME_LONG_OPTION + ") -- set the name of the configuration.]");
        say("If the configuration name is omitted, it is assumed there is exactly one in the given file and that is to be used.");
    }

    protected int getAction(String input) {
        if (input.toUpperCase().equals(ACTION_KEY_CREATE)) return ACTION_CREATE;
        if (input.toUpperCase().equals(ACTION_KEY_DELETE)) return ACTION_DELETE;
        if (input.toUpperCase().equals(ACTION_KEY_READ)) return ACTION_READ;
        if (input.toUpperCase().equals(ACTION_KEY_UPDATE)) return ACTION_UPDATE;
        if (input.toUpperCase().equals(ACTION_KEY_EXIT)) return ACTION_EXIT;
        return ACTION_UNKNOWN;
    }
}
