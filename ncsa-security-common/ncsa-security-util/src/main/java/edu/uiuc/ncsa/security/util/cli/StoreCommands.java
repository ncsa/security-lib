package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
     * Constructor that sets the indent level for this command processor. Every bit of
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
        return getName() + ">";
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    Store store;


    protected void showUpdateHelp() {
        say("update index");
        sayi("Use: Updates an entry, that is to say, allows you to edit the values stored for an entry");
    }


    // Note to self -- cannot just have these here because all of the machinery to do this to and from XML resides in another
    // module and that would create a circular set of dependencies. This is the reason there is the subclass StoreCommands2
    // in OA4MP. Best we can do with how Java works until we have a large-scale restructuring of the packages, which we
    // don't want to do.

    public abstract void serialize(InputLine inputLine);

    public abstract void deserialize(InputLine inputLine);

    public abstract void search(InputLine inputLine);

    public abstract void edit(InputLine inputLine);

    public void update(InputLine inputLine) throws IOException {
        if (showHelp(inputLine)) {
            showUpdateHelp();
            return;
        }

        Identifiable identifiable = findItem(inputLine);
        if (identifiable == null) {
            say("You must supply the index or id of the item to update");
            return;
        }
        for (int i = 0; i < inputLine.size(); i++) {
            // now that people can specify keys and values, there are types. if there
            // is a type, don't make the user jump through every property of their object.
            if (inputLine.getArg(i).startsWith("-")) {
                say("Unknown command line switch. Aborting.");
                return;
            }
        }
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

    public Identifiable createNew() {
        return this.getStore().create();
    }

    /**
     * This is the workhorse method for the object that lets you edit the values.
     * Generally this should do validation and checking so that updates to the store
     * are not garbage.
     *
     * @param identifiable
     * @return returns true if the passed object needs to be saved, false otherwise.
     */
    public abstract boolean update(Identifiable identifiable) throws IOException;

    /**
     * This is a hook for extensions so they don't have to completely
     * rewrite complex {@link #update(edu.uiuc.ncsa.security.core.Identifiable)} methods.
     * It will be invoked before
     * update displays the completed item and saves it, allowing any properties not in the base class
     * to be queried and saved.
     *
     * @param identifiable
     */
    public abstract void extraUpdates(Identifiable identifiable) throws IOException;

    /**
     * In listing operations, take the {@link Identifiable} argument and make a string version that a
     * user can understand
     *
     * @param identifiable
     */
    protected abstract String format(Identifiable identifiable);

    /**
     * Give a long (multi-line) formatted object. This should allow users to see everything cleanly.
     * This assumes the long format, not the verbose
     *
     * @param identifiable
     * @return the width of the left field when formatting (for consistent look and feel in overrides).
     */
    protected abstract int longFormat(Identifiable identifiable);

    /**
     * Long formatting with the switch for verbose or not. If false, that means use the long format
     *
     * @param identifiable
     * @param isVerbose
     * @return the width of the left field when formatting (for consistent look and feel in overrides).
     */

    protected abstract int longFormat(Identifiable identifiable, boolean isVerbose);

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
        // There WAS a fix for Fix for OAUTH-119, skipping null identifiers, but the store should
        // now take care of this edge case. I am keeping the JIRA issue number here for future reference.

        allEntries = getStore().getAll();

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
                say("----");
            } else {
                say((i++) + ". " + format(x));
            }
        }
        return allEntries;
    }

    protected void showCreateHelp() {
        say("create [id]\n");
        sayi("Usage: Create a new entry in the currently active store.");
        sayi("where the id is a unique uri which will identify the object. (Note that there is not a lead slash! ");
        sayi("Just enter the identifier if you want to set it. Be sure it is a valid uri.) If you do not specify an");
        sayi("identifier, you will be prompted for one. You may also elect to have a new, random one created and assigned.");
    }


    /**
     * Creates a new item. The argument is
     *
     * @param inputLine
     */
    public void create(InputLine inputLine) throws IOException {
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
            //sayi2("edit [y/n]?");
            if (isOk(readline("edit [y/n]?"))) {
                update(x);
                info("updated object with id " + x.getIdentifierString());
            }
            getStore().save(x);
            clearEntries();
        }
    }

    protected void create() throws IOException {
        boolean tryAgain = true;
        Identifier id = null;
        Identifiable c = null;
        while (tryAgain) {
            //  sayi2("enter the id of the object you want to create or return for a random one >");
            String inLine = readline("enter the id of the object you want to create or return for a random one >");
            if (!isEmpty(inLine)) {
                try {
                    id = BasicIdentifier.newID(inLine);
                    tryAgain = false;
                } catch (Throwable t) {
                    //  sayi2("That is not a valid uri. Try again (y/n)? ");
                    tryAgain = isOk(readline("That is not a valid uri. Try again (y/n)? "));
                }
            } else {
                tryAgain = false;
            }
        } // end input loop.
        c = createNew();

        if (id != null) {
            if (getStore().containsKey(id)) {
                throw new IllegalArgumentException("Error: the identifier \"" + id + "\" is already in use.");
            }
            c.setIdentifier(id);
        }
        getStore().save(c);
        info("Created object " + c.getClass().getSimpleName() + " with identifier " + c.getIdentifierString());
        sayi("Object created with identifier " + c.getIdentifierString());
        //sayi2("edit [y/n]?");
        if (isOk(readline("edit [y/n]?"))) {
            update(c);
            info("Updated object " + c.getClass().getSimpleName() + " with identifier " + c.getIdentifierString());

        }
        getStore().save(c);
        clearEntries();
    }

    protected void showRMHelp() {
        say("rm [index|uid]");
        sayi("Usage: Remove an item from the store. ");
        sayi("where index is the index number from the list (ls) command or the unique identifier for the item, preceeded");
        sayi("by a forward slash\n");
        say("Examples\n");
        sayi("rm 12\n");
        sayi("This removes item 12 on the list\n");
        sayi("rm /http://cilogon.org/serverT/uid/123\n");
        sayi("This removes the item with the given unique identifier from the current store, regardless of its position in the list command");
    }

    Identifier id = null;

    public void clear_id(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showClearIDHelp();
            return;
        }
        id = null;
    }

    private void showClearIDHelp() {
        say("clear_id");
        sayi("Usage: Clears the current defualt identifier.");
        say("See also: set_id, get_id");
    }

    public void set_id(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showSetIDHElp();
            return;
        }
        String rawID = inputLine.getLastArg();
        if (rawID.equals("set_id")) {
            // they forgot an argument
            say("sorry, but you have to supply an id to set.");
            return;
        }
        int index = -1;
        try {
            index = Integer.parseInt(rawID);
        } catch (NumberFormatException nfx) {
            // alles ok...
        }
        if (index == -1 && !rawID.startsWith("/")) {
            inputLine.setLastArg("/" + rawID);
        }
        Identifiable thingy = findItem(inputLine);
        if (thingy != null) {
            rawID = thingy.getIdentifierString();
        } else {
            rawID = inputLine.getLastArg();
            if (rawID.startsWith("/")) {
                rawID = rawID.substring(1);
            }
        }
        id = BasicIdentifier.newID(rawID);
        if (id == null) {
            say("warning: no identifier set");
            return;
        }
        say("Identifier set to " + id);
    }

    private void showSetIDHElp() {
        say("set_id [/]id | index = sets the current identifier.  All subsequent operations will ");
        say("use this identifier unless it is cleared or you explicitly override it.");
        say("The arguments may be the id, the escaped version (commonly used elsewhere, starts with \"/\") or the index.");
        say("The result will be the actual id of the object.");
        say("E.g.");
        say("set_id 3 --  sets the id to the 3rd object in the most recent ls command");
        say("set_id /foo:bar -- sets the id to foo:bar");
        say("set_id foo:bar -- sets the id to foo:bar");
        say("See also: clear_id, get_id");
    }

    public void get_id(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showGetIDHelp();
            return;
        }
        if (id == null) {
            say("no id set");
            return;
        }
        say(id.toString());
    }

    private void showGetIDHelp() {
        say("get_id");
        sayi("Usage: Show the current id if any.");
        sayi("See also: clear_id, set_id");
    }

    /**
     * Resolves the first argument of a command line into either a unique identifier
     *
     * @param inputLine
     * @return
     */
    protected Identifiable findItem(InputLine inputLine) {
        // first case is one in which this does not apply since there is no argument.
        // Second case is to try and interpret the last argument as an index or id.
        // First look for overrides to local id

        Identifier localID = null;
        int index = -1;
        if (inputLine.getLastArg().startsWith("/")) {
            localID = BasicIdentifier.newID(inputLine.getLastArg().substring(1));
        } else {
            try {
                index = Integer.parseInt(inputLine.getLastArg());
            } catch (Throwable t) {
                // rock on
            }
        }
        if (localID == null) {
            if (index != -1) {
                if (allEntries == null || allEntries.isEmpty()) {
                    loadAllEntries(); // just in case...
                }
                return allEntries.get(index);
            }
        } else {
            return (Identifiable) getStore().get(localID);
        }

        if (hasId()) {
            return (Identifiable) getStore().get(id);
        }

/*
        if (inputLine.size() <= 1) {
            return null;
        }
*/


        return null;
    }

    public void rm(InputLine inputLine) throws IOException {
        if (showHelp(inputLine)) {
            showRMHelp();
            return;
        }
        Identifiable x = findItem(inputLine);
        //"Are you sure you want to remove this client(y/n)[n]:"
        if (!"y".equals(getInput("Are you sure you want to remove this object(y/n)", "n"))) {
            say("remove aborted.");
            return;
        }

        getStore().remove(x.getIdentifier());
        if (x.getIdentifier().equals(id)) {
            id = null;
        }
        say("Done. object with id = " + x.getIdentifierString() + " has been removed from the store.");
        info("Removed object " + x.getClass().getSimpleName() + " with id " + x.getIdentifierString());
        clearEntries();
    }


    protected void showLSHelp() {
        say("ls [flags] [number]");
        sayi("Usage: Show information about an object or objects.");
        sayi("flags are");
        sayi(StringUtils.RJustify(LINE_LIST_COMMAND, 4) + " = " + "line list of an object or all objects. Longer entries will be truncated.");
        sayi(StringUtils.RJustify(ALL_LIST_COMMAND, 4) + " = " + " list of **every** entry in the store. You have been warned.");
        sayi(StringUtils.RJustify(VERBOSE_COMMAND, 4) + " = " + "verbose list. All entries will be shown in their entirety.");
        sayi("If you have listed all objects you may use the index number as the argument. Or you can supply");
        sayi("the identifier escaped with a /");
        say("E.g.");
        sayi("ls " + LINE_LIST_COMMAND + " " + ALL_LIST_COMMAND + " = line listing of entire store. This may be huge.");
        sayi("ls " + LINE_LIST_COMMAND + " = line list of the currently active object.");
        sayi("ls " + ALL_LIST_COMMAND + "  = short list of the entire store.");
        sayi("ls " + LINE_LIST_COMMAND + " 4 = line list of the 4th item from the ls -a command");
        sayi("ls " + LINE_LIST_COMMAND + " /foo:bar = line list of the object with identifier foo:bar");
        sayi("ls " + LINE_LIST_COMMAND + " foo:bar = line list of the object with identifier foo:bar");
        sayi("ls " + VERBOSE_COMMAND + " foo:bar = verbose list of the object with identifier foo:bar");
    }

    protected final String LINE_LIST_COMMAND = "-l";
    protected final String ALL_LIST_COMMAND = "-E";
    protected final String VERBOSE_COMMAND = "-v";

    protected boolean hasId() {
        return id != null;
    }

    public void ls(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showLSHelp();
            return;
        }
        // Any form of the all flag prints everything.
        if (inputLine.hasArg(ALL_LIST_COMMAND)) {
            if (inputLine.hasArg(LINE_LIST_COMMAND)) {
                listAll(true, "");
            } else {
                listAll(false, "");
            }
            return;
        }

        boolean isVerbose = inputLine.hasArg(VERBOSE_COMMAND);
        if (hasId()) {
            Identifiable identifiable = findItem(inputLine);
            if (identifiable == null) {
                say("Object not found.");
                return;
            }
            if (inputLine.hasArg(LINE_LIST_COMMAND)) {
                longFormat(identifiable, false);
            } else {
                if (isVerbose) {
                    longFormat(identifiable, true);

                } else {
                    say(format(identifiable));
                }
            }
            return;
        }

        // No id set.

        if (1 == inputLine.size()) {
            // No id set , no args lists everything in short form
            listAll(false, "");
            return;
        }


        if (inputLine.getLastArg().startsWith("-")) {
            // only possible case is if(in
            if (inputLine.getLastArg().equalsIgnoreCase(LINE_LIST_COMMAND)) {
                say("No id set. Please specify one or add the -a flag.");
            }
            return;
        } else {
            Identifiable identifiable = findItem(inputLine);
            if (identifiable == null) {
                say("Sorry, object not found.");
                return;
            }
            if (inputLine.hasArg(LINE_LIST_COMMAND)) {
                longFormat(identifiable, false);
            } else {
                if (isVerbose) {
                    longFormat(identifiable, true);

                } else {
                    say(format(identifiable));
                }
            }
        }

    }
    protected static String SIZE_ALL_FLAG = "-all";
    protected static String SIZE_VERSIONS_FLAG = "-versions";
    protected void showSizeHelp() {
        say("size [" + SIZE_ALL_FLAG + " | " + SIZE_VERSIONS_FLAG + "]");
        sayi("Usage: Prints out the number of  entries in the store ");
        sayi("This by default does not count versions.");
        sayi(SIZE_ALL_FLAG + " = include a count of everything, including versions");
        sayi(SIZE_VERSIONS_FLAG + " = include a count of only versions.");
    }

    public void size(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showSizeHelp();
            return;
        }
        if (inputLine.hasArg(SIZE_ALL_FLAG)) {
            sayi("Current store has " + getStore().size(true) + " entries");
            return;
        }
        if (inputLine.hasArg(SIZE_VERSIONS_FLAG)) {
            sayi("Current store has " + (getStore().size(true) - getStore().size(false)) + " versions.");
            return;
        }

        sayi("Current store has " + getStore().size(false) + " entries (excluding versions).");
    }


    @Override
    public void print_help(InputLine inputLine) throws Exception {
        super.print_help(inputLine);
        say("--Serialization commands: Reading and writing objects.");
        sayi("deserialize = read the object from a file");
        sayi("serialize = write an object to a file");
        say("--id commands:");
        sayi("clear_id = clear the current id");
        sayi("get_id = print the current id");
        sayi("set_id = set the current id");
        say("--Object commands:");
        sayi("create = create a new object");
        sayi("edit = edit and object");
        sayi("list_keys = list the properties aka keys for objects of this typer");
        sayi("ls = list properties in an object, an object or objects.");
        sayi("rm = remove and object or properties in an object");
        sayi("size = the number of objects.");
        sayi("update = update (change) a property or all properties for an object.");
    }
}

