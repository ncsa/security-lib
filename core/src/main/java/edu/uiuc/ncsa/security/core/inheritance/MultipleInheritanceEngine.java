package edu.uiuc.ncsa.security.core.inheritance;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;

import java.util.*;

/**
 * Resolves ordered multiple inheritance lists, used e.g. in configurations.
 * This only works on names and relations between them. It returns a list of
 * names (all names and aliases are assumed unique).
 * <h3>Multiple Inheritance Model</h3>
 * <p>There are many multiple inheritance schemes. This supports linearly order-based inheritance.
 * So this engine gets lists of overrides (unique names) and aliases (unique) and makes a list. It is then
 * up to the calling program to navigate the inheritance order. This allows for an easy solution to the
 * so-called diamond problem which cannot exist here because of the linear ordering.
 * </p>
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/21 at  6:29 AM
 */
public class MultipleInheritanceEngine {
    // atomic + unresolvedAliases = all nodes.
    InheritanceMap atomicNodes; // nodes that do not have aliases.
    InheritanceMap unresolvedAliases; // alias + node
    InheritanceMap unresolvedOverrides; // name or alias
    // Overrides means this is a list of things that overrides the current node.
    // Probably should rename this to unresolvedExtends cf. resolvedOverrides variable

    /**
     * If the resolve method for this class has been run sucessfully.
     * @return
     */
    public boolean isResolutionsRun() {
        return resolutionsRun;
    }

    boolean resolutionsRun = false;

    public static boolean DEBUG_ON = false; // This has to be set internally for deep debugging. Lots of output!
    public InheritanceMap getAtomicNodes() {
        return atomicNodes;
    }

    public void setAtomicNodes(InheritanceMap atomicNodes) {
        this.atomicNodes = atomicNodes;
    }

    public InheritanceMap getUnresolvedAliases() {
        return unresolvedAliases;
    }

    public void setUnresolvedAliases(InheritanceMap unresolvedAliases) {
        this.unresolvedAliases = unresolvedAliases;
    }

    public InheritanceMap getUnresolvedOverrides() {
        return unresolvedOverrides;
    }

    public void setUnresolvedOverrides(InheritanceMap unresolvedOverrides) {
        this.unresolvedOverrides = unresolvedOverrides;
    }

    public Map<String, AliasAndOverrides> getResolvedAliases() {
        return resolvedAliases;
    }

    public void setResolvedAliases(InheritanceMap resolvedAliases) {
        this.resolvedAliases = resolvedAliases;
    }

    public Map<String, InheritanceList> getResolvedOverrides() {
        return resolvedOverrides;
    }

    public void setResolvedOverrides(Map<String, InheritanceList> resolvedOverrides) {
        this.resolvedOverrides = resolvedOverrides;
    }

    InheritanceMap resolvedAliases = new InheritanceMap(); // alias + node
    Map<String, InheritanceList> resolvedOverrides = new HashMap<>();

    public MultipleInheritanceEngine(
            InheritanceMap atomicNodes,
            InheritanceMap unresolvedAliases,
            InheritanceMap unresolvedOverrides) {
        this.atomicNodes = atomicNodes;
        this.unresolvedAliases = unresolvedAliases;
        this.unresolvedOverrides = unresolvedOverrides;

    }

    public void resolve() {
        resolveSimpleOverrides();
        resolveAlias();
        resolveAliasOverrides();
         if(DEBUG_ON){
             debugPrint();
         }
         resolutionsRun = true;
    }

