package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static edu.uiuc.ncsa.security.oauth_2_0.OA2ConfigTags.SCOPES;
import static edu.uiuc.ncsa.security.oauth_2_0.OA2ConfigTags.SCOPE_ENABLED;
import static edu.uiuc.ncsa.security.oauth_2_0.OA2Constants.SCOPE;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/26/15 at  3:59 PM
 */
public class OA2ConfigurationLoaderUtils extends ConfigUtil {
    static Collection<String> scopes = null;

    public static Collection<String> getScopes(ConfigurationNode cn) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (scopes == null) {
            scopes = new ArrayList<String>();
            // First thing is to take all the basic scopes supported and include them.
            for (String s : OA2Scopes.basicScopes) {
                scopes.add(s);
            }
            if (0 < cn.getChildrenCount(SCOPES)) {
                // Then we have some scopes
                ConfigurationNode node = Configurations.getFirstNode(cn, SCOPES);
                List kids = node.getChildren(SCOPE);
                for (int i = 0; i < kids.size(); i++) {
                    ConfigurationNode currentNode = (ConfigurationNode) kids.get(i);

                    String currentScope = (String) currentNode.getValue();
                    String x = Configurations.getFirstAttribute(currentNode, SCOPE_ENABLED);
                    if (x != null) {
                        boolean isEnabled = Boolean.parseBoolean(x);
                        if (isEnabled) {
                            scopes.add(currentScope);

                        } else {
                            scopes.remove(currentScope);

                        }
                    } else {
                        // default is if the enabled flag is omitted, to assume it is enabled and add it.
                        scopes.add(currentScope);
                    }
                }
            }
        }
        return scopes;
    }

}
