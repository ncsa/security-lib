package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This also has the machinery for parsing configurations since the user should be able
 * to load one from the command line.
 * <p>Created by Jeff Gaynor<br>
 * on 5/20/13 at  3:22 PM
 */
public abstract class StoreCommands extends CommonCommands {
    public void setSortable(Sortable sortable) {
        this.sortable = sortable;
    }

    Sortable sortable = null;

    protected Sortable getSortable() {
        return sortable;
    }

    /**
     * Constructor that sets the indent level for this command processor. Every bit ouf
     * output will be indented by this amount.
     *
     * @param defaultIndent
     * @param store
     */

    protected StoreCommands(MyLoggingFacade logger, String defaultIndent, Store store) {
        super(logger);
        this.defaultIndent = defaultIndent;
        this.store = store;
        setSortable(new BasicSorter());
    }

    public StoreCommands(MyLoggingFacade logger, Store store) {
        super(logger);
        setStore(store);
        setSortable(new BasicSorter());
    }

    // The name that will be shown in the prompt.
    public abstract String getName();


    @Override
    public String getPrompt() {
        return getName() + " >";
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    Store store;

    protected void showUpdateHelp() {
        say("Updates an entry, that is to say, allows you to edit the values stored for an entry");
        say("Syntax:\n");
        say("update index\n");
        say("where the index is the index in the list command.");

    }

    public void update(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showUpdateHelp();
            return;
        }
        if (inputLine.size() == 1) {
            say("You must supply the index or id of the item to update");
            return;
        }
        Identifiable identifiable = findItem(inputLine);
        if (identifiable != null) {
            // Note that the contract should be that a clone is passed in and that is saved if the user
            // decides to.
            Identifiable identifiable1 = identifiable.clone();
            if (update(identifiable1)) {
                getStore().save(identifiable1);
                clearEntries(); // CIL-240: Remove entries so the updated item is displayed.
            }
            return;
        }

        say("no object with that index or id found. Please try again");

    }

    /**
     * This is the workhorse method for the object that lets you edit the values.
     * Generally this should do validation and checking so that updates to the store
     * are not garbage.
     *
     * @param identifiable
     * @return returns true if the passed object needs to be saved, false otherwise.
     */
    public abstract boolean update(Identifiable identifiable);

    /**
     * This is a hook for extensions so they don't have to completely
     * rewrite complex {@link #update(edu.uiuc.ncsa.security.core.Identifiable)} methods.
     * It will be invoked before
     * update displays the completed item and saves it, allowing any properties not in the base class
     * to be queried and saved.
     *
     * @param identifiable
     */
    public abstract void extraUpdates(Identifiable identifiable);

    /**
     * In listing operations, take the {@link Identifiable} argument and make a string version that a
     * user can understand
     *
     * @param identifiable
     */
    protected abstract String format(Identifiable identifiable);

    /**
     * Give a long (multi-line) formatted object. This should allow users to see everything cleanly.
     *
     * @param identifiable
     * @return
     */
    protected abstract void longFormat(Identifiable identifiable);

    protected List<Identifiable> allEntries = null;

    /**
     * Tell if the user has run the listAll command.
     *
     * @return
     */
    protected boolean hasEntries() {
        return allEntries == null || allEntries.isEmpty();
    }

    protected List<Identifiable> loadAllEntries() {
        Set keys = getStore().keySet();
        allEntries = new LinkedList<Identifiable>();
        int i = 0;
        for (Object key : keys) {
            if (key == null) {
                // Fix for OAUTH-119.
                System.out.println("Warning, skipping null identifier. Cannot resolve object...");
            } else {
                Identifiable x = (Identifiable) getStore().get(key);
                allEntries.add(x);
            }

        }
        return allEntries;
    }

    /**
     * Clears the list of entries so next call will get it afresh
     */
    protected void clearEntries() {
        allEntries = new LinkedList<Identifiable>();
    }


    protected List<Identifiable> listAll(boolean useLongFormat, String otherFlags) {
        loadAllEntries();
        if (allEntries.isEmpty()) {
            say("(no entries found)");
            return allEntries;
        }

        int i = 0;
        getSortable().setState(otherFlags);
        allEntries = getSortable().sort(allEntries);
        for (Identifiable x : allEntries) {
            if (useLongFormat) {
                longFormat(x);
            } else {
                say((i++) + ". " + format(x));
            }
        }
        return allEntries;
    }

    protected void showCreateHelp() {
        say("Create a new entry in the currently active store.");
        say("Syntax is\n");
        say("create [/id]\n");
        say("where the id is a unique uri which will identify the object. If you do not specify an");
        say("identifier, you will be prompted for one. You may also elect to have a new, random one created and assigned.");
    }

