package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;
import org.junit.Test;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/30/19 at  3:46 PM
 */
public class TemplateTest extends TestBase {
    /**
     * NBasic test to show that templates can be used for replacements
     *
     * @throws Exception
     */
    HashMap<String, String> replacements;

    public HashMap<String, String> getReplacements() {
        if (replacements == null) {
            replacements = new HashMap<>();
            replacements.put("y", "yy");
            replacements.put("xyy", "mairzey doats");
            replacements.put("a", "${qq}");
            replacements.put("abc", "defg");
            replacements.put("foo", "fnord");
            replacements.put("uu", "abc${foo}de");
            replacements.put("qq", "${uu}${abc}");
            replacements.put("tt", "/home/ncsa/templates/json");
            replacements.put("de", "deserialize -file ${tt}");
        }
        return replacements;
    }

    /**
     * Test basic replacements.
     *
     * @throws Exception
     */
    @Test
    public void testReplace() throws Exception {
        String template = "abc${abc}";
        String result = "abcdefg";
        assert result.equals(TemplateUtil.replaceAll(template, getReplacements()));
    }

    /**
     * Tests that a replacement within a replacement will be resolved correctly
     *
     * @throws Exception
     */
    @Test
    public void testNested() throws Exception {
        String x = "xyz${uu}t";
        String result ="xyzabcfnorddet";
        assert result.equals(TemplateUtil.replaceAll(x, getReplacements()));

    }

    @Test
    public void testNested2() throws Exception{
        String x = "ab ${x${y}}...";
        String result = "ab mairzey doats...";
        String rc = TemplateUtil.replaceAll(x, getReplacements());
        System.out.println("rc=\"" + rc + "\"");
        assert result.equals(rc);
    }

    /**
     * Tests that multiple replacements in a single template are all replaced
     *
     * @throws Exception
     */
    @Test
    public void testMultiples() throws Exception {
        String x = "${qq}foo${uu}";
        String result = "abcfnorddedefgfooabcfnordde";
        assert result.equals(TemplateUtil.replaceAll(x, getReplacements()));
    }

    /**
     * Check that a command like object (with an embedded template) is expanded correctly.
     * @throws Exception
     */
    @Test
    public void testDe() throws Exception{
        String x = "${de}/a123.xml";
        String result ="deserialize -file /home/ncsa/templates/json/a123.xml";
        assert result.equals(TemplateUtil.replaceAll(x, getReplacements()));

    }
}
