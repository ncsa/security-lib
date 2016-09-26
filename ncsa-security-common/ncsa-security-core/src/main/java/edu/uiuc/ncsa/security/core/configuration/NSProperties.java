package edu.uiuc.ncsa.security.core.configuration;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * In addition to the abilities of XProperties this allows for
 * <ul>
 * * <li>Basic but extremely useful namespace support. You may get/set properties using a namespace in addition
 * to a property key. Be warned this is simple since it just gloms the namespace in front of the key, but it
 * works quite well for most cases.</li>
 * </ul>
 * <p/>
 * There is prefix resolution, if prefixes are found. For instance, this might be a properties file:
 * <pre>
 *     xmlns\:fileStore=http://my.ns/fileStore#
 *     xmlns\:email=myproxy:ncsa,2011/1.1/email#
 *     fileStore\:dataPath=/tmp/data
 *     fileStore\:indexPath=/tmp/index
 *     email\:enabled=false
 * </pre>
 * (The "\" on the left-hand side is colon escaping from the Java Properties class.)
 * In which case you could retrieve values as getBoolean("email", "enabled")
 * or getBoolean("myproxy:ncsa,2011/1.1/email#enabled")
 * <p/>
 * You may add or remove prefixes by calling the appropriate {@link #addNSPrefix(String, String)}
 * method. These will be saved as part of the properties file.
 */
public class NSProperties extends XProperties  {

    public static final String XMLNS_PREFIX = "xmlns";
    HashMap<String, String> nsToPrefix;

    public HashMap<String, String> getPrefixToNS() {
        if (prefixToNS == null) {
            prefixToNS = new HashMap<String, String>();
        }
        return prefixToNS;
    }

    HashMap<String, String> prefixToNS;

    public void addNSPrefix(String ns, String prefix) {
        getNsToPrefix().put(ns, prefix);
        getPrefixToNS().put(prefix, ns);
    }

    public void removeNS(String ns) {
        String prefix = getNsToPrefix().get(ns);
        getNsToPrefix().remove(ns);
        if (prefix != null) {
            getPrefixToNS().remove(prefix);
        }
    }

    public Collection<String> getNamespaces() {
        return getNsToPrefix().keySet();
    }

    public Collection<String> getPrefixes() {
        return getPrefixToNS().keySet();
    }

    /**
     * Support for namespace resolution.
     *
     * @return
     */
    protected HashMap<String, String> getNsToPrefix() {
        if (nsToPrefix == null) {
            nsToPrefix = new HashMap<String, String>();
        }
        return nsToPrefix;
    }


    public boolean getBoolean(String ns, String key) {
        return getBoolean(resolveKey(ns, key));
    }

    /**
     * Resolve a namespace to a single key. This is very simple. It appends the key to
     * the namespace, checking if the {@link #NAMESPACE_DELIMITER} is needed.
     * If there is no namespace provided, this just returns the key.
     *
     * @param ns
     * @param key
     * @return
     */

    protected String resolveKey(String ns, String key) {
        String realNS = ns;
        if (getPrefixToNS().containsKey(ns)) {
            realNS = getPrefixToNS().get(ns);
        }

        return realNS + key;
    }

    /**
     * Given a namespace, or a prefix, resolve to the actual namespace.
     *
     * @param key
     * @return
     */
    protected String toPrefix(String key) {
        return replaceHeader(getNamespaces(), getNsToPrefix(), key, false);
    }

    public class StringLengthComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            if (o1.length() < o2.length()) {
                return -1;
            } else if (o1.length() > o2.length()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Headers is a list of prefixes or namespaces sorted in order from longest to shortest
     *
     * @param headers
     * @param lookup
     * @param key
     * @param removeDelimeter boolean that if true means strip out the namespace delimiter (usually :), otherwise, add it in.
     * Being true effectively means convert the string from a prefix to a namespace.
     * @return
     */
    String replaceHeader(Collection<String> headers, Map<String, String> lookup, String key, boolean removeDelimeter) {
        if (headers == null || headers.size() == 0) {
            return key;
        }
        String[] keyArrays = new String[headers.size()];
        keyArrays = headers.toArray(keyArrays);
        Arrays.sort(keyArrays, new StringLengthComparator());
        String resolvedKey = key;

        for (String x : keyArrays) {
            if (key.startsWith(x)) {
                // case that there is no embedded ns or prefix, User just wants the key itself resolved.
                if (x.equals(key)) {
                    return lookup.get(x);
                }

                return lookup.get(x) + (removeDelimeter?"":NAMESPACE_DELIMITER) + key.substring(x.length() + (removeDelimeter ? NAMESPACE_DELIMITER.length() : 0));
            }
        }
        return resolvedKey;

    }


    /**
     * Given a key with a prefix or a namespace, resolve it to the namepsace + key.
     * If there is no key this is equivalent to just doing a lookup.
     *
     * @param key
     * @return
     */
    protected String toNS(String key) {
        return replaceHeader(getPrefixes(), getPrefixToNS(), key, true);
    }

    public void setBoolean(String ns, String key, boolean value) {
        setBoolean(resolveKey(ns, key), value);
    }

    public int getInt(String ns, String key) {
        return getInt(resolveKey(ns, key));
    }

    public void setInt(String ns, String key, int value) {
        setInt(resolveKey(ns, key), value);
    }

    public long getLong(String ns, String key) {
        return getLong(resolveKey(ns, key));
    }

    public void setLong(String ns, String key, long value) {
        setLong(resolveKey(ns, key), value);
    }

    public File getFile(String ns, String key) {
        return getFile(resolveKey(ns, key));
    }

    public File getFile(String ns, String key, File parent) {
        return getFile(resolveKey(ns, key), parent);
    }

    public void setFile(String ns, String key, File f) {
        setFile(resolveKey(ns, key), f);
    }

    public String getString(String ns, String key) {
        return getString(resolveKey(ns, key));
    }

    public void setString(String ns, String key, String value) {
        setString(resolveKey(ns, key), value);
    }

    public Object getSerializable(String ns, String key) {
        return getSerializable(resolveKey(ns, key));
    }

    public void setSerializable(String ns, String key, Serializable object) {
        setSerializable(resolveKey(ns, key), object);
    }

    public void setSerializableList(String ns, String key, Object[] oList) {
        setSerializableList(resolveKey(ns, key), oList);

    }

    public Object[] getSerializableList(String ns, String key) {
        return getSerializableList(resolveKey(ns, key));
    }

    public byte[] getBytes(String ns, String key) {
        return getBytes(resolveKey(ns, key));
    }

    public void setBytes(String ns, String key, byte[] ba) {
        setBytes(resolveKey(ns, key), ba);
    }

    public void setDouble(String ns, String key, double value) {
        setDouble(resolveKey(ns, key), value);
    }

    public double getDouble(String ns, String key) {
        return getDouble(resolveKey(ns, key));
    }

    public Object setProperty(String ns, String key, String value) {
        return setProperty(resolveKey(ns, key), value);
    }

    public void setDate(String ns, String key, Date d) {
        setDate(resolveKey(ns, key), d);
    }

    public URI getURI(String ns, String key) {
        return getURI(resolveKey(ns, key));
    }

    public void setURI(String ns, String key, URI uri) {
        setURI(resolveKey(ns, key), uri);
    }

    public void setURI(String ns, String key, String uriString) {
        setURI(resolveKey(ns, key), uriString);
    }

    public URL getURL(String ns, String key) {
        return getURL(resolveKey(ns, key));
    }

    public void setURL(String ns, String key, URL url) {
        setURL(resolveKey(ns, key), url);
    }

    public void setURL(String ns, String key, String urlString) {
        setURL(resolveKey(ns, key), urlString);
    }

    public Date getDate(String ns, String key) {
        return getDate(resolveKey(ns, key));
    }

    /**
     * Get the list using the namespace and the separator. This is named this way to avoid a method name clash
     */
    public String[] getListByNS(String ns, String key, String separator) {
        return getList(resolveKey(ns, key), separator);
    }

    /**
     * Get the list using the namespace. This is named this way to avoid a method name clash.
     *
     * @param ns
     * @param key
     * @return
     */
    public String[] getListByNS(String ns, String key) {
        return getList(resolveKey(ns, key));
    }

    public void setList(String ns, String key, String list) {
        setList(resolveKey(ns, key), list);
    }

    public void setList(String ns, String key, String[] list) {
        setList(resolveKey(ns, key), list);
    }

    public void setList(String ns, String key, String[] list, String separator) {
        setList(resolveKey(ns, key), list, separator);

    }

    @Override
    public void load(InputStream inStream) throws IOException {
        XProperties xp0 = new XProperties();
        XProperties xp1 = new XProperties();
        xp0.load(inStream);
        // now we have to pull out the namespaces
        for (Object key : xp0.keySet()) {
            String keyS = key.toString();
            if (keyS.startsWith(XMLNS_PREFIX + NAMESPACE_DELIMITER)) {
                addNSPrefix(xp0.getString(keyS), keyS.substring((XMLNS_PREFIX + NAMESPACE_DELIMITER).length()));
                //addNSPrefix(keyS.substring((XMLNS_PREFIX + NAMESPACE_DELIMITER).length()), xp0.getString(keyS));
            } else {
                xp1.put(keyS, xp0.getString(keyS));
            }
        }
        // now we have everything. Let's add it to the current object
        for (Object key : xp1.keySet()) {
            String keyS = toNS(key.toString());
            //       System.out.println("Adding key=" + keyS + ", value=" + xp1.getString(key.toString()));
            setString(keyS, xp1.getString(key.toString()));
        }
    }

    @Override
    public void store(OutputStream os, String header) {
        try {
            XProperties xp = new XProperties();

            for (String prefix : getPrefixToNS().keySet()) {
                xp.put(XMLNS_PREFIX + NAMESPACE_DELIMITER + prefix, getPrefixToNS().get(prefix));
            }
            //xp.store(os, "");

            //XProperties xp2 = new XProperties();
            XProperties xp2 = xp;

            for (Object key : keySet()) {
                xp2.put(toPrefix(key.toString()), getString(key.toString()));
            }

            xp2.store(os, header);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    @Override
    public void list(PrintStream out) {
        XProperties xp = new XProperties();
        for (String prefix : getPrefixToNS().keySet()) {
            xp.put("xmlns" + NAMESPACE_DELIMITER + prefix, getPrefixToNS().get(prefix));
        }
        xp.list(out);

        XProperties xp2 = new XProperties();

        for (Object key : keySet()) {
            xp2.put(toPrefix(key.toString()), getString(key.toString()));
        }

        xp2.list(out);
    }
}