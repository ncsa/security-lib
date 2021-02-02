package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.inheritance.AliasAndOverrides;
import edu.uiuc.ncsa.security.core.inheritance.InheritanceMap;
import edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.util.*;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.getConfiguration;
import static edu.uiuc.ncsa.security.core.configuration.Configurations.getFirstAttribute;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * Configuration Utility that allows for inheritance from other configurations
 * <p>Created by Jeff Gaynor<br>
 * on 1/31/21 at  4:59 PM
 */
public class Configurations2 {
    public static final String OVERRIDES_TAG = "overrides";
    public static final String NAME_TAG = "name";
    public static final String ALIAS_TAG = "alias";

    Map<String, ConfigurationNode> allNodes = new HashMap<>();
    InheritanceMap namedNodes = new InheritanceMap();
    InheritanceMap unresolvedAliases = new InheritanceMap(); // alias + node
    InheritanceMap resolvedAliases = new InheritanceMap(); // alias + node
    InheritanceMap unresolvedParents = new InheritanceMap(); // name  + list
    Map<String, List<String>> resolvedParents = new HashMap<>();

    public MultipleInheritanceEngine getInheritanceEngine() {
        return inheritanceEngine;
    }

    MultipleInheritanceEngine inheritanceEngine;

    /*
    One major difference between this and the old way is that inheritance means that the entire configuration
    has to be processed rather than before where only the specific requested named configuration would ne
    resolved (so if there were alias cycles some place else, they would not get found.
    Same with duplicate named configurations.
     */
    protected void ingestConfig(XMLConfiguration cfg,
                                String topNodeTag // the tag of elements to look at, e.g. service
    ) {

        List list = cfg.configurationsAt(topNodeTag);

        for (int i = 0; i < list.size(); i++) {
            SubnodeConfiguration cn = (SubnodeConfiguration) list.get(i);
            String name = getFirstAttribute(cn.getRootNode(), NAME_TAG);
            if (isTrivial(name)) {
                throw new MyConfigurationException("Missing name. The configuration does not contain a name. Configuration load is aborted.");
            }
            if (allNodes.containsKey(name)) {
                throw new MyConfigurationException("Duplicated name \"" + name + "\"  Configuration load is aborted.");

            }
            allNodes.put(name, cn.getRootNode());
            String alias = getFirstAttribute(cn.getRootNode(), ALIAS_TAG);
            String parents = getFirstAttribute(cn.getRootNode(), OVERRIDES_TAG);
            if (parents != null) {
                unresolvedParents.put(name, splitParentList(parents));
            }

            if (alias == null) {
                namedNodes.put(name, name, splitParentList(parents));
                if (parents != null) {
                    unresolvedParents.put(name, splitParentList(parents));
                }

            } else {
                unresolvedAliases.put(name, alias, splitParentList(parents));
            }
        }
        inheritanceEngine = new MultipleInheritanceEngine(namedNodes, unresolvedAliases, unresolvedParents);
        inheritanceEngine.resolve();
  }

    public List<ConfigurationNode> getNamedConfig(String cfgName) {
        // everything has been resolved.
        // Get the list of names to return
        if (!allNodes.containsKey(cfgName)) {
            throw new MyConfigurationException("Configuration \"" + cfgName + "\" not found. Configuration load is aborted.");
        }

        List<String> nodeNames = new ArrayList<>();

        if (resolvedAliases.containsKey(cfgName)) {
            nodeNames.addAll(resolvedParents.get(cfgName));
        } else {
            nodeNames.add(cfgName);
            nodeNames.addAll(namedNodes.get(cfgName).getOverrides());
        }
        System.out.println("        returning:" + nodeNames);

        List<ConfigurationNode> nodes = new ArrayList<>();

        for (String targetName : nodeNames) {
            nodes.add(allNodes.get(targetName));
        }
        return nodes;

    }

    protected InheritanceMap resolveAlias() {
        InheritanceMap resolvedAliases = new InheritanceMap();
        for (String key : unresolvedAliases.keySet()) {
            String currentKey = key;
            String currentValue = unresolvedAliases.get(key).getAlias();
            TreeSet<String> checkedAliases = new TreeSet<>();
            List<String> runningList = new ArrayList<>();
            runningList.addAll(unresolvedAliases.get(key).getOverrides());
            AliasAndOverrides newAPs = new AliasAndOverrides(currentValue, runningList);
            while (!namedNodes.containsKey(currentValue)) {
                if (checkedAliases.contains(currentKey)) {
                    throw new MyConfigurationException("Error: cyclic alias \"" + currentKey);
                }
                checkedAliases.add(currentKey);
                currentKey = currentValue;
                currentValue = unresolvedAliases.get(currentKey).getAlias();
                runningList.addAll(unresolvedAliases.get(currentKey).getOverrides());
            }
            List<String> xxx = new ArrayList<>();
            xxx.add(currentValue);
            xxx.addAll(newAPs.getOverrides());
            xxx.addAll(namedNodes.get(currentValue).getOverrides());
            newAPs.setOverrides( xxx);
            resolvedAliases.put(key, newAPs);
        }
        return resolvedAliases;

    }



