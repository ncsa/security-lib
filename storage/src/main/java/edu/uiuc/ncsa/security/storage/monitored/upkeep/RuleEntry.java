package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.storage.monitored.Monitored;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  9:27 AM
 */
public interface RuleEntry extends UpkeepConstants {
    boolean applies(Identifier id, Long created, Long accessed, Long modified);
    boolean applies(Monitored monitored);

}
