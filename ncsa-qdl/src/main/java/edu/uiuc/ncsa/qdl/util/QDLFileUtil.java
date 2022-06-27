package edu.uiuc.ncsa.qdl.util;

import edu.uiuc.ncsa.qdl.variables.QDLStem;
import edu.uiuc.ncsa.qdl.variables.StemUtility;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Bunch of file reading and writing utilities so I don't have to boiler plate this stuff
 * <p>Created by Jeff Gaynor<br>
 * on 1/29/20 at  9:52 AM
 */
public class QDLFileUtil extends FileUtil {

    public static StemVariable readFileAsStem(String fileName) throws Throwable {
        checkFile(fileName);
        StemVariable out = new StemVariable();
        Path path = Paths.get(fileName);

        List<String> contents = Files.readAllLines(path);
        int i = 0;
        //Read from the stream
        for (String content : contents) {
            out.put(Integer.toString(i++), content);
        }

        return out;
    }



    /**
     * Note that this is a stem list or the ouotput is random.
     *
     * @param filename
     */
    public static void writeStemToFile(String filename, QDLStem contents) throws Throwable {
        FileWriter fileWriter = new FileWriter(new File(filename));
        fileWriter.write(StemUtility.stemListToString(contents, true));
        fileWriter.flush();
        fileWriter.close();
    }


}
