package edu.uiuc.ncsa.security.core.configuration;


import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * A subclass of java.util.Properties with many, many features. This is huge, boring and
 * extremely useful.
 * <H2>Usage</H2>
 * This may be used wherever a {@link java.util.Properties} object may be used.
 * <H2>Lifecycle</H2>
 * Instantiate as needed. Since this is a subclass of Properties, it is effectively a replacement for it.
 * <p/>
 * The specific problems it solves are as follows.
 * <ul>
 * <li> methods for saving/getting specific types (see below) This saves having a lot of little snippets to make
 * things like dates and numbers over and over. Also, error handling is better and only runtime exceptions are ever thrown.</li>
 * <li>methods for adding (vs. simply replacing) values from another property file</li>
 * <li>methods for loading from other sources without replacing all the properties</li>
 * <li>a few more convenient constructors</li>

 * </ul>
 * <p> There is a main method to test this. It takes a single argument which is a file name which will be created
 * (thereby allowing a test of writing a file and reading it.)
 * <H2>Property types </H2>
 * Here is a list of the types supported. The type is given in the first column and either the suffix for the setter of getter
 * is given (so <code>Int</code> as an entry means there are two methods named <code>setInt</code> and <code>getInt</code>)
 * or the explicit names of these are given. The last column gives a bit more description.
 * <TABLE BORDER="1" CELLPADDING="3" CELLSPACING="0" WIDTH="100%">
 * <TR BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <TD COLSPAN=3><font SIZE="+2">Property Summary</font></TD>
 * </TR>
 * <TR>
 * <TD><B>Type</B></TD>
 * <TD><B>Set/Get</B></TD>
 * <TD><B>Description</B></TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>java.util.Date</code></font></TD>
 * <TD><font SIZE="-1"><code>Date</code></font></TD>
 * <TD>A standard Java date. This is actually stored as a long.</TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>integer</code></font></TD>
 * <TD><font SIZE="-1"><code>Int</code></font></TD>
 * <TD>An <code>integer</code>.</TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>long</code></font></TD>
 * <TD><font SIZE="-1"><code>Long</code></font></TD>
 * <TD>A <code>long</code></TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>String</code></font></TD>
 * <TD><font SIZE="-1"><code>String</code></font></TD>
 * <TD>A string. Note that the values are escaped in the resulting properties file.</TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>boolean</code></font></TD>
 * <TD><font SIZE="-1"><code>Boolean</code></font></TD>
 * <TD>A <code>boolean</code> value. This automatically parses values of
 * <br><b><code>yes, on, enable(d), ok, true, yup, yeah, 1</code></b>
 * <br>to be logical true and
 * <br><b><code>false,no, disable(d), nope, off, nay, 0</code></b>
 * <br>to be logical false. These are case insensitive. If the value is not recognized an exception is thrown.</TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>bytes array</code></font></TD>
 * <TD><font SIZE="-1"><code>Bytes</code></font></TD>
 * <TD>This will store or retrieve an arbitrary array of bytes</TD>
 * </TR>
 *  <TR>
 * <TD><font SIZE="-1"><code>url, uri</code></font></TD>
 * <TD><font SIZE="-1"><code>URL, URI</code></font></TD>
 * <TD>urls and uris.</TD>
 * </TR>
 *  <TR>
 * <TD><font SIZE="-1"><code>double</code></font></TD>
 * <TD><font SIZE="-1"><code>Double</code></font></TD>
 * <TD>a double value</TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>serializable java object</code></font></TD>
 * <TD><font SIZE="-1"><code>Serializable</code></font></TD>
 * <TD>Works if the object implements the <code>java.io.Serializable</code> interface.</TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>serializable list</code></font></TD>
 * <TD><font SIZE="-1"><code>SerializableList</code></font></TD>
 * <TD>An array of java objects each of which implements the <code>java.io.Serializable</code> interface.</TD>
 * </TR>
 * <TR>
 * <TD><font SIZE="-1"><code>file</code></font></TD>
 * <TD><font SIZE="-1"><code>getFile(key), getFile(key, parent), setFileName</code></font></TD>
 * <TD>This will store a file name in the properties. There are <b>two</b> getters, so that if the users needs
 * to resolve the file against a specific parent (vs. the java default of the invocation directory) it may be done.</TD>
 * </TR>
 * <p/>
 * </TABLE>
 *
 * @author Jeff Gaynor
 * @version 1.0
 * @date on March 30, 2004, 3:51 PM
 */
