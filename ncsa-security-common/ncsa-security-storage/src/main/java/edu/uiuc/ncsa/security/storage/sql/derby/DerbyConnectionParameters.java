package edu.uiuc.ncsa.security.storage.sql.derby;

import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;

/**
 * NOTE that the database name is the complete file path to the directory, e.g. "~/test"
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:43 PM
 */
public class DerbyConnectionParameters extends SQLConnectionImpl {
    @Override
    public String getJdbcUrl() {
        String url = "jdbc:derby:";
        if(inMemory){
           url = url + "memory:" + databaseName + ";create=true";
        }else{
            url = url + databaseName;
        }
        return url;
        //
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
                                     boolean inMemory
    ) {
        super(username, password, databaseName, schema, host, port, jdbcDriver, useSSL);
        this.inMemory = inMemory;
    }

    protected boolean inMemory = false;

}
