package edu.uiuc.ncsa.security.core.util;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility do to certain common file operations. These are mostly aimed at smaller files.
 * It uses NIO when possible.
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
     * Reads an {@link InputStream} as a single string. This is useful when reading
     * a resource as an input stream.
     * @param inputStream
     * @return
     * @throws Throwable
     */
    public static String readFileAsString(InputStream inputStream) throws Throwable {
        if(inputStream == null){
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader br = new BufferedReader(inputStreamReader);
        String in = br.readLine();
        StringBuilder stringBuilder = new StringBuilder();
        while(in != null){
             stringBuilder.append(in + "\n");
             in = br.readLine();
        }
        return stringBuilder.toString();
    }

    /**
     * Reads an {@link InputStream} as a set of lines. This is useful when reading
     * a resource as an input stream.
     * @param inputStream
     * @return
     * @throws Throwable
     */
    public static List<String> readFileAsLines(InputStream inputStream) throws Throwable {
        if(inputStream == null){
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        BufferedReader br = new BufferedReader(inputStreamReader);
        String in = br.readLine();
        List<String> out = new ArrayList<>();
        while(in != null){
            out.add(in);
             in = br.readLine();
        }
        return out;
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