public class XProperties extends Properties {

    static final long serialVersionUID = 42L;
    static final String NAMESPACE_DELIMITER = ":";

    /**
     * The default separator for lists. This is a comma.
     */
    public static String DEFAULT_LIST_SEPARATOR = ",";
    protected String storeFileName = null;
    String _listSeparator;

    /**
     * Get the list separator. The default is in <code>DEFAULT_LIST_SEPARATOR</code>.
     *
     * @return the list separator
     */
    public String getListSeparator() {
        if (_listSeparator == null) {
            _listSeparator = DEFAULT_LIST_SEPARATOR;
        } /* end if */
        return _listSeparator;
    }

    /**
     * Set the list separator
     *
     * @param listSeparator the list separator
     */
    public void setListSeparator(String listSeparator) {
        _listSeparator = listSeparator;
    }

    /**
     * Creates a new instance of XProperty
     */
    public XProperties() {
    }

    /**
     * Convenience method. This loads all the properties in the given file.
     *
     * @param fileName
     */
    public XProperties(String fileName) {
        super();
        setStoreFileName(fileName);
        load();

    }


    /**
     * Create a new XProperties object by parsing a properties file.
     *
     * @param file the (standard) properties file.
     */
    public XProperties(File file) {
        super();
        try {
            setStoreFileName(file.getCanonicalPath());
        } catch (IOException e) {
            throw new GeneralException("Error setting property file name", e);
        }
        load();

    }


    /**
     * Same as the constructor in the superclass, this gives this object all the same properties as
     * the argument. <p>
     * <B>Grrrrr</B> this is busted in the super class (!!). This works here...
     */
    public XProperties(Properties p) {
        //  super(p);
        super();
        add(p, true);
    }

    /**
     * This will allow for an array of Properties objects. Note that the order is important if overwriting is enabled,
     * since this starts with an empty properties
     * list so earlier sources have right of way over later ones.
     *
     * @param sources   an array of <code>Properties</code>
     * @param overwrite
     */
    public XProperties(Properties[] sources, boolean overwrite) {
        super();
        add(sources, overwrite);
    }

    /**
     * This will instantiate the properties taking the value ssupplied. Each given source Properties object
     * as an associated flag indicating overwrite permission. Therefore, order is important for loading these.
     *
     * @param sources
     * @param overwrite
     */
    public XProperties(Properties[] sources, boolean[] overwrite) {
        super();
        add(sources, overwrite);
    }

    /**
     * Strings that this will treat as equivalent to logical true.
     */
    public static String[] LOGICAL_TRUES = new String[]{"true", "ok", "yes", "1", "on", "yup", "yeah", "enable", "enabled"};

    /**
     * Strings this will treat as equivalent to logical false.
     */
    public static String[] LOGICAL_FALSES = new String[]{"false", "no", "0", "off", "nope", "nay", "disable", "disabled"};

    /**
     * Retrieves the boolean value. Any of <code>LOGICAL_TRUES</code> are equivalent to
     * logical true. Any of <code>LOGICAL_FALSES</code> are equivalent to logical false. All
     * comparisons are done case-insensitive
     *
     * @param key
     * @return the boolean value of the property.
     */
    public boolean getBoolean(String key) {
        String rawOut = getProperty(key);
        String out = rawOut.toLowerCase();
        if (out == null) {
            throw new GeneralException("null value for key >>" + key + "<< encountered.");
        }
        for (int i = 0; i < LOGICAL_TRUES.length; i++) {
            if (LOGICAL_TRUES[i].equals(out)) {
                return true;
            }
        }
        for (int i = 0; i < LOGICAL_FALSES.length; i++) {
            if (LOGICAL_FALSES[i].equals(out)) {
                return false;
            }
        }
        throw new GeneralException("unknown value >" + rawOut + "< for boolean key >" + key + "<");
    }//end getBoolean(String)


    /**
     * Sets the given boolean value.
     *
     * @param key
     * @param value
     */
    public void setBoolean(String key, boolean value) {
        setProperty(key, value ? LOGICAL_TRUES[0] : "false");
    }


