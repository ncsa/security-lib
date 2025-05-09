package edu.uiuc.ncsa.security.storage.cli;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.exceptions.ObjectNotFoundException;
import edu.uiuc.ncsa.security.core.util.*;
import edu.uiuc.ncsa.security.storage.MonitoredStoreInterface;
import edu.uiuc.ncsa.security.storage.XMLMap;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.data.SerializationKeys;
import edu.uiuc.ncsa.security.storage.monitored.MonitoredKeys;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepResponse;
import edu.uiuc.ncsa.security.storage.sql.SQLStore;
import edu.uiuc.ncsa.security.storage.sql.internals.ColumnMap;
import edu.uiuc.ncsa.security.util.cli.*;
import edu.uiuc.ncsa.security.util.cli.editing.EditorEntry;
import edu.uiuc.ncsa.security.util.cli.editing.EditorUtils;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static edu.uiuc.ncsa.security.core.util.StringUtils.*;
import static edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfigUtils.processUpkeep;
import static edu.uiuc.ncsa.security.util.cli.CLIDriver.*;

/**
 * This also has the machinery for parsing configurations since the user should be able
 * to load one from the command line.
 * <p>Created by Jeff Gaynor<br>
 * on 5/20/13 at  3:22 PM
 */
public abstract class StoreCommands extends CommonCommands {
    public StoreCommands(MyLoggingFacade logger) throws Throwable {
        super(logger);
    }

    public StoreCommands(AbstractEnvironment environment) throws Throwable {
        super(environment.getMyLogger());
        this.environment = environment;
    }

    public AbstractEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(AbstractEnvironment environment) {
        this.environment = environment;
    }

    AbstractEnvironment environment = null;

    public void setSortable(Sortable sortable) {
        this.sortable = sortable;
    }

    protected Sortable sortable = null;

    protected Sortable getSortable() {
        if (sortable == null) {
            sortable = new BasicSorter();
        }
        return sortable;
    }

    /**
     * Constructor that sets the indent level for this command processor. Every bit of
     * output will be indented by this amount.
     *
     * @param defaultIndent
     * @param store
     */

    protected StoreCommands(MyLoggingFacade logger, String defaultIndent, Store store) throws Throwable {
        this(logger, store);
        this.defaultIndent = defaultIndent;
        setSortable(new BasicSorter());
    }

    public StoreCommands(MyLoggingFacade logger, Store store) throws Throwable {
        this(logger);
        setStore(store);
        if (store instanceof MonitoredStoreInterface) {
            ((MonitoredStoreInterface) store).setMonitorEnabled(false); // do not fire monitor event in CLI!
        }
        setSortable(new BasicSorter());
    }

    public boolean isMonitored() {
        return (getStore() instanceof MonitoredStoreInterface);

    }

    UpkeepConfiguration upkeepConfiguration = null;

    UpkeepConfiguration getUpkeepConfiguration() {
        if (upkeepConfiguration == null) {
            if (isMonitored()) {
                upkeepConfiguration = ((MonitoredStoreInterface) getStore()).getUpkeepConfiguration();
            }
        }
        return upkeepConfiguration;
    }

    public static String UPKEEP_FLAG_TEST = "-test";
    public static String UPKEEP_FLAG_SHOW = "-show";
    public static String UPKEEP_FLAG_CFG = "-cfg";
    public static String UPKEEP_FLAG_RUN = "-run";
    public static String UPKEEP_FLAG_ENABLE = "-enable";

    protected void showUpkeepHelp() {
        say("upkeep ["
                + UPKEEP_FLAG_TEST + " (true | false) | "
                + UPKEEP_FLAG_SHOW + " | "
                + UPKEEP_FLAG_CFG + " path | "
                + UPKEEP_FLAG_RUN + " | "
                + UPKEEP_FLAG_ENABLE + " (true | false) ]" +
                " = do upkeep for this store"
        );
        int width = 8;

        say(StringUtils.RJustify(UPKEEP_FLAG_TEST, width) + " = true or false, all rules are tun in test mode only. This overrides individual rules settings.");
        say(StringUtils.RJustify(UPKEEP_FLAG_SHOW, width) + " = print the current configuration");
        say(StringUtils.RJustify(UPKEEP_FLAG_CFG, width) + " = the full path to an XML configuration. This is exactly the upkeep block for a store, nothing else.");
        say(StringUtils.RJustify(UPKEEP_FLAG_RUN, width) + " = flag, if present, run upkeep for thr current store with the current configuration. A report is printed");
        say(StringUtils.RJustify(UPKEEP_FLAG_ENABLE, width) + " = true or false, enable or disable the entire current configuration");
        say("Note that you cannot change the stored upkeep for the store, with this utility,");
        say("though you may load different one and " + UPKEEP_FLAG_RUN + "  it.");
        say();
        say("");
        /*
    UPKEEP_FLAG_TEST
    UPKEEP_FLAG_SHOW
    UPKEEP_FLAG_CFG
    UPKEEP_FLAG_RUN
    UPKEEP_FLAG_ENABLE
         */
    }

    public void upkeep(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            showUpkeepHelp();
            return;
        }
        if (!isMonitored()) {
            say("Unmonitored store -- upkeep not supported for this store");
            return;
        }
        MonitoredStoreInterface monitored = (MonitoredStoreInterface) getStore();
        UpkeepConfiguration upkeepConfiguration = monitored.getUpkeepConfiguration(); // may be null
        if (inputLine.hasArg(UPKEEP_FLAG_CFG)) {
            String fileName = inputLine.getNextArgFor(UPKEEP_FLAG_CFG);
            inputLine.removeSwitchAndValue(UPKEEP_FLAG_CFG);
            File file = new File(fileName);
            if (!file.exists()) {
                say("sorry but \"" + file.getAbsolutePath() + "\" does not exist.");
                return;
            }
            if (!file.isFile()) {
                say("sorry but \"" + file.getAbsolutePath() + "\" is not a file.");
                return;
            }
            try {
                XMLConfiguration xmlConfiguration = Configurations.getConfiguration(file);
                ConfigurationNode root = xmlConfiguration.getRoot();
                UpkeepConfiguration cfg = processUpkeep(root);
                upkeepConfiguration = cfg;
            } catch (Throwable t) {
                say("sorry but \"" + file.getAbsolutePath() + "\" could not be parsed:" + t.getMessage());
                return;
            }
        }
        boolean configurationCloned = false;
        if (inputLine.hasArg(UPKEEP_FLAG_TEST)) {
            String raw = inputLine.getNextArgFor(UPKEEP_FLAG_TEST);
            boolean testOnly = "true".equals(raw);
            if (!testOnly) {
                // spellcheck, basically. since if we only test fopr "true" and they mistype it
                // we don't want to toggle testing and have something run that should not.
                if (!"false".equals(raw)) {
                    say("unknown option \"" + raw + "\" for " + UPKEEP_FLAG_TEST);
                    return;
                }

            }
            inputLine.removeSwitchAndValue(UPKEEP_FLAG_TEST);
            if (upkeepConfiguration != null) {
                if (upkeepConfiguration.isTestOnly() != testOnly) {
                    // The user want to run this in test mode only. Clone it
                    upkeepConfiguration = cloneConfiguration(upkeepConfiguration);
                    upkeepConfiguration.setTestOnly(testOnly);
                    configurationCloned = true;
                }
            }
        }
        if (inputLine.hasArg(UPKEEP_FLAG_ENABLE)) {
            boolean enableTest = "true".equals(inputLine.getNextArgFor(UPKEEP_FLAG_ENABLE));
            inputLine.removeSwitchAndValue(UPKEEP_FLAG_ENABLE);
            if (upkeepConfiguration.isEnabled() != enableTest) {
                if (!configurationCloned) {
                    upkeepConfiguration = cloneConfiguration(upkeepConfiguration);
                    configurationCloned = true;
                }
                upkeepConfiguration.setEnabled(enableTest);
            }
        }

        if (inputLine.hasArg(UPKEEP_FLAG_SHOW)) {
            if (upkeepConfiguration == null) {
                say("no upkeep configuration");
                return;
            }
            say(upkeepConfiguration.toString(true));
            return;
        }

