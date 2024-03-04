package edu.uiuc.ncsa.security.storage.sql.derby;

import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.sql.SQLConnectionImpl;
import net.sf.json.JSONObject;

import java.io.File;

import static edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags.*;
import static edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider.BOOT_PASSWORD;
import static edu.uiuc.ncsa.security.storage.sql.ConnectionPoolProvider.DERBY_STORE_TYPE;

/**
 * NOTE that the database name is the complete file path to the directory, e.g. "~/test"
 * <p>Created by Jeff Gaynor<br>
 * on 5/2/12 at  1:43 PM
 */
public class DerbyConnectionParameters extends SQLConnectionImpl {

    public DerbyConnectionParameters(JSONObject jsonObject) {
        super(jsonObject);
    }

    /**
     * In cases that this is a file store, the root directory can be specified and the
     * database location etc. will be managed for the user.
     */
    String rootDirectory;
    String jdbcURL = null;

    @Override
    public String getJdbcUrl() {
        if (jdbcURL == null) {
            createJdbcUrls();
        }
        return jdbcURL;
    }

    public void createJdbcUrls() {
        jdbcURL = "jdbc:derby:";
        switch (storeType) {
            case DERBY_STORE_TYPE_MEMORY:
                /*
                   Example from the manual
                   jdbc:derby:memory:myDB;create=true
                 */

                jdbcURL = jdbcURL + "memory:" + databaseName + ";create=true";
                setCreateURL(jdbcURL); // always create  in memory store
                setCreateOne(true);
                break;

            case DERBY_STORE_TYPE_FILE:
                // connect 'jdbc:derby:/home/ncsa/dev/derby/oa4mp;create=true;dataEncryption=true;bootPassword=o7MtXykd;user=oa4mp';
                jdbcURL = jdbcURL + databaseName;
                setShutdownURL(jdbcURL + ";shutdown=true");
                jdbcURL = jdbcURL +
                        ";dataEncryption=true" +
                        ";user=" + username +
                        ";bootPassword=" + bootPassword
                ;
                File f = new File(databaseName);
                setCreateURL(jdbcURL + ";create=true;");
                jdbcURL = jdbcURL + ";password=" + password;
                setCreateOne(!new File(databaseName).exists());
                break;
            case DERBY_STORE_TYPE_SERVER:
                /*
                   Example full connection string from the reference manual
                   'jdbc:derby://localhost:8246/mchrystaEncryptedDB;create=true;
                      user=mchrysta;password=mchrysta;dataEncryption=true;
                      encryptionAlgorithm=Blowfish/CBC/NoPadding;
                      bootPassword=mySuperSecretBootPassword;ssl=peerAuthentication';
                 */
                setCreateURL(null);
                setCreateOne(false);
                jdbcURL = jdbcURL + host + ":" + port + "/" +
                        databaseName +
                        ";dataEncryption=true" +
                        ";user=" + username +
                        ";password=" + password +
                        (!StringUtils.isTrivial(bootPassword) ? ";bootPassword=" + bootPassword : "");
                break;
            default:
                throw new IllegalStateException("unknown derby " + DERBY_STORE_TYPE + "\"" + storeType + "\"");
        }
    }
    public String getDerbyConnectionString(){
        return "connect '" + jdbcURL + "';";

    }
    public String getShutdownURL() {
        return shutdownURL;
    }

    public void setShutdownURL(String shutdownURL) {
        this.shutdownURL = shutdownURL;
    }

    String shutdownURL = null;

    public void setCreateOne(boolean createOne) {
        this.createOne = createOne;
    }

    /**
     * Create this database?
     *
     * @return
     */
    public boolean isCreateOne() {
        return createOne;
    }

    boolean createOne = false;

    public String getCreateURL() {
        return createURL;
    }

    public void setCreateURL(String createURL) {
        this.createURL = createURL;
    }

    String createURL;

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public DerbyConnectionParameters(String username,
                                     String password,
                                     String rootDirectory,
                                     String databaseName,
                                     String schema,
                                     String host,
                                     int port,
                                     String jdbcDriver,
                                     boolean useSSL,
                                     String storeType,
                                     String bootPassword,
                                     String parameters
    ) {
        super(username, password, databaseName, schema, host, port, jdbcDriver, useSSL, parameters);
        this.storeType = storeType;
        this.bootPassword = bootPassword;
        this.rootDirectory = rootDirectory;
        createJdbcUrls(); // this will set a bunch of other URLs.
    }

    protected String bootPassword = null;

    public String getStoreType() {
        return storeType;
    }

    protected String storeType;

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put(BOOT_PASSWORD, bootPassword);
        json.put(DERBY_STORE_TYPE, storeType);
        return json;
    }

    @Override
    public void fromJSON(JSONObject json) {
        super.fromJSON(json);
        if (json.containsKey(BOOT_PASSWORD)) bootPassword = json.getString(BOOT_PASSWORD);
        if (json.containsKey(DERBY_STORE_TYPE)) storeType = json.getString(DERBY_STORE_TYPE);
    }

    @Override
    public String toString() {
        return "DerbyConnectionParameters{" +
                "\n bootPassword='" + bootPassword + '\'' +
                ",\n createOne=" + createOne +
                ",\n createURL='" + createURL + '\'' +
                ",\n databaseName='" + databaseName + '\'' +
                ",\n host='" + host + '\'' +
                ",\n jdbcDriver='" + jdbcDriver + '\'' +
                ",\n jdbcURL='" + jdbcURL + '\'' +
                ",\n password='" + password + '\'' +
                ",\n port=" + port +
                ",\n schema='" + schema + '\'' +
                ",\n shutdownURL='" + shutdownURL + '\'' +
                ",\n storeType='" + storeType + '\'' +
                ",\n useSSL=" + useSSL +
                ",\n username='" + username + '\'' +
                ",\nrootDirectory='" + rootDirectory + '\'' +
                '}';
    }
}