    /**
     * Get the value associated with this key as an integer.
     *
     * @param key
     * @return
     * @throws Exception if the value cannot be parsed as an integer
     */
    public int getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }



    /**
     * Set the property from the integer value.
     *
     * @param key
     * @param value
     */
    public void setInt(String key, int value) {
        setProperty(key, Integer.toString(value));
    }


    /**
     * Get the property as a long.
     *
     * @param key
     * @return the long value
     * @throws Exception if the value cannot be interpreted as a long.
     */
    public long getLong(String key) {
        return Long.parseLong(getProperty(key));
    }



    /**
     * Set the value of the property from the give long.
     *
     * @param key
     * @param value
     */
    public void setLong(String key, long value) {
        setProperty(key, Long.toString(value));
    }


    /**
     * Retrieve the file stored by this key, resolving it against the given invocation directory.
     *
     * @param key
     * @return the <code>File</code> object.
     */
    public File getFile(String key) {
        return new File(getProperty(key));
    }


    /**
     * Retrieve the file stored by this key, resolving it against the given file.
     *
     * @param key
     * @param parent
     * @return
     */
    public File getFile(String key, File parent) {
        return new File(parent, getProperty(key));
    }


    /**
     * Puts the file name into the properties, (<b>not</b>) the contents! This will not be portable
     * between platforms, since this is the full path, properly escaped.
     *
     * @param key
     * @param f
     */
    public void setFile(String key, File f) {
        try {
            setProperty(key, f.getCanonicalPath());
        } catch (IOException e) {
            throw new GeneralException("Error setting file property", e);
        }
    }


    /**
     * Gets the value corresponding to the key as a string. This always works.
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return getProperty(key);
    }


    /**
     * Sets the value of the key with the given string.
     *
     * @param key
     * @param value
     */
    public void setString(String key, String value) {
        setProperty(key, value);
    }



    /**
     * Gets a single serializable java object.
     *
     * @param key
     * @return
     */
    public Object getSerializable(String key) {
        Object[] x = getSerializableList(key);
        if (x == null || x.length == 0) {
            return x;
        }
        return x[0];
    }


    /**
     * Retrieves a single serializable java object.Don't forget that you need to have any
     * classes for this available to the current virtual machine to correctly deserialize the result.
     *
     * @param object the serializable object
     * @parm key
     */
    public void setSerializable(String key, Serializable object) {
        Object[] x = new Object[]{object};
        setSerializableList(key, x);
    }

    /**
     * Stores a list of serializable java objects in the properties.
     *
     * @param key
     * @param oList
     */
    public void setSerializableList(String key, Object[] oList) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(oList);
            oos.flush();
            oos.close();
            setProperty(key, Base64.encodeBase64String(baos.toByteArray()));
        } catch (Exception e) {
            throw new GeneralException("Error serializing object", e);
        }
    }


    /**
     * Retrieves a list of serializable objects from the property. Don't for get that you need to have the
     * classes for these available to the current virtual machine to correctly deserialize these.
     *
     * @param key
     * @return
     */
    public Object[] getSerializableList(String key) {
        if (!containsKey(key)) {
            return new Object[0];
        }
        byte[] x = Base64.decodeBase64(getProperty(key));
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(x);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object[] objects;
            objects = (Object[]) ois.readObject();
            ois.close();
            return objects;

        } catch (Exception e) {
            throw new GeneralException("Error deserializing list", e);
        }
    }


    /**
     * Returns the value associated with this key as a byte array.
     *
     * @param key
     * @return the byte array
     */
    public byte[] getBytes(String key) {
        return Base64.decodeBase64(getProperty(key));
    }


    /**
     * Sets the value for the given key to the byte array.
     *
     * @param key
     * @param ba
     */
    public void setBytes(String key, byte[] ba) {
        setProperty(key, Base64.encodeBase64String(ba));
    }


    /**
     * Set the value associated with this key to the given double value.
     *
     * @param key
     * @param value
     */
    public void setDouble(String key, double value) {
        setProperty(key, Double.toString(value));
    }


    /**
     * Attempts to retrieve the value associated with the key as a double.
     *
     * @param key
     * @return
     */
    public double getDouble(String key) {
        return Double.parseDouble(getProperty(key));
    }


    /**
     * A bug fix for Properties... Attempting to set a non-existent property with a null value throws
     * a <code>NullPointerException</code>. This is intercepted here.
     */
    public Object setProperty(String key, String value) {
        if (null == getProperty(key) && value == null) {
            return null;

        }
        return super.setProperty(key, value);

    }


    /*
    * One of the irritating limitations of the Properties object is that you cannot take one set of properties
    * and put them into a new properties object directly. Using the original <code>load</code> method overwrites
    * all of the current properties.
    * @param source
    * @param overwrite if this is <code>true</code> then the current properties will be replaced by those in the argument
    */
    public void add(Map source, boolean overwrite) {
        // we can't just use the built-in streams to do this (which would really easy) because they completely overwrite everything
        // and we allow the user not to do that.
        Set e = source.keySet();
        for (Object key : e ) {
            if (!containsKey(key) || overwrite) {
                Object value = source.get(key);
                setProperty(key.toString(), value.toString());
            }
        } //end while

    }

    /**
     * Adds each of the given array of properties. The flag deteremines if pre-exisiting key-value pairs should be replaced.
     * This uses the same flag for every property.
     *
     * @param sources
     * @param overwrite
     */
    public void add(Properties[] sources, boolean overwrite) {
        for (int i = 0; i < sources.length; i++) {
            add(sources[i], overwrite);
        }
    }

    /**
     * Adds each property set with a flag for the set indicating overwrite permission.
     *
     * @param sources
     * @param overwrite
     * @throws Exception thrown if the arguments are not the same length.
     */
    public void add(Properties[] sources, boolean[] overwrite) {
        if (sources.length != overwrite.length) {
            throw new GeneralException("Properties[" + sources.length + "] and boolean[" + overwrite.length + "] arrays are not the same length.");
        }
        for (int i = 0; i < sources.length; i++) {
            add(sources[i], overwrite[i]);
        }
    }

    /**
     * Set the value of with the given Date. Note: internally this is actually stored as a long.
     *
     * @param key
     * @param d
     * @throws Exception
     */
    public void setDate(String key, Date d) {
        setLong(key, d.getTime());
    }


    /**
     * Get the value as a uri
     *
     * @param key
     * @return the URI
     * @throws Exception if the value cannot be parsed as a URI.
     */

    public URI getURI(String key) {
        if (null == getString(key)) {
            throw new GeneralException("Error, a null value for the uri >>" + key + "<< was encountered");
        }
        return URI.create(getString(key));
    }


    /**
     * Sets the value for the key from the URI
     *
     * @param key
     * @param uri
     * @throws Exception
     */

    public void setURI(String key, URI uri) {
        setString(key, uri.toString());
    }


    /**
     * A convenience. This takes a string and checks that it is a valid URI before setting
     * the property.
     *
     * @param key
     * @param uriString the uri
     * @throws Exception if the supplied string cannot be parsed as a URI.
     */
    public void setURI(String key, String uriString) {
        setURI(key, URI.create(uriString));
    }


    /**
     * Gets the value as a URL.
     *
     * @param key
     * @return the URL
     */
    public URL getURL(String key) {
        if (null == getString(key)) {
            throw new GeneralException("Error, a null value for the url >>" + key + " << was encountered");
        }
        try {
            return new URL(getString(key));
        } catch (MalformedURLException e) {
            throw new GeneralException("Malformed url", e);
        }
    }


    /**
     * Set the value from the given URL
     *
     * @param key
     * @param url the new value
     * @throws Exception
     */
    public void setURL(String key, URL url) {
        setString(key, url.toString());
    }


    /**
     * A convenience. This takes a string and checks that it is a valid URL before setting
     * the property.
     *
     * @param key
     * @param urlString
     * @throws Exception
     */
    public void setURL(String key, String urlString) {
        try {
            setURL(key, new URL(urlString));
        } catch (MalformedURLException e) {
            throw new GeneralException("Malformed url", e);
        }
    }


    /**
     * Get the value as a date.
     *
     * @param key
     * @return the date
     * @throws Exception if the value is not parseable as a long.
     */
    public Date getDate(String key) {
        return new Date(getLong(key));
    }


    /**
     * This will take a c list separated by a string and return a vector of strings.
     *
     * @param key
     * @param separator a string that separates each element of the list.
     * @throws Exception
     */
    public String[] getList(String key, String separator) {
        String list = getString(key);
        StringTokenizer st = new StringTokenizer(list, separator);
        String[] outS = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            outS[i++] = st.nextToken();
        } /* end while*/
        return outS;
    }//end getList




    /**
     * Gets the value associated with the key and splits it into an array of strings
     * using the value from <code>getListSeparator</code>
     *
     * @param key
     * @return the array of strings
     * @throws Exception
     */
    public String[] getList(String key) {
        return getList(key, getListSeparator());
    }


    /**
     * Sets the name of the file that calling the default <code>store()</code> method
     * will write to. It may be changed at any time.
     *
     * @param fileName the name of the file
     */
    public void setStoreFileName(String fileName) {
        storeFileName = fileName;
    }

    /**
     * Get the name of the store file
     *
     * @return the name of the file.
     */
    public String getStoreFileName() {
        return storeFileName;
    }

    /**
     * Loads the content of the file in <code>getStoreFileName</code>
     *
     * @throws Exception if the file cannot be read
     */
    public void load() {
        try {
            load(getStoreFileName());
            FileInputStream fis = new FileInputStream(getStoreFileName());
            load(fis);
            fis.close();
        } catch (Exception e) {
            new GeneralException("Error loading properties", e);
        }
    }

    /**
     * Load the content of the given file into the current properties object, over-writing any
     * duplicate values.
     *
     * @param storeFileName
     */
    public void load(String storeFileName) {
        load(new File(storeFileName));
    }

    /**
     * Loads from a file rather than from a stream. The parent class really needs one of these...
     * Note that this does not set the default property file name for this object! This allows you
     * to load multiple property files into the current properties object. Be advised that no vetting of
     * the result occurs -- a property's value will be the last one loaded.
     *
     * @param f the file
     */
    public void load(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            load(fis);
            fis.close();
        } catch (Exception e) {
            throw new GeneralException("Error loading properties", e);
        }
    }


    /**
     * This sets a list directly, so the list itself is not touched. This is equivalent to calling
     * <code>setString(key,list)</code> and is simply provided as a convenience.
     *
     * @param key
     * @param list
     */
    public void setList(String key, String list) {
        setString(key, list);
    }



    /**
     * Set the value of a list using the default separator.
     *
     * @param key
     * @param list the array of strings to store as a list
     */
    public void setList(String key, String[] list) {
        setList(key, list, getListSeparator());
    }


    /**
     * Writes the properties to the store file. The header is written at the start
     * of the file. It may be empty.
     *
     * @param header the header (i.e. intial comment) for the file
     * @throws java.io.IOException if no store file has been set.
     */

    public void store(String header)  {
        if (getStoreFileName() == null) {
            throw new IllegalStateException("Error, no file set for default save");

        }
        try {
            FileOutputStream fos = new FileOutputStream(getStoreFileName());
            store(fos, header);
            fos.flush();
            //fos.close();
        } catch (Exception e) {
            throw new GeneralException("Error storing file", e);
        }
    }

    /**
     * This just invokes <code>store</code> with an empty header.
     *
     */

    public void store() {
        store("");
    }

    /**
     * Sets the value from an array of strings, delimiting them with the supplied separator.
     *
     * @param key
     * @param list      the array of strings
     * @param separator
     */
    public void setList(String key, String[] list, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.length; i++) {
            String temp = (String) list[i];
            if (temp != null) {
                sb.append(temp);
                if (i != list.length - 1) {
                    // if we aren't at the end of the list, append a comma
                    sb.append(separator);
                }
            } /* end if */
        }/* end for */
        setString(key, sb.toString());
    }


    /**
     * This will return <code>true</code> if the current properties has no entries and <code>false</code>
     * otherwise.
     */
    public boolean isEmpty() {
        return !elements().hasMoreElements();
    }

    /**
     * Creates a deep copy of this object.
     *
     * @return the XProperties object
     */
    public Object clone() {
        return new XProperties(this);

    }

    /**
     * Gets a clone of this object. Note that each call causes a new clone to be produced.
     *
     * @return the XProperties object as an XProperties object, so unlike the <code>clone</code>
     *         method no casting is required.
     */
    public XProperties getClone() {
        return (XProperties) clone();
    }

    /**
     * Checks if this XProperties object is the same as a given object. Note that if these are
     * large this might be expensive.
     *
     * @param obj the Object to compare to
     */

    public boolean equals(Object obj) {
        if (!(obj instanceof XProperties)) {
            return false;
        }
        XProperties xp = (XProperties) obj;
        // check that the key sets match
        if (!(keySubset(xp) && xp.keySubset(this))) {
            return false;
        }
        // now actually compare that the values match.
        Enumeration thisKeys = keys();

        // have to check that the sets of keys are equal first
        while (thisKeys.hasMoreElements()) {
            String key = (String) thisKeys.nextElement();
            if (!getProperty(key).equals(xp.getProperty(key))) {
                return false;
            } //end if

        } // end while

        return true;
    }

    /**
     * Checks that the keys of b are a subset of the keys in this <code>XProperties</code>.
     *
     * @param b the <code>XProperties</code> to check against.
     */
    protected boolean keySubset(XProperties b) {
        Enumeration bKeys = b.keys();
        while (bKeys.hasMoreElements()) {
            if (!containsKey(bKeys.nextElement())) {
                return false;
            }
        } //end while
        return true;
    }

    /**
     * This exists simply for testing this object. You supply the name of a file that will be written so that this can check
     * that it does read and write files correctly. It will also put various properties in and retrieve them. The file
     * is left should you want to look at it.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Usage: a single argument that is a file to be created.\n" +
                        "The main method for this class is only for showing functionality");
                return;
            }
            XProperties xp = new XProperties();
            xp.setBoolean("boolean.test", true);
            java.io.File f = new File(args[0]);
            xp.setFile("file.test", f);
            int integerTest = 123456789;
            xp.setInt("integer.test", integerTest);
            long longTest = System.currentTimeMillis();
            xp.setLong("long.test", longTest);
            String stringTest = "you need more thneeds.";
            xp.setString("string.test", stringTest);
            String b64Test = "Four score and seven years ago,\nour\tfathers\n\nset forth, uh, I forget the rest...";
            Date d = new Date();
            d.setTime(10000000); // oh just some number...
            xp.setDate("date.test", d);
            double dd = Math.random();
            xp.setDouble("double.test", dd);
            Object[] oList = new Object[]{stringTest, new Integer(integerTest), new Double(dd)};
            xp.setSerializableList("serList.test", oList);
            URI uri = new URI("http://foo/bar/baz#fnord");
            xp.setURI("uri.test", uri);
            xp.setBytes("b64.test", b64Test.getBytes());
            String[] testList = new String[]{"foo", "fnord", "fnibble"};
            xp.setList("list.test", testList); // with the default delimiter
            String customDelimiter = "**";
            xp.setList("list.test2", testList, customDelimiter); // with a truly custom delimiter.

            // this serializes a string, since this is serializable, an object and has a non-trivial equals method
            boolean ok = false;
            xp.setSerializable("ser.test", b64Test);
            xp.store(new java.io.FileOutputStream(args[0]), "test header");
            xp = new XProperties();
            xp.load(new java.io.FileInputStream(args[0]));
            xp.list(System.out);
            System.out.println("\n-----\ntesting properties\n-----\n");
            System.out.println("boolean test " + (xp.getBoolean("boolean.test") ? "ok" : "failed"));
            System.out.println("file = " + xp.getFile("file.test"));
            System.out.println("string test " + (xp.getString("string.test").equals(stringTest) ? "ok" : "failed"));
            System.out.println("integer test " + (xp.getInt("integer.test") == integerTest ? "ok" : "failed"));
            System.out.println("long test " + (xp.getLong("long.test") == longTest ? "ok" : "failed"));
            System.out.println("date test " + (xp.getDate("date.test").equals(d) ? "ok" : "failed"));
            System.out.println("double test " + (xp.getDouble("double.test") == dd ? "ok" : "failed"));
            String b64String2 = new String(xp.getBytes("b64.test"));
            System.out.println("byte test " + (b64String2.equals(b64Test) ? "ok" : "failed"));
            System.out.println("byte string = " + b64String2);
            String serTest = (String) xp.getSerializable("ser.test");
            System.out.println("serialized test " + (serTest.equals(b64Test) ? "ok" : "failed"));
            System.out.println("uri.test " + (uri.equals(xp.getURI("uri.test")) ? "ok" : "failed"));
            String[] list1 = xp.getList("list.test2", customDelimiter);
            ok = testList[0].equals(list1[0]) && testList[1].equals(list1[1]) && testList[2].equals(list1[2]);
            System.out.println("list test with custom delimiter " + (ok ? "ok" : "failed"));
            String[] list0 = xp.getList("list.test");
            ok = testList[0].equals(list0[0]) && testList[1].equals(list0[1]) && testList[2].equals(list0[2]);
            System.out.println("list test with default delimiter " + (ok ? "ok" : "failed"));
            Object[] oList2 = xp.getSerializableList("serList.test");
            ok = ((String) oList2[0]).equals(stringTest);
            ok = ok && ((Integer) oList[1]).intValue() == integerTest;
            ok = ok && ((Double) oList[2]).doubleValue() == dd;
            System.out.println("serialized list test = " + (ok ? "ok" : "failed"));
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
