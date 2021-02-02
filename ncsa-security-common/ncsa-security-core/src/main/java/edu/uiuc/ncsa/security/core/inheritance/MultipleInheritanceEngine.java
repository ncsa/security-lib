package edu.uiuc.ncsa.security.core.inheritance;

import java.util.*;

/**
 * Resolves ordered multiple inheritance lists, used e.g. in configurations.
 * This only works on names and relations between them. It returns a list of
 * names (all names and aliases are assumed unique)
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/21 at  6:29 AM
 */
public class MultipleInheritanceEngine {
    // atomic + unresolvedAliases = all nodes.
    InheritanceMap atomicNodes; // nodes that do not have aliases.
    InheritanceMap unresolvedAliases; // alias + node
    InheritanceMap unresolvedOverrides; // name or alias

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
        System.out.println("              nodes:");
        System.out.println("        atomicNodes:" + atomicNodes);
        System.out.println("  unresolvedAliases:" + unresolvedAliases);
        System.out.println("            Aliases:" + unresolvedAliases.getAliases("B"));
        System.out.println("    resolvedAliases:" + resolvedAliases);
        System.out.println("unresolvedOverrides:" + unresolvedOverrides);
        System.out.println("  resolvedOverrides:" + resolvedOverrides + "\n");

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
                currentValue = unresolvedAliases.get(currentKey).getAlias();
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
            }
        }
    }

    /**
     * Loops through a list that is not resolved. If <b><i>every</i></b> element in the
     * list is resolved, they are replaced with their resolution. If this fails
     * (for instance, there is an unresolved alias on the list) then null is returned.
     * Generally invoke this and if null, skip it.
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

    /**
     * Snoops through the aliases by tracing the known resolved nodes.
     */
    protected void resolveAliasOverrides() {
        // unresolved overrides has a string as its value that is the unparsed list
        // of overrides for this entry.
        List<String> keysToRemove = new ArrayList<>();
        for (String k : unresolvedOverrides.keySet()) {
             if(!resolvedOverrides.containsKey(k)){
                 InheritanceList resolvedList = resolveInhertanceList(unresolvedOverrides.get(k));
                 if(resolvedList != null){
                     if(unresolvedOverrides.get(k).getAlias() == null) {
                         // so it's nto an alias, throw in the element 
                         resolvedList.prepend(k);
                     }
                     resolvedOverrides.put(k, resolvedList);
                     keysToRemove.add(k);
                 }
             }
        }
        for(String k : keysToRemove){
            unresolvedOverrides.remove(k);
        }
        for (String name : atomicNodes.keySet()) {
            List<String> foundNames = unresolvedAliases.getAliases(name);
            if (0 < foundNames.size()) {
                // then there is an unresolved alias.
                for (String foundName : foundNames) {
                    InheritanceList overrides2 = new InheritanceList();
                    AliasAndOverrides aliasAndOverrides2 = unresolvedAliases.get(foundName);
                    overrides2.append(aliasAndOverrides2.getOverrides());
                    overrides2.append(resolvedOverrides.get(name));
                    resolvedOverrides.put(foundName, overrides2);
                }
            }
        }
    }


    public static void main(String[] args) {
        InheritanceMap unresolvedAlias = new InheritanceMap();
        unresolvedAlias.put("A", "B", new MyArrayList<>(new String[]{"X"}));

        InheritanceMap unresolvedOverrides = new InheritanceMap();
        unresolvedOverrides.put("B", new MyArrayList<>(new String[]{}));
        unresolvedOverrides.put("X", new MyArrayList<>(new String[]{}));
        //MultipleInheritanceEngine mie = new MultipleInheritanceEngine()
    }
}
