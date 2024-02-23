package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import edu.uiuc.ncsa.security.util.configuration.XMLConfigUtil;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.*;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;
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
    public static final int UPKEEP_DEFAULT_RUN_COUNT = -1; // no counting
    public static final String UPKEEP_VERBOSE = "verbose";
    public static final String UPKEEP_OUTPUT = "output";
    public static final String UPKEEP_RUN_COUNT = "runCount";
    public static final String RULE_TAG = "rule";
    public static final String RULE_NAME = "name";
    public static final String RULE_ACTION = "action";
    public static final String RULE_ENABLED = "enabled";
    public static final String RULE_EXTENDS = "extends";
    public static final String RULE_SKIP_VERSIONS = "skipVersions";
    public static final String ID_TAG = "id";
    public static final String ID_REGEX_FLAG = "regex";
    public static final String ID_NOT_FLAG = "negate";
    public static final String DATE_TAG = "date";
    public static final String DATE_TYPE = "type";
    public static final String DATE_WHEN = "when";
    public static final String DATE_VALUE = "value";
    public static final long DATE_NO_VALUE = -1L;

    /**
     * Processes the XML configuration and returns the {@link UpkeepConfiguration} for
     * the given store.
     *
     * @param upkeepNode
     * @return
     */
    public static UpkeepConfiguration processUpkeep(ConfigurationNode upkeepNode) {
        UpkeepConfiguration upkeepConfiguration = new UpkeepConfiguration();
        if (upkeepNode == null || !upkeepNode.getName().equals(UPKEEP_TAG)) {
            //  upkeepConfiguration.setEnabled(false);
            return null;
        }
        upkeepConfiguration.setEnabled(getFirstBooleanValue(upkeepNode, UPKEEP_ENABLED, true));
        upkeepConfiguration.setDebug(getFirstBooleanValue(upkeepNode, UPKEEP_DEBUG, false));
        upkeepConfiguration.setTestOnly(getFirstBooleanValue(upkeepNode, UPKEEP_TEST_ONLY, false));
        upkeepConfiguration.setAlarms(getAlarms(upkeepNode, UPKEEP_ALARMS));
        upkeepConfiguration.setVerbose(getFirstBooleanValue(upkeepNode, UPKEEP_VERBOSE, false));
        upkeepConfiguration.setSkipVersions(doSkipVersions(upkeepNode));

        upkeepConfiguration.setVerbose(getFirstBooleanValue(upkeepNode, RULE_SKIP_VERSIONS, true));
        upkeepConfiguration.setOutput(getFirstAttribute(upkeepNode, UPKEEP_OUTPUT));
        String raw = getFirstAttribute(upkeepNode, UPKEEP_RUN_COUNT);
        if (!isTrivial(raw)) {
            try {
                upkeepConfiguration.setRunCount(Integer.parseInt(raw));
            } catch (NumberFormatException nfx) {
                upkeepConfiguration.setRunCount(UPKEEP_DEFAULT_RUN_COUNT);
            }
        }
        raw = getFirstAttribute(upkeepNode, UPKEEP_INTERVAL);
        // contract is to use default interval if neither alarms nor interval is set.
        if (isTrivial(raw)) {
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
        // Now to process extends clauses
        for (RuleList ruleList : upkeepConfiguration.getRuleList()) {
            if (ruleList.getExtendsList() != null && !ruleList.getExtendsList().isEmpty()) {
                RuleList targetRL = new RuleList();
                /*
                  This is very simple-minded. The assumption is that these lists are well-behaved.
                  Future improvement would be to recurse all the extensions for cycles and
                  rank dependencies but that is a large-scale undertaking.

                  Mebbe...

                The argument for keeping is really dumb is that this facility is for deleting things
                from stores and a mistake might be very costly indeed to recover from. Making this stupid and
                prone to just failing outright means the administrator has to do the work and can't just
                throw configurations at it.
                 */

                for (String rlName : ruleList.getExtendsList()) {
                    RuleList sourceRL = upkeepConfiguration.getRulesMap().get(rlName);
                    if (sourceRL == null) {
                        throw new IllegalArgumentException("Cannot find rule with name \"" + rlName + "\"");
                    } else {
                        targetRL = doExtension(sourceRL, targetRL);
                    }
                } //end for
                targetRL = doExtension(ruleList, targetRL); // add the source at the end
                targetRL.setEnabled(ruleList.isEnabled()); // this is not set in the method.
                targetRL.setName(ruleList.getName());
                // now list surgery
                upkeepConfiguration.getRulesMap().put(targetRL.getName(), targetRL);
                for (int i = 0; i < upkeepConfiguration.getRuleList().size(); i++) {
                    if (upkeepConfiguration.getRuleList().get(i).getName().equals(targetRL.getName())) {
                        upkeepConfiguration.getRuleList().set(i, targetRL);
                        break;
                    }
                }
            }
        }
        return upkeepConfiguration;
    }

    protected static boolean doSkipVersions(ConfigurationNode node) {
        String raw = getFirstAttribute(node, RULE_SKIP_VERSIONS);
        if (!isTrivial(raw)) {
            // We DON'T want Boolean to parse this since it returns false if the value is not exactly "true"
            // The value is a Boolean hence overloaded and we need to know if it is null to know if
            // it is overridden later.
            if (raw.equals("true")) {
                return Boolean.TRUE;
            }
            if (raw.equals("false")) {
                return Boolean.FALSE;
            }
        }
        // Yes blow up here, since if this ends up being wrong, it could unintentionally
        // delete all of the versions of an object,
        // which is very bad.
        throw new IllegalArgumentException("could not parse " + RULE_SKIP_VERSIONS + " value of \"" + raw + "\"");
    }

    /**
     * Takes the attributes and rules of the source rule list = parent and
     * overwrites targetRL.
     *
     * @param sourceRL
     * @param targetRL
     */
    protected static RuleList doExtension(RuleList sourceRL, RuleList targetRL) {
        targetRL.setSkipVersions(sourceRL.isSkipVersions());
        targetRL.setVerbose(sourceRL.isVerbose());
        if (sourceRL.getAction() != null) {
            targetRL.setAction(sourceRL.getAction());
        }
        //targetRL.setName(sourceRL.getName());
        targetRL.addAll(sourceRL);
        return targetRL;
    }

    static SecureRandom secureRandom = new SecureRandom();

    protected static String getRandomRuleName() {
        byte[] b = new byte[5]; // use base32 which is 5 bits, so this matches with no padding.
        secureRandom.nextBytes(b);
        return "rule_" + b32Encode(b).toLowerCase();
    }

    protected static RuleEntry createRuleEntry(ConfigurationNode ruleEntryNode) {
        RuleEntry ruleEntry = null;
        switch (ruleEntryNode.getName()) {
            case DATE_TAG:
                String type = getFirstAttribute(ruleEntryNode, DATE_TYPE);
                String when = getFirstAttribute(ruleEntryNode, DATE_WHEN);
                String rawDate = getFirstAttribute(ruleEntryNode, DATE_VALUE);
                DateValue dateValue = createDateValue(rawDate);
                ruleEntry = new DateEntry(type, when, dateValue);
                break;
            case ID_TAG:
                Object obj = ruleEntryNode.getValue();
                if (!(obj instanceof String)) {
                    throw new IllegalArgumentException("unknown id type \"" + obj + "\"");
                }
                String v = (String)obj;
                if(isTrivial(v)){
                    throw new IllegalArgumentException("missing value for " + ID_TAG + " element" );
                }

                IDEntry idEntry = new IDEntry(getFirstBooleanValue(ruleEntryNode, ID_REGEX_FLAG, false), (String) obj);
                idEntry.setNegation(getFirstBooleanValue(ruleEntryNode, ID_NOT_FLAG, false));
                ruleEntry = idEntry;
                break;
            default:
                throw new MyConfigurationException("unknown rule type \"" + ruleEntryNode.getName() + "\"");
        }
        return ruleEntry;
    }

    private static RuleList createRuleList(ConfigurationNode ruleNode) {
        RuleList ruleList = new RuleList();
        ruleList.setEnabled(getFirstBooleanValue(ruleNode, RULE_ENABLED, true));
        ruleList.setAction(getFirstAttribute(ruleNode, RULE_ACTION));
        ruleList.setVerbose(getFirstBooleanValue(ruleNode, UPKEEP_VERBOSE, false));
        ruleList.setSkipVersions(doSkipVersions(ruleNode));
        String rawList = getFirstAttribute(ruleNode, RULE_EXTENDS);
        if (!isTrivial(rawList)) {
            List<String> rr = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(rawList, ",");
            while (tokenizer.hasMoreTokens()) {
                rr.add(tokenizer.nextToken().trim());
            }
            ruleList.setExtendsList(rr);
        }

        String name = getFirstAttribute(ruleNode, RULE_NAME);
        if (isTrivial(name)) {
            name = getRandomRuleName();
        }
        ruleList.setName(name);
        // need to process in order
        List<ConfigurationNode> kids = ruleNode.getChildren();
        for (ConfigurationNode kid : kids) {
            RuleEntry ruleEntry = createRuleEntry(kid);
            if (ruleEntry != null) {
                ruleList.add(ruleEntry);
            }
        }
        return ruleList;
    }


    protected static DateValue createDateValue(String rawDate) {
        DateValue dateValue;
        if (isTrivial(rawDate)) {
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




}
