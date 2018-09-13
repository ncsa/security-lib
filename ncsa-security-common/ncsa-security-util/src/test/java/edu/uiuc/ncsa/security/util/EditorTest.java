package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.cli.EditorInputLine;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/31/18 at  3:22 PM
 */
public class EditorTest extends TestBase {
    @Test
    public void testInputLine() throws Exception {
        String lineIn = "a [2,5]";
        EditorInputLine eil = new EditorInputLine(lineIn);
        // plain vanilla test
        assert eil.getCommand().equals("a");
        assert eil.hasIndices();
        assert eil.getIndices()[0] == 2;
        assert eil.getIndices()[1] == 5;

        lineIn = "a [ 2,   6]";
        eil = new EditorInputLine(lineIn);
        assert eil.getCommand().equals("a");
        assert eil.hasIndices();
        assert eil.getIndices()[0] == 2;
        assert eil.getIndices()[1] == 6;


        lineIn = "a [ 2,   6, 11]";
        eil = new EditorInputLine(lineIn);
        assert eil.getCommand().equals("a");
        assert eil.hasIndices();
        assert eil.getIndices()[0] == 2;
        assert eil.getIndices()[1] == 6;
        assert eil.getIndices()[2] == 11;
    }

}