    protected void debugPrint(){
        System.out.println("            nodes:");
        System.out.println("      atomicNodes:" + atomicNodes);
        System.out.println("unresolvedAliases:" + unresolvedAliases);
        System.out.println("          Aliases:" + unresolvedAliases.getAliases("B"));
        System.out.println("  resolvedAliases:" + resolvedAliases);
        System.out.println("unresolvedExtends:" + unresolvedOverrides);
        System.out.println("  resolvedExtends:" + resolvedOverrides + "\n");

    }
    protected void resolveAlias() {
        resolvedAliases = new InheritanceMap();
        for (String key : unresolvedAliases.keySet()) {
            String currentKey = key;
            String currentValue = unresolvedAliases.get(key).getAlias();
            TreeSet<String> checkedAliases = new TreeSet<>();
            List<String> runningList = new ArrayList<>();
            runningList.addAll(unresolvedAliases.get(key).getOverrides());
            AliasAndOverrides newAPs = new AliasAndOverrides(currentValue, runningList);
            while (!atomicNodes.containsKey(currentValue)) {
                if (checkedAliases.contains(currentKey)) {
                    throw new CyclicalError("cyclic alias \"" + currentKey + "\" detected");
                }
                checkedAliases.add(currentKey);
                currentKey = currentValue;
                AliasAndOverrides y = unresolvedAliases.get(currentKey);
                if(y == null){
                    throw new MyConfigurationException("Unresolvable alias for \"" + currentKey + "\". Check the spelling, or is one of your includes missing?");
                }
                currentValue = y.getAlias();
                runningList.addAll(unresolvedAliases.get(currentKey).getOverrides());
            }
            List<String> xxx = new ArrayList<>();
            xxx.addAll(newAPs.getOverrides());
            xxx.add(currentValue);
            xxx.addAll(atomicNodes.get(currentValue).getOverrides());
            newAPs.setOverrides(xxx);
            resolvedAliases.put(key, newAPs);
        }
        // Now handle simple case where every alias is either trivial (singleton) or
        // has a list that is already resolved.
        for (String k : resolvedAliases.keySet()) {
            InheritanceList resolvedList = resolveInhertanceList(resolvedAliases.get(k));
            if (resolvedList != null) {
                resolvedOverrides.put(k, resolvedList);
                unresolvedAliases.remove(k);
                unresolvedOverrides.remove(k);
            }
        }
    }

    /**
     * Loops through a list that is not resolved. If <b><i>every</i></b> element in the
     * list is resolved, they are replaced with their resolution. If this fails
     * (for instance, there is an unresolved alias on the list) then null is returned.
     * Generally invoke this and if null, skip it.
     *
     * @param aao
     * @return
     */
    protected InheritanceList resolveInhertanceList(AliasAndOverrides aao) {
        InheritanceList resolved = new InheritanceList();
        for (String foundOverride : aao.getOverrides()) {
            if (resolvedOverrides.containsKey(foundOverride)) {
                resolved.append(resolvedOverrides.get(foundOverride));
            } else {
                return null;
            }
        }
        return resolved;
    }

    protected void resolveSimpleOverrides() {
        resolvedOverrides = new HashMap<>();
        // Two passes. First is to resolve anything that does not have an override.
        // Then resolve things that have overrides
        // to-do this does not handle aliases in the override.
        // Probably just skip adding anything with an unknown override then retry after
        // aliases have been resolved??

        for (String name : atomicNodes.keySet()) {
            InheritanceList overrides = new InheritanceList();
            AliasAndOverrides aliasAndOverrides = atomicNodes.get(name);
            if (aliasAndOverrides.getOverrides().isEmpty()) {
                overrides.append(name);
                overrides.append(aliasAndOverrides.getOverrides());
                resolvedOverrides.put(name, overrides);
            }
        }

        for (String name : atomicNodes.keySet()) {
            if (resolvedOverrides.containsKey(name)) {
                continue;
            }
            InheritanceList overrides = new InheritanceList();
            AliasAndOverrides aliasAndOverrides = atomicNodes.get(name);
            overrides.append(name);
            InheritanceList workingList = new InheritanceList();
            List<String> unresolvedOverrides = aliasAndOverrides.getOverrides();
            boolean skipIt = false;
            for (int i = unresolvedOverrides.size() - 1; 0 <= i; i--) {
                InheritanceList z = resolvedOverrides.get(unresolvedOverrides.get(i));
                if (z == null) {
                    // this means that there is an alias that is as yet unresolved
                    skipIt = true;
                    break;
                }
                workingList.prepend(z);
            }
            if (!skipIt) {
                overrides.append(workingList);
                resolvedOverrides.put(name, overrides);
            }
        }
        for (String k : resolvedOverrides.keySet()) {
            unresolvedOverrides.remove(k);
        }
    }

