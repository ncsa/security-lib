package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This extension of XProperties allows for loading XProperties with template
 * resolution by flag -- use system and JVM properties or by supplying
 * a template file. Each line is read and processed with the template,
 * so you can have expressions line
 * <pre>
 *     ${db.type}_${USER}=${NCSA_DEV_INPUT}/scripts/database.sh -u ${db.logon}
 * </pre>
 * and have the user name used as part of the property name. Note that in this
 * example, the uppercase properties are from the OS, the lower case one
 * is (assumed to be) a Java property passed to the JVM. this might resolve to
 * the property named
 * <pre>
 *     mysql_jeff
 * </pre>
 * with the value
 * <pre>
 *     /home/ncsa/dev/scripts/database.sh -u oa4mp-user42
 * </pre>
 *
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/23 at  9:41 AM
 */
public class XXProperties extends XProperties {
    /**
     * If true, use the system and environment properties for loading.
     * @param fileName
     * @param useSystemProperties use system and environment properties as templates
     * @throws IOException
     */
    public void load(String fileName, boolean useSystemProperties) throws IOException {
        load(fileName, useSystemProperties?getAllProperties():null);
    }
    
    public void load(String fileName, Map templates) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        load(fileInputStream, templates);
    }

    /**
     *
     * @param file
     * @param useSystemProperties use system and environment properties as templates
     * @throws IOException
     */
    public void load(File file, boolean useSystemProperties) throws IOException {
        load(file, useSystemProperties?getAllProperties():null);
    }
    public void load(File file, Map templates) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        load(fileInputStream, templates);
    }

    /**
     *
     * @param inputStream
     * @param useSystemProperties use system and environment properties as templates
     * @throws IOException
     */
    public void load(InputStream inputStream, boolean useSystemProperties) throws IOException {
        load(inputStream, useSystemProperties?getAllProperties():null);
    }
    public void load(InputStream inputStream, Map templates) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        load(inputStreamReader, templates);
    }

    /**
     * Actual work method for this entire class
     *
     * @param reader
     * @param templates
     */
    public void load(Reader reader, Map templates) throws IOException {
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
        String linein = bufferedReader.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while (linein != null) {
            stringBuilder.append(TemplateUtil.newReplaceAll(linein, templates) + "\n");
            linein = bufferedReader.readLine();
        }
        bufferedReader.close();
        ByteArrayInputStream bais = new ByteArrayInputStream(stringBuilder.toString().getBytes());
        load(bais);
    }

    /**
     * Resolves the name as a resource in the local class loader, rather than
     * as a file on the system.
     *
     * @param resourceName
     * @param templates
     */
    public void loadResource(String resourceName, Map templates) throws IOException {
        InputStream inputStream = DevUtils.class.getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new FileNotFoundException("Could not locate resource \"" + resourceName + "\"");
        }
        load(inputStream, templates);
    }

    /**
     *
     * @param resourceName
     * @param useSystemProperties use system and environment properties as templates
     * @throws IOException
     */
    public void loadResource(String resourceName, boolean useSystemProperties) throws IOException {
           loadResource(resourceName, useSystemProperties?getAllProperties():null);
    }
    
    HashMap<String, String> allProperties = null;

    /**
     * A {@link java.util.Map} of all the OS and java properties for use resolving
     * configuration testing files. Java properties override OS properties.
     * Typically given to the JVM at startup with a -D prefix, so to set the
     * property named my.prop at startup for the application MyApp you'd write
     * <pre>
     *     java -Dmy.prop="my-value" MyApp
     * </pre>
     * Note that this is pretty expensive so will be done only when called then stored,
     * so you cannot update the properties on the fly. For that consider using
     * {@link #getAllProperties(boolean)}.
     * @return
     */
    public Map<String, String> getAllProperties() {
        return getAllProperties(false);
    }

    /**
     * Force a reload of system properties.
     * @param reload  if true, properties are reloaded at each call. If false, they are cached.
     * @return
     */
    public Map<String, String> getAllProperties(boolean reload) {
        if (reload || allProperties == null) {
            allProperties = new HashMap<>();
            allProperties.putAll(System.getenv());
            for (String x : System.getProperties().stringPropertyNames()) {
                allProperties.put(x, System.getProperty(x));
            }
        }
        return allProperties;
    }
    public static void main(String[] args) {
        try {
            // Just for testing.
            XXProperties xxp = new XXProperties();
            xxp.loadResource("/sec-lib.xp", true);
            System.out.println(xxp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
