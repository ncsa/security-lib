package edu.uiuc.ncsa.security.storage.sql.derby;

import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;
import net.sf.json.JSONObject;

import static edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider.BOOT_PASSWORD;
import static edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider.IN_MEMORY;

/**
 * NOTE that the database name is the complete file path to the directory, e.g. "~/test"
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:43 PM
 */
public class DerbyConnectionParameters extends SQLConnectionImpl {

    public DerbyConnectionParameters(JSONObject jsonObject) {
        super(jsonObject);
    }

    @Override
    public String getJdbcUrl() {
        String url = "jdbc:derby:";
        if(inMemory){
           url = url + "memory:" + databaseName + ";create=true";
        }else{
            url = url + databaseName + ";user=" + username + ";password=" + password +
                    (!StringUtils.isTrivial(bootPassword)?";bootPassword=" + bootPassword:"");
        }
        return url;
        // jdbc:derby:PATH_TO_DATABASE;create=true;dataEncryption=true;bootPassword=BOOT_PASSWORD;
        //return "'jdbc:derby:" + (useSSL?"ssl":"tcp") + "://" + host  + (0<port?":"+port:"")  + "/" + databaseName;
    }

    public DerbyConnectionParameters(String username,
                                     String password,
                                     String databaseName,
                                     String schema,
                                     String host,
                                     int port,
                                     String jdbcDriver,
                                     boolean useSSL,
                                     boolean inMemory,
                                     String bootPassword,
                                     String parameters
    ) {
        super(username, password, databaseName, schema, host, port, jdbcDriver, useSSL, parameters);
        this.inMemory = inMemory;
        this.bootPassword = bootPassword;
    }

    protected String bootPassword = null;
    protected boolean inMemory = false;

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put(BOOT_PASSWORD, bootPassword);
        json.put(IN_MEMORY, inMemory);
        return json;
    }

    @Override
    public void fromJSON(JSONObject json) {
        super.fromJSON(json);
        if(json.containsKey(BOOT_PASSWORD))bootPassword = json.getString(BOOT_PASSWORD);
        if(json.containsKey(IN_MEMORY))inMemory = json.getBoolean(IN_MEMORY);
    }
}