    protected void resolveAliasOverrides() {
        int oldResolvedCount = resolvedOverrides.size() - 1; // kick it off for a first iteration
        while (oldResolvedCount < resolvedOverrides.size()) {
            oldResolvedCount = resolvedOverrides.size();
            resolveAliasOverrides2();
        }
        if (!unresolvedOverrides.isEmpty()) {
            throw new UnresolvedReferenceException("Error: unresolved inheritances: " + unresolvedOverrides.keySet());
        }
        if (!unresolvedAliases.isEmpty()) {
            throw new UnresolvedReferenceException("Error: unresolved aliases: " + unresolvedAliases.keySet());
        }


    }

    /**
     * Snoops through the aliases by tracing the known resolved nodes.
     */
    protected void resolveAliasOverrides2() {
        // unresolved overrides has a string as its value that is the unparsed list
        // of overrides for this entry.
        List<String> keysToRemove = new ArrayList<>();
        for (String k : unresolvedOverrides.keySet()) {
            if (!resolvedOverrides.containsKey(k)) {
                InheritanceList resolvedList = resolveInhertanceList(unresolvedOverrides.get(k));
                if (resolvedList != null) {
                    if (unresolvedOverrides.get(k).getAlias() == null) {
                        // so it's nto an alias, throw in the element
                        resolvedList.prepend(k);
                    } else {
                        if (!resolvedOverrides.containsKey(unresolvedOverrides.get(k).getAlias())) {
                            continue;  // not resolved yet, skip it and try later.
                        }
                    }
                    resolvedOverrides.put(k, resolvedList);
                    keysToRemove.add(k);
                }
            }
        }
        for (String k : keysToRemove) {
            unresolvedOverrides.remove(k);
        }
        keysToRemove.clear();
        for (String k : unresolvedAliases.keySet()) {
            if (!resolvedOverrides.containsKey(k)) {
                InheritanceList resolvedList = resolveInhertanceList(unresolvedAliases.get(k));
                if (resolvedList != null) {
                    if (unresolvedAliases.get(k).getAlias() == null) {
                        // so it's not an alias, throw in the element
                        if(resolvedOverrides.containsKey(k)) {
                            resolvedList.prepend(resolvedOverrides.get(k)); // only in alias case. Add it if already resolved.
                            resolvedOverrides.put(k, resolvedList);
                            keysToRemove.add(k);
                        }

                    } else {
                        if (!resolvedAliases.containsKey(unresolvedAliases.get(k).getAlias())) {
                            continue;  // not resolved yet, skip it and try later.
                        }else{
                            String a = unresolvedAliases.get(k).getAlias();
                            if(resolvedOverrides.containsKey(a)) {
                                resolvedList.prepend(resolvedOverrides.get(a)); // only in alias case. Add it if already resolved.
                                resolvedOverrides.put(k, resolvedList);
                                keysToRemove.add(k);
                            }
                        }
                    }
                }
            }
        }
        for (String k : keysToRemove) {
            unresolvedAliases.remove(k);
            unresolvedOverrides.remove(k);
        }

        for (String name : atomicNodes.keySet()) {
            List<String> foundNames = unresolvedAliases.getAliases(name);
            if (0 < foundNames.size()) {
                // then there is an unresolved alias.
                for (String foundName : foundNames) {
                    InheritanceList overrides2 = resolveInhertanceList(unresolvedAliases.get(foundName));
                    if (overrides2 != null) {
                        overrides2.append(overrides2);
                        overrides2.append(resolvedOverrides.get(name));
                        resolvedOverrides.put(foundName, overrides2);
                        unresolvedAliases.remove(foundName);
                        unresolvedOverrides.remove(foundName);
                    }
                }
            }
        }
    }


}
