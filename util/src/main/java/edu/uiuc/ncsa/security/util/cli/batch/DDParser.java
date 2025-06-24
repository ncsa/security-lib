package edu.uiuc.ncsa.security.util.cli.batch;

import edu.uiuc.ncsa.security.core.util.FileUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A darned dumb parser for batch files. This has a basic grammar of
 * <ol>
 *     <li>Any line that starts with a # is a comment</li>
 *     <li>A statement may span multiple lines and terminates with a ;</li>
 *     <li>Each statement is rendered as a single line with single blanks where the line feeds were</li>
 *     <li>Statements are then ready to be fed one after another to the CLI and interpreted.</li>
 * </ol>
 * This reads a stream and spits out a list of lines that are fed to a CLI.
 * It is <i>very</i> stupid. The reason it exists is that sometimes really simple
 * batch files are needed, e.g., when someone is installing OA4MP and some webkeys need to be
 * created. They do not have a full set of utilities (such as QDL), might not have
 * bash or other standard tools availble and really there is not
 * much more than running through a set of simple commands with a few environment variables.
 * <h3>Bottom line...</h3>
 * <pre>
 *     *****************************************
 *     * For anything other than very specific *
 *     * systems programming,                  *
 *     *            >> Use QDL <<              *
 *     *****************************************
 * </pre>
 */
public class DDParser {
    public static void main(String[] args) {
        try {
            DDParser ddParser = new DDParser();
            List<String> content = ddParser.parse(new FileInputStream("/home/ncsa/dev/ncsa-git/oa4mp/server-admin/src/main/scripts/jwt-scripts/ex_env.cmd"));
            for (String line : content) {
                System.out.println(line);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public List<String> parse(Reader reader) throws Throwable {
        BufferedReader bufferedReader;
        if (reader instanceof BufferedReader) {
            bufferedReader = (BufferedReader) reader;
        } else {
            bufferedReader = new BufferedReader(reader);
        }
        List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (line != null) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        reader.close();
        return process(lines);
    }

    public List<String> parse(InputStream inputStream) throws Throwable {
        List<String> lines = FileUtil.readFileAsLines(inputStream);
        return process(lines);
    }

    protected List<String> process(List<String> lines) {
        List<String> output = new ArrayList<String>();
        StringBuilder buffer = new StringBuilder();
        for (String lineIn : lines) {
            if (lineIn.trim().startsWith("#")) {
                buffer = new StringBuilder();
                continue;
            }
            if (StringUtils.isTrivial(lineIn)) continue; // skip blank lines
            lineIn = lineIn.trim();
            if (lineIn.endsWith(";")) {
                buffer.append(" " + lineIn.substring(0, lineIn.length() - 1)); // whack off final ";"
                output.add(buffer.toString());
                buffer = new StringBuilder(); // reset buffer
            } else {
                buffer.append(" " + lineIn); // whack off final ";"
            }
        }
        return output;

    }
}