    /**
     * Creates a new item. The argument is
     *
     * @param inputLine
     */
    public void create(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showCreateHelp();
            return;
        }
        Identifier id = null;
        if (1 == inputLine.size()) {
            create();
            clearEntries(); //make sure the internal list is now updated with this.
            return;
        }
        // for the case where an identifier is supplied explicitly.
        if (1 < inputLine.size()) {
            try {
                id = BasicIdentifier.newID(inputLine.getArg(1));
                if (getStore().containsKey(id)) {
                    throw new IllegalArgumentException("Error: The identifier \"" + id + "\" already has an entry in this store.");
                }

            } catch (Throwable t) {
                say("Identifier is not a valid URI, request rejected");
                return;
            }

            Identifiable x = getStore().create();
            x.setIdentifier(id);
            getStore().save(x);
            info("Created and saved " + x.getClass().getSimpleName() + " with id " + x.getIdentifierString());
            say("Created object with id \"" + x.getIdentifier() + "\"");
            sayi2("edit [y/n]?");
            if (isOk(readline())) {
                update(x);
                info("updated object with id " + x.getIdentifierString());
            }
            getStore().save(x);
            clearEntries();
        }
    }

    protected void create() {
        boolean tryAgain = true;
        Identifier id = null;
        Identifiable c = null;
        while (tryAgain) {
            sayi2("enter the id of the object you want to create or return for a random one >");
            String inLine = readline();
            if (!isEmpty(inLine)) {
                try {
                    id = BasicIdentifier.newID(inLine);
                    tryAgain = false;
                } catch (Throwable t) {
                    sayi2("That is not a valid uri. Try again (y/n)? ");
                    tryAgain = isOk(readline());
                }
            } else {
                tryAgain = false;
            }
        } // end input loop.
        c = getStore().create();

        if (id != null) {
            if (getStore().containsKey(id)) {
                throw new IllegalArgumentException("Error: the identifier \"" + id + "\" is already in use.");
            }
            c.setIdentifier(id);
        }
        getStore().save(c);
        info("Created object " + c.getClass().getSimpleName() + " with identifier " + c.getIdentifierString());
        sayi("Object created with identifier " + c.getIdentifierString());
        sayi2("edit [y/n]?");
        if (isOk(readline())) {
            update(c);
            info("Updated object " + c.getClass().getSimpleName() + " with identifier " + c.getIdentifierString());

        }
        getStore().save(c);
        clearEntries();
    }

    protected void showRMHelp() {
        say("Remove an item from the store. ");
        say("Syntax:\n");
        say("rm [index|uid]\n");
        say("where index is the index number from the list (ls) command or the unique identifier for the item, preceeded");
        say("by a forward slash\n");
        say("Examples\n");
        say("rm 12\n");
        say("This removes item 12 on the list\n");
        say("rm /http://cilogon.org/serverT/uid/123\n");
        say("This removes the item with the given unique identifier from the current store, regardless of its position in the list command");
    }

    /**
     * Resolves the first argument of a command line into either a unique identifier
     *
     * @param inputLine
     * @return
     */
    protected Identifiable findItem(InputLine inputLine) {
        // first case is one in which this does not apply since there is no argument.
        if (inputLine.size() <= 1) {
            return null;
        }
        if (inputLine.getLastArg().startsWith("/")) {
            // then try to process this as a unique identifier.
            String arg = inputLine.getLastArg().substring(1);
            Identifier id = BasicIdentifier.newID(arg);
            return (Identifiable) getStore().get(id);
        }
        int choice = inputLine.getIntArg(inputLine.size() - 1);
        if (allEntries == null || allEntries.isEmpty()) {
            loadAllEntries(); // just in case...
        }
        return allEntries.get(choice);
    }

    public void rm(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showRMHelp();
            return;
        }
        Identifiable x = findItem(inputLine);
        getStore().remove(x.getIdentifier());
        say("Done. object with id = " + x.getIdentifierString() + " has been removed from the store");
        info("Removed object " + x.getClass().getSimpleName() + " with id " + x.getIdentifierString());
        clearEntries();
    }


    protected void showLSHelp() {
        say("lists the current store or lists details about an entry. Each entry is given a number of calls to other");
        say("tools will use the most numbers from the most recent call to this.");
        say("Synatx:\n");
        say("ls [number]\n");
        say("If no number is supplied, then a complete list of all elements with numbering will be displayed.");
        say("If a number has been supplied, a detailed report on that item is shown.");
    }

    public void ls(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showLSHelp();
            return;
        }
        if (1 == inputLine.size()) {
            // lists everything.
            listAll(false, "");
            return;
        }
        //
        // Size of input line includes the command itself as argument 0. This tell it to ignore all
        // command line switches and just print out the long form of a single entry.
        if (!inputLine.getLastArg().startsWith("-")) {
            Identifiable identifiable = findItem(inputLine);
            if (identifiable == null) {
                say("Sorry, object not found.");
                return;
            }
            longFormat(identifiable);
            return;
        }

        boolean longForm = false;
        if (inputLine.getArg(1).contains("l")) longForm = true;

        if (longForm) {
            listAll(true, inputLine.getArg(1));
        } else {
            listAll(false, inputLine.getArg(1));
        }
        return;
    }

    protected void showSizeHelp() {
        sayi("Prints out the number of elements in the current store.");
    }

    public void size(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showSizeHelp();
            return;
        }
        sayi("Current store has " + getStore().size() + " entries");
    }


}