        if (inputLine.hasArg(UPKEEP_FLAG_RUN)) {
            if (getEnvironment() == null) {
                say("No runtime environment for this store has been set.");
                return;
            }
            UpkeepResponse response = ((MonitoredStoreInterface) getStore()).doUpkeep(upkeepConfiguration, getEnvironment());
            say(response.report(true));
        }
    }

    private static UpkeepConfiguration cloneConfiguration(UpkeepConfiguration upkeepConfiguration) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream
                = new ObjectOutputStream(baos);
        objectOutputStream.writeObject(upkeepConfiguration);
        objectOutputStream.flush();
        objectOutputStream.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream objectInputStream
                = new ObjectInputStream(bais);
        UpkeepConfiguration tempCfg = (UpkeepConfiguration) objectInputStream.readObject();
        tempCfg.setTestOnly(true);
        upkeepConfiguration = tempCfg;
        return upkeepConfiguration;
    }


    // The name that will be shown in the prompt.
    //public abstract String getName();


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
        String blanks = getBlanks(5);
        say("update [" + KEY_FLAG + " key | list ] [" + VALUE_FLAG + " value | " + FILE_FLAG + " file_path ]" +
                "[index] ");
        sayi("Usage: Update the properties of the object.");
        sayi(RJustify(KEY_FLAG, 8) + " - specify a single property for update");
        sayi(blanks + "You may use the " + KEY_SHORTHAND_PREFIX + " for a single property, but a list requires the flag");
        sayi(RJustify(VALUE_FLAG, 8) + " - specify a value or list of them for the propert(ies)");
        sayi(RJustify(FILE_FLAG, 8) + " - specify a file containing a *single* value for the propert(ies)");

        sayi("This has three modes. Just an id will prompt you for every value to update. This only works on");
        say("single objects and will be rejected for a set of them.");
        sayi("Alternately, you may either specify a single key + value OR you may specify an array of keys");
        sayi("of the form [key0,key1,...]. (The list_keys command will tell what the keys are.)");
        showKeyShorthandHelp();
        say("E.g.");
        sayi("update foo:bar");
        sayi("no arguments means to interactively ask for every attribute. foo:bar is the identifier for this object.");
        say("E.g. Update a property from a file");
        sayi("update >cfg -file /path/to/file /foo:bar");
        sayi("read the contents of the file (as a string) into the attribute. The shorthand for a single  ");
        sayi(blanks + "property was used here.");
        say("E.g. Mass update #1");
        sayi("update -key cfg -file /path/to/file my_result_set");
        sayi("read the contents of the file (as a string) into the attribute for every elements in the");
        sayi("result set named my_result_set.");
        say("E.g. Mass update #2, specify a file");
        sayi("update " + KEY_SHORTHAND_PREFIX + "cfg -file /path/to/file " + RS_RANGE_SHORT_KEY + " [2,5,6] my_result_set");
        sayi("Update items 2, 5 and 6 in the given result set.");
        say("E.g. Mass update #3, specify values");
        sayi("update -key [allow_qdl,allow_custom_ids] -value [true,false] " + RS_RANGE_SHORT_KEY + " [2,5,6] my_result_set");
        sayi("Update items 2, 5 and 6 in the given result set with the given values.");
        say("E.g. Mass update #4, entering values");
        sayi("update -key [allow_qdl,allow_custom_ids] " + RS_RANGE_SHORT_KEY + " [2,5,6] my_result_set");
        sayi("Update items 2, 5 and 6. Since no values are specified, you will be prompted for the first");
        say(blanks + "pass and these will be applied to all other objects.");

        say("E.g. Update with explicit value");
        sayi("update " + KEY_SHORTHAND_PREFIX + "name " + VALUE_FLAG + " \"My client\" foo:bar");
        sayi("This changes the value of the 'name' attribute to 'My client' for the object with id 'foo:bar'");
        sayi("Note that no prompting is done! The value will be updated.");
        say("E.g. Update selected proeprties, being prompted for each");
        sayi("update " + KEYS_FLAG + " [name,callback_uri] foo:bar");
        sayi("This would prompt to update the values for the 'name' and 'callback_uri' properties");
        sayi("of the object with id 'foo:bar'");
        sayi("A few notes.");
        sayi("1. If the value of the property is a JSON object, you can edit it.");
        sayi("2. If the value of the property is an array, then you may add a value, delete a value,");
        sayi("   replace the entire contents (new entries are comma separated, blanks removed) or simply clear the");
        sayi("   entire list of entries. You may also back out of the update request.");
        sayi("3. What you type in is stored without change, so if you need to save the hash of something,");
        sayi("    e.g. a password, use create_hash to make the hash first and save that.");
        sayi("See also: list_keys, create_hash");
    }


    protected void showSerializeHelp() {
        say("serialize  [-file path] index");
        sayi("Usage: XML serializes an object and either shows it on the ");
        sayi("   command line or put it in a file. Cf. deserialize.");
        printIndexHelp(true);
        sayi("See also: deserialize.");
    }

    public void serialize(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showSerializeHelp();
            return;
        }

        serialize(inputLine, findSingleton(inputLine));
    }

    protected void showDeserializeHelp() {
        say("deserialize  [-new] -file path [" + SHORT_UPDATE_FLAG + "|" + UPDATE_FLAG + "]");
        sayi("Usage: Deserializes an object into the current store overwriting the contents. Cf. serialize.");
        sayi("This replaces the object with the given index in the store.\n");
        sayi("If the -new flag is used, it is assumed that the object should be new. This means that if there is an existing object");
        sayi("with that identifier the operation will fail. If there is no identifier, one will be created.");
        sayi("Omitting the -new flag means that any object will be overwritten and if needed, a new identifier will be created");
        sayi("If the  " + UPDATE_FLAG + " or " + SHORT_UPDATE_FLAG + " is used, the existing object is simply updated");
        sayi("Note that an object cannot be new and updated at the same time.");
        say("See also: serialize, create_hash");
    }

    public void deserialize(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showDeserializeHelp();
            return;
        }
        InputStream is;
        boolean isNew = inputLine.hasArg("-new");
        boolean isUpdate = inputLine.hasArg(SHORT_UPDATE_FLAG) || inputLine.hasArg(UPDATE_FLAG);
        if (isNew && isUpdate) {
            say("Sorry. You have asked me to make a new item and update an existing one.");
            return;
        }
        if (!inputLine.hasArg(FILE_FLAG)) {
            say("Missing file argument. Cannot deserialize.");
            return;
        }
        try {
            is = new FileInputStream(inputLine.getNextArgFor(FILE_FLAG));

            XMLMap map = new XMLMap();
            map.fromXML(is);
            is.close();
            // x contains the object that is now of the correct type.
            Identifiable x = getStore().getXMLConverter().fromMap(map, null);
            if (isNew) {
                if (getStore().containsKey(x.getIdentifier())) {
                    say("Error! The object with identifier \"" + x.getIdentifierString() + "\" already exists and you specified the item was new. Aborting.");
                    return;
                }
                getStore().save(x);
                return;
            }
            if (isUpdate) {
                if (!getStore().containsKey(x.getIdentifier())) {
                    say("Error! The object with identifier \"" + x.getIdentifierString() + "\" does not exist and therefore cannot be updated.  Aborting.");
                    return;
                }
                // Get the current one
                Identifiable oldVersion = (Identifiable) getStore().get(x.getIdentifier());
                XMLMap oldValues = new XMLMap();
                getStore().getXMLConverter().toMap(oldVersion, oldValues);
                for (String key : map.keySet()) {
                    oldValues.put(key, map.get(key));

                }
                Identifiable updated = getStore().getXMLConverter().fromMap(oldValues, null);
                getStore().save(updated);

                return;
            }
            if (x.getIdentifier() == null) {
                //handles the case where this is new and needs an identifier created. Only way to get
                // a new unused identifier reliably is to have the store create a new entry then we update that.
                Identifiable c = getStore().create();
                x.setIdentifier(c.getIdentifier());
                say("Created identifier \"" + c.getIdentifierString() + "\".");
            }
            // second case, overwrite whatever.
            getStore().save(x);

            say("done!");
        } catch (Throwable e) {
            say("warning, load file at path \"" + inputLine.getNextArgFor(FILE_FLAG) + "\": " + e.getMessage());
        }
    }

    protected final String NEXT_N_COMMAND = "-n";
    public static final String SEARCH_REGEX_FLAG = "-regex";
    public static final String SEARCH_SHORT_REGEX_FLAG = "-r";
    public static final String SEARCH_SIZE_FLAG = "-size";
    public static final String SEARCH_DEBUG_FLAG = "-debug";
    public static final String SEARCH_VERSIONS_FLAG = "-versions";
    public static final String SEARCH_VERSIONS_ONLY_VALUE = "only";
    public static final String SEARCH_VERSIONS_TRUE_VALUE = "true";
    public static final String SEARCH_VERSIONS_FALSE_VALUE = "false";
    public static final String SEARCH_RETURNED_ATTRIBUTES_FLAG = "-attr";
    public static final String SEARCH_RESULT_SET_NAME = "-rs";
    /**
     * Used as a command line switch to name a result set.
     */
    public static final String RESULT_SET_KEY = "-rs";
    public static final String SEARCH_BEFORE_TS_FLAG = "-before";
    public static final String SEARCH_AFTER_TS_FLAG = "-after";
    public static final String SEARCH_DATE_FLAG = "-date";
    public static final String SEARCH_IS_NULL_FLAG = "-isNull";

    public HashMap<String, RSRecord> getResultSets() {
        return resultSets;
    }

    public void setResultSets(HashMap<String, RSRecord> resultSets) {
        this.resultSets = resultSets;
    }

    protected static HashMap<String, RSRecord> resultSets = new HashMap<>();
    String tzOffset = null;

    public void search(InputLine inputLine) {
        if (showHelp(inputLine)) {
            if (inputLine.hasArg("-ex")) {
                showSearchHelpExamples();
            }else{
                showSearchHelp();
            }
            return;
        } else {
            if (inputLine.hasArg("--ex")) {
                showSearchHelpExamples();
                return;
            }
        }
        Boolean isNullCase = null;
        boolean checkNullCase = inputLine.hasArg(SEARCH_IS_NULL_FLAG);
        if (checkNullCase) {
            isNullCase = inputLine.getBooleanNextArgFor(SEARCH_IS_NULL_FLAG);
            inputLine.removeSwitchAndValue(SEARCH_IS_NULL_FLAG);
            if (isNullCase == null) {
                say("unrecognized argument for " + SEARCH_IS_NULL_FLAG + ". aborting...");
                return;
            }
        }

        // Fix https://github.com/ncsa/security-lib/issues/48
        boolean hasVersions = inputLine.hasArg(SEARCH_VERSIONS_FLAG);
        boolean searchVersions = false;
        boolean searchForVersionsOnly = false;
        if (hasVersions) {
            switch (inputLine.getNextArgFor(SEARCH_VERSIONS_FLAG)) {
                case SEARCH_VERSIONS_FALSE_VALUE:
                    searchVersions = false;
                    break;
                case SEARCH_VERSIONS_TRUE_VALUE:
                    searchVersions = true;
                    break;
                case SEARCH_VERSIONS_ONLY_VALUE:
                    searchForVersionsOnly = true;
                    searchVersions = false;
                    break;
                default:
                    say("unknown " + SEARCH_VERSIONS_FLAG + " argument. aborting...");
                    return;
            }
            inputLine.removeSwitchAndValue(SEARCH_VERSIONS_FLAG);
        }

        int list_n = 10; // default
        boolean hasListN = inputLine.hasArg(NEXT_N_COMMAND);

        if (hasListN) {
            if (getStore() instanceof SQLStore) {
                SQLStore s = (SQLStore) getStore();
                if (s.getCreationTSField() == null) {
                    say("Sorry, but this is not supported for this type of store");
                    return;
                }

            } else {
                // Not supported (yet) for memory or file stores...
                say("Sorry, but this is not supported for this type of store");
                return;
            }
            try {
                list_n = inputLine.getNextIntArg(NEXT_N_COMMAND);
            } catch (ArgumentNotFoundException t) {

            }

        }
        boolean showStackTraces = inputLine.hasArg(SEARCH_DEBUG_FLAG);
        boolean storeRS = inputLine.hasArg(SEARCH_RESULT_SET_NAME);
        String rsName = null;
        if (storeRS) {
            rsName = inputLine.getNextArgFor(SEARCH_RESULT_SET_NAME);
            inputLine.removeSwitchAndValue(SEARCH_RESULT_SET_NAME);
        }
        boolean hasDate = inputLine.hasArg(SEARCH_DATE_FLAG);
        String dateField = null;
        Date afterDate = null;
        Date beforeDate = null;
        if (hasDate) {
            dateField = inputLine.getNextArgFor(SEARCH_DATE_FLAG);
            inputLine.removeSwitchAndValue(SEARCH_DATE_FLAG);
            if(!getKeys().allKeys().contains(dateField)) {
                say("The date field \"" + dateField + "\" does not exist.");
                return;
            }
            try {
                if (inputLine.hasArg(SEARCH_BEFORE_TS_FLAG)) {
                    beforeDate = getDateFromArg(inputLine, SEARCH_BEFORE_TS_FLAG);
                }
                if (inputLine.hasArg(SEARCH_AFTER_TS_FLAG)) {
                    afterDate = getDateFromArg(inputLine, SEARCH_AFTER_TS_FLAG);
                }
            } catch (ParseException pe) {
                say("Sorry, could not parse date: " + pe.getMessage());
                if (DebugUtil.isEnabled() || showStackTraces) {
                    pe.printStackTrace();
                }
                return;
            }

        }
        String key = getAndCheckKeyArg(inputLine);
        List<Identifiable> values = null;
        List<String> returnedAttributes = null;
        String condition = null;
        boolean isRegEx = false;
        boolean hasKey = key != null;
        if (inputLine.hasArg(SEARCH_RETURNED_ATTRIBUTES_FLAG)) {
            returnedAttributes = inputLine.getArgList(SEARCH_RETURNED_ATTRIBUTES_FLAG);
            inputLine.removeSwitchAndValue(SEARCH_RETURNED_ATTRIBUTES_FLAG);
        }
        if (hasKey) {

            condition = inputLine.getLastArg(); // default
            isRegEx = inputLine.hasArg(SEARCH_REGEX_FLAG) || inputLine.hasArg(SEARCH_SHORT_REGEX_FLAG);
            if (isRegEx) {
                if (inputLine.hasArg(SEARCH_REGEX_FLAG)) {
                    condition = inputLine.getNextArgFor(SEARCH_REGEX_FLAG);
                    inputLine.removeSwitchAndValue(SEARCH_REGEX_FLAG);
                }
                if (inputLine.hasArg(SEARCH_SHORT_REGEX_FLAG)) {
                    condition = inputLine.getNextArgFor(SEARCH_SHORT_REGEX_FLAG);
                    inputLine.removeSwitchAndValue(SEARCH_SHORT_REGEX_FLAG);
                }
            }
        }
        try {
            if (checkNullCase) {
                // Fix https://github.com/ncsa/security-lib/issues/49
                values = getStore().search(key, isNullCase);
            } else {
                values = getSearchValues(list_n, hasListN,
                        hasDate, dateField, afterDate, beforeDate,
                        key, values,
                        returnedAttributes, condition, isRegEx, hasKey);

            }

            if (values == null) {
                String kk = getKeyArg(inputLine);
                say("No searchable criteria found." + ((kk == null) ? " No key or date." : " \"" + kk + "\" is not a valid key for this store."));
                return;
            }
            // filter for versions. Fix https://github.com/ncsa/security-lib/issues/48
            if (hasVersions) {
                List<Identifiable> vList = new ArrayList<>(values.size());
                for (Identifiable i : values) {
                    if (searchForVersionsOnly) {
                        if (StoreArchiver.isVersioned(i.getIdentifier())) {
                            vList.add(i);
                        }
                    } else {
                        if (searchVersions) {
                            vList = values; // take them all
                            break;
                        } else {
                            if (!StoreArchiver.isVersioned(i.getIdentifier())) {
                                vList.add(i);
                            }
                        }
                    }
                }
                values = vList;
            } else {
                // default is to filter out versions
                List<Identifiable> vList = new ArrayList<>(values.size());
                for (Identifiable i : values) {
                    if (StoreArchiver.isVersioned(i.getIdentifier())) {
                        continue;
                    }
                    vList.add(i);
                }
                values = vList;
            }
            if (storeRS) {
                if (returnedAttributes == null) {
                    returnedAttributes = getKeys().allKeys(); // no attributes specified means get them all.
                }
                resultSets.put(rsName, new RSRecord(values, returnedAttributes));
                say("got " + values.size() + " match" + (values.size() == 1 ? "." : "es."));
                return;
            }
        } catch (Throwable t) {
            if (showStackTraces) {
                t.printStackTrace();
            }
            if (t.getCause() == null) {
                say("Sorry, that didn't work:" + t.getMessage());
            } else {
                say("Sorry, that didn't work:" + t.getCause().getMessage());
            }
            return;
        }
        if (printRS(inputLine, values, returnedAttributes, null)) return;
        say("\ngot " + values.size() + " match" + (values.size() == 1 ? "." : "es."));
    }

    private List<Identifiable> getSearchValues(int list_n, boolean hasListN, boolean hasDate, String dateField, Date afterDate, Date beforeDate, String key, List<Identifiable> values, List<String> returnedAttributes, String condition, boolean isRegEx, boolean hasKey) {
        if (hasListN) {
            return getStore().getMostRecent(list_n, returnedAttributes);
        }
        if (hasKey) {
            if (hasDate) {
                return getStore().search(
                        key,
                        condition,
                        isRegEx,
                        returnedAttributes,
                        dateField,
                        beforeDate, afterDate);
            }
            return getStore().search(
                    key,
                    condition,
                    isRegEx,
                    returnedAttributes,
                    null,
                    null,
                    null);
        }
        if (hasDate) {
            return getStore().search(
                    null,
                    null,
                    false,
                    returnedAttributes,
                    dateField,
                    beforeDate,
                    afterDate);
        } else {
            // no query, do nothing.
            return null;
        }
    }

    /**
     * If limits is empty or null, show everything. Limits contains the indices to show.
     *
     * @param inputLine
     * @param values
     * @param returnedAttributes
     * @param limits
     * @return
     */
    public boolean printRS(InputLine inputLine,
                           List<Identifiable> values,
                           List<String> returnedAttributes,
                           List limits) {
        if (values.isEmpty()) {
            say("no matches");
            return true;
        }

        if (inputLine.hasArg(SEARCH_SIZE_FLAG)) {
            say("Got " + values.size() + " results");
            return true;
        }
        boolean hasAttributes = !(returnedAttributes == null || returnedAttributes.isEmpty());
        boolean longFormat = inputLine.hasArg(VERBOSE_COMMAND) || inputLine.hasArg(LINE_LIST_COMMAND);
        List<List<String>> table = null;
        if (hasAttributes) {
            table = new ArrayList<>();
        }
        int start = 0;
        if (limits == null) {
            limits = new ArrayList<>(values.size());
            for (int i = 0; i < values.size(); i++) {
                limits.add(i);
            } // makes an iterator so show everything
        } else {
            if (limits.get(0) instanceof Long) {
                start = ((Long) limits.get(0)).intValue();
            } else {
                throw new IllegalArgumentException("limit must be a number.");
            }
        }
        // get start in the right range
        while (start < 0) {
            start = start + values.size();
        }
        for (Object key : limits) {

            int i;
            if (key instanceof Integer) {
                i = ((Integer) key).intValue();
            } else {
                if (key instanceof Long) {
                    i = ((Long) key).intValue();
                } else {
                    throw new IllegalArgumentException("list values must be integers");
                }
            }
            int ii = i;
            while (ii < 0) {
                ii = ii + values.size(); // get in the right range
            }
            i = ii;
            Identifiable identifiable = values.get(i);
            if (longFormat) {
                longFormat(identifiable, returnedAttributes, inputLine.hasArg(VERBOSE_COMMAND));
                if (1 < values.size()) {
                    say("-----"); // or the output runs together
                }
            } else {
                if (hasAttributes) {
                    List<String> row = new ArrayList<>();
                    XMLMap map = new XMLMap();
                    try {
                        getMapConverter().toMap(identifiable, map);
                    } catch (ClassCastException e) {
                        throw new GeneralException("Could not convert the result set object -- is this the right store type?");
                    }

                    for (int j = 0; j < returnedAttributes.size(); j++) {
                        Object obj = map.get(returnedAttributes.get(j));
                        if (obj == null) {
                            row.add(null);
                        } else {
                            row.add(obj.toString());
                        }
                    }
                    table.add(row);
                } else {
                    say(format(identifiable));
                }

            }
        } //end for loop
        if (hasAttributes) {
            List<String> formattedTable = StringUtils.formatTable(returnedAttributes, table, 40, true);
            // **(%@ Java and its Math functions.
            BigDecimal bigDecimal = new BigDecimal(Double.toString(Math.ceil(Math.log(formattedTable.size()) + 0.1)));

            int indexWidth = bigDecimal.intValue();
            say("             ".substring(0, indexWidth + 2) + formattedTable.get(0)); // print out headers
            for (int i = 1; i < formattedTable.size(); i++) {
                say(StringUtils.RJustify(Integer.toString(start + i - 1), indexWidth) + ". " + formattedTable.get(i));
            }
        }
        return false;
    }

    MapConverter mapConverter = null;

    protected MapConverter getMapConverter() {
        if (mapConverter == null) {
            XMLConverter xmlConverter = getStore().getXMLConverter();
            if (!(xmlConverter instanceof MapConverter)) {
                warn("internal error: The XML converter for the store is not a MapConverter.");
                say("internal error: check logs");
                return null;
            }
            mapConverter = (MapConverter) xmlConverter;
        }
        return mapConverter;
    }

    public static class RSRecord {
        public RSRecord() {
        }

        public RSRecord(List<Identifiable> rs, List<String> fields) {
            this.rs = rs;
            this.fields = fields;
        }

        public List<Identifiable> rs;
        public List<String> fields;

        public List<Identifiable> getSubset(List indices) {
            List<Identifiable> out = new ArrayList<>(rs.size());
            for (Object key : indices) {
                int rawIndex;
                if (key instanceof Integer) {
                    rawIndex = ((Integer) key).intValue();
                    //out.add(rs.get((Integer) key));
                } else {
                    if (key instanceof Long) {
                        rawIndex = ((Long) key).intValue();
                        //out.add(rs.get(((Long) key).intValue()));

                    } else {
                        throw new IllegalArgumentException("list values must be integers");
                    }
                }
                // if they pass in negative indices, get them in the right range.
                while (rawIndex < 0) {
                    rawIndex = rawIndex + rs.size();
                }
                out.add(rs.get(rawIndex));

            }
            return out;
        }
    }


    public void edit(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            say("edit index");
            sayi("Usage: Edit a single object in an external editor.");
            say("You must have configured an editor in the configuration for this to work.");
            say("If you have not, the default line editor will be used.");
            sayi("Note that this will update the object on exiting the editor");
            printIndexHelp(true);
        }
        File tempFile;
        try {
            tempFile = File.createTempFile("edit", ".oa2", getTempDir());
        } catch (IOException iox) {
            if (DebugUtil.isEnabled()) {
                iox.printStackTrace();
            }
            say("could not open file:" + iox.getMessage());
            return;
        }
        Identifiable x = findSingleton(inputLine);
        XMLMap c = new XMLMap();
        getStore().getXMLConverter().toMap(x, c);
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            c.toXML(fos);
            fos.flush();
            fos.close();
        } catch (IOException iox) {
            if (DebugUtil.isEnabled()) {
                iox.printStackTrace();
            }

            say("could not write temp file:" + iox.getMessage());
            return;
        }
        if (EditorUtils.EDITOR_RC_OK == EditorUtils.editFile(getEditorEntry(), tempFile)) {
            try {
                FileInputStream fis = new FileInputStream(tempFile);
                c = new XMLMap();

                c.fromXML(fis);
                fis.close();
                Identifiable identifiable = getStore().getXMLConverter().fromMap(c, null);
                if (!identifiable.getIdentifier().equals(x.getIdentifier())
                ) {
                    boolean ok = readline("You realize that the identifier for the original and edited objects are not the same, right? Save anyway(y/n)?").equals("y");
                    if (ok) {
                        getStore().save(identifiable);

                    } else {
                        say("save aborted");
                        return;
                    }

                }
                getStore().save(identifiable);

            } catch (IOException iox) {
                if (DebugUtil.isEnabled()) {
                    iox.printStackTrace();
                }
                say("there was a problem reading the edited file:" + iox.getMessage());
            }
        }
        say("update ok.");
    }


    String LIST_START_DELIMITER = "[";
    String LIST_END_DELIMITER = "]";
    String LIST_SEPARATOR = ",";

    /**
     * Older version of update. Not nearly as full-featured but still useful, so keep
     * it and in cases it is needed, just invoke it on behalf of the user.
     *
     * @param inputLine
     * @throws IOException
     */
    protected void oldUpdate(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showUpdateHelp();
            return;
        }
        List<Identifiable> identifiables = findItem(inputLine);

        if (identifiables == null) {
            say("You must supply the index or id of the item to update");
            return;
        }
        // Note that the contract should be that a clone is passed in and that is saved if the user
        // decides to. This allows rollbacks.
        Identifiable identifiable1 = identifiables.get(0).clone();
        if (update(identifiable1)) {
            getStore().save(identifiable1);
            clearEntries(); // CIL-240: Remove entries so the updated item is displayed.
        }
        return;


    }

    /**
     * This is the workhorse method for the object that lets you edit the values.
     * Generally this should do validation and checking so that updates to the store
     * are not garbage.
     *
     * @param identifiable
     * @return returns true if the passed object needs to be saved, false otherwise.
     */
    public boolean update(Identifiable identifiable) throws IOException {
        return update(identifiable, true, DEFAULT_MAGIC_NUMBER);
    }

    /**
     * Update the object. doSave if true means to prompt the user to save it (usually what you want).
     * The magic number is for when you are over-riding this with some specific tweak.
     *
     * @param identifiable
     * @param doSave
     * @param magicNumber
     * @return
     * @throws IOException
     */
    public boolean update(Identifiable identifiable, boolean doSave, int magicNumber) throws IOException {

        String newIdentifier = null;
        SerializationKeys keys = getSerializationKeys();
        info("Starting update for object with id = " + identifiable.getIdentifierString());
        say("Update the values. A return accepts the existing or default value in []'s");
        say("For properties you can type /help or --help and print out the online help for the topic.");

        newIdentifier = getPropertyHelp(keys.identifier(), "enter the identifier", identifiable.getIdentifierString());

        // if they change the identifier for this, prompt to see if they want to delete
        // the original one.
        boolean removeCurrentObject = false;
        Identifier oldID = identifiable.getIdentifier();

        identifiable.setDescription(getPropertyHelp(keys.description(), "enter description", identifiable.getDescription()));
        extraUpdates(identifiable, magicNumber); // all the other properties
        say("here is the complete object:");
        longFormat(identifiable);
        if (!newIdentifier.equals(identifiable.getIdentifierString())) {
            //  sayi2(" remove client with id=\"" + client.getIdentifier() + "\" [y/n]? ");
            removeCurrentObject = isOk(readline(" remove object with id=\"" + identifiable.getIdentifier() + "\" [y/n]? "));
            identifiable.setIdentifier(BasicIdentifier.newID(newIdentifier));
        }
        if (doSave) {
            if (isOk(readline("save (y/n)?[n]"))) {
                //getStore().save(client);
                if (removeCurrentObject) {
                    info("removing object with id = " + oldID);
                    getStore().remove(identifiable.getIdentifier());
                    sayi("object with id " + oldID + " removed. Be sure to save any changes.");
                }
                getStore().save(identifiable);
                sayi("object updated.");

                info("Object with id " + identifiable.getIdentifierString() + " saving...");

                return true;
            }
            sayi("object not updated, losing changes...");
            info("User terminated updates for object with id " + identifiable.getIdentifierString());
            return false;
        }
        return false;
    }

    /**
     * This is a hook for extensions so they don't have to completely
     * rewrite complex {@link #update(edu.uiuc.ncsa.security.core.Identifiable)} methods.
     * It will be invoked before
     * update displays the completed item and saves it, allowing any properties not in the base class
     * to be queried and saved.
     *
     * @param identifiable
     * @param magicNumber
     */
    public abstract void extraUpdates(Identifiable identifiable, int magicNumber) throws IOException;

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
    protected int longFormat(Identifiable identifiable) {
        return longFormat(identifiable, false);
    }

    /**
     * Long formatting with the switch for verbose or not. If false, that means use the long format
     *
     * @param identifiable
     * @param isVerbose
     * @return the width of the left field when formatting (for consistent look and feel in overrides).
     */

    // protected abstract int longFormat(Identifiable identifiable, boolean isVerbose);
    protected int longFormat(Identifiable identifiable, boolean isVerbose) {
        return longFormat(identifiable, null, isVerbose);
    }


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


    protected List<Identifiable> listEntries(List<Identifiable> entries, boolean lineList, boolean verboseList) {
        if (entries.isEmpty()) {
            say("(no entries found)");
            return entries;
        }

        int i = 0;
        getSortable().setState(null);
        entries = getSortable().sort(entries);
        for (Identifiable x : entries) {
            if (lineList) {
                longFormat(x);
                say("----");
            } else {
                say((i++) + ". " + format(x));
            }
        }
        return entries;
    }

    protected void showCreateHelp() {
        say("create [id]\n");
        sayi("Usage: Create a new entry in the currently active store.");
        sayi("where the id is a unique identifier for the object.");
        sayi("If you do not specify an");
        sayi("identifier, you will be prompted for one. You may also elect to have a new, random one created and assigned.");
        sayi("This will *not* use the current set index.");
    }


    /**
     * Creates a new item. The optional argument is the new identifier.
     *
     * @param inputLine
     */
    public void create(InputLine inputLine) throws IOException {
        actualCreate(inputLine, DEFAULT_MAGIC_NUMBER);
    }

    /**
     * Wraps the store create method. This can be overridden in certain cases (e.g. creating users)
     * where special handling is needed.
     *
     * @return
     */
    protected Identifiable createEntry(int magicNumber) {
        return getStore().create();
    }

    SerializationKeys serializationKeys = null;

    /**
     * Get the serialization keys for the main store.
     *
     * @return
     */
    protected SerializationKeys getSerializationKeys() {
        if (serializationKeys == null) {
            serializationKeys = getMapConverter().getKeys();
        }
        return serializationKeys;
    }

    /**
     * does the actual creation and returns the created object. If you override {@link #create(InputLine)},
     * this is what does the actual work.
     *
     * @param inputLine
     * @param magicNumber
     * @return
     * @throws IOException
     */
    protected Identifiable actualCreate(InputLine inputLine, int magicNumber) throws IOException {
        if (showHelp(inputLine)) {
            showCreateHelp();
            return null;
        }
        Identifier id = null;
        Identifiable x = createEntry(magicNumber);
        x = setIDFromInputLine(x, inputLine);
        if (x == null) {
            say("Identifier is not a valid URI, cannot create object.");
            return null;
        }
        return create(x, magicNumber);
    }

    /**
     * if the user specified the new identifier on the command line, peel it off and use it.
     *
     * @param x
     * @param inputLine
     * @return
     */
    protected Identifiable setIDFromInputLine(Identifiable x, InputLine inputLine) {
        if (1 < inputLine.size()) {
            try {
                Identifier id = BasicIdentifier.newID(inputLine.getLastArg());
                if (getStore().containsKey(id)) {
                    throw new IllegalArgumentException("Error: The identifier \"" + id + "\" already has an entry in this store.");
                }
                x.setIdentifier(id);
            } catch (Throwable t) {
                return null;
            }
        }
        return x;
    }

    /**
     * How to customize different objects this command processor creates (e.g.
     * creating ersatz clients as a special case with a flag). These are invoked right
     * after creation, but before the object is saved, so you can just set properties
     * or prompt the user for specific properties. Note that if the user elects to
     * {@link #update(Identifiable)} the properties, then in the course of that
     * {@link #extraUpdates(Identifiable, int)} will be invoked, so that is another
     * location for the user to get prompted for properties.
     *
     * @param identifiable
     * @param magicNumber
     * @return
     */
    protected Identifiable preCreation(Identifiable identifiable, int magicNumber) {
        return identifiable;
    }


    protected Identifiable create(Identifiable identifiable) throws IOException {
        return create(identifiable, DEFAULT_MAGIC_NUMBER);
    }

    /**
     * This is the system default (only one defined, equals zero). use another number so you can specify cases for
     * overrides. This way your commands can work with slightly different types of objects
     * (such as ersatz or service clients) and be able to disambiguate without having some more
     * complex system in the API to do it. Specify any non-zero magic numbers in your commands and use those.
     */
    public static final int DEFAULT_MAGIC_NUMBER = 0;

    protected Identifiable create(Identifiable c, int magicNumber) throws IOException {
        c = preCreation(c, magicNumber);
        info("Created object " + c.getClass().getSimpleName() + " with identifier " + c.getIdentifierString());
        if (isOk(readline("object created, edit (y/n)?"))) {
            update(c, false, magicNumber);
        }
        if (isOk(readline("save (y/n)?"))) {
            getStore().save(c);
            say("updates saved");
        } else {
            say("updates not saved");
            c = null;
        }
        clearEntries();
        return c;
    }


    List<Identifier> idList = null;

    public boolean hasID() {
        return idList != null;
    }

    /**
     * Mutators for sub classes
     *
     * @return
     */
    public List<Identifier> getID() {
        return idList;
    }

    public void setID(List<Identifier> id) {
        this.idList = id;
    }

    public void clear_id(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showClearIDHelp();
            return;
        }
        idList = null;
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
        String originalLine = inputLine.getOriginalLine(); // might need later
        // case 1: Set with an id or
        String lastArg = inputLine.getLastArg();
        List<Identifier> oldIDs = idList;
        idList = null; // ull it or the next call just finds whatever is set.
        List<Identifiable> identifiables = findItem(inputLine);
        if (identifiables != null) {
            List<Identifier> ids = new ArrayList<>(identifiables.size());
            for (Identifiable x : identifiables) {
                ids.add(x.getIdentifier());
            }
            setID(ids);
            return;
        } else {
            idList = oldIDs; // set it back in case this bombs later, don't just zero it.
        }
        // try to get it directly

        // case 2: There is a list of some sort, but no result set.
        // Allow for
        // set_id -- [my:id, -1]
        // So explicit list, relative index in allEntries

        InputLine inputLine2 = new InputLine(originalLine);
        if (inputLine2.hasArg(RS_RANGE_KEY, RS_RANGE_SHORT_KEY)) {
            List list = processList(inputLine2, inputLine2.whichArg(RS_RANGE_KEY, RS_RANGE_SHORT_KEY));
            if (list != null && list.size() > 0) {
                // then this is a list of strings, or integers. The strings are ids, the
                // integers are indices in allEntries
                HashSet<Identifier> idsSet = new HashSet<>(list.size()); // make sure they are unique
                for (Object x : list) {

                    Identifier y = getIdentifierfromIndex(x);
                    if (y != null) {
                        idsSet.add(y);
                    }
                } //end for
                List<Identifier> ids = new ArrayList<>(idsSet.size());
                ids.addAll(idsSet);
                idList = ids;
                return;
            }
        }
        // case 3: Maybe an integer, not a list?
        int index = -1;
        try {
            int ndx = Integer.parseInt(lastArg);
            while (ndx < 0) {
                ndx = ndx + allEntries.size(); // get in right range
            }
            while (allEntries.size() <= ndx) {
                ndx = ndx - allEntries.size();
            }
            idList = new ArrayList<>(1);
            idList.add(allEntries.get(index).getIdentifier());
        } catch (NumberFormatException nfx) {
            if (lastArg.startsWith("/")) {
                lastArg = lastArg.substring(1);
            }
            try {
                Identifier id = BasicIdentifier.newID(lastArg);
                idList = new ArrayList<>(1);
                idList.add(id);
                if (!getStore().containsKey(getID())) {
                    say("warning: unknown id. ");
                }
                // alles ok...
            } catch (Throwable t) {
                say("Could not parse identifier \"" + lastArg + "\", aborting...");
                return;
            }
        }

        if (idList == null) {
            say("warning: no identifier set");
            return;
        }

        say("Identifier set to " + idList);

    }

    /**
     * If there is a list of objects, this will try to ferret out the
     * identifier for the object.
     *
     * @param x
     * @return
     */
    protected Identifier getIdentifierfromIndex(Object x) {
        Integer ndx = null;
        if (x instanceof Long) {
            ndx = ((Long) x).intValue();
        } else {
            if (x instanceof Integer) {
                ndx = ((Integer) x).intValue();
            } else {
                if (x instanceof String) {
                    try {
                        ndx = Integer.parseInt((String) x);
                    } catch (Throwable t) {
                        return BasicIdentifier.newID((String) x);
                    }
                }
            }
        }
        if (ndx == null) {
            throw new NFWException("illegal list element type:" + x);
        }
        if (allEntries == null) {
            throw new NFWException("all entries not set. Run ls first if the store is not huge.");
        } else {
            while (ndx < 0) {
                ndx = ndx + allEntries.size(); // get in right range
            }
            while (allEntries.size() <= ndx) {
                ndx = ndx - allEntries.size();
            }
            return allEntries.get(ndx).getIdentifier();
        }
    }

    private void showSetIDHElp() {
        say("set_id [/]id | index = sets the current identifier.  All subsequent operations will ");
        say("use this identifier unless it is cleared or you explicitly override it.");
        say("The arguments may be the id, the escaped version (commonly used elsewhere, starts with \"/\") or the index.");
        say("The result will be the actual id of the object.");
        printIndexHelp(true);
        say("E.g.");
        say("set_id 3 --  sets the id to the 3rd object in the most recent ls command");
        say("set_id /foo:bar -- sets the id to foo:bar");
        say("set_id foo:bar -- sets the id to foo:bar");
        say("set_id " + RESULT_SET_KEY + " ligo " + RS_RANGE_KEY + " 2 -- sets the id to the second entry of the result set.");
        say("See also: clear_id, get_id");
    }

    public void get_id(InputLine inputLine) {
        if (showHelp(inputLine)) {
            showGetIDHelp();
            return;
        }
        if (idList == null) {
            say("no id set");
            return;
        }
        say(idList.toString());
    }

    private void showGetIDHelp() {
        say("get_id");
        sayi("Usage: Show the current id if any.");
        sayi("See also: clear_id, set_id");
    }

    /**
     * This will find an object <b><i>in the given store</i></b> assuming the name is an identifier.
     * Failing that, it tries to find a result set with that name. If there is no such element,
     * a null is returned, rather than raising an error, so you can, e.g. prompt the user
     * or some such.
     * <p>
     * This is used for positional argument, like
     * </p>
     * <pre>
     *     link_clients my_rs foo:/bar/baz
     * </pre>
     * which allows the command to get the result set my_rs and iterate over it, applying the operation
     * with the other argument.
     *
     * @param name
     * @return
     */
    // Nota Bene: This is not used in the class, but in subclasses.
    protected FoundIdentifiables findByIDOrRS(Store store, String name) {
        Identifiable identifiable = (Identifiable) store.get(BasicIdentifier.newID(name));
        if (identifiable == null) {
            RSRecord rs = getResultSets().get(name);
            if (rs == null) {
                return null;
            } else {
                return new FoundIdentifiables(true, rs.rs);
            }
        } else {
            FoundIdentifiables out = new FoundIdentifiables(false, 1);
            out.add(identifiable);
            return out;
        }
    }

    /**
     * See {@link #findItem(InputLine, boolean)}. This calls that with a default of true.
     *
     * @param inputLine
     * @return
     * @throws Throwable
     */
    protected FoundIdentifiables findItem(InputLine inputLine) throws Throwable {
        return findItem(inputLine, true);
    }

    /**
     * Resolves the first argument of a command line into either a unique identifier
     * against the ambient store.
     * The contract is that <b>IF</b> there is an ID set (with {@link #set_id(InputLine)})
     * then use that. Otherwise, take the last argument of the input line and try to find that.
     * <p><b>Note:</b> If there is no such item, such as an empty set, this will return a null.
     * Therefore, either the result is a null or has at least one element.</p>
     * <p>This removes the arguments for this from the {@link InputLine} since that
     * might mess up parsing it later. Properly, if this is needed for a command, it
     * should be called as early as practical.</p>
     *
     * @param inputLine
     * @return
     */

    protected FoundIdentifiables findItem(InputLine inputLine, boolean allowResultSets) throws Throwable {
        return findItem(getStore(), inputLine, allowResultSets);
    }

    /**
     * General case for finding items from a store. Pass in the store.
     *
     * @param store
     * @param inputLine
     * @param allowResultSets
     * @return
     * @throws Throwable
     */
    protected FoundIdentifiables findItem(Store store, InputLine inputLine, boolean allowResultSets) throws Throwable {
        Identifier localID = null;
        int index = -1;
        FoundIdentifiables out;
        // In order of likelihood
        // Case 1: ID has been set. Use it.
        if (hasID()) {
            out = new FoundIdentifiables(false);
            for (Identifier id : idList) {
                Identifiable x = (Identifiable) store.get(id);
                if (x == null) {
                    continue; // or throw object nor found exception???
                }
                out.setLocalID(true);
                out.add(x);
            }
            return out;
        }
        // case 2: Explicit identifier given
        String lastArg = inputLine.getLastArg();
        Identifiable identifiable = null;
        try {
            // really old CLI legacy that might be in scripts. IDs were prefixed with
            // a slash.
            if (lastArg.startsWith("/")) {
                lastArg = lastArg.substring(1);
            }
            identifiable = (Identifiable) store.get(BasicIdentifier.newID(lastArg));
            out = new FoundIdentifiables(false);
            if (identifiable != null) {
                out.setGivenID(true);
                out.add(identifiable);
                inputLine.removeLastArg();
                return out;
            }
        } catch (Throwable t) {
            // some stores (such as the user store) throw an exception since that is their contract.
            // assume no such element found
        }
        // case 3: a result set is given
        RSRecord rs = getResultSets().get(lastArg);

        if (rs != null) {
            String key = null;
            if (inputLine.hasArg(RS_RANGE_KEY)) {
                key = RS_RANGE_KEY;
            } else if (inputLine.hasArg(RS_RANGE_SHORT_KEY)) {
                key = RS_RANGE_SHORT_KEY;
            }
            if (key != null) {
                List indices = processList(inputLine, key);
                out = new FoundIdentifiables(true, rs.getSubset(indices));
                if (out.isEmpty()) {
                    out = null;
                } else {
                    out.setRSName(lastArg);
                    out.setRsIndexList(indices);
                }
            } else {
                if (rs.rs == null || rs.rs.isEmpty()) {
                    out = null;
                } else {
                    out = new FoundIdentifiables(true, rs.rs);
                }
            }
            inputLine.removeLastArg();
            return out;
        }
        try {
            index = Integer.parseInt(lastArg);
            if (allEntries == null || allEntries.isEmpty()) {
                loadAllEntries(); // just in case...
            }
            if (index < 0 || allEntries.size() < index) {
                return null;
            }
            out = new FoundIdentifiables(false, 1);
            out.add(allEntries.get(index));
            out.setNumericIndex(true);
            out.setNumericIndex(index);
            return out;
        } catch (Throwable t) {
        }
        // last possible case, it's some sort of list?
        if (!inputLine.hasArg(RS_RANGE_KEY, RS_RANGE_SHORT_KEY)) {
            return null;
        }
        List list = processList(inputLine, inputLine.whichArg(RS_RANGE_KEY, RS_RANGE_SHORT_KEY));
        if (list != null && list.size() > 0) {
            FoundIdentifiables identifiables = new FoundIdentifiables(false, list.size());

            for (Object x : list) {
                identifiables.add((Identifiable) store.get(getIdentifierfromIndex(x)));
            }
            return identifiables;
        }
        return null;
    }


    protected void oldrm(InputLine inputLine) throws Throwable {
        FoundIdentifiables identifiables = findItem(inputLine);
        //"Are you sure you want to remove this client(y/n)[n]:"
        if (!"y".equals(getInput("Are you sure you want to remove " + (1 < identifiables.size() ? ("these " + identifiables.size() + " objects") : "this object") + "?(y/n)", "n"))) {
            say("remove aborted.");
            return;
        }
        getStore().remove(identifiables); // batch remove
        for (Identifiable identifiable : identifiables) {
            // reset ID
            if (idList != null) {
                if (identifiable.getIdentifier().equals(idList)) {
                    idList = null;
                }
            }
        }
        rmCleanup(identifiables);
        // work is done, print something
        if (identifiables.size() == 1) {
            say("Done. object with id = " + identifiables.get(0).getIdentifierString() + " has been removed from the store.");
        } else {
            say("Done. Removed = " + identifiables.size() + " objects have been removed from the store.");
        }
        clearEntries();
    }

    public void rm(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showRMHelp();
            return;
        }
        boolean forceRemove = inputLine.hasArg(RM_FORCE_FLAG);
        inputLine.removeSwitch(RM_FORCE_FLAG);
        String key = getAndCheckKeyArg(inputLine);
        List keys = processList(inputLine, KEYS_FLAG);
        if (key != null && keys != null) {
            say("both " + KEY_FLAG + " and " + KEYS_FLAG + " are set. Cannot resolve request, aborted.");
            return;
        }
        // legacy case.
        if (key == null && keys == null && !forceRemove) {
            oldrm(inputLine);
            return;
        }
        FoundIdentifiables identifiables = findItem(inputLine);
        if (identifiables == null || identifiables.isEmpty()) {
            say("no such objects found. aborting...");
            return;
        }
        if (forceRemove) {
            getStore().remove(identifiables);
            if (isVerbose()) {
                say(identifiables.size() + " objects have been removed from the store.");
            }
            return;
        }
        // By this point, exactly one of key or keys is not null
        if (key != null) {
            keys = new ArrayList();
            keys.add(key);
        }
        for (int i = 0; i < identifiables.size(); i++) {
            Identifiable identifiable = identifiables.get(i);
            identifiable = removeEntries(identifiable, keys);
            if (identifiables.isRS()) {
                // if its result set, set the updated entry
                identifiables.set(i, identifiable);
            }
        }
    }

    /*
     * Old version. Using it as a resource for rewrite.
     *
     * @param inputLine
     * @throws IOException
     */
