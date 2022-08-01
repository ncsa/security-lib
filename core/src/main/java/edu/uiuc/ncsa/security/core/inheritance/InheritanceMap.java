package edu.uiuc.ncsa.security.core.inheritance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/21 at  7:18 AM
 */
public class InheritanceMap extends HashMap<String, AliasAndOverrides> {
    public void put(String name,  List<String> overrides) {
        put(name, null, overrides);

    }
    public void put(String name, String alias, List<String> overrides) {
        AliasAndOverrides aliasAndOverrides = new AliasAndOverrides(alias, overrides);
        put(name, aliasAndOverrides);
    }
    public List<String> getAliases(String name){
          List<String> aliases = new ArrayList<>();
          for(String key : keySet()){
              AliasAndOverrides aliasAndOverrides = get(key);
              if(aliasAndOverrides.getAlias().equals(name)){
                  aliases.add(key);
              }
          }
          return aliases;
    }

}