    public static final String LIST_DELIMITERS = " ,;:/&";
    public static final String DEFAULT_PARENT_LIST_DELIMITER = " ";

  /*  protected void resolveParents(String targetParent,
                                  LinkedHashSet<String> currentList // Critical class choice.
    ) {

        if (unresolvedParents.containsKey(targetParent)) {
            // if the target is not on the list, add it.
            if (resolvedAliases.containsKey(targetParent)) {
                currentList.add(resolvedAliases.get(targetParent).getAlias());
            } else {
                currentList.add(targetParent);
            }
            // now resolve any targets.
            List<String> targets = splitParentList(unresolvedParents.get(targetParent));
            for (String target : targets) {
                if (resolvedAliases.containsKey(target)) {
                    resolveParents(resolvedAliases.get(target).getAlias(), currentList);
                } else {
                    currentList.add(target); // not on current list yet.
                    resolveParents(target, currentList);
                }
            }
        }
        if (resolvedAliases.containsKey(targetParent)) {
            currentList.add(resolvedAliases.get(targetParent).getAlias());
        } else {
            currentList.add(targetParent);
        }
    }*/

    // If the list starts with one of the LIST_DELIMTERS, use that
    // otherwise, use a blank
    // E.g. a list of
    // ;A;B;C
    // means ; is the delimiter.

    protected static List<String> splitParentList(String rawList) {
        List<String> output = new ArrayList<>();
        if (isTrivial(rawList)) {
            return output;
        }
        String delimiter = DEFAULT_PARENT_LIST_DELIMITER;
        if (-1 != LIST_DELIMITERS.indexOf(rawList.substring(0, 1))) {
            delimiter = rawList.substring(0, 1);
        }
        StringTokenizer st = new StringTokenizer(rawList, delimiter);
        while (st.hasMoreTokens()) {
            String r = st.nextToken();
            if (isTrivial(r)) {
                continue;
            }
            output.add(r);
        }
        return output;
    }

/*    protected Map<String, List<String>> resolveParents() {
        Map<String, List<String>> resolvedParents = new HashMap<>();
        for (String key : unresolvedParents.keySet()) {
            if (key.equals("E")) {
                System.out.println("E");
            }
            List<String> rawList = splitParentList(unresolvedParents.get(key));

            LinkedHashSet<String> currentParents = new LinkedHashSet<>();
            // Add the key since that is the configuration that should be
            // checked first
            if (resolvedAliases.containsKey(key)) {
                currentParents.add(resolvedAliases.get(key).getAlias());
            } else {
                currentParents.add(key);
            }
            // Kicks off recursive call for parents
            for (String target : rawList) {
                resolveParents(target, currentParents);
                List<String> rp2 = new ArrayList<>();
                rp2.addAll(currentParents);
                resolvedParents.put(key, rp2);
            }

        }

        return resolvedParents;
    }*/

    protected Map<String, List<String>> resolveParents2(Map<String, String> unresolvedParents
                                                        ) {
        Map<String, List<String>> resolvedParents = new HashMap<>();
        for (String key : resolvedAliases.keySet()) {
            List<String> parents = null;
            parents = resolvedAliases.get(key).getOverrides();
            LinkedHashSet<String> currentParents = new LinkedHashSet<>();
            currentParents.addAll(parents); // preserves order, will not add duplicates.
            List<String> output = new ArrayList<>();
            output.addAll(currentParents);
            resolvedParents.put(key, output);

        }
        for (String key : namedNodes.keySet()) {
            List<String> parents = null;
            parents = namedNodes.get(key).getOverrides();
            LinkedHashSet<String> currentParents = new LinkedHashSet<>();
            currentParents.addAll(parents); // preserves order, will not add duplicates.
            List<String> output = new ArrayList<>();
            output.addAll(currentParents);
            resolvedParents.put(key, output);

        }

        return resolvedParents;
    }

    public static void main(String[] args) {
        try {

            String path = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-core/src/test/resources/cfg_loader/";
            String fileName;
            String cfgName;

            //fileName = path + "min-test.xml"; cfgName="A";
            //fileName = path + "min-alias-test.xml"; cfgName="A";
            //fileName = path + "min-alias-no-inherit.xml"; cfgName = "A";
            //fileName = path + "override-in-alias-test.xml"; cfgName = "A";
            fileName = path + "multiple-overrides-test.xml"; cfgName = "A";
            //  fileName = path + "diamond-test.xml";cfgName = "D";
            String clientFile = "/home/ncsa/dev/csd/config/clients.xml";


            //XMLConfiguration xmlConfiguration = getConfiguration(new File(clientFile));
            //ConfigurationNode nodes = ingestConfig(xmlConfiguration, "client");

            XMLConfiguration xmlConfiguration = getConfiguration(new File(fileName));
            Configurations2 configurations2 = new Configurations2();
            configurations2.ingestConfig(xmlConfiguration, "service");
            //System.out.println(configurations2.getNamedConfig(cfgName));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
