package edu.uiuc.ncsa.qdl.util;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileNotFoundException;
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
public class FileUtil {
    /**
     * This just checks various things about the file so the user gets a better experience about what might be wrong.
     *
     * @param fileName
     * @return
     * @throws FileNotFoundException
     */
    protected static File checkFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new IllegalArgumentException("Error: The file \"" + fileName + "\" does not exist.");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Error: \"" + fileName + "\" is not a file.");
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException("Error: the file \"" + fileName + "\" is not readable.");
        }
        return file;
    }

    public static String readFileAsBinary(String fileName) throws Throwable {
        checkFile(fileName);
        byte[] contents = Files.readAllBytes(Paths.get(fileName));
        return Base64.encodeBase64URLSafeString(contents);
    }

    public static void writeFileAsBinary(String filename, String contents) throws Throwable {
        byte[] bytes = Base64.decodeBase64(contents);
        Files.write(Paths.get(filename), bytes);
    }

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

    public static String readFileAsString(String fileName) throws Throwable {
        checkFile(fileName);
        StringBuffer stringBuffer = new StringBuffer();
        Path path = Paths.get(fileName);

        List<String> contents = Files.readAllLines(path);
        int i = 0;
        //Read from the stream
        for (String content : contents) {
            stringBuffer.append(content+"\n");
        }

        return stringBuffer.toString();

    }

    /**
     * Note that this is a stem list or the ouotput is random.
     *
     * @param filename
     */
    public static void writeStemToFile(String filename, StemVariable contents) throws Throwable {
        FileWriter fileWriter = new FileWriter(new File(filename));
        StringBuilder stringBuilder = new StringBuilder();
        if (!contents.containsKey("0")) {
            throw new IllegalArgumentException("Error: The given stem is not a list. It must be a list to use this function.");
        }
        for (int i = 0; i < contents.size(); i++) {
            stringBuilder.append(contents.get(Integer.toString(i)) + "\n");
        }
        fileWriter.write(stringBuilder.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    public static void writeStringToFile(String filename, String contents) throws Throwable {
        FileWriter fileWriter = new FileWriter(new File(filename));
        fileWriter.write(contents);
        fileWriter.flush();
        fileWriter.close();
    }
}
