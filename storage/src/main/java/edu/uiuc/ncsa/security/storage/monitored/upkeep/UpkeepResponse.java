package edu.uiuc.ncsa.security.storage.monitored.upkeep;

import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

import static edu.uiuc.ncsa.security.core.util.StringUtils.RJustify;
import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/14/24 at  11:19 AM
 */
public class UpkeepResponse {

    /**
     * Total possible clients with zero or null last accessed
     */
    /**
     * Total number of deletions attempted
     */
    public int attempted = 0;
    public int deleteCount = 0;

    /**
     * The identifiers (as strings) to remove.
     */
    public List<String> deletedList = new ArrayList<>();

    public UpkeepStats deletedStats = new UpkeepStats();
    /**
     * The identifiers (as strings) to archive
     */
    public int archiveCount = 0;
    public List<String> archivedList = new ArrayList<>();
    public UpkeepStats archivedStats = new UpkeepStats();
    public List<String> retainedList = new ArrayList<>();
    /**
     * The number skipped, i.e., that had retain = true.
     */
    public int retainedCount = 0;

    public String name = null;
    public int testedCount = 0;
    public boolean testModeOnly = false;
    /**
     * Would have been deleted normally, but some other rule said to retain.
     */
    public int skipped = 0;

    public Map<String, Long> getCollateralMap() {
        return collateralMap;
    }

    public void setCollateralMap(Map<String, Long> collateralMap) {
        this.collateralMap = collateralMap;
    }

    Map<String, Long> collateralMap = new HashMap<>();

    Date now = new Date();

    public String report(boolean prettyPrint) {
        String n = prettyPrint ? "\n" : " ";
        int width = prettyPrint ? 20 : -1; // turns off right justification below
        String spacer = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"; // 40 of these in case it ever changes  /u2501
        String ends = "════════════════════════════════════════"; // 40 of these in case it ever changes  /u2550
        return n +
                (prettyPrint ? ends + n : "") +
                (isTrivial(name) ? "" : (RJustify("name = ", width) + name + "," + n)) +
                RJustify("timestamp  = ", width) + now + n +
                RJustify("test mode = ", width) + testModeOnly + n +
                RJustify("attempted = ", width) + attempted + "," + n +
                RJustify("archived # = ", width) + archiveCount + "," + n +
                RJustify("deleted # = ", width) + deleteCount + "," + n +
                RJustify("retained = ", width) + retainedCount + "," + n +
                RJustify("skipped = ", width) + skipped + "," + n +
                formatCollaterals(prettyPrint, width) +
                (prettyPrint ? spacer + n : "") +
                RJustify("archived list# = ", width) + (archivedList == null ? 0 : archivedList.size()) + "," + n +
                RJustify("archived stats = ", width) + (archivedStats == null ? "(none)" : archivedStats.report()) + "," + n +
                //   (prettyPrint?spacer.substring(0,20)+n:"") +
                RJustify("deleted list# = ", width) + (deletedList == null ? 0 : deletedList.size()) + "," + n +
                RJustify("deleted stats = ", width) + (deletedStats == null ? "(none)" : deletedStats.report()) + "," + n +
                //     (prettyPrint?spacer.substring(0,20)+n:"") +
                RJustify("retained list# = ", width) + (retainedList == null ? 0 : retainedList.size()) + "," + n +
                (prettyPrint ? ends + n : "");
    }

    /**
     * Format the contents of the collateral map. This offsets each entry for proper display
     * within the margins.
     * @param prettyPrint
     * @param width
     * @return
     */
    protected String formatCollaterals(boolean prettyPrint, int width){
        if(getCollateralMap().isEmpty()) return "";
        String n = prettyPrint ? "\n" : " ";
        String entry = "";
        boolean isFirst = true;
        for(String key : getCollateralMap().keySet()){
            if(isFirst){
                isFirst = false;
            }else{
                entry = entry + ", ";
            }
            entry = key + "=" + getCollateralMap().get(key);

        }

        String out = RJustify("collateral : ", width)+ entry + n;
        
        return out;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + report(false) + "}";
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("timestamp" , Iso8601.date2String(now));
        // Do counts
        JSONObject counts = new JSONObject();
        counts.put("total", attempted);
        counts.put("archived", archiveCount);
        counts.put("deleted", deleteCount);
        counts.put("retained", retainedCount);
        counts.put("skipped", skipped);
        jsonObject.put("counts", counts);
        if(!getCollateralMap().isEmpty()) {
            JSONObject collaterals = new JSONObject();
            collaterals.putAll(getCollateralMap());
            jsonObject.put("collateral", collaterals);
        }
        // do lists
        JSONObject lists = new JSONObject();
        JSONArray array = new JSONArray();
        array.addAll(deletedList);
        lists.put("deleted", array);
        array = new JSONArray();
        array.addAll(archivedList);
        lists.put("archived", array);
        array = new JSONArray();
        array.addAll(retainedList);
        lists.put("retained", array);
        jsonObject.put("lists", lists);

        return jsonObject;

    }
}

/*
How to read this

════════════════════════════════════════
             name = upkeep for SQLClientStore,                     |  Name
       timestamp  = Mon Feb 19 23:32:32 CST 2024                   |  timestamp
        test mode = true                                           |  no changes made, just what was found
        attempted = 484,                                           |  total number of items all rules applied to
       archived # = 0,                                             |  number found to archive
        deleted # = 431,                                           | number found to delete
         retained = 44,                                            |  total number of items to retain from action="retain" rules
          skipped = 9,                                             |  kept because on retain list, but other rules would have deleted otherwise
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
   archived list# = 0,                                             | number of elements actually added to the archive list (0 in test mode)
   archived stats = {success=0, noInfo=0, failed=0, unknown=0},    | actual tally of what happened
    deleted list# = 0,                                             | number of elements actually added to the archive list (0 in test mode)
    deleted stats = {success=0, noInfo=0, failed=0, unknown=0},    | actual tally of what happened
   retained list# = 44,                                            | number of elements found from rules with action = "retain"
════════════════════════════════════════


 */