package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.AbstractListeningStore;
import edu.uiuc.ncsa.security.storage.cli.StoreArchiver;
import edu.uiuc.ncsa.security.storage.monitored.Monitored;
import edu.uiuc.ncsa.security.util.configuration.XMLConfigUtil;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.*;
import static edu.uiuc.ncsa.security.core.util.TokenUtil.b32Encode;

/**
 * Utilities for processing upkeep configurations found in a server configuration file.
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/24 at  1:21 PM
 */
public class UpkeepConfigUtils extends XMLConfigUtil {
    public static final String UPKEEP_TAG = "upkeep";
    public static final String UPKEEP_INTERVAL = "interval";
    public static final String UPKEEP_ALARMS = "alarms";
    public static final String UPKEEP_TEST_ONLY = "testOnly";
    public static final String UPKEEP_DEBUG = "debug";
    public static final String UPKEEP_ENABLED = "enabled";
    public static final Long UPKEEP_DEFAULT_INTERVAL = 1000 * 3600 * 6L; // 6 hours
    public static final String RULE_TAG = "rule";
    public static final String RULE_NAME = "name";
    public static final String RULE_ACTION = "action";
    public static final String RULE_ENABLED = "enabled";
    public static final String RULE_EXTENDS = "extends";
    public static final String LIST_TAG = "list";
    public static final String LIST_ID = "id";
    public static final String REGEX_FLAG = "regex";
    public static final String DATE_TAG = "date";
    public static final String DATE_TYPE = "type";
    public static final String DATE_WHEN = "when";
    public static final String DATE_VALUE = "value";
    public static final long DATE_NO_VALUE = -1L;

    public static UpkeepConfiguration processUpkeep(ConfigurationNode upkeepNode) {
        UpkeepConfiguration upkeepConfiguration = new UpkeepConfiguration();
        if (upkeepNode == null) {
            upkeepConfiguration.setEnabled(false);
            return upkeepConfiguration;
        }
        upkeepConfiguration.setEnabled(getFirstBooleanValue(upkeepNode, UPKEEP_ENABLED, true));
        upkeepConfiguration.setDebug(getFirstBooleanValue(upkeepNode, UPKEEP_DEBUG, false));
        upkeepConfiguration.setTestOnly(getFirstBooleanValue(upkeepNode, UPKEEP_TEST_ONLY, false));
        upkeepConfiguration.setAlarms(getAlarms(upkeepNode, UPKEEP_ALARMS));
        String raw = getFirstAttribute(upkeepNode, UPKEEP_INTERVAL);
        // contract is to use default interval if neither alarms nor interval is set.
        if (StringUtils.isTrivial(raw)) {
            if (!upkeepConfiguration.hasAlarms()) {
                upkeepConfiguration.setInterval(UPKEEP_DEFAULT_INTERVAL);
            }
        } else {
            upkeepConfiguration.setInterval(XMLConfigUtil.getValueSecsOrMillis(raw, true));
        }
        List<ConfigurationNode> ruleNodes = upkeepNode.getChildren(RULE_TAG);
        for (ConfigurationNode ruleNode : ruleNodes) {
            RuleList rule = createRuleList(ruleNode);
            if (rule != null) {
                upkeepConfiguration.add(rule);
            }
        }


        // Setting nothing sets default intervale
        // Fix https://github.com/ncsa/oa4mp/issues/139


        return upkeepConfiguration;
    }

    static SecureRandom secureRandom = new SecureRandom();

