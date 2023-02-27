package edu.uiuc.ncsa.security.util.help;

import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.cli.HelpUtil;
import edu.uiuc.ncsa.security.util.cli.InputLine;

import java.io.*;
import java.util.*;

/**
 * A command line tool that will take a help file and turn it into
 * a Github style markdown document. This is very simple minded.
 * <b>Very!</b> The interactive help is definitive, not the markdown.
 * <p>Created by Jeff Gaynor<br>
 * on 2/9/23 at  4:56 PM
 */
public class MarkdownConverter {
    public static String INPUT_SWITCH = "-in";
    public static String RESOURCE_SWITCH = "-res";
    public static String OUTPUT_SWITCH = "-out";
    public static String VERBOSE_SWITCH = "-v";

    public MarkdownConverter(InputLine inputLine) {
        init(inputLine);
    }
    public MarkdownConverter() {
     }
    public static void main(String[] args) throws Throwable {
        MarkdownConverter markdownConverter = new MarkdownConverter();
        if (args.length == 0) {
            markdownConverter.showHelp();
            return;
        }
        Vector v = new Vector();
        v.add("dummy"); // dummy arg
        for (String x : args) {
            v.add(x);
        }
        if (RC_OK != markdownConverter.init(new InputLine(v))) {
            return;
        }
        markdownConverter.convert();
    }

    protected List<String> toList(String x) {
        if (x == null) {
            return null;
        }
        if (StringUtils.isTrivial(x.trim())) {
            return null;
        }
        List<String> entries = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(x, File.pathSeparator);
        while (tokenizer.hasMoreTokens()) {
            entries.add(tokenizer.nextToken());
        }
        return entries;
    }

    protected int init(InputLine inputLine) {
        boolean verboseOn = inputLine.hasArg(VERBOSE_SWITCH);
        inputLine.removeSwitch(VERBOSE_SWITCH);

        if (inputLine.hasArg("--help")) {
            showHelp();
            return RC_HELP;
        }
        boolean hasResources = inputLine.hasArg(RESOURCE_SWITCH);
        List<String> resources = new ArrayList();
        if (hasResources) {
            resources = toList(inputLine.getNextArgFor(RESOURCE_SWITCH));
            inputLine.removeSwitchAndValue(RESOURCE_SWITCH);
        }

        List<String> inputFiles = new ArrayList<>();
        boolean hasInputFiles = inputLine.hasArg(INPUT_SWITCH);
        if (hasInputFiles) {
            inputFiles = toList(inputLine.getNextArgFor(INPUT_SWITCH));
            inputLine.removeSwitchAndValue(INPUT_SWITCH);
        } else {
            if (inputLine.getArgCount() == 1) {
                inputFiles = toList(inputLine.getLastArg());
            } else {
                say("could not determine input file(s)");
                return RC_FAIL;
            }
        }


        boolean hasOutputFile = inputLine.hasArg(OUTPUT_SWITCH);
        String outFilename = null;
        if (hasOutputFile) {
            outFilename = inputLine.getNextArgFor(OUTPUT_SWITCH);
            //outFile = new File(inputLine.getNextArgFor(OUTPUT_SWITCH));
            inputLine.removeSwitchAndValue(OUTPUT_SWITCH);
        }

        if (RC_FAIL == init(resources, inputFiles, outFilename, verboseOn)) {
            return RC_FAIL;
        }
        return RC_OK;
    }

    public static int RC_OK = 0;
    public static int RC_FAIL = 1;
    public static int RC_HELP = 2;

