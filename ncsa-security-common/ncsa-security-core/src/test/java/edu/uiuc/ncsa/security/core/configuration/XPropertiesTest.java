package edu.uiuc.ncsa.security.core.configuration;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.util.Date;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Apr 28, 2010 at  2:24:00 PM
 */
public class XPropertiesTest extends TestCase {

    @Test
    public void testAll() throws Exception {
        URI x = URI.create("foo.bar.baz");
        XProperties xp = new XProperties();
        xp.setBoolean("boolean.test", true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        java.io.File f = new File("foo");
        xp.setFile("file.test", f);
        int integerTest = 123456789;
        xp.setInt("integer.test", integerTest);
        Long longTest = System.currentTimeMillis();
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
        xp.store(baos, "test header");
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        //xp.store(new java.io.FileOutputStream(args[0]), "test header");
        xp = new XProperties();
        xp.load(bais);
        // ** print it out for fun.
        xp.list(System.out);
        assert xp.getBoolean("boolean.test");
        assert xp.getString("string.test").equals(stringTest);
        assert xp.getInt("integer.test") == integerTest;
        assert xp.getLong("long.test") == longTest;
        assert xp.getDate("date.test").equals(d);
        assert xp.getDouble("double.test") == dd;
        String b64String2 = new String(xp.getBytes("b64.test"));
        assert b64String2.equals(b64Test);
        String serTest = (String) xp.getSerializable("ser.test");
        assert serTest.equals(b64Test);
        assert uri.equals(xp.getURI("uri.test"));
        String[] list1 = xp.getList("list.test2", customDelimiter);
        assert testList[0].equals(list1[0]) && testList[1].equals(list1[1]) && testList[2].equals(list1[2]);
        String[] list0 = xp.getList("list.test");
        assert testList[0].equals(list0[0]) && testList[1].equals(list0[1]) && testList[2].equals(list0[2]);
        Object[] oList2 = xp.getSerializableList("serList.test");
        assert ((String) oList2[0]).equals(stringTest) && ((Integer) oList[1]) == integerTest && ((Double) oList[2]) == dd;

    }

    public void testNamespace() throws Exception {
        NSProperties nsp = new NSProperties();
        String ns1 = "my.first.namespace#";
        String ns2 = "my:other:namespace#";
        nsp.addNSPrefix(ns1, "ns1");
        nsp.addNSPrefix(ns2, "ns2");
        String key = "string";
        String testString1 = "testString" + System.currentTimeMillis();
        String testString2 = "testOtherString" + System.currentTimeMillis();
        nsp.setString(ns1, key, testString1);
        nsp.setString("ns2", key, testString2);
        assert nsp.getString("ns1", key).equals(testString1);
        assert nsp.getString(ns2, key).equals(testString2);
        nsp.store(System.out, "");

    }

    @Test
    public void testNSResolution() throws Exception{
        String ns = "test:xprops#";
        String ns1 = "test:xprops1#";
        String prefix = "fnord";
        String prefix1 = "fnibble";
        NSProperties nsProperties = new NSProperties();
        nsProperties.addNSPrefix(ns, prefix);
        nsProperties.addNSPrefix(ns1, prefix1);
        assert nsProperties.toPrefix(ns).equals(prefix);
        assert nsProperties.toPrefix(prefix).equals(prefix);
        assert nsProperties.toPrefix(nsProperties.toPrefix(ns)).equals(prefix);
        String nsKey1=ns1+"testKey1";
        String prefixKey1=prefix1+NSProperties.NAMESPACE_DELIMITER + "testKey1";
        assert nsProperties.toNS(prefixKey1).equals(nsKey1);
        assert nsProperties.toNS(nsProperties.toNS(prefixKey1)).equals(nsKey1);
        assert nsProperties.toNS(nsKey1).equals(nsKey1);
    }

    @Test
    public void testNSAll() throws Exception {
        URI x = URI.create("foo.bar.baz");
        System.out.println("abs?=" + x.isAbsolute() + ", path=" + x.getPath() + ",scheme=" + x.getScheme());
        String ns = "test:xprops#";
        String prefix = "fnord";
        NSProperties nsProperties = new NSProperties();
        nsProperties.addNSPrefix(ns, prefix);
        nsProperties.setBoolean(ns, "boolean", true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        java.io.File f = new File("foo");
        nsProperties.setFile(ns, "file", f);
        int integerTest = 123456789;
        nsProperties.setInt(ns, "integer", integerTest);
        long longTest = System.currentTimeMillis();
        nsProperties.setLong(ns, "long", longTest);
        String stringTest = "you need more thneeds.";
        nsProperties.setString(ns, "string", stringTest);
        String b64Test = "Four score and seven years ago,\nour\tfathers\n\nset forth, uh, I forget the rest...";
        Date d = new Date();
        d.setTime(10000000); // oh just some number...
        nsProperties.setDate(prefix, "date", d);
        double dd = Math.random();
        nsProperties.setDouble(prefix, "double", dd);
        Object[] oList = new Object[]{stringTest, new Integer(integerTest), new Double(dd)};
        nsProperties.setSerializableList(prefix, "serialized", oList);
        URI uri = new URI("http://foo/bar/baz#fnord");
        nsProperties.setURI(prefix, "uri", uri);
        nsProperties.setBytes(prefix, "base64", b64Test.getBytes());
        String[] testList = new String[]{"foo", "fnord", "fnibble"};
        nsProperties.setList(prefix, "list", testList); // with the default delimiter
        String customDelimiter = "**";
        nsProperties.setList(prefix, "customlist", testList, customDelimiter); // with a truly custom delimiter.

        // this serializes a string, since this is serializable, an object and has a non-trivial equals method
        boolean ok = false;
        nsProperties.setSerializable(prefix, "serialized2", b64Test);
        nsProperties.store(baos, "test header");
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        //xp.store(new java.io.FileOutputStream(args[0]), "test header");
        nsProperties = new NSProperties();

        nsProperties.load(bais);
        // ** print it out for fun.
        System.out.print("\n\n");
        nsProperties.store(System.out, "serialized directly, with a groovy custom header");

        assert nsProperties.getBoolean(prefix, "boolean");
        assert nsProperties.getString(prefix, "string").equals(stringTest);
        assert nsProperties.getInt(prefix, "integer") == integerTest;
        assert nsProperties.getLong(ns, "long") == longTest;
        assert nsProperties.getDate(ns, "date").equals(d);
        assert nsProperties.getDouble(prefix, "double") == dd;
        String b64String2 = new String(nsProperties.getBytes(prefix, "base64"));
        assert b64String2.equals(b64Test);
        String serTest = (String) nsProperties.getSerializable(prefix, "serialized2");
        assert serTest.equals(b64Test);
        assert uri.equals(nsProperties.getURI(ns, "uri"));
        String[] list1 = nsProperties.getListByNS(prefix, "customlist", customDelimiter);
        assert testList[0].equals(list1[0]) && testList[1].equals(list1[1]) && testList[2].equals(list1[2]);
        String[] list0 = nsProperties.getListByNS(prefix, "list");
        assert testList[0].equals(list0[0]) && testList[1].equals(list0[1]) && testList[2].equals(list0[2]);
        Object[] oList2 = nsProperties.getSerializableList(prefix, "serialized");
        assert ((String) oList2[0]).equals(stringTest) && ((Integer) oList[1]) == integerTest && ((Double) oList[2]) == dd;
        ByteArrayOutputStream ps = new ByteArrayOutputStream();
        //String fileName = "/tmp/nsp.xml";
        //FileOutputStream ps = new FileOutputStream(fileName);
        nsProperties.storeToXML(ps, "Test for XML");
        ps.flush();
        ps.close();
        NSProperties zzz = new NSProperties();
        ByteArrayInputStream bais1 = new ByteArrayInputStream(ps.toByteArray());
        //zzz.loadFromXML(new FileInputStream(fileName));
        zzz.loadFromXML(bais1);
        nsProperties = zzz;
        nsProperties.addNSPrefix(ns, prefix);
        assert nsProperties.getBoolean(prefix, "boolean");
        assert nsProperties.getString(prefix, "string").equals(stringTest);
        assert nsProperties.getInt(prefix, "integer") == integerTest;
        assert nsProperties.getLong(ns, "long") == longTest;
        assert nsProperties.getDate(ns, "date").equals(d);
        assert nsProperties.getDouble(prefix, "double") == dd;
         b64String2 = new String(nsProperties.getBytes(prefix, "base64"));
        assert b64String2.equals(b64Test);
        serTest = (String) nsProperties.getSerializable(prefix, "serialized2");
        assert serTest.equals(b64Test);
        assert uri.equals(nsProperties.getURI(ns, "uri"));
        list1 = nsProperties.getListByNS(prefix, "customlist", customDelimiter);
        assert testList[0].equals(list1[0]) && testList[1].equals(list1[1]) && testList[2].equals(list1[2]);
         list0 = nsProperties.getListByNS(prefix, "list");
        assert testList[0].equals(list0[0]) && testList[1].equals(list0[1]) && testList[2].equals(list0[2]);
         oList2 = nsProperties.getSerializableList(prefix, "serialized");
        assert ((String) oList2[0]).equals(stringTest) && ((Integer) oList[1]) == integerTest && ((Double) oList[2]) == dd;
    }
}