    protected static String getRandomRuleName() {
        byte[] b = new byte[5]; // use base32 which is 5 bits, so this matches with no padding.
        secureRandom.nextBytes(b);
        return "rule_" + b32Encode(b).toLowerCase();
    }
     protected static RuleEntry createRuleEntry(ConfigurationNode ruleEntryNode){
         RuleEntry ruleEntry = null;
         switch (ruleEntryNode.getName()){
             case DATE_TAG:
                 String type = getFirstAttribute(ruleEntryNode, DATE_TYPE);
                 String when = getFirstAttribute(ruleEntryNode, DATE_WHEN);
                 String rawDate = getFirstAttribute(ruleEntryNode, DATE_VALUE);
                 DateValue dateValue = createDateValue(rawDate);
                 ruleEntry = new DateEntry(type, when, dateValue);
                 break;
             case LIST_ID:
                 Object obj = ruleEntryNode.getValue();
                 if(!(obj instanceof String)){
                     throw new IllegalArgumentException("unknown id type \"" + obj +"\"");
                 }
                 ruleEntry = new IDEntry(getFirstBooleanValue(ruleEntryNode, REGEX_FLAG, false), (String)obj );
                 break;
             default:
                 throw new IllegalArgumentException("unknown rule type \"" + ruleEntryNode.getName() + "\"");
         }
       return ruleEntry;
     }
    private static RuleList createRuleList(ConfigurationNode ruleNode) {
        RuleList ruleList = new RuleList();
        ruleList.setEnabled(getFirstBooleanValue(ruleNode, RULE_ENABLED, true));
        ruleList.setAction(getFirstAttribute(ruleNode, RULE_ACTION));
        String name = getFirstAttribute(ruleNode, RULE_NAME);
        if (StringUtils.isTrivial(name)) {
            name = getRandomRuleName();
        }
        ruleList.setName(name);
        // need to process in order
        List<ConfigurationNode> kids = ruleNode.getChildren();
        for(ConfigurationNode kid : kids){
            RuleEntry ruleEntry = createRuleEntry(kid);
            if(ruleEntry != null) {
                ruleList.add(ruleEntry);
            }
        }
      return ruleList;
    }


    protected static DateValue createDateValue(String rawDate) {
        DateValue dateValue;
        if (StringUtils.isTrivial(rawDate)) {
            return new DateValue(DATE_NO_VALUE);
        }

        try {
            Calendar calendar = Iso8601.string2Date(rawDate);
            dateValue = new DateValue(calendar.getTime());
        } catch (ParseException e) {
            dateValue = new DateValue(getValueSecsOrMillis(rawDate, true));
        }
        return dateValue;
    }


    public static void main(String[] args) {
        // For testing only. Takes an XML file of exactly the configuration and creates a
        // configuration object. The alternative is to fire up, say, all of OA4MP to try
        // and debug it as part of a configuration.
        String testFile = "/home/ncsa/dev/ncsa-git/security-lib/storage/src/main/resources/upkeep_test.xml";
        File f = new File(testFile);
        XMLConfiguration xmlConfiguration = Configurations.getConfiguration(f);
        ConfigurationNode root = xmlConfiguration.getRoot();
        UpkeepConfiguration upkeepConfiguration = processUpkeep(root);
        System.out.println(upkeepConfiguration);
    }

    /**
     * This is used for an entire store that has no other way to access elements,
     * e.g. a FileStore. This is going to be slow and espensive for all but smallish
     * stores, so if you really need to have a large number of entries, consider some form
     * of SQL or other supported database.
     * @param store
     * @return
     */
    public UpkeepResponse upKeepStore(Store<Identifiable> store){
        UpkeepResponse upkeepResponse = new UpkeepResponse();
        if(!(store instanceof AbstractListeningStore)){
            return null; // unsupported operation.
        }
        AbstractListeningStore als = (AbstractListeningStore) store;
        if(!als.hasUpkeepConfiguration()){
            return null;
        }
        UpkeepConfiguration cfg = als.getUpkeepConfiguration();
        if(!cfg.isEnabled()){
            return null;
        }
        StoreArchiver storeArchiver = new StoreArchiver(store);
        List<Identifiable> toDelete = new ArrayList<>();
        for(Identifiable v : store.values()){
              Monitored monitored = (Monitored)  v;
            for(RuleList ruleList : cfg.getRuleList()){
               boolean applies =  ruleList.applies(monitored);
            }

        }
        return upkeepResponse;
    }

}
