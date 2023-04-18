package edu.uiuc.ncsa.security.core.util;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility do to certain common file operations. These are mostly aimed at smaller files.
 * <p>Created by Jeff Gaynor<br>
 * on 1/26/21 at  7:10 AM
 */
public class FileUtil {
    /**
     * This just checks various things about the file, such as does it exist, is it a file,
     * can it be read? so the user gets a better experience about what might be wrong.
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    protected static File checkFile(String fileName) throws FileNotFoundException, IllegalAccessException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("Error: The file \"" + fileName + "\" does not exist.");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Error: \"" + fileName + "\" is not a file.");
        }
        if (!file.canRead()) {
            throw new IllegalAccessException("Error: the file \"" + fileName + "\" is not readable.");
        }
        return file;
    }

    /**
     * Read a (possibly binary) file and convert the contents to a base64 escaped string.
     * @param fileName
     * @return
     * @throws Throwable
     */
    // Next two are used in OA4MP, QDL but not in this module, so don't delete.
    public static String readFileAsBinary(String fileName) throws Throwable {
        checkFile(fileName);
        byte[] contents = Files.readAllBytes(Paths.get(fileName));
        return Base64.encodeBase64URLSafeString(contents);
    }

    /**
     * Compliment to {@link #readFileAsBinary(String)}. This will take a base64 encoded string, decode it to a
     * byte array and write the result to a file.
     * @param filename
     * @param contents
     * @throws Throwable
     */
    public static void writeFileAsBinary(String filename, String contents) throws Throwable {
        byte[] bytes = Base64.decodeBase64(contents);
        Files.write(Paths.get(filename), bytes);
    }

    /**
     * Read a (text) file in as a long string.
     * @param fileName
     * @return
     * @throws Throwable
     */
    public static String readFileAsString(String fileName) throws Throwable {
        checkFile(fileName);
        StringBuffer stringBuffer = new StringBuffer();
        Path path = Paths.get(fileName);

        List<String> contents = Files.readAllLines(path);
        int i = 0;
        //Read from the stream
        for (String content : contents) {
            stringBuffer.append(content + "\n");
        }

        return stringBuffer.toString();

    }

    /**
     * Read a (text) file in as a list of strings, one per line.
     * @param fileName
     * @return
     * @throws Throwable
     */
    public static List<String> readFileAsLines(String fileName) throws Throwable {
        checkFile(fileName);
        Path path = Paths.get(fileName);
        return Files.readAllLines(path);
    }

    /**
     * Compliment to {@link #readFileAsString(String)}, which writes the string to a file.
     * @param filename
     * @param contents
     * @throws Throwable
     */
    public static void writeStringToFile(String filename, String contents) throws Throwable {
        FileWriter fileWriter = new FileWriter(new File(filename));
        fileWriter.write(contents);
        fileWriter.flush();
        fileWriter.close();
    }
}