    protected int init(List<String> resources, List<String> inFiles, String outFile, boolean isVerbose) {
        if (resources == null || resources.isEmpty()) {
            if (inFiles == null || inFiles.isEmpty()) {
                say("No resources or input files found");
                return RC_FAIL;
            }
        }

        if (outFile != null) {
            try {
                output = new PrintStream(new FileOutputStream(outFile));
            } catch (IOException e) {
                say("uh-oh could not create output file \"" + outFile + "\"");
                if (isVerbose) {
                    say("error: " + e.getMessage());
                }
                return RC_FAIL;
            }
        } else {
            output = System.out;
            setSysOut(true);
        }
        if (inFiles == null || inFiles.isEmpty()) {
            say("no input file(s) found.");
            return RC_FAIL;
        }
        helpUtil = new HelpUtil();
        for (String resource : resources) {
            try {
                helpUtil.load(resource);
            } catch (Throwable e) {
                say("sorry, there was a problem loading the resource \"" + resource + "\"");
                if (isVerbose) {
                    say("error: " + e.getMessage());
                }
                return RC_FAIL;
            }

        }
        for (String inFile : inFiles) {
            try {
                FileInputStream fis = new FileInputStream(inFile);
                helpUtil.load(fis);
            } catch (Throwable e) {
                say("sorry, there was a problem loading the help file \"" + inFile + "\"");
                if (isVerbose) {
                    say("error: " + e.getMessage());
                }
                return RC_FAIL;
            }
        }


        return RC_OK;
    }

    public boolean isSysOut() {
        return sysOut;
    }

    public void setSysOut(boolean sysOut) {
        this.sysOut = sysOut;
    }

    public PrintStream getOutput() {
        return output;
    }

    public void setOutput(PrintStream output) {
        this.output = output;
    }

    boolean sysOut = false;
    PrintStream output;

    /**
     * Prints a blank line
     */
    protected void say() {
        say("");
    }

    protected void say(String x) {
        System.out.println(x);
    }

    protected void showHelp() {
        say(MarkdownConverter.class.getSimpleName() + " " + OUTPUT_SWITCH + " output_file [" + INPUT_SWITCH + "] [" + VERBOSE_SWITCH + "] input_file");
        say("Convert the file (in help file format) to Github markdown.");
        say(OUTPUT_SWITCH + " - (optional) where to send results. If no file is specified, it will be dumped to the console.");
        say(INPUT_SWITCH + " - (optional) if given you can order your arguments as you like, but otherwise the last argument");
        say("   Note that you may specify multiple input files separated by " + File.pathSeparator + ", which will be processing in order.");
        say(VERBOSE_SWITCH + " - be more chatty while running.");
        say("      is assumed to be the input file");
        say();
        say("Entries are sorted and minimially formatted.");
        say("E.g.");
        say("java " + MarkdownConverter.class.getCanonicalName() + " -in \"/home/ncsa/dev/ncsa-git/security-lib/storage/src/main/resources/basic-help.xml:/home/ncsa/dev/ncsa-git/security-lib/storage/src/main/resources/store-help.xml\" -v ");
        say("");
        say("");
    }

    public HelpUtil getHelpUtil() {
        return helpUtil;
    }

    public void setHelpUtil(HelpUtil helpUtil) {
        this.helpUtil = helpUtil;
    }

    HelpUtil helpUtil;

    public static String MD_LINE_BREAK = " <br>\n";

    protected void convertEntry(
            String name,
            String altName,
            String content,
            String examples) {
        if (StringUtils.isTrivial(altName)) {
            getOutput().println("\n# " + name + "\n");
        } else {
            getOutput().println("\n# " + name + " (" + altName + ")\n");
        }
        content = content.replace("\n", MD_LINE_BREAK); // Keeps line breaks
        getOutput().println(content);
        if (examples != null && !StringUtils.isTrivial(examples.trim())) {
            getOutput().println("#### Examples" + "\n");
            examples = examples.replace("\n", MD_LINE_BREAK);
            getOutput().println(examples);
        }
    }

    protected void convert() {
        TreeSet<String> sortedTopics = new TreeSet<>();
        sortedTopics.addAll(getHelpUtil().getOnlineHelp().keySet());
        for (String key : sortedTopics) {
            convertEntry(key,
                    getHelpUtil().getAltLookup().get(key),
                    getHelpUtil().getHelpTopic(key),
                    getHelpUtil().getHelpTopicExample(key));
        }
        getOutput().flush();
    }
}

