package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/20/18 at  1:10 PM
 */
public class ParserUtil {
    public static String BATCH_FILE_COMMENT_CHAR = "#";
    public static String BATCH_FILE_LINE_TERMINATION = ";"; // last character on a line

    public static void main(String[] args) {
        try {
            File testFile = null;
            if(0 < args.length){
                    testFile = new File(args[0]);
            }
           // testFile = new File("/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/main/resources/test1.cmd");
            if(testFile == null || !testFile.exists()){
                System.out.println("Sorry, but the file you specified does not exist.");
                System.out.println("  This program takes a fully qualified path to a command file, which will be executed.");
                System.out.println("  It will echo the results of each executable command then exit. ");
                System.out.println("  Then the contents of the environment map. ");
                System.out.println("  Exiting...");
                return;
            }

            FileReader fr = new FileReader(testFile);
            List<String> commands = processInput(fr);

            JFunctorFactory functorFactory = new JFunctorFactory(true);

            EventDrivenParser parser = new EventDrivenParser(functorFactory);

            for (String x : commands) {
                AbstractHandler abstractHandler = parser.parse(x, functorFactory.getEnvironment());
                switch (abstractHandler.getHandlerType()) {
                    case AbstractHandler.FUNCTOR_TYPE:
                        System.out.println("Functor result=" + ((FunctorHandler) abstractHandler).getFResult());

                        break;
                    case AbstractHandler.CONDITIONAL_TYPE:
                        System.out.println("Conditional result=" + ((ConditionalHandler) abstractHandler).getLogicBlock().getConsequent().getListResult());
                        break;
                    case AbstractHandler.SWITCH_TYPE:
                        System.out.println("Switch result functor map=" + ((SwitchHandler) abstractHandler).getLogicBlocks().getFunctorMap());
                        break;
                    default:
                        System.out.println("YIKES!!! unknown parse type");

                }

            }
            DebugUtil.trace(ParserUtil.class, "replacement templates=" + functorFactory.getReplacementTemplates());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static List<String> processInput(Reader reader) throws Throwable {
                 return processInput(reader, false);
    }

    /**
     *  Takes a reader that contains a command file and returns a list of executable string. You can pass these to
     *  a parser. Note that you should parse each command then inspect the resulting {@link AbstractHandler}
     *  that results from parsing. If the isStrict flag is true, then this will check if there were no terminators
     *  and throw an exception if no commands were found.
     * @param reader
     * @param isStrict
     * @return
     * @throws Throwable
     */
    public static List<String> processInput(Reader reader, boolean isStrict) throws Throwable {
        BufferedReader br = new BufferedReader(reader);
        LinkedList<String> commands = new LinkedList<>();
         boolean foundCommandTerminator = false;
        int lineNumber = 1;
        String lineIn = br.readLine();  // actual lines in the file, comments and all
        boolean isExecuteLine = false;

        String executableLine = "";
        while (lineIn != null) {
            // strip comment
            String fullLine = null;
            if (lineIn.trim().startsWith(BATCH_FILE_COMMENT_CHAR)) {
                // If the first character on a line is this, the entire line is omitted.
                lineIn = br.readLine();
                lineNumber++;
                continue;
            } else {
                // no comment
                fullLine = lineIn.trim();
            }
            if (fullLine.endsWith(BATCH_FILE_LINE_TERMINATION)) {
                fullLine = fullLine.substring(0, fullLine.lastIndexOf(BATCH_FILE_LINE_TERMINATION));
                isExecuteLine = true;
                foundCommandTerminator = true;
            } else {
                isExecuteLine = false;
            }
            executableLine = executableLine + " " + fullLine;

            executableLine = executableLine.trim();
            if (isExecuteLine) {
                if (!executableLine.isEmpty()) {
                    // do stuff here with the parser
                    commands.add(executableLine.trim());
                }
                // reset state even if nothing executes at this point.
                executableLine = "";
                isExecuteLine = false;
            }

            lineIn = br.readLine();
            lineNumber++;
        }
        br.close();

        if(isStrict && !foundCommandTerminator){
            throw new IllegalStateException("Error: The command file had no terminator \"" + BATCH_FILE_LINE_TERMINATION + "\", hence no commands were found to execute");
        }

        return commands;
    }
}