/*
    protected void rm2(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showRMHelp();
            return;
        }
        boolean forceRemove = inputLine.hasArg(RM_FORCE_FLAG);
        inputLine.removeSwitch(RM_FORCE_FLAG);
        String key = getAndCheckKeyArg(inputLine);
        boolean isRS = hasRS(inputLine);
        if (isRS) {
            RSRecord rsRecord = resultSets.get(inputLine.getNextArgFor(RESULT_SET_KEY));
            if (rsRecord == null) {
                say("no such stored result.");
                return;
            }
            List<Identifier> identifiers = new ArrayList<>(rsRecord.rs.size());
            for (Identifiable identifiable : rsRecord.rs) {
                identifiers.add(identifiable.getIdentifier());
            }
            if (key != null || inputLine.hasArg(KEYS_FLAG)) {
                say("removal of specific keys for result set not yet supported");
                return;
            }
            say("removing " + identifiers.size() + " objects..");

            getStore().remove(identifiers);
            say("done!");
            return;
        }
        if (forceRemove) {
            // create a dummy and try that.
            Identifier identifier = BasicIdentifier.newID(inputLine.getLastArg());
            getStore().remove(identifier);
            return;
        }
        List<Identifiable> identifiables = findItem(inputLine);
        if (identifiables == null) {
            say("Object not found");
            return;
        }
        // if the request does not have new stuff, do old stuff.
        if (key == null && !inputLine.hasArg(KEYS_FLAG)) {
            oldrm(inputLine);
            for (Identifiable identifiable : identifiables) {
                rmCleanup(identifiable);
            }
            return;
        }
        if (inputLine.hasArg(KEYS_FLAG)) {
            List<String> array = inputLine.getArgList(KEYS_FLAG);

            if (array == null) {
                say("sorry, but this requires a list for this option.");
                return;
            }
            if (identifiables == null) {
                say("sorry, I could not find that object. Check your id.");
                return;
            }
            for (Identifiable identifiable : identifiables) {
                removeEntries(identifiable, array);
            }
        }
        if (key != null) {
            if (identifiables == null) {
                say("sorry, I could not find that object. Check your id.");
                return;
            }
            for (Identifiable identifiable : identifiables) {
                removeEntry(identifiable, key);

            }
            say("removed attribute \"" + key + "\"");
        }
    }
    /
 */

    protected void showLSHelp() {
        String blanks = getBlanks(5);
        say("ls [flags] [>key key | " + KEYS_FLAG + " list] index");
        sayi("Usage: Show information about an object or objects.");
        sayi("flags are");
        sayi(StringUtils.RJustify(LINE_LIST_COMMAND, 4) + " = " + "line list of an object or all objects. Longer entries will be truncated.");
        sayi(StringUtils.RJustify(ALL_LIST_COMMAND, 4) + " = " + " list of **every** entry in the store. You have been warned.");
        sayi(StringUtils.RJustify(VERBOSE_COMMAND, 4) + " = " + "verbose list. All entries will be shown in their entirety.");
        sayi(StringUtils.RJustify(LOAD_ONLY_COMMAND, 4) + " = " + "Loads the entire store into memory, displaying nothing.");
        sayi(blanks + "Use with care! A really large store may swamp your machine and crash it.");
        sayi(blanks + "Note: If you are going to refer to objects by their numerical index, you will need to");
        say(blanks + "       load the store explicitly first with this flag.");
        sayi(KEY_SHORTHAND_PREFIX + "key | " + KEY_FLAG + " key - a single key Only this property will be shown.");
        sayi(KEYS_FLAG + " list -  a list of properties to display. You amy specify list or verbose");
        printIndexHelp(false);
        say("E.g.'s");
        sayi("ls " + LINE_LIST_COMMAND + " " + ALL_LIST_COMMAND + " = line listing of entire store. This may be huge.");
        sayi("ls " + LINE_LIST_COMMAND + " = line list of the currently active object.");
        sayi("ls " + ALL_LIST_COMMAND + "  = short list of the entire store.");
        sayi("ls " + LINE_LIST_COMMAND + " 4 = line list of the 4th item from the " + ALL_LIST_COMMAND + " or " + LOAD_ONLY_COMMAND + " command");
        sayi("ls " + LINE_LIST_COMMAND + " /foo:bar = line list of the object with identifier foo:bar");
        sayi("ls " + LINE_LIST_COMMAND + " foo:bar = line list of the object with identifier foo:bar");
        sayi("ls " + VERBOSE_COMMAND + " foo:bar = verbose list of the object with identifier foo:bar");
        sayi("ls " + KEYS_FLAG + " [client_id, create_date] my_results = prints only the client ids and create date");
        sayi(blanks + "from the result set my_results. Remember that this prints what is in the store, not the result set");
        sayi(blanks + "To print the result set, use the rs command.");
        sayi("ls " + KEY_SHORTHAND_PREFIX + "cfg foo:bar = show the value of the cfg property in the object with ID foo:bar");
    }

    protected final String LINE_LIST_COMMAND = "-l";
    protected final String ALL_LIST_COMMAND = "-E";
    protected final String LOAD_ONLY_COMMAND = "-load";
    protected final String VERBOSE_COMMAND = "-v";

    protected boolean hasId() {
        return idList != null;
    }

    protected void oldls1(InputLine inputLine) throws Throwable {

        boolean listAll = inputLine.hasArg(ALL_LIST_COMMAND);
        boolean listLines = inputLine.hasArg(LINE_LIST_COMMAND);
        boolean isVerbose = inputLine.hasArg(VERBOSE_COMMAND);
        inputLine.removeSwitch(ALL_LIST_COMMAND);
        inputLine.removeSwitch(LINE_LIST_COMMAND);
        inputLine.removeSwitch(VERBOSE_COMMAND);
        // Any form of the all flag prints everything.
        if (listAll) {
            loadAllEntries();
            listEntries(loadAllEntries(), listLines, isVerbose);
            return;
        }
        // common case that they type just ls.
        if (inputLine.getArgCount() == 0) {
            listEntries(loadAllEntries(), listLines, isVerbose); // list it all
            return;
        }
        FoundIdentifiables identifiables = findItem(inputLine);

        if (identifiables == null) {
            say("sorry, no such object. Check your id.");
            return;
        } else {// found something
            int count = 0;
            for (Identifiable identifiable : identifiables) {
                if (identifiables.isRS()) { // list the stored version, not the one in the RS!
                    identifiable = (Identifiable) getStore().get(identifiable.getIdentifier());
                }
                if (listLines) {
                    longFormat(identifiable, false);
                } else {
                    if (isVerbose) {
                        longFormat(identifiable, true);
                    } else {
                        say(format(identifiable));
                    }
                }
                count++;
                if (1 < count) {
                    say("------ end " + identifiable.getIdentifierString()); // spacer when listing multiple
                    say(); // add a blanks in between too...
                }
            }
        }
    }

    public void ls(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showLSHelp();
            return;
        }
        if (inputLine.hasArg(LOAD_ONLY_COMMAND)) {
            loadAllEntries();
            say("all " + allEntries.size() + " entries from store loaded");
            return;
        }
        boolean listAll = inputLine.hasArg(ALL_LIST_COMMAND);
        boolean listSingleLines = inputLine.hasArg(LINE_LIST_COMMAND);
        boolean listMultiLines = inputLine.hasArg(VERBOSE_COMMAND);
        inputLine.removeSwitch(ALL_LIST_COMMAND);
        inputLine.removeSwitch(LINE_LIST_COMMAND);
        inputLine.removeSwitch(VERBOSE_COMMAND);
        boolean shortForm = !(listMultiLines || listSingleLines);

        if ((listSingleLines && listMultiLines)) {
            say("inconsistent flags. You cannot have both single and multiline output at the same time.");
            return;
        }

        // common case that they type just ls.
        if (!hasID() && (inputLine.getArgCount() == 0 || listAll)) {
            if (listSingleLines || listMultiLines) {
                if (!"y".equals(getInput("are you sure you want to list all entries?(y/n)", "n"))) {
                    say("aborted...");
                    return;
                }
            }
            listEntries(loadAllEntries(), listSingleLines, listMultiLines); // list it all
            return;
        }
        String key = getKeyArg(inputLine, true); // grab if there, remove it
        List<String> keys = processList(inputLine, KEYS_FLAG);

        // a this point, there should be nothing on the line except
        // for the index. Therefore, if nothing comes back grom findItems
        // they mean to do this on the whole store.
        boolean useAll = (!hasID()) && inputLine.getArgCount() == 0;
        FoundIdentifiables identifiables = findItem(inputLine);
        if (identifiables == null) {
            if (useAll) {
                if (allEntries == null || allEntries.size() == 0) {
                    loadAllEntries();
                }
                if (allEntries == null || allEntries.size() == 0) {
                    say("Sorry, no objects found.");
                    return;
                }
                identifiables = new FoundIdentifiables(allEntries);
                identifiables.setRS(false);
            }else {
                say("Sorry, no objects found.");
                return;
            }
        }

        if (key != null) {
            if (!getKeys().allKeys().contains(key)) {
                say("unrecognized key: " + key);
                return;
            }
            say(key + ":");
            for (Identifiable identifiable : identifiables) {
                if (identifiables.isRS()) {
                    identifiable = (Identifiable) getStore().get(identifiable.getIdentifier());
                }
                showEntry(identifiable, key, inputLine.hasArg(VERBOSE_COMMAND));
                if (1 < identifiables.size()) {
                    say("------ end " + identifiable.getIdentifierString()); // spacer when listing multiple
                    say(); // add a blanks in between too...
                }

            }
            return;
        }
        if (keys != null) {
            int count = 0;
            shortForm = false;
            for (Identifiable identifiable : identifiables) {
                if (identifiables.isRS()) {
                    identifiable = (Identifiable) getStore().get(identifiable.getIdentifier());
                }
                showEntrySubset(identifiable, keys, inputLine.hasArg(VERBOSE_COMMAND));
                count++;
                if (!shortForm && !identifiables.isSingleton()) {
                    say("------ end " + identifiable.getIdentifierString()); // spacer when listing multiple
                    say(); // add a blanks in between too...
                }

            }
            return;
        }

        int count = 0;
        for (Identifiable identifiable : identifiables) {
            if (identifiables.isRS()) { // list the stored version, not the one in the RS!
                identifiable = (Identifiable) getStore().get(identifiable.getIdentifier());
            }
            if (listSingleLines) {
                longFormat(identifiable, false);
            } else {
                if (listMultiLines) {
                    longFormat(identifiable, true);
                } else {
                    say(format(identifiable));
                }
            }
            count++;
            if (!shortForm && 1 < count) {
                say("------ end " + identifiable.getIdentifierString()); // spacer when listing multiple
                say(); // add a blanks in between too...
            }
        }

    }

    /*public void ls1(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showLSHelp();
            return;
        }

        String key = getAndCheckKeyArg(inputLine);
        if (key == null && !inputLine.hasArg(KEYS_FLAG)) {
            oldls1(inputLine);
            return;
        }
        FoundIdentifiables identifiables = findItem(inputLine);
        if (identifiables == null) {
            say("object not found");
            return;
        }
        if (inputLine.hasArg(KEYS_FLAG)) {
            List<String> array = processList(inputLine, KEYS_FLAG);
            for (Identifiable identifiable : identifiables) {
                if (identifiables.isRS()) {
                    identifiable = (Identifiable) getStore().get(identifiable.getIdentifier());
                }
                showEntries(identifiable, array, inputLine.hasArg(VERBOSE_COMMAND));
            }
            return;
        }
        if (key != null) {
            for (Identifiable identifiable : identifiables) {
                if (identifiables.isRS()) {
                    identifiable = (Identifiable) getStore().get(identifiable.getIdentifier());
                }
                showEntry(identifiable, key, inputLine.hasArg(VERBOSE_COMMAND));
            }
        }
    }*/

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
    public void print_help() throws Exception {
        super.print_help();
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

    protected int display_width = 120;

    /**
     * Gets a consistent look and feel. If you have to override {@link #longFormat(Identifiable)}
     * and add your own entries, use this.
     *
     * @param leftSide
     * @param rightSide
     * @param leftColumWidth
     * @return
     */
    protected String formatLongLine(String leftSide, String rightSide, int leftColumWidth, boolean isVerbose) {
        int dd = indentWidth() + 3; // the default indent plus the " : " in the middle
        int realWidth = display_width - dd;
        boolean shortLine = rightSide.length() + leftColumWidth + 1 <= realWidth;
        if (isVerbose) {

            List<String> flowedtext = StringUtils.wrap(0, StringUtils.toList(rightSide), realWidth - leftColumWidth);

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(RJustify(leftSide, leftColumWidth) + " : " + flowedtext.get(0) + ((flowedtext.size() <= 1 && shortLine) ? "" : "\n"));
            boolean isFirstLine = true;
            for (int i = 1; i < flowedtext.size(); i++) {
                if (isFirstLine) {
                    isFirstLine = false;
                    stringBuffer.append(getBlanks(dd + leftColumWidth) + flowedtext.get(i));
                } else {
                    stringBuffer.append("\n" + getBlanks(dd + leftColumWidth) + flowedtext.get(i));
                }
            }
            return stringBuffer.toString();

        }
        return RJustify(leftSide, leftColumWidth) + " : " + truncate(rightSide.replace("\n", "").replace("\r", ""));
    }


    /**
     * Prints a restricted set of keys from the first argument. Note that a missing
     * or empty subset means print everything. The output is key values in a readable format
     * using {@link StringUtils#formatMap(Map, List, boolean, boolean, int, int, boolean)}
     *
     * @param identifiable object to print
     * @param keySubset    list of keys to restrict to
     * @param isVerbose    multi-line output, otherwise only a single line, possibly truncated, per property is shown
     * @return
     */
    protected int longFormat(Identifiable identifiable, List<String> keySubset, boolean isVerbose) {
        XMLMap map = new XMLMap();
        getStore().getXMLConverter().toMap(identifiable, map);
        // CIL1677 fallout we must store last_accessed as a long so we can do comparisons
        // across various types of stores, but we *really* want this to display as a date.
        // So, we just convert it here for display purposes.
        if (getStore() instanceof MonitoredStoreInterface) {
            MonitoredKeys keys = (MonitoredKeys) getSerializationKeys();
            if (map.containsKey(keys.lastAccessed())) {
                long la = map.getLong(keys.lastAccessed());
                // only display if it has been initialized. 0 is default = no value.
                if (la <= 0) {
                    map.remove(keys.lastAccessed());
                } else {

                    map.put(keys.lastAccessed(), new Date(la));
                }
            }
        }
        List<String> outputList = StringUtils.formatMap(map,
                keySubset,
                true,
                isVerbose,
                indentWidth(),
                display_width);
        for (String x : outputList) {
            say(x);
        }
        return 0;
    }


    public static final String FILE_FLAG = "-file";
    public static final String UPDATE_FLAG = "-update";
    public static final String SHORT_UPDATE_FLAG = "-u";
    String KEY_FLAG = "-key";
    String VALUE_FLAG = "-value";
    String KEYS_FLAG = "-keys";

    protected void serialize(InputLine inputLine, Identifiable x) {

        OutputStream os = System.out;
        boolean hasFile = false;
        if (inputLine.hasArg(FILE_FLAG)) {
            try {
                os = new FileOutputStream(inputLine.getNextArgFor(FILE_FLAG));
                hasFile = true;
            } catch (FileNotFoundException e) {
                say("warning, could not find file in argument \"" + inputLine.getNextArgFor(FILE_FLAG));
            }
            inputLine.removeSwitchAndValue(FILE_FLAG);
        }

        XMLMap c = new XMLMap();
        getStore().getXMLConverter().toMap(x, c);

        if (inputLine.hasArg(KEYS_FLAG)) {
            List<String> keys = inputLine.getArgList(KEYS_FLAG);
            inputLine.removeSwitchAndValue(KEYS_FLAG);
            // c now contains all the fields. We remove anything
            XMLMap subset = new XMLMap();
            // put in the identifier
            subset.put(getKeys().identifier(), x.getIdentifierString());
            for (String key : keys) {
                if (c.containsKey(key)) {
                    subset.put(key, c.get(key));
                }
            }
            c = subset; // set it to the right variable to get serialized.
        }

        try {
            c.toXML(os);
            if (hasFile) {
                os.flush();
                os.close();
            }
            say("done!");
        } catch (IOException e) {
            say("Error serializing object.");
        }
    }

    protected void showSearchHelp() {
        say("search ");
        sayi("[" + KEY_FLAG + " key | " + KEY_SHORTHAND_PREFIX + "key] ");
        sayi("[(" + SEARCH_REGEX_FLAG + "|" + SEARCH_SHORT_REGEX_FLAG + ") regex] ");
        sayi("[" + SEARCH_SIZE_FLAG + "] ");
        sayi("[" + SEARCH_DEBUG_FLAG + "] ");
        sayi("[" + SEARCH_IS_NULL_FLAG + " true | false] ");
        sayi("[" + LINE_LIST_COMMAND + " | " + VERBOSE_COMMAND + "]");
        sayi("[" + SEARCH_DATE_FLAG + " date_field (" + SEARCH_BEFORE_TS_FLAG + " time | " + SEARCH_AFTER_TS_FLAG + " time)] ");
        sayi("[" + SEARCH_RESULT_SET_NAME + " name] ");
        sayi("[" + SEARCH_VERSIONS_FALSE_VALUE + " " +
                SEARCH_VERSIONS_TRUE_VALUE + " | " + SEARCH_VERSIONS_FALSE_VALUE + "| " + SEARCH_VERSIONS_ONLY_VALUE + "] ");
        sayi("[" + SEARCH_RETURNED_ATTRIBUTES_FLAG + " list | property]");
        sayi("[condition]");
        say("----");
        sayi("Usage: Searches the current component for all entries satisfying the condition.");
        sayi("You may search either on a key, by date or both");
        sayi("The key search can be with a regular expression. Use the regex flag, other wise the last argument");
        sayi("[condition] will be matched exactly.");
        sayi("Note that condition is used if there is no regex specified. If there is a regex");
        sayi("specified as well, that takes precedence.");
        say("Search options");
        say("----");
        int w = 15; // field with for lh side
        String blanks = "                                                                   ";
        String link = " - ";
        blanks = blanks.substring(0, w + link.length()); // padding for any lines that run over
        say(StringUtils.RJustify(KEY_FLAG + " key | " + KEY_SHORTHAND_PREFIX + "key", w) + link + "the name of the key to be searched for");
        say(StringUtils.RJustify(SEARCH_REGEX_FLAG + "|" + SEARCH_SHORT_REGEX_FLAG + " regex", w) + link + "attempt to interpret regex as a regular expression");
        say(StringUtils.RJustify(SEARCH_RESULT_SET_NAME + " name", w) + link + "save the result as the name.");
        say(StringUtils.RJustify(SEARCH_DEBUG_FLAG, w) + link + "show stack traces. Only use this if you really need it.");
        say(StringUtils.RJustify(SEARCH_IS_NULL_FLAG, w) + link + "Searches for null (i.e. unset values). true returns all objects that have the property null (unset)");
        say(blanks + "while false returns objects that are not null (i.e. have been set). Very useful when looking for ");
        say(blanks + "cases where some value is missing. E.g. in transactions, missing access tokens means the flow might be abandoned.");
        sayi(StringUtils.RJustify(NEXT_N_COMMAND + " = [n]", w) + link + "lists most recent n (0<n) or first n (n<0) entries. No argument implies n = 10.");
        // Fix https://github.com/ncsa/security-lib/issues/48
        say(StringUtils.RJustify(SEARCH_VERSIONS_FLAG, w) + link + "return or ignore versions. Options are");
        say(blanks + SEARCH_VERSIONS_TRUE_VALUE + " = return all values plus any versions");
        say(blanks + SEARCH_VERSIONS_FALSE_VALUE + " = (default) return no versioned objects.");
        say(blanks + SEARCH_VERSIONS_ONLY_VALUE + " = return only versioned objects.");
        say(StringUtils.RJustify(SEARCH_DATE_FLAG, w) + link + "search by date. You must have at least one time using either " + SEARCH_BEFORE_TS_FLAG + " or " + SEARCH_AFTER_TS_FLAG);
        say(blanks + "For date searches you may use either an ISO 8601 date in the form ");
        say(blanks + "YYYY-MM-DD or time, e.g. 2021-04-05T13:00:00Z");
        say(blanks + "would at 1 pm in UTC. If you do not supply a final Z or other timezone");
        say(blanks + "(+-HH:mm) then your current timezone is used.");
        say(blanks + "To search on a date range (i.e. between two times) specify both your");
        say(blanks + "before and after dates. Date searches are inclusive of the dates.");
        say(blanks + "For SQL stores you may search booleans as 1 (true) or 0 false.");
        say(blanks + "E.g.\n> search >can_substitute 1\nwould search the can_substitute property for true values.");
        say("----");
        say("Other supported options");
        showCommandLineSwitchesHelp();
        showKeyShorthandHelp();
        sayi("If you want to see examples, invoke help with -ex.");
    }

    protected void showSearchHelpExamples() {
        say("All of these examples are in the client store, but the syntax works in any store.");
        say("Be aware that different stores have difference attributes.");
        say("E.g. ");
        sayi("clients>search " + KEY_SHORTHAND_PREFIX + "client_id " + SEARCH_REGEX_FLAG + " \".*07028.*\"");
        say("Searches for the client_id keys that contains '07028'");
        say("E.g.");
        sayi("clients> search " + KEY_FLAG + " email " + SEARCH_SHORT_REGEX_FLAG + " \".*bigstate\\.edu.*\"");
        say("Searches the email keys that contain 'bigstate.edu'.");
        say("Note that the period must be escaped for a regex.");
        say("E.g.");
        sayi("clients>search " + KEY_SHORTHAND_PREFIX + "client_id " +
                SEARCH_RETURNED_ATTRIBUTES_FLAG + " [name, email] " +
                SEARCH_SHORT_REGEX_FLAG + " " + ".*237.*");
        say("Searches the client_id keys that contain the string 237 and only print out the name and email from those.");
        say("E.g.");
        sayi("clients>search " + SEARCH_DATE_FLAG + " creation_ts " + SEARCH_BEFORE_TS_FLAG + " 2021-01-02 " + SEARCH_RESULT_SET_NAME + " last_year");
        say("Searches the creation_ts keys as dates, returning all that are before Jan 2, 20201.");
        say("This also stores the result under the name last_year. See also the rs command help");
        say("E.g. search for all approvals by date and status");
        say("search >status none -date approval_ts -after 2025-03-01T00:00:0");
        say("searches for all approvals after the given date of March 1, 2025 at midnight.");
        say("E.g.");
        sayi("clients>search " + KEY_SHORTHAND_PREFIX + "email " + SEARCH_SHORT_REGEX_FLAG + " \".*bigstate\\.edu.*\" " + SEARCH_DATE_FLAG + " creation_ts " + SEARCH_BEFORE_TS_FLAG + " 2021-01-02 " + SEARCH_RESULT_SET_NAME + " last_year_email");
        say("Searches per date as in the previous example and further restricts it to matching the given key.");
        say("This also stores the result under the name last_year_email. See also the rs command help");
        say("E.g.");
        say("clients>search " + KEY_SHORTHAND_PREFIX + "client_id " + SEARCH_RETURNED_ATTRIBUTES_FLAG + " email " +
                SEARCH_SHORT_REGEX_FLAG + ".*fnal\\.gov " + RESULT_SET_KEY + " fnal_emails");
        say("searches for clients whose id ends in fnal.gov, returning only the contact email and stashing the output");
        say("into a result set called \"email\" (which can be saved to external storage for, say, for sending mass notifications.)");
        say("Note that the returned attributes can be a single property name, though a list with one element works too.");
        say("E.g. A date search");
        say("This searches by client id for clients created between the given dates. It stores the result");
        say("in the result set named 's234'");
        say("clients>search >client_id -r .*234.* -date creation_ts -after 2020-05-01 -before 2020-05-30 -rs s234");
        say("got 4 matches");
        say("E.g. getting the most recent entries");
        say("clients>search " + NEXT_N_COMMAND + " 15");
        say("This returns the most recent 15 entries to this store. An argument of -15 woudl return the oldest 15.");
        say("E.g. search for a subset");
        say("clients>search -n 5 -out [name, creation_ts, client_id]");
        say("\nSee also: rs");
    }


    //    static String SEARCH_LIST_KEYS_FLAG = "-listKeys";

    public static String KEY_SHORTHAND_PREFIX = ">";
    public static String RANDOM_ID_FLAG = "-random_id";
    public static String FORCE_COPY_FLAG = "-f";
    public static String SKIP_UPDATE_PERMISSIONS_FLAG = "-skipPermissions";

    /**
     * resolves key shorthand of &gt;key_name or -key key_name
     * returns null if no such key OR if it is not valid in the
     * key list. Does <b>not</b> remove the argument from the inputLine!
     *
     * @param inputLine
     * @return
     */
    protected String getAndCheckKeyArg(InputLine inputLine) {
        String keyArg = getKeyArg(inputLine);
        if (keyArg == null) {
            return null;
        }

        if (getSerializationKeys().allKeys().contains(keyArg)) {
            return keyArg;
        }
        return null;
    }

    protected String getKeyArg(InputLine inputLine) {
        return getKeyArg(inputLine, false);
    }

    /**
     * Just gets the key argument or null if not present. This does no checking if
     * the key that is found is valid for the store.
     *
     * @param inputLine
     * @param removeIt
     * @return
     */
    protected String getKeyArg(InputLine inputLine, boolean removeIt) {
        if (inputLine.size() <= 1) {
            // so no actual arguments supplied.
            return null;
        }
        if (inputLine.hasArg(KEY_FLAG)) {
            String k = inputLine.getNextArgFor(KEY_FLAG);
            if (removeIt) {
                inputLine.removeSwitchAndValue(KEYS_FLAG);
            }
            return k;
        }

        // have to search
        for (int i = 1; i < inputLine.size(); i++) {
            String arg = inputLine.getArg(i);
            if (arg.startsWith(KEY_SHORTHAND_PREFIX)) {
                String k = arg.substring(1);
                if (removeIt) {
                    inputLine.removeSwitch(arg); // not switch and value!!
                }
                return k;
            }
        }
        return null;
    }


    /**
     * For dates. Allow users to supply ISO dates without a timezone and add in the current.
     * This computes it from the running JVM.
     *
     * @return
     */
    String getTzOffset() {
        if (tzOffset == null) {
            TimeZone tz = TimeZone.getDefault();

            tzOffset = (tz.getRawOffset() < 0) ? "-" : "+";
            int hr = Math.abs(tz.getRawOffset() / 1000 / 60 / 60);
            int min = Math.abs(tz.getRawOffset() / 1000 % 60);
            tzOffset = tzOffset + ((hr < 10 ? "0" : "") + hr) + ":" + ((min < 10 ? "0" : "") + min);
        }
        return tzOffset;
    }

    // use clients
    // search -date creation_ts -after 2020-05-01 -before 2020-05-30 -rs s2
    // search >client_id -r .*234.* -date creation_ts -after 2020-05-01 -before 2020-05-30 -rs s234
    // search >client_id -r .*234.* -rs all-234
    private Date getDateFromArg(InputLine inputLine, String arg) throws ParseException {
        String computedDateString = inputLine.getNextArgFor(arg);
        if (computedDateString.equals("now")) {
            return new Date();
        }
        inputLine.removeSwitchAndValue(arg);
        if (-1 == computedDateString.indexOf("T")) {
            // then there is no time, just a date. Convert to ISO
            computedDateString = computedDateString + "T00:00:00" + getTzOffset();
        }
        try {
            return Iso8601.string2Date(computedDateString).getTime();
        } catch (ParseException pe) {
            computedDateString = computedDateString + getTzOffset();
            // try it again in case they are going local
            try {
                return Iso8601.string2Date(computedDateString).getTime();
            } catch (ParseException e) {
                throw e;
            }

        }
    }

    protected void showCommandLineSwitchesHelp() {
        sayi(LINE_LIST_COMMAND + " (optional) print the result in long format.");
        sayi(VERBOSE_COMMAND + " (optional) print the result in verbose format.");
        sayi(SEARCH_SIZE_FLAG + " (optional) print *only* the number of results.");
        sayi(SEARCH_RETURNED_ATTRIBUTES_FLAG + " [attr0,attr1,...] = return only those attributes. " +
                "Note you may specify long or verbose format too.");

    }

    protected void showKeyShorthandHelp() {
        sayi("Note: The argument idiom '-key key_name' may be replaced with '" + KEY_SHORTHAND_PREFIX + "key_name' as a shorthand");
    }

    /**
     * Override this as needed to update any permissions for this store. This is used in {@link #change_id}
     * and {@link #copy}.
     *
     * @param newID
     * @param oldID
     * @param copy  - if true copy the permissions with the new ID. If false, create new ones
     * @return
     */
    protected abstract int updateStorePermissions(Identifier newID, Identifier oldID, boolean copy);

    public void copy(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            showCopyHelp();
            return;
        }
        boolean forceIt = inputLine.hasArg(FORCE_COPY_FLAG);
        boolean randomID = inputLine.hasArg(RANDOM_ID_FLAG);
        boolean skipUpdatePermissions = inputLine.hasArg(SKIP_UPDATE_PERMISSIONS_FLAG);
        inputLine.removeSwitch(FORCE_COPY_FLAG);
        inputLine.removeSwitch(RANDOM_ID_FLAG);
        inputLine.removeSwitch(SKIP_UPDATE_PERMISSIONS_FLAG);

        String sourceString = inputLine.getArg(1); // zero-th arg is name of command.
        String targetString = null;
        if (inputLine.getArgCount() == 2) {
            targetString = inputLine.getArg(2);
        }
        Identifier sourceId = null;
        Identifier targetId = null;
        try {
            sourceId = BasicIdentifier.newID(sourceString);
            if (!getStore().containsKey(sourceId)) {
                say("The source object with id '" + sourceString + "' does not exist.");
                return;
            }
        } catch (Throwable t) {
            say("sorry, but the first argument \"" + sourceString + "\" is not a valid identifier");
            return;
        }
        if (isTrivial(targetString)) {
            if (!randomID) {
                say("No id specified and you did not supply the " + RANDOM_ID_FLAG + " flag. Cannot process.");
                return;
            }
        } else {
            try {
                targetId = BasicIdentifier.newID(targetString);
                // check for existence later with -force flag
                if (isBadID(targetId.getUri())) {
                    say("Bad target ID: " + targetId.getUri());
                    return;
                }
            } catch (Throwable t) {
                say("sorry, but the second argument \"" + targetString + "\" is not a valid identifier");
                return;
            }
        }

        Identifiable source = (Identifiable) getStore().get(sourceId);
        // store is charged with making a valid, unused random id, so no need to check if that is requested.
        if (!forceIt && !randomID && getStore().containsKey(targetId)) {
            say("sorry, but \"" + targetId + "\" already exists. Consider using the " + FORCE_COPY_FLAG + " flag if you need to overwrite it.");
            return;
        }

        Identifier newID = doCopy(source, targetId, randomID);
        int updatedPermissionCount = 0;
        if (!skipUpdatePermissions) {
            updatedPermissionCount = updateStorePermissions(targetId, sourceId, true);
        }
        if (randomID) {
            say("new copy with id \"" + newID.toString() + "\" created.");
        }
        if (!skipUpdatePermissions) {
            say(updatedPermissionCount + " permissions updated.");
        }
    }

    /**
     * Do the copy. Note that if useRandomID is true, targetID is ignored,
     *
     * @param source
     * @param targetId
     * @param useRandomID
     */
    protected Identifier doCopy(Identifiable source, Identifier targetId, boolean useRandomID) {
        MapConverter mc = getMapConverter();
        Identifiable newVersion = getStore().create();
        Identifier newVersionIdentifier = newVersion.getIdentifier();
        XMLMap map = new XMLMap();
        mc.toMap(source, map);

        mc.fromMap(map, newVersion);
        // Caveat: this copy will set the target id equal to the source if
        if (useRandomID) {
            newVersion.setIdentifier(newVersionIdentifier);
        } else {
            newVersion.setIdentifier(targetId);
        }
        getStore().save(newVersion);
        if (useRandomID) {
            return newVersionIdentifier;
        } else {
            return targetId;
        }
    }

    private void showCopyHelp() {
        say("copy source target [" + FORCE_COPY_FLAG + "] [" + RANDOM_ID_FLAG + "] [" + SKIP_UPDATE_PERMISSIONS_FLAG + "]");
        sayi("Usage: Copy source to target");
        sayi(FORCE_COPY_FLAG + " - force it, so overwrite if the target exists.");
        sayi(RANDOM_ID_FLAG + " - create a random id for the target.");
        sayi(SKIP_UPDATE_PERMISSIONS_FLAG + " - skip copying any permissions for the target. Default is to copy them.");
        sayi("new_id - specify the new id. Note if this is present, " + RANDOM_ID_FLAG + " is ignored.");
        sayi("This will create a complete copy of source and store it with the id of target.");
        sayi("Note: This will refuse to do this if target exists.");
        sayi("Note: If you do not specify to use a random id and do not supply one, this will abort.");
        sayi("This only makes a simple copy, with permissions (unless those are skipped).  If this is a client, ");
        sayi("the approval will be copied too. ");
        sayi("Other objects may need to be updated, depending on the object.");
        sayi("Note: source and target are identifiers.");
        sayi("E.g. In the client store:\n");
        sayi("  client>copy dev:command.line dev:no_cfg\n");
        sayi("would take the client configuration with id dev:command.line and create a new client config. that is");
        sayi("identical except with id dev:no_cfg. In this case, as a new client, it needs to be approved.");
        say("E.g.");
        sayi("  client>copy dev:command.line " + RANDOM_ID_FLAG + "\n");
        sayi("Creates a copy of dev:command.line with a random id, which is printed");

    }

    /**
     * A placeholder. StoreCommands2 in OA4MP does this with QDL,
     * but that dependency here would create a compilation circularity.
     * <p>
     * This looks for <b>key [...]</b> and returns a list for what's between the [].
     * If there is no such list, a null is returned. (E.g. the key is missing)
     * </p>
     * <p>
     * Contract is that the input line will have this entry removed and will be reparsed.
     * This is to prevent bad parsing later.
     * </p>
     *
     * @param inputLine
     * @param key
     * @return
     * @throws Exception
     */
    protected List processList(InputLine inputLine, String key) throws Exception {
        if (key == null) {
            return null;
        }
        return inputLine.getArgList(key);
    }


    public void rs(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showResultSetHelp();
            return;
        }
        String action = inputLine.getArg(1);
        // we have sequential switch statements, since we are whittling down the command line arguments
        switch (action) {
            case RS_APPEND_ACTION:
                doRSAppend(inputLine);
                return;
            case RS_CLEAR_ACTION:
                int s = getResultSets().size();
                getResultSets().clear();
                say(s + " results have been cleared.");
                return;
            case RS_FIELDS_ACTION:
                doRSListFieldsInfo(inputLine);
                return;
            case RS_LIST_ACTION:
                doRSList();
                return;
            case RS_READ_ACTION:
                doRSRead(inputLine);
                return;
            case RS_SIZE_ACTION:
                doRSSize(inputLine);
                return;
            case RS_SUBSET_ACTION:
                doRSSubset(inputLine);
                return;
        }

        String name = inputLine.getLastArg();
        if (!resultSets.containsKey(name)) {
            say("result set named \"" + name + "\" not found");
            return;
        }
        inputLine.removeLastArg();

        switch (action) {
            case RS_DROP_ACTION:
                doRSDrop(name);
                break;
            case RS_REMOVE_ACTION:
                doRSRemove(inputLine, name);
                break;
            case RS_SAVE_ACTION:
                doRSSave(inputLine, name);
                break;
            case RS_WRITE_ACTION:
                doRSWrite(inputLine, name);
                break;
            case RS_SHOW_ACTION:
                doRSShow(inputLine, name);
                break;
            default:
                say("unknown action \"" + action + "\"");
        }
    }

    private void doRSSave(InputLine inputLine, String name) throws Throwable {
        FoundIdentifiables foundIdentifiables = findItem(inputLine);
        if (foundIdentifiables == null) {
            say("no objects found.");
            return;
        }
        if (!"y".equals(getInput("preparing to overwrite " + foundIdentifiables.size() + " objects in the store. Proceed(y/n)?", "n"))) {
            say("aborted...");
            return;
        }
        for (Identifiable identifiable : foundIdentifiables) {
            getStore().save(identifiable);
        }
        say(foundIdentifiables.size() + " objects have been saved.");
    }

    /**
     * Select a subset. rs subset new_name
     *
     * @param inputLine
     * @throws Throwable
     */
    private void doRSSubset(InputLine inputLine) throws Throwable {
        FoundIdentifiables foundIdentifiables = findItem(inputLine);
        if (foundIdentifiables == null) {
            say("no elements found in subset. aborted.");
            return;
        }
        if (!foundIdentifiables.isRS()) {
            say("can only subset an existing result set");
        }
        String newName = inputLine.getLastArg();
        boolean overwrite = inputLine.hasArg(RM_FORCE_FLAG);
        inputLine.removeSwitch(RM_FORCE_FLAG);
        if (RS_SUBSET_ACTION.equals(newName)) {
            say("no new name for result set found. aborting.");
            return;
        }
        if (!overwrite && getResultSets().containsKey(newName)) {
            if (!"y".equals(getInput("The result set \"" + newName + "\" already exists. Overwrite?(y/n)", "n"))) {
                say("aborted");
                return;
            }
        }
        RSRecord original = getResultSets().get(foundIdentifiables.getRSName());
        RSRecord newRecord = new RSRecord(foundIdentifiables, original.fields);
        getResultSets().put(newName, newRecord);
    }

    private void doRSWrite(InputLine inputLine, String name) throws IOException {
        MapConverter mapConverter = (MapConverter) getStore().getXMLConverter();
        RSRecord rsRecord = getResultSets().get(name);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        JSONArray fieldArray = new JSONArray();
        fieldArray.addAll(rsRecord.fields);
        jsonObject.put("fields", fieldArray);
        JSONArray entries = new JSONArray();
        for (Identifiable identifiable : rsRecord.rs) {
            if (mapConverter.isA(identifiable)) {
                entries.add(mapConverter.toJSON(identifiable));
            } else {
                System.out.println(identifiable.getClass().getSimpleName() + " is not convertible to this type");
            }
        }
        jsonObject.put("entries", entries);
        if (inputLine.hasArg(RS_FILE_KEY)) {
            String fileName = inputLine.getNextArgFor(RS_FILE_KEY);
            inputLine.removeSwitchAndValue(RS_FILE_KEY);
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(jsonObject.toString(1));
            fileWriter.flush();
            fileWriter.close();
        } else {
            System.out.println(jsonObject.toString(1));
        }
        return;
    }

    private void doRSListFieldsInfo(InputLine inputLine) {
        if (resultSets.isEmpty()) {
            return;
        }
        Set<String> keys;
        if (inputLine.getArgCount() == 0) {
            keys = resultSets.keySet();
        } else {
            keys = new HashSet<>(inputLine.getArgCount());
            for (int i = 2; i <= inputLine.getArgCount(); i++) {
                keys.add(inputLine.getArg(i));
            }
        }
        for (String key : keys) {
            if (!getResultSets().containsKey(key)) {
                say(key + " not found");
                continue;
            }
            String indent = getBlanks(2);
            String out = key + " has " + resultSets.get(key).rs.size() + " entries, " + resultSets.get(key).fields.size() + " fields";
            say(out);
            say(hLine("-", out.length()));
            if (resultSets.get(key).fields != null) {
                for (String field : resultSets.get(key).fields) {
                    say(indent + field);
                }
            } else {
                say(indent + "(empty)");
            }
            if (1 < keys.size()) {
                say();
            }
            // Someday use this: StringUtils.formatMap()???
            // This however gets messy for clients where there are client approvals.
        }
    }

    /**
     * Remove a named result set from the system
     *
     * @param inputLine
     * @param name
     * @throws Exception
     */
    private void doRSRemove(InputLine inputLine, String name) throws Exception {
        String rangeKey = null;
        if (inputLine.hasArg(RS_RANGE_KEY)) {
            rangeKey = RS_RANGE_KEY;
        } else {
            if (inputLine.hasArg(RS_RANGE_SHORT_KEY)) {
                rangeKey = RS_RANGE_SHORT_KEY;
            } else {
                say("range required for " + RS_REMOVE_ACTION + " action");
                return;
            }
        }
        if (!getResultSets().containsKey(name)) {
            say("no such result set name \"" + name + "\"");
            return;
        }
        List indices = processList(inputLine, rangeKey);
        if (indices.isEmpty()) {
            say("no elements in range.");
            return;
        }
        List<Identifiable> items = getResultSets().get(name).rs;

        List<Integer> normalizedIndices = getNormalizedIndices(indices);
        if (normalizedIndices == null) return;
        if (normalizedIndices.size() == items.size()) {
            say("you cannot remove every element from a result set. Use " + RS_DROP_ACTION + " instead.");
        }

        List<Identifiable> out = new ArrayList<>(items.size() - normalizedIndices.size());
        Iterator<Integer> iterator = normalizedIndices.iterator();
        Integer currentPointerIndex = iterator.next();
        boolean hasNextElement = true;
        for (int i = 0; i < items.size(); i++) {
            if (hasNextElement && i == currentPointerIndex) {
                hasNextElement = iterator.hasNext();
                if (hasNextElement) {
                    currentPointerIndex = iterator.next();
                    while (currentPointerIndex < 0) {
                        currentPointerIndex = currentPointerIndex + items.size();
                    }
                }
                continue;
            }
            out.add(items.get(i));
        }
        getResultSets().get(name).rs = out;
        say("result set named \"" + name + "\" has had " + normalizedIndices.size() + " elements removed");
        return;
    }

    private void doRSDrop(String name) {
        if (resultSets.containsKey(name)) {
            resultSets.remove(name);
            say("removed result set \"" + name + "\"");
        } else {
            say("no such result set name \"" + name + "\"");
        }
        return;
    }

    private void doRSClear() throws IOException {
        if (getInput("clear all results(y|n)?", "n").equals("y")) {
            resultSets = new HashMap<>();
            say("all results cleared");
        } else {
            say("remove aborted.");
        }
        return;
    }

    private void doRSShow(InputLine inputLine, String name) throws Exception {
        // The syntax is
        // -show name | index [start,stop]
        // so this takes a bit of special handling. Basically, everything at the end
        // of the line gets parsed in special cases.
        inputLine.removeSwitch(RS_SHOW_ACTION);

        int count = -1;
        //List<Integer> limits = new ArrayList<>();
        List limits = null;
        String key = null;

        if (inputLine.hasArg(RS_RANGE_KEY)) {
            key = RS_RANGE_KEY;
        } else if (inputLine.hasArg(RS_RANGE_SHORT_KEY)) {
            key = RS_RANGE_SHORT_KEY;
        }
        if (key != null) {
            limits = processList(inputLine, key);
        }
        List requestedKeys = null;
        if (inputLine.hasArg(SEARCH_RETURNED_ATTRIBUTES_FLAG)) {
            requestedKeys = processList(inputLine, SEARCH_RETURNED_ATTRIBUTES_FLAG);
            //requestedKeys = inputLine.getArgList(SEARCH_RETURNED_ATTRIBUTES_FLAG); // might be empty
            inputLine.removeSwitchAndValue(SEARCH_RETURNED_ATTRIBUTES_FLAG);
        }
        List<String> foundKeys = resultSets.get(name).fields;
        if (requestedKeys != null && !requestedKeys.isEmpty()) {
            if (foundKeys == null) {
                foundKeys = requestedKeys;
            } else {
                // Get the intersection of the lists. Only print what is actually there.
                Set<String> result = foundKeys.stream()
                        .distinct()
                        .filter(requestedKeys::contains)
                        .collect(Collectors.toSet());
                foundKeys = new ArrayList<>();
                // Add them back in, in the order requested.
                for (Object ob : requestedKeys) {
                    if (result.contains(ob.toString())) {
                        foundKeys.add(ob.toString());
                    }
                }
            }
        }
        printRS(inputLine, resultSets.get(name).rs, foundKeys, limits);
        return;
    }

    private void doRSRead(InputLine inputLine) throws Throwable {


        if (!inputLine.hasArg(RS_FILE_KEY)) {
            say("no file given. aborting...");
            return;
        }
        int passed = 0;
        int failed = 0;
        String in = FileUtil.readFileAsString(inputLine.getNextArgFor(RS_FILE_KEY));
        inputLine.removeSwitchAndValue(RS_FILE_KEY);
        String name = null;
        if (0 < inputLine.getArgCount()) {
            name = inputLine.getLastArg();
        }
        JSONObject jsonObject = JSONObject.fromObject(in);
        if (name != null) {
            // if no name is given, use the stored one.
            name = jsonObject.getString("name");
        }
        JSONArray fields = jsonObject.getJSONArray("fields");
        JSONArray entries = jsonObject.getJSONArray("entries");
        List<Identifiable> x = new ArrayList<>(entries.size());
        MapConverter mapConverter = (MapConverter) getStore().getXMLConverter();

        for (int i = 0; i < entries.size(); i++) {
            Identifiable identifiable = mapConverter.fromJSON(entries.getJSONObject(i), null);
            if (identifiable != null) {
                x.add(identifiable);
                passed++;
            } else {
                failed++;
            }
        }
        RSRecord rsRecord = new RSRecord(x, fields);
        getResultSets().put(name, rsRecord);
        say("read result set \"" + name + "\", " + passed + " entries processed,  " + failed + " entries skipped.");
    }

    private void doRSSize(InputLine inputLine) {
        //handles case there is a size action, but no result sets were named -- do them all.
        inputLine.removeSwitch(RS_SIZE_ACTION);
        if (inputLine.getArgCount() == 0) {
            if (getResultSets().size() == 0) {
                say("no result sets.");
                return;
            }
            String nameTitle = "name";
            String sizeTitle = "size";
            int width = nameTitle.length();
            int sizeWidth = sizeTitle.length();
            for (String name : getResultSets().keySet()) {
                width = Math.max(width, name.length());
                sizeWidth = Math.max(getResultSets().get(name).rs.size(), sizeWidth);
            }
            width = width + 2;
            sizeWidth = sizeWidth + 2;

            say(center(nameTitle, width) + "|" + center(sizeTitle, sizeWidth));
            say(hLine("-", width) + "+" + hLine("-", sizeWidth));
            for (String key : getResultSets().keySet()) {
                say(center(key, width) + "|  " + center(Integer.toString(getResultSets().get(key).rs.size()), sizeWidth));
            }
            say("\n" + getResultSets().size() + " result sets processed");
        }
        return;
    }

    private void doRSList() {
        if (getResultSets().isEmpty()) {
            say("No results found.");
            return;
        }
        // print it pretty. The assumption is that this not thousands of entries
        // so performance is not an issue, and we do two passes. One to get field size,
        // the second to print it.
        String nameTitle = "name";
        String countTitle = "entry count";
        int width = nameTitle.length();
        int sizeWidth = countTitle.length();
        for (String name : getResultSets().keySet()) {
            width = Math.max(width, name.length());
            sizeWidth = Math.max(getResultSets().get(name).rs.size(), sizeWidth);
        }
        width = width + 2;
        sizeWidth = sizeWidth + 2;
        say(StringUtils.center(nameTitle, width) + " : " + StringUtils.center(countTitle, sizeWidth));
        for (String name : getResultSets().keySet()) {
            say(StringUtils.center(name, width) + " : " + StringUtils.center(getResultSets().get(name).rs.size(), sizeWidth));
        }
        return;
    }

    private void doRSAppend(InputLine inputLine) {
        if (inputLine.getArgCount() == 1) {
            return;
        }
        String targetName = inputLine.getLastArg();
        if (getResultSets().containsKey(targetName)) {
            say("A result set with the name \"" + targetName + "\" already exists.");
            return;
        }
        TreeSet<String> fields = new TreeSet<>();
        RSRecord B = new RSRecord(new ArrayList<Identifiable>(), new ArrayList<>());

        int pass = 0;
        for (int i = 1; i < inputLine.getArgCount() - 1; i++) {
            String an = inputLine.getArgs().get(i);
            RSRecord a = getResultSets().get(an);
            if (a == null) {
                say(an + " not found, skipping...");
                continue;
            }
            pass++;
            fields.addAll(a.fields);
            B.rs.addAll(a.rs);
        }
        ArrayList<String> f = new ArrayList<>(fields);
        B.fields = f;
        getResultSets().put(targetName, B);
        if (pass == 0) {
            say("No records appended to " + targetName);
            return;
        }
        say(pass + " records appended to " + targetName);
    }

    /**
     * The trick is that not all of the indices might be distinct, and they may be in random
     * order. Either very complex logic is needed for those cases, or we normalize the indices to
     * a unique, ordered set and skip any in that set.
     */
    private List<Integer> getNormalizedIndices(List indices) {
        TreeSet<Integer> normalizedIndices = new TreeSet<>();

        for (Object o : indices) {
            if (o instanceof Integer) {
                normalizedIndices.add((Integer) o);
            } else {
                if (o instanceof Long) {
                    normalizedIndices.add(((Long) o).intValue());
                } else {
                    say("unrecognized index: " + o + ", aborting");
                    return null;
                }
            }
        }
        List result = new ArrayList<>(normalizedIndices.size());
        result.addAll(normalizedIndices);
        return result;
    }

    /*
    Actions allowed for result sets.
     */
    public static final String RS_APPEND_ACTION = "append";
    public static final String RS_CLEAR_ACTION = "clear";
    public static final String RS_DROP_ACTION = "drop";
    public static final String RS_FIELDS_ACTION = "fields";
    public static final String RS_LIST_ACTION = "list";
    public static final String RS_READ_ACTION = "read";
    public static final String RS_REMOVE_ACTION = "rm";
    public static final String RS_SAVE_ACTION = "save";
    public static final String RS_SHOW_ACTION = "show";
    public static final String RS_SIZE_ACTION = "size";
    public static final String RS_SUBSET_ACTION = "subset";
    public static final String RS_WRITE_ACTION = "write";
    public static String RS_RANGE_KEY = "-range";
    public static String RS_RANGE_SHORT_KEY = "--";
    public static String RS_FILE_KEY = "-file";

    protected String rangeHelpSnippet() {
        return "[(" + RS_RANGE_KEY + " | " + RS_RANGE_SHORT_KEY + ") (list|value)]";
    }

    protected void showResultSetHelp() {
        String blanks = getBlanks(5);
        sayi("rs action [flags] rs_name.");
        sayi("The list of actions is");
        sayi(RS_APPEND_ACTION + " A0 A1 ..  B - append result sets A0, A1... to B. B must not exist.");
        sayi(RS_CLEAR_ACTION + " - clear ALL results sets.");
        sayi(RS_DROP_ACTION + " name - Remove the named result set");
        sayi(RS_FIELDS_ACTION + " name0  name1 ... - List information about the named result sets, such as number of elements and fields.");
        sayi(RS_LIST_ACTION + " - list the current results sets.");
        sayi(RS_READ_ACTION + " [" + RS_FILE_KEY + " file] name - Read the result set from the file, giving it the given name.");
        sayi(RS_REMOVE_ACTION + " " + rangeHelpSnippet() + " name - Remove the range of values from the given result");
        sayi(blanks + "set. The elements will be reordered from 0. This is useful if you have done a search and");
        sayi(blanks + "your result set has a couple of extra, unwanted entries.");
        sayi(RS_SAVE_ACTION + " " + rangeHelpSnippet() + " name - Save the result set to the backing store. This overwrites");
        say(blanks + "the current object with the result set. Since result sets are static, this provides a way to rollback");
        say(blanks + "to a previous state. You will always be prompted. Note that " + RS_WRITE_ACTION + " writes the result");
        say(blanks + "set to a file and has nothing to do with the store.");
        sayi(RS_SHOW_ACTION + " " + rangeHelpSnippet() + " [" + SEARCH_RETURNED_ATTRIBUTES_FLAG + " list ] name - Show the result set, restricting to the given range");
        sayi(blanks + "(if given) and given fields (if given).");
        sayi(blanks + "No range means show the entire result set. No attributes means show all fields.");
        sayi(RS_SIZE_ACTION + " [name] - Print just the size of the result set. No names prints them all.");
        sayi(RS_SUBSET_ACTION + " [" + RM_FORCE_FLAG + "] new_name " + rangeHelpSnippet() + " rs_name = create a subset of rs_name");
        sayi(blanks + "using the given list and save it to new_name. If there is no such existing set named new_name, the operation");
        sayi(blanks + "is just done. If it exists, you are prompted, unless you supply the " + RM_FORCE_FLAG + " to force overwriting.");
        sayi(RS_WRITE_ACTION + " [" + RS_FILE_KEY + " file] " + rangeHelpSnippet() + " name - Write the result set to the file. No file given will dump");
        sayi(blanks + "it to the console.");
        showCommandLineSwitchesHelp();
    }

    /**
     * Shows a subset of an entry.
     *
     * @param identifiable
     * @param keys
     * @param isVerbose
     */
    protected void showEntrySubset(Identifiable identifiable, List<String> keys, boolean isVerbose) {
        if (keys.size() == 1) {
            // earlier case
            showEntry(identifiable, keys.get(0), isVerbose);
            return;
        }
        XMLMap object = toXMLMap(identifiable);
        List<String> outputList = StringUtils.formatMap(object,
                keys,
                false, // let them specify the order
                isVerbose,
                indentWidth(),
                display_width);
        for (String lineOut : outputList) {
            say(lineOut);
        }
    }

    /**
     * Show the value of a single property from an entry.
     *
     * @param identifiable
     * @param key
     * @param isVerbose
     */
    protected boolean showEntry(Identifiable identifiable, String key, boolean isVerbose) {
        if (hasKey(key)) {
            ColumnMap c;
            XMLMap object = toXMLMap(identifiable);
            if (object.containsKey(key)) {
                Object v = object.get(key);
                if (v instanceof String) {
                    try {
                        JSON json = JSONSerializer.toJSON(v);
                        say(json.toString(1));

                    } catch (Throwable t) {
                        say(object.get(key).toString());
                    }

                } else {
                    if (v instanceof JSON) {
                        say(((JSON) v).toString(1));
                    } else {
                        if(v == null){
                            say("(no value)");
                        }else {
                            say(v.toString());
                        }
                    }
                }
            } else {
                say("(no value)");
                return false;
            }
        } else {
            say("sorry, but \"" + key + "\" is not a recognized key.");
            return false;
        }
        return true;
    }

    protected boolean hasKey(String key) {
        return getKeys().allKeys().contains(key);
    }

    /**
     * Once an object is found in the store, convert it to JSON so that the properties may be
     * accessed in a canonical way. This lets us take any identifiable object and manipulate its
     * properties without knowing anything else about it.
     *
     * @param identifiable
     * @return
     */
    protected XMLMap toXMLMap(Identifiable identifiable) {
        Identifiable x = (Identifiable) getStore().get(identifiable.getIdentifier());
        XMLMap map = new XMLMap();
        MapConverter mapConverter = (MapConverter) getStore().getXMLConverter();
        mapConverter.toMap(x, map);
        return map;
    }

    /**
     * Take the <b>updated</b> values for the object and return a new, updated object.
     * This does not store it, so you have to do that if you want to keep the changes.
     *
     * @param map
     */
    protected Identifiable fromXMLMap(XMLMap map) {
        Identifiable identifiable = getStore().create();
        MapConverter mapConverter = (MapConverter) getStore().getXMLConverter();
        mapConverter.fromMap(map, identifiable);
        return identifiable;
    }


    /**
     * Allows for entering a new JSON object. This permits multi-line entry so formatted JSON can be cut and pasted
     * into the command line (as long as there are no blank lines). This will validate the JSON, print out a message and
     * check that you want to keep the new JSON. Note that you cannot overwrite the value of a configuration at this point
     * mostly as a safety feature. So hitting return or /exit will have the same effect of keeping the current value.
     *
     * @param oldJSON
     * @return null if the input is terminated (so retain the old object)
     */
    protected JSONObject inputJSON(JSONObject oldJSON, String key) throws IOException {
        String out;
        if (oldJSON == null) {
            out = multiLineInput(null, key);
        } else {
            out = multiLineInput(oldJSON.toString(1), key);
        }
        if (out == null) {
            // do nothing
            return oldJSON;
        }
        if (out.length() == 0) {
            return new JSONObject(); // clear the current object
        }

        try {
            JSONObject json = null;
            // Old was the following line.
            //json = JSONObject.fromObject(rawJSON);
            // new allows for HOCON at command line.
            Config config = ConfigFactory.parseString(out);
            json = JSONObject.fromObject(config.root().render(ConfigRenderOptions.concise()));
            sayi("Success! JSON is valid.");
            return json;
        } catch (Throwable t) {
            sayi("uh-oh... It seems this was not a valid JSON object. The parser message reads:\"" + t.getMessage() + "\"");
        }

        return oldJSON;
    }

    protected String multiLinePropertyInput(String propertyName, String oldValue, String key) throws IOException {
        boolean loopForever = true;
        String inLine = null;
        while (loopForever) {
            inLine = multiLineInput(oldValue, key);
            if (inLine == null) {
                return inLine;
            }
            if (inLine.trim().equals("--help") || inLine.trim().equals("help")) {
                if (getHelpUtil() == null) {
                    say("no help for the topic \"" + propertyName + "\"");
                } else {
                    getHelpUtil().printHelp(new InputLine("/help", propertyName));
                }
            } else {
                break;
            }
        }
        return inLine;
    }

    /**
     * For entering muli-line strings (includes JSON).
     *
     * @param oldValue may be null if a new value
     * @param key      used for constructing prompts.
     * @return
     * @throws IOException
     */
    protected String multiLineInput(String oldValue, String key) throws IOException {
        if (oldValue == null) {
            sayi("no current value for " + key);
        } else {
            sayi("current value for " + key + ":");
            say(oldValue);
        }
        sayi("Enter new value. An empty line terminates input. Entering a line with " + EXIT_COMMAND + " will terminate input losing changes.\n " +
                "Hitting " + CLEAR_BUFFER_COMMAND + " will clear the contents of this.");
        String rawInput = "";
        boolean redo = true;
        while (redo) {
            try {
                String inLine = readline();
                while (!isEmpty(inLine)) {
                    if (inLine.equals(EXIT_COMMAND)) {
                        say("losing changes");
                        return null;  // null means no changes
                    }
                    if (inLine.equals(CLEAR_BUFFER_COMMAND)) {
                        return ""; // empty string means clear the current contents
                    }
                    rawInput = rawInput + inLine + "\n";
                    inLine = readline();
                }
                return rawInput;
            } catch (ExitException x) {
                // ok, so user terminated input. This ends the whole thing
                return null;
            }
        }
        return null; // should never get here.
    }

    /**
     * Add to an existing entry.
     *
     * @param identifiable
     * @param jjj
     */
    protected void addEntry(Identifiable identifiable, JSON jjj) {
        if (!(jjj instanceof JSONObject)) {
            say("sorry, but that is not a valid JSON object for this operation.");
            return;
        }
        JSONObject json = (JSONObject) jjj;
        XMLMap object = toXMLMap(identifiable);
        for (Object k : json.keySet()) {
            String key = k.toString();
            Object newValue = json.get(key);
            if (hasKey(key)) {
                Object currentValue = object.containsValue(k);
                if (currentValue == null) {
                    object.put(key, newValue);
                } else {
                    if (currentValue instanceof JSONArray) {
                        ((JSONArray) currentValue).add(newValue);
                    } else {
                        object.put(key, newValue);
                    }
                }
            } else {
                say("sorry, but \"" + key + "\" is not a recognized key. Skipping...");
            }
        }
        getStore().save(fromXMLMap(object));
    }

    protected void addEntry(Identifiable identifiable, String key, String value) {
        XMLMap object = toXMLMap(identifiable);
        if (hasKey(key)) {
            Object currentValue = object.get(key);
            if (currentValue == null) {
                object.put(key, value);
            } else {
                if (currentValue instanceof JSONArray) {
                    ((JSONArray) currentValue).add(value);
                } else {
                    object.put(key, value);
                }
            }
        } else {
            say("sorry, but \"" + key + "\" is not a recognized key. Skipping...");

        }
        getStore().save(fromXMLMap(object));
    }


    /**
     * Removes the list of properties from the {@link Identifiable} and returns an
     * altered one.
     *
     * @param identifiable
     * @param keys
     * @return
     */
    protected Identifiable removeEntries(Identifiable identifiable, List<String> keys) {
        XMLMap object = toXMLMap(identifiable);
        boolean gotOne = false;
        for (String key : keys) {
            if (hasKey(key)) {
                if (object.containsKey(key)) {
                    object.remove(key);
                    gotOne = true;
                }
            }
        }
        if (gotOne) {
            getStore().save(fromXMLMap(object));
        }
        return fromXMLMap(object);
    }


    protected void removeEntry(Identifiable identifiable, String key) {
        XMLMap object = toXMLMap(identifiable);
        if (hasKey(key)) {
            if (object.containsKey(key)) {
                object.remove(key);
                getStore().save(fromXMLMap(object));
            } else {
                say("key \"" + key + "\" not found for this object.");
            }
        }
    }

    /**
     * Update a single value for a key, prompting the user for each value. This returns the value the user supplied
     *
     * @param map
     * @param key
     * @return
     * @throws IOException
     */
    protected Object updateSingleValue(XMLMap map, String key) throws IOException {
        String currentValue = map.getString(key);

        JSON json = null;
        if (currentValue != null) {
            // edge case to avoid  a &^*%@! JSON null object.
            // JSONNull means parsing a null into a JSON object that bombs everyplace like a regular null.,
            // i.e,. every operation throws the equivalent of an NPE.
            // They just do it so they have a typed null of type JSON...
            try {
                json = JSONSerializer.toJSON(currentValue);
            } catch (Throwable t) {
                // ok, it's not JSON
            }
        }

        if (json == null) {
            // This handles every other value type...
            String newValue = getPropertyHelp(key, "Enter new value for " + key + " ", currentValue);
            if (newValue.equals(currentValue)) {
                return false;
            }
            map.put(key, newValue);
            return newValue;
        }
        if (json != null && (json instanceof JSONObject)) {

            JSONObject newJSON = inputJSON((JSONObject) json, key);
            if (newJSON == null) {
                return false;
            } // user cancelled
            map.put(key, newJSON);
            return newJSON;

        }
        if (json != null && (json instanceof JSONArray)) {
            JSONArray newArray = updateSingleValue(key, (JSONArray) json);
            // really hard to tell if the array is updated in the general case.
            // so just always save it.
            if (newArray == null) {
                return null;
            }
            map.put(key, newArray);
            return newArray;
        }
        return null; // Just in case, do nothing.
    }


    protected JSONArray updateSingleValue(String key, JSONArray currentValue) throws IOException {
        say("current value=" + currentValue);
        String action = getInput("Add, clear, delete, replace or exit?(a/c/d/r/x)", "a").toLowerCase();
        if (action.equals("x")) {
            return null; // do nothing.
        }
        if (action.equals("r")) {
            say("Enter the new elements with commas between them");
        }
        String newValue = null;
        if (action.equals("d")) {
            newValue = getInput("Value to remove", "");
        } else {
            newValue = getInput("New value", "");
        }
        switch (action) {
            case "a":
                // Append a value to the list
                currentValue.add(newValue);
                return currentValue;
            case "c":
                // clear the entire contents
                currentValue.clear();
                return currentValue;
            case "d":
                // delete a single value in the list
                currentValue.remove(newValue);
                return currentValue;
            case "r":
                // replace the entire contents.
                currentValue.clear();
                if (newValue.equals("")) {
                    return currentValue;
                }
                StringTokenizer st = new StringTokenizer(newValue, ",");
                while (st.hasMoreElements()) {
                    String x = st.nextToken().trim();
                    if (!x.isEmpty()) {
                        // don't add missing elements
                        // so 'a,  ,, ,,b' is the same as 'a,b'
                        currentValue.add(x);
                    }
                }
                return currentValue;
        }
        say("sorry, I did not understand what you want to do.");
        return null;
    }

    /**
     * Slightly special case. This will look on the input line and extract a list of the form
     * <pre>
     *     [a,b,c,...]
     * </pre>
     * So to avoid having a lot of parsing (and the fact that there is pretty much at most one
     * array per line) this will take everything between [ ] and try to turn it in to a list.
     * The alternative would be make the list syntax have to conform to
     * {@link InputLine}'s fairly primitive system of checking for flags.
     *
     * @param inputLine
     * @return
     * @deprecated
     */
    protected List<String> getArgList(InputLine inputLine) {
        List<String> list = new ArrayList<>();
        String rawLine = inputLine.getOriginalLine();
        if (rawLine == null || rawLine.isEmpty()) {
            return list;
        }
        int startListIndex = rawLine.indexOf(LIST_START_DELIMITER);
        int endListIndex = rawLine.indexOf(LIST_END_DELIMITER);
        if (startListIndex == -1 || endListIndex == -1) {
            return list;
        }
        String rawList = rawLine.substring(startListIndex + 1, endListIndex);
        StringTokenizer st = new StringTokenizer(rawList, LIST_SEPARATOR);
        while (st.hasMoreElements()) {
            list.add(st.nextToken().trim());
        }

        return list;
    }

    String JSON_FLAG = "-json";
    EditorEntry editorEntry = null;

    public EditorEntry getEditorEntry() {
        if (editorEntry == null) {
            editorEntry = new EditorEntry();
            editorEntry.exec = "vim";
            editorEntry.name = "vim";
        }
        return editorEntry;
    }

    String TEMP_FILE = "/opt/cilogon-oa2/var/temp";
    File tempDir = null;

    protected File getTempDir() {
        if (tempDir == null) {
            tempDir = new File(TEMP_FILE);
            if (tempDir.exists()) {
                if (!tempDir.isDirectory()) {
                    say("sorry, but could not find the temp directory");
                }
            } else {
                tempDir.mkdirs();
            }
        }
        return tempDir;
    }

    /**
     * @param inputLine
     * @throws Exception
     * @experimental
     */
    public void archive(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showArchiveHelp();
            return;
        }
        // special case -- show accepts the version number. If we aren't careful, the version number
        // will get fed to the findItem call and give very bad results.
        // Intercept it here for later if needed.
        boolean isShow = inputLine.hasArg(ARCHIVE_SHOW_FLAG);
        String showArg = null;
        if (isShow) {
            showArg = inputLine.getNextArgFor(ARCHIVE_SHOW_FLAG);
            inputLine.removeSwitchAndValue(ARCHIVE_SHOW_FLAG);
        }


        if (inputLine.hasArg(ARCHIVE_RESTORE_FLAG)) {
            String rawID = inputLine.getLastArg();
            if (rawID.startsWith("/")) {
                rawID = rawID.substring(1);
            }
            String rawTargetVersion = inputLine.getNextArgFor(ARCHIVE_RESTORE_FLAG);

            boolean doLatest = rawTargetVersion.equals(ARCHIVE_LATEST_VERSION_ARG);
            long targetVersion = 0;

            if (doLatest) {
                targetVersion = -1L;
            } else {
                try {
                    targetVersion = Long.parseLong(rawTargetVersion);
                } catch (NumberFormatException nfx) {
                    say("sorry, but \"" + rawTargetVersion + "\" could not be parsed as a version number");
                    return;
                }
            }
            FoundIdentifiables identifiables = findItem(inputLine);
            if (identifiables == null) {
                say("sorry, no such object");
                return;
            }
            // Give them one last chance
            if (!identifiables.isSingleton()) {
                if (!getInput("Are you sure that you want to restore " + identifiables.size() + " items?(y/n)", "n").equals("y")) {
                    say("aborting...");
                    return;
                }

            }
            for (Identifiable identifiable : identifiables) {
                Identifiable storedVersion = getStoreArchiver().getVersion(identifiable.getIdentifier(), targetVersion);
                if (storedVersion == null) {
                    say("sorry, but the version you requested for id \"" + rawID + "\" does not exist.");
                }
                if (StoreArchiver.isVersioned(identifiable.getIdentifier())) {
                    say("object with id \"" + identifiable.getIdentifier() + "\" is an archived object. Cannot restore. skipping");
                    continue;
                }
                if (identifiables.isSingleton()) {
                    // A singleton can have a better message.
                    if (getInput("Are you sure that you want to replace the current version with version \"" + (targetVersion == -1 ? "latest" : targetVersion) + "\"?(y/n)", "n").equals("y")) {
                        storedVersion.setIdentifier(identifiable.getIdentifier());
                        getStore().save(storedVersion);
                        say("done! Note: you may have to e.g. approve this again if it is a client.");
                        return;
                    } else {
                        say("aborted.");
                    }
                } else {
                    storedVersion.setIdentifier(identifiable.getIdentifier());
                    getStore().save(storedVersion);
                }
            }
            if (!identifiables.isSingleton()) {
                say("done! " + identifiables.size() + " objects processed");
            }
            return;
        }
        FoundIdentifiables identifiables = findItem(inputLine);
        if (identifiables == null) {
            // they supplied non-existent id
            say("object not found.");
            return;
        }

          /*
                  Contract is that identifiers never have fragments -- these are used by the system for information.
                  In this case, a fragment of the form version_x where x is a non-negative integer is added.
                   */
        if (inputLine.hasArg(ARCHIVE_VERSIONS_FLAG)) {
            for (Identifiable identifiable : identifiables) {
                TreeMap<Long, Identifiable> sortedMap = getStoreArchiver().getVersionsMap(identifiable);
                if (sortedMap == null) {
                    say("(no versions)");
                    return;
                }
                say(sortedMap.size() + " versions of " + identifiable.getIdentifier() + ":");
                for (Long index : sortedMap.keySet()) {
                    say("   " + archiveFormat(sortedMap.get(index)));
                }
            }
            return;
        }

        if (isShow) {
            for (Identifiable identifiable : identifiables) {
                if (!identifiables.isSingleton()) {
                    say("----- object: " + identifiable.getIdentifier() + " -----");
                }
                Identifiable target = getVersion(showArg, identifiable);
                if (target == null) {
                    if (showArg.equals(ARCHIVE_LATEST_VERSION_ARG)) {
                        say("(no versions)");
                    } else {
                        say("no such version");
                    }
                    continue;
                }
                longFormat(target, true); // show everything!
                if (!identifiables.isSingleton()) {
                    say();
                }
            }
            return;
        }
        if (identifiables.size() == 1) {
            if (!getInput("Archive object \"" + identifiables.get(0).getIdentifierString() + "\"?(y/n)", "n").equals("y")) {
                say("aborted.");
                return;
            }
        } else {
            if (!getInput("Archive object " + identifiables.size() + " objects?(y/n)", "n").equals("y")) {
                say("aborted.");
                return;
            }
        }
        // If we are at this point, then the user wants to version the object
        for (Identifiable identifiable : identifiables) {
            DoubleHashMap<URI, Long> versionNumbers = getStoreArchiver().getVersions(identifiable.getIdentifier());
            long newIndex = 0L; // for first version if none found
            if (versionNumbers != null && !versionNumbers.isEmpty()) {
                newIndex = getStoreArchiver().getLatestVersionNumber(versionNumbers) + 1;
            }

            // last check
            if (newIndex < 0) {
                say("internal error: check logs");
                warn("error: in creating a version, a negative version number was encountered. This implies something is off with auto-numbering.");
                return;
            }
            // to and from map are charged with being faithful at all times, so we use these to clone the
            Identifiable newVersion = getStore().create();
            XMLMap map = new XMLMap();

            MapConverter mc = getMapConverter();
            mc.toMap(identifiable, map);
            mc.fromMap(map, newVersion);
            Identifier newID = getStoreArchiver().createVersionedID(identifiable.getIdentifier(), newIndex);
            newVersion.setIdentifier(newID);
            getStore().save(newVersion);
        }
    }

    /**
     * Get the version. The arguments are the version number (as a string) and the object. Returns
     * a null if there is no such version.
     *
     * @param versionString a string that is either a 0 < number, or the value {@link #ARCHIVE_LATEST_VERSION_ARG} for the latest
     * @param identifiable
     * @return
     */
    private Identifiable getVersion(String versionString, Identifiable identifiable) {
        long targetVersion = -1L;

        DoubleHashMap<URI, Long> versionNumbers = getStoreArchiver().getVersions(identifiable.getIdentifier());
        if (versionString.equalsIgnoreCase(ARCHIVE_LATEST_VERSION_ARG)) {
            targetVersion = getStoreArchiver().getLatestVersionNumber(versionNumbers);
        } else {
            try {
                targetVersion = Long.parseLong(versionString);
            } catch (NumberFormatException nfx) {
                return null;
            }
        }
        if (versionNumbers.getByValue(targetVersion) == null) {
            return null;
        }
        Identifiable target = (Identifiable) getStore().get(BasicIdentifier.newID(versionNumbers.getByValue(targetVersion)));
        return target;
    }


    String ARCHIVE_VERSIONS_FLAG = "-versions";
    String ARCHIVE_RESTORE_FLAG = "-restore";
    String ARCHIVE_SHOW_FLAG = "-show";
    String ARCHIVE_LATEST_VERSION_ARG = "latest";


    protected void showArchiveHelp() {
        String blanks = StringUtils.getBlanks(5);
        say("archive [" + ARCHIVE_VERSIONS_FLAG + "] | [" + ARCHIVE_RESTORE_FLAG + " version] | [" +
                ARCHIVE_SHOW_FLAG + " number | " + ARCHIVE_LATEST_VERSION_ARG + "] [index] - archive an object");
        say("Usage: This will either create a copy of the current version");
        sayi(blanks + "or restore a versioned object.");
        sayi("(no argument) - version the object, assigning it the last version.");
        sayi(ARCHIVE_VERSIONS_FLAG + " - list the versions of an object");
        sayi("archive - show the number of the latest version (-1 if no versions exist)");

        sayi(ARCHIVE_RESTORE_FLAG + " (number | " + ARCHIVE_LATEST_VERSION_ARG + ") - restore the given version");
        sayi(blanks + "of this. If a number is given, use that. If the word \"" + ARCHIVE_LATEST_VERSION_ARG + "\" (no quotes)");
        sayi(blanks + "is used, give back the latest version.");
        sayi(ARCHIVE_SHOW_FLAG + " (number | " + ARCHIVE_LATEST_VERSION_ARG + ") - show the given version. " +
                "You may also use the word \"" + ARCHIVE_LATEST_VERSION_ARG + "\"");
        sayi(blanks + "to get the latest version.");
        sayi("Note that archive version numbers increase, so the highest number is the most recent.");
    }

    protected String archiveFormat(Identifiable id) {
        return format(id);
    }


    public StoreArchiver getStoreArchiver() {
        if (storeArchiver == null) {
            storeArchiver = new StoreArchiver(getStore());
        }
        return storeArchiver;
    }

    StoreArchiver storeArchiver;


    public void update(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            showUpdateHelp();
            return;
        }
        String key = getAndCheckKeyArg(inputLine);
        List<String> keys;
        if (key != null) {
            keys = new ArrayList<>(1);
            keys.add(key);
        } else {
            keys = processList(inputLine, KEY_FLAG);
        }
        boolean hasKeys = keys != null && !keys.isEmpty();

        if (inputLine.hasArg(VALUE_FLAG) && inputLine.hasArg(FILE_FLAG)) {
            say("Sorry, you have specified both a value and a file for the value.");
            return;
        }
        if (!hasKeys) {
            oldUpdate(inputLine);
            return;
        }
        // Now we can do the more modern version of this.
        List value = null;
        if (inputLine.hasArg(VALUE_FLAG)) {
            value = processList(inputLine, VALUE_FLAG);
        } else {
            if (inputLine.hasArg(FILE_FLAG)) {
                try {
                    value = new ArrayList<>(1);
                    value.add(FileUtil.readFileAsString(inputLine.getNextArgFor(FILE_FLAG)));
                } catch (Throwable throwable) {
                    say("Sorry, but I could not seem to read the file named \"" + inputLine.getNextArgFor(FILE_FLAG) + "\"");
                    return;
                }
                inputLine.removeSwitchAndValue(FILE_FLAG);
            }
        }
        FoundIdentifiables identifiables = findItem(inputLine);
        if (identifiables == null) {
            throw new ObjectNotFoundException("sorry, I could not find that object. Check your id.");
        }
        if (keys.size() == 1) {
            if (value != null && value.size() != 1) {
                say("A single key with multiple values was found. Aborting...");
                return;
            }
        } else {
            if (value != null && keys.size() != value.size()) {
                say("Key/values size mismatch. Aborting...");
                return;
            }
        }
        for (int i = 0; i < identifiables.size(); i++) {
            Identifiable identifiable = identifiables.get(i);
            if (identifiables.isRS()) {
                // If we are using an RS this passes in the elements' identifiers we want to update
                identifiable = (Identifiable) getStore().get(identifiable.getIdentifier());
            }
            XMLMap map = toXMLMap(identifiable);

            if (value == null) {
                value = new ArrayList<>(keys.size());
                for (String kk : keys) {
                    // This makes it so only prompted on the first pass
                    Object vv = updateSingleValue(map, kk); // map is updated in call
                    value.add(vv);
                }
            } else {
                for (int j = 0; j < keys.size(); j++) {
                    map.put(keys.get(j), value.get(j));
                }
            }
            Identifiable updatedI = fromXMLMap(map);
            if (identifiables.isRS()) {
                identifiables.set(i, updatedI);
            }
            getStore().save(updatedI);
        }

    }

    public SerializationKeys getKeys() {
        return getMapConverter().getKeys();
    }


    /**
     * The contract is that this gets the entire current config and updates <i>exactly</i>
     * the bits relating to QDL. This is then saved elsewhere.
     *
     * @param currentConfig
     * @return
     */
    protected JSONObject loadQDLScript(JSONObject currentConfig) throws IOException {
        return currentConfig; // do nothing.
    }

    public void list_keys(InputLine inputLine) throws Exception {
        if (showHelp(inputLine)) {
            showListKeysHelp(inputLine);
            return;
        }
        XMLConverter xmlConverter = getStore().getXMLConverter();
        String primaryKey = null;
        if (xmlConverter instanceof MapConverter) {
            primaryKey = ((MapConverter) xmlConverter).getKeys().identifier();
        }
        if (xmlConverter instanceof MapConverter) {
            MapConverter mc = (MapConverter) xmlConverter;
            ArrayList<String> kk = new ArrayList<>();
            kk.addAll(mc.getKeys().allKeys());
            if (primaryKey != null) {
                for (int i = 0; i < kk.size(); i++) {
                    if (kk.get(i).equals(primaryKey)) {
                        // kk.remove(i);
                        kk.set(i, primaryKey + "*");
                        break;
                    }
                }
            }
            // print them in order.
            FormatUtil.formatList(inputLine, kk);
        }
    }

    protected void showListKeysHelp(InputLine inputLine) {
        say("list_keys");
        sayi("Usage: This lists the keys of the current store.");
        sayi("The primary key will have a '*' added to the end of it");
        FormatUtil.printFormatListHelp(getIOInterface(), INDENT, inputLine);
    }

    protected void showLSHelp3() {
        say("ls [" + LINE_LIST_COMMAND + "  | " + VERBOSE_COMMAND + " | " + ALL_LIST_COMMAND + "] | [" + KEY_FLAG + " key | " + KEYS_FLAG + " array] id");
        sayi("Usage: Lists information about the contents of the store, an entry and");
        sayi("   individual values of the entry.");
        sayi("When listing multiple entries, tools will use the most numbers from the most recent call to this.");
        sayi("A line listing is tabular and will shorten entries that are too long, ending them with " + ELLIPSIS);
        sayi("A verbose command will format every bit of every entry within the margins.");
        showKeyShorthandHelp();
        say("E.g.");
        sayi("ls " + LINE_LIST_COMMAND + "  " + ALL_LIST_COMMAND);
        sayi("Prints out the line form of *every* object in this store. This may be simply huge");
        say("E.g.");
        sayi("ls");
        sayi("Prints out the short form of *every* object in this store. This may also be huge.");
        sayi("If you are using this to find things, you probably want to look at the search command");
        say("E.g.");
        sayi("ls " + LINE_LIST_COMMAND + "  /foo:bar");
        sayi("Prints a line format for the entry with id foo:bar");
        say("E.g.");
        sayi("ls " + VERBOSE_COMMAND + " /foo:bar");
        sayi("prints out a verbose listing of the entry with id foo:bar.");
        say("E.g.");
        sayi("ls " + KEY_FLAG + " id /foo:bar");
        sayi(">   foo:bar");
        sayi("Prints out the id property for the object with identifier foo:bar");
        sayi("");
        sayi("You may also supply a list of keys in an array of the form [key0,key1,...].");
        say("E.g.");
        sayi("ls " + KEYS_FLAG + " [id,callback_uris,create_ts] /foo:bar");
        sayi("would print the id, callback_uri and create_ts properties for the object with id");
        sayi("foo:bar. ");
        sayi("\nSee also list_keys, search, archive");
    }

    public static String RM_FORCE_FLAG = "-force";

    protected void showRMHelp() {
        say("rm [" + KEY_FLAG + " | " + KEYS_FLAG + " list] [" + RM_FORCE_FLAG + "] index");
        sayi("Usage: remove an object or removes a property (or list of them) from an object.");
        sayi("If you supply a list, all of the properties in the list will be removed");
        sayi("No list of keys means to remove the entire object from the store (!)");
        sayi(RM_FORCE_FLAG + " = if included, the identifier is removed from the store directly");
        sayi("     without trying to load the object first. This means no checks are done before hand");
        sayi("     Only use this if e.g. the store itself cannot load the object and removing it is the only path forward.");
        showKeyShorthandHelp();
        printIndexHelp(false);
        say("E.g.");
        sayi("rm " + KEY_SHORTHAND_PREFIX + "error_uri foo:bar");
        sayi("Removes the value of the property 'error_uri' from the object with id foo:bar");
        say("E.g.");
        sayi("rm /foo:bar");
        sayi("removes the object with id foo:bar completely from the store");
        say("E.g.");
        sayi("rm " + KEYS_FLAG + " [error_uri,home_uri] foo:bar");
        sayi("removes the values of the properties error_uri and home_uri from the object with id");
        sayi("equal to foo:bar");
        sayi("rm " + RS_DROP_ACTION);
    }

    /**
     * Called if there is additional clean up needed. For instance, removing a client
     * requires removing its approval record. The contract states that this is called
     * <i>after</i> the objects have been removed from the main store.
     *
     * @param identifiable
     */
    protected void rmCleanup(FoundIdentifiables identifiable) {
    }


    public void bootstrap() throws Throwable {
        super.bootstrap();
        getHelpUtil().load("/store-help.xml");
    }

    public void clear_store(InputLine line) throws Exception {
        if (showHelp(line)) {
            say("clear_store -- clear every entry in the current store");
            say("This is mostly used in cases where there has been some great failure or");
            say("perhaps as a debugging aid. It will remove every entry in the currently");
            say("active store **unrecoverably**. Note this only works on precisely the current store,");
            say("so related stores (e.g. approvals for clients) have to be cleared separately.");
            say("This is inconvenient, but does prevent unwanted side effects.");
            say("You will always be prompted before this operation.");
            return;
        }
        if (getInput("Do you really want to clear every entry from this store?(YES/n)", "n").equals("YES")) {
            getStore().clear();
            say("done");
        } else {
            say("clear store skipped!");
        }
    }

    public static class ChangeIDRecord {
        public Identifiable identifiable;
        public Identifier newID;
        public Identifier oldID;
        public boolean updatePermissions = true;
        public int updateCount = 0;
    }

    /**
     * Does the work of changing the ID for an object. This returns the state object from basic changes (so the
     * object and it runs the {@link #updateStorePermissions(Identifier, Identifier, boolean)} method,
     * which you should override as needed. A typical use would be to change the ID's for a client, then use the
     * returned record to change the approval record.
     * <p><b>Note:</b>This is invoked after the changes to the base store items have been saved.</p>
     *
     * @param identifiable
     * @param newID
     * @param updatePermissions
     * @return
     */
    // Fix https://github.com/ncsa/security-lib/issues/45
    public ChangeIDRecord doChangeID(Identifiable identifiable, Identifier newID, boolean updatePermissions) {
        ChangeIDRecord changeIDRecord = new ChangeIDRecord();
        changeIDRecord.identifiable = identifiable;
        changeIDRecord.newID = newID;
        changeIDRecord.updatePermissions = updatePermissions;
        Identifier oldID = identifiable.getIdentifier();
        changeIDRecord.oldID = oldID;

        // update the object
        identifiable.setIdentifier(newID);
        getStore().save(identifiable);
        getStore().remove(oldID);
        // update the state in the CLI
        if (idList != null) {
            if (idList.size() != 1) {
                throw new IllegalArgumentException("too many ids set. Can only change 1.");
            }
            if (idList.get(0).equals(oldID)) {
                idList.set(0, newID);
            }
        }
        // update any permissions
        if (updatePermissions) {
            changeIDRecord.updateCount = updateStorePermissions(newID, oldID, false);
        }
        return changeIDRecord;
    }

    /**
     * Allows changing the identifier for an object. This also updates the permissions that
     * refer to this if the  {@link #SKIP_UPDATE_PERMISSIONS_FLAG} is set.
     *
     * @param inputLine
     */

    public void change_id(InputLine inputLine) throws Throwable {
        if (showHelp(inputLine)) {
            say("change_id new [" + SKIP_UPDATE_PERMISSIONS_FLAG + " on|off] index - change the identifier for a single client.");
            say(SKIP_UPDATE_PERMISSIONS_FLAG + " if on, change the identifier in any permissions too. Default is on");
            say("This will update all permissions. At the end of this operations, the");
            say("old identifier will not be in the system any longer.");
            printIndexHelp(true);
            return;
        }

        Boolean updatePermissions = true;
        if (inputLine.hasArg(SKIP_UPDATE_PERMISSIONS_FLAG)) {
            updatePermissions = inputLine.getBooleanNextArgFor(SKIP_UPDATE_PERMISSIONS_FLAG);
            if (updatePermissions == null) {
                say("unrecognized argument for " + SKIP_UPDATE_PERMISSIONS_FLAG);
                return;
            }
            inputLine.removeSwitch(SKIP_UPDATE_PERMISSIONS_FLAG);
        }

        Identifiable identifiable = findSingleton(inputLine); // get it so the positional argument next is right!
        String rawID;
        if (inputLine.getArgCount() == 0) {
            rawID = getInput("Missing identifier. Please enter it:", "");
        } else {
            rawID = inputLine.getArg(1);
        }
        URI uri;
        try {
            uri = URI.create(rawID);
            // really stupid check for a bad identifier. If they get their command line arguments out of whack this should catch it
            if (isBadID(uri)) {
                if (!"y".equals(getInput("The identifier you gave \"" + rawID + "\" is possibly incorrect. Proceed(y/n)?", "n"))) {
                    say("aborted...");
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            say("identifier is not a URI:" + e.getMessage());
            return;
        }
        Identifier newID = BasicIdentifier.newID(uri);
        Identifier oldID = identifiable.getIdentifier();
        if (newID.equals(identifiable.getIdentifier())) {
            say("the source and target are the same, no changes made.");
            return;
        }
        if (getStore().containsKey(newID)) {
            say("there is already an entry in the store with id \"" + newID + "\". aborting...");
            return;
        }
        ChangeIDRecord changeIDRecord = doChangeID(identifiable, newID, updatePermissions);
        say(oldID + " has been changed to " + newID);
        // update any permissions
        if (updatePermissions) {
            say(changeIDRecord.updateCount + " permissions have been updated");
        }
    }

    protected boolean isBadID(URI uri) {
        if (uri.getPath() == null) {
            if (uri.getSchemeSpecificPart() == null) {
                return true;
            }
            return false;
        } else {
            if (uri.getSchemeSpecificPart() == null) {
                return true;
            }
            return uri.getPath().equals(uri.getSchemeSpecificPart());
        }
    }
 /*   protected boolean hasRS(InputLine inputLine) {
        return inputLine.hasArg(RESULT_SET_KEY);
    }*/

    /**
     * Does all the checks for a command that accepts a single store object.
     * Throws an exception if not found. Note that this returns the
     * object from the store
     *
     * @param inputLine
     * @return
     */
    protected Identifiable findSingleton(InputLine inputLine, String errorMessage) throws Throwable {
        FoundIdentifiables ii = findItem(inputLine);
        if (ii == null || ii.isEmpty()) {
            throw new ObjectNotFoundException(errorMessage);
        }
        if (!ii.isSingleton()) {
            throw new ObjectNotFoundException("Only a single object is supported.");
        }
        if (ii.isRS()) {
            return (Identifiable) getStore().get(ii.get(0).getIdentifier());
        }
        return ii.get(0);
    }

    protected Identifiable findSingleton(InputLine inputLine) throws Throwable {
        return findSingleton(inputLine, "object not found");
    }

    protected void printIndexHelp(boolean singletonsOnly) {
        if (singletonsOnly) {
            say("Note: This component only operates on a single object");
        }
        say("To see help on how to specify an index, issue");
        say(LIST_ALL_METHODS_COMMAND + " index");
    }

    // Actually, any juxtaposed dyadic QDL operators (except ++, --) work,
    // since if somehow it gets in the parser
    // it will blow up. Monadic ones could be misinterpreted, e.g. !!
    public static final String LIST_EOL_MARKER = "^^";

    /**
     * Assumes there is a key and the original line is of the form -key [x, y, ... ].
     * *If the user has a more complex list they can terminate it with {@link #LIST_EOL_MARKER},
     * e.g.
     * <pre>
     *     my_command -foo -my_list [3, 5, -1] !! -other_flag fnord
     * </pre>
     * <p>
     * Tells this function that everything from -my_list to !! is a single expression.
     * <p>
     * Otherwise, this extracts everything between the [ and ] inclusive (most common case).
     * It then truncates the original line
     * and reparses it. This allows for the "whittle while you work" approach to input lines.
     * </p>
     * <p>Note that there is a call for getting a list, {@link InputLine#getArgList(String)}
     * which does this, but with very simple logic.
     * The difference is that the {@link #processList(InputLine, String)}
     * function in this class should allow for executing the lists as QDL later. </p>
     * <p>This utility is used in other implementations of {@link #processList(InputLine, String)}</p>
     *
     * @param inputLine
     * @param key
     * @return
     */
    protected String extractRawList(InputLine inputLine, String key) {
        String keyArg = inputLine.getNextArgFor(key);
        boolean isList = keyArg != null && keyArg.startsWith(LIST_START_DELIMITER);
        if (isList) {
            String originalLine = inputLine.getOriginalLine();
            int startKeyIndex = originalLine.indexOf(key); // index where key is found
            int startStmtIndex = startKeyIndex + key.length(); // index where the list statement begins
            int endStmtIndex = -1;
            int eolIndex = originalLine.indexOf(LIST_EOL_MARKER); // index of EOL, if present
            String list;
            if (eolIndex == -1) {
                eolIndex = originalLine.indexOf(LIST_END_DELIMITER, startKeyIndex);
                if (eolIndex == -1) {
                    throw new ObjectNotFoundException("mal-formed list -- no end of list found");
                }
                endStmtIndex = eolIndex + LIST_END_DELIMITER.length(); // index of last char in list statement
                list = originalLine.substring(startStmtIndex, eolIndex + 1); // include end delimiter
            } else {
                endStmtIndex = eolIndex + LIST_EOL_MARKER.length(); // index of last char in list statement
                list = originalLine.substring(startStmtIndex, eolIndex); // exclude end delimiter
            }
            // clean up
            String newOL = originalLine.substring(0, startKeyIndex) + " " + originalLine.substring(endStmtIndex);
            inputLine.setOriginalLine(newOL);
            inputLine.reparse();
            return list;
        }
        return "[" + keyArg + "]";
    }

}


/*
     SQL Command to get non-versions from client store:
     SELECT client_id FROM clients WHERE client_id  NOT LIKE '%#version%';

     Counting non-versions:
     SELECT count(*)  FROM clients WHERE client_id  NOT LIKE '%#version%';
     */