package edu.uiuc.ncsa.security.util.cli;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Since it is close to impossible to get consistent behavior at the command line between different
 * JVM hosts, the only reasonable way to edit things at the command line is going to be with some form
 * of line editor. Rather than be clever and create one, this is a <b>basic</b> port of the tried and true
 * Unix line editor, ed.
 * <h3>Usage</h3>
 * You use this by creating an instance with either a buffer of lines or a string which is parsed simply into lines (this
 * is not perfect since there can be some cross-platform issues -- better to roll it yourself). You then invoke
 * {@link #execute()} and this runs the editor. When done, this method exist and you check the {@link #isSaved()}
 * flag. If true, then the user saved the buffer and wants to keep it, so you can either get the buffer and process the
 * lines or invoke the {@link #bufferToString()} method.
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/18 at  11:23 AM
 */

public class LineEditor {
    public boolean isSaved() {
        return saved;
    }

    boolean saved = false;

    public List<String> getBuffer() {
        if (buffer == null) {
            buffer = new LinkedList<>();
        }
        return buffer;
    }

    List<String> buffer;

    /**
     * Reconstruct the input as a string with new line characters.
     *
     * @return
     */
    public String bufferToString() {
        String output = "";
        for (int i = 0; i < getBuffer().size(); i++) {
            output = output + (i == 0 ? "" : "\n") + getBuffer().get(i);
        }
        return output;
    }

    public LineEditor(StringBuffer buffer) {
        this(buffer.toString());
    }

    public LineEditor(List<String> buffer) {
        this.buffer = buffer;
    }

    /**
     * This will split the text at new lines and put it in the buffer.
     *
     * @param rawText
     */
    public LineEditor(String rawText) {
        StringTokenizer stringTokenizer = new StringTokenizer(rawText, "\n");
        buffer = new LinkedList<>();
        while (stringTokenizer.hasMoreElements()) {
            buffer.add(stringTokenizer.nextToken());
        }
    }

    CommandLineTokenizer CLT = new CommandLineTokenizer();

    public static String PROMPT = "edit>";
    public static final String END_COMMAND = "."; // end input to buffer
    public static final String APPEND_COMMAND = "a"; // append to end of file
    public static final String APPEND_COMMAND_LONG = "append"; // append to end of file
    public static final String VIEW_CLIPBOARD_COMMAND = "b"; // copy a range of lines to the clipboard
    public static final String VIEW_CLIPBOARD_COMMAND_LONG = "view"; // copy a range of lines to the clipboard
    public static final String COPY_COMMAND = "c"; // copy a range of lines to the clipboard
    public static final String COPY_COMMAND_LONG = "copy"; // copy a range of lines to the clipboard
    public static final String DELETE_COMMAND = "d"; // delete a range of lines
    public static final String DELETE_COMMAND_LONG = "delete"; // delete a range of lines
    public static final String EDIT_A_LINE_COMMAND = "e"; // edit a specific line (i.e. replace it)
    public static final String EDIT_A_LINE_COMMAND_LONG = "edit"; // edit a specific line (i.e. replace it)
    public static final String INSERT_COMMAND = "i"; // insert
    public static final String INSERT_COMMAND_LONG = "insert"; // insert
    public static final String FIND_COMMAND = "f"; // insert
    public static final String FIND_COMMAND_LONG = "find"; // insert
    public static final String CLEAR_COMMAND = "l"; // clear the buffer
    public static final String CLEAR_COMMAND_LONG = "clear"; // clear the buffer
    public static final String MOVE_COMMAND = "m"; // [x,y,z] move lines x - y to before z, then delete from the original location
    public static final String MOVE_COMMAND_LONG = "move"; // [x,y,z] move lines x - y to before z, then delete from the original location
    public static final String PRINT_COMMAND = "p"; // print a range of lines
    public static final String PRINT_COMMAND_LONG = "print"; // print a range of lines
    public static final String QUIT_COMMAND = "q"; // quit the editor
    public static final String QUIT_COMMAND_LONG = "quit"; // quit the editor
    public static final String READ_COMMAND = "r";
    public static final String READ_COMMAND_LONG = "read";
    public static final String SUBSITUTE_COMMAND = "s"; // substitute
    public static final String SUBSITUTE_COMMAND_LONG = "replace"; // substitute
    public static final String PASTE_COMMAND = "t"; // paste the contents of the clipboard before the line numberr.
    public static final String PASTE_COMMAND_LONG = "paste"; // paste the contents of the clipboard before the line numberr.
    public static final String CUT_COMMAND = "u"; // cut a range of lines to the clipboard
    public static final String CUT_COMMAND_LONG = "cut"; // cut a range of lines to the clipboard
    public static final String VERBOSE_COMMAND = "v"; // toggles verbose mode on or off.
    public static final String VERBOSE_COMMAND_LONG = "verbose"; // toggles verbose mode on or off.
    public static final String WRITE_COMMAND = "w"; // write the file
    public static final String WRITE_COMMAND_LONG = "write"; // write the file
    public static final String SIZE_COMMAND = "z"; // how many lines are in this buffer?
    public static final String SIZE_COMMAND_LONG = "size"; // how many lines are in this buffer?
    public static final String HELP_COMMAND = "?"; // Help command

    protected void allHelp() {
        say("This is the line editor. It operates per line. Each command is of the form");
        say("command [start,stop,target] arg0 arg1 arg2...");
        say("where ");
        say("command is a command for the editor (list below). There are long and short forms");
        say("[start,stop,target] are line numbers hence integers.");
        say("    start = the starting index, always 0 or greater");
        say("    stop = the ending index. All operations are inclusive of this line number");
        say("    target = where to apply the [start,stop] interval. E.g. the insertion point for copying text.");
        say("arg0, arg1,... = list of strings, possibly in quotes if needed, that are arguments to the command.");
        say("\nTo see help on a specific command, type ? after the command, e.g. to get help on the insert command type");
        say("i ?");
        say("List of commands");
        say(APPEND_COMMAND + " (" + APPEND_COMMAND_LONG + ") append text either before a given line or to the end of the buffer.");
        say(VIEW_CLIPBOARD_COMMAND + " (" + VIEW_CLIPBOARD_COMMAND_LONG + ") view the contents of the clipboard. Contents not editable.");
        say(COPY_COMMAND + " (" + COPY_COMMAND_LONG + ") copies a range of lines to the clipboard.");
        say(DELETE_COMMAND + " (" + DELETE_COMMAND_LONG + ") deletes a range of lines");
        say(EDIT_A_LINE_COMMAND + " (" + EDIT_A_LINE_COMMAND_LONG + ") edit a range of lines. Note this will effectively replace those lines.");
        say(FIND_COMMAND + " (" + FIND_COMMAND_LONG + ") search lines that match a given regular expression, printing each line found.");
        say(INSERT_COMMAND + " (" + INSERT_COMMAND_LONG + ") insert lines starting at a give index or append lines to the end of the buffer.");
        say(CLEAR_COMMAND + " (" + CLEAR_COMMAND_LONG + ") clear the buffer (but not the clipboard)");
        say(MOVE_COMMAND + " (" + MOVE_COMMAND_LONG + ") move a block of text using the clipboard");
        say(PRINT_COMMAND + " (" + PRINT_COMMAND_LONG + ") print the buffer or a subset of it");
        say(QUIT_COMMAND + " (" + QUIT_COMMAND_LONG + ") quit the editor. If you supply a \"!\" as the argument, the buffer is cleared.");
        say(READ_COMMAND + " (" + READ_COMMAND_LONG + ") read a file into the buffer");
        say(SUBSITUTE_COMMAND + " (" + SUBSITUTE_COMMAND_LONG + ") substitute over a range of lines using a regex");
        say(PASTE_COMMAND + " (" + PASTE_COMMAND_LONG + ") paste the contents of the clipboard into the buffer");
        say(CUT_COMMAND + " (" + CUT_COMMAND_LONG + ") cut a range of lines from the buffer and leave in the clipboard");
        say(VERBOSE_COMMAND + " (" + VERBOSE_COMMAND_LONG + ") turn on verbosity, i.e. print more about the functioning of the editor");
        say(WRITE_COMMAND + " (" + WRITE_COMMAND_LONG + ") write the buffer to a file");
        say(SIZE_COMMAND + " (" + SIZE_COMMAND_LONG + ") query the buffer for its current size");
        say(HELP_COMMAND + " print this help, or in context, print the help for a command.");
    }


    boolean verboseOn = false;

    public void execute() throws Throwable {
        say("CLI editor.");
        if (getBuffer().isEmpty()) {
            say("Empty buffer. Type ? to see help.");
        }
        while (!isDone) {
            say2(PROMPT);
            String command = readline().trim();
            EditorInputLine eil = null;
            boolean isUnkownCommand = true;
            try {
                eil = new EditorInputLine(command);
            } catch (Throwable t) {
                say("Sorry, there was an error processing that command");
                continue;
            }
            if (eil.isCommand(QUIT_COMMAND, QUIT_COMMAND_LONG)) {
                isDone = true;
                isUnkownCommand = false;
                if(eil.hasArg("!")){
                    buffer = new LinkedList<>();
                    sayv("Buffer cleared. bye-bye...");
                }else{
                    sayv("bye-bye...");
                }
                break;
            }
            if (eil.isCommand(APPEND_COMMAND, APPEND_COMMAND_LONG)) {
                isUnkownCommand = false;
                doInsert(eil);
                saved = false;
            }
            if (eil.isCommand(INSERT_COMMAND, INSERT_COMMAND_LONG)) {
                isUnkownCommand = false;
                doInsert(eil);
                saved = false;
            }
            if (eil.isCommand(PRINT_COMMAND, PRINT_COMMAND_LONG)) {
                isUnkownCommand = false;
                doPrint(eil);
            }
            if (eil.isCommand(SIZE_COMMAND, SIZE_COMMAND_LONG)) {
                isUnkownCommand = false;
                say("buffer contains " + getBuffer().size() + " line" + (getBuffer().size() == 1 ? "." : "s."));
            }
            if (eil.isCommand(CLEAR_COMMAND, CLEAR_COMMAND_LONG)) {
                isUnkownCommand = false;
                say("are you sure you want to clear the buffer (y|n)?");
                if (readline().equals("y")) {
                    buffer = new LinkedList<>();
                    say("buffer cleared.");
                }
                saved = true; // nothing to save.

            }
            if (eil.isCommand(COPY_COMMAND, COPY_COMMAND_LONG)) {
                isUnkownCommand = false;
                doCopy(eil);
            }
            if (eil.isCommand(CUT_COMMAND, CUT_COMMAND_LONG)) {
                isUnkownCommand = false;
                doCut(eil);
                saved = false;

            }

            if (eil.isCommand(MOVE_COMMAND, MOVE_COMMAND_LONG)) {
                isUnkownCommand = false;
                doMove(eil);
                saved = false;

            }
            if (eil.isCommand(EDIT_A_LINE_COMMAND, EDIT_A_LINE_COMMAND_LONG)) {
                isUnkownCommand = false;
                doEditLines(eil);
                saved = false;

            }
            if (eil.isCommand(PASTE_COMMAND, PASTE_COMMAND_LONG)) {
                isUnkownCommand = false;
                doPaste(eil);
                saved = false;

            }
            if (eil.isCommand(READ_COMMAND, READ_COMMAND_LONG)) {
                isUnkownCommand = false;
                doRead(eil);
            }
            if (eil.isCommand(WRITE_COMMAND, WRITE_COMMAND_LONG)) {
                isUnkownCommand = false;
                doWrite(eil);
                saved = true;
            }
            if (eil.isCommand(HELP_COMMAND)) {
                isUnkownCommand = false;
                if (!eil.hasArgs()) {
                    allHelp();
                }
            }
            if (eil.isCommand(FIND_COMMAND, FIND_COMMAND_LONG)) {
                isUnkownCommand = false;
                doFind(eil);
            }
            if (eil.isCommand(SUBSITUTE_COMMAND, SUBSITUTE_COMMAND_LONG)) {
                isUnkownCommand = false;
                doSubstitute(eil);
                saved = false;
            }
            if (eil.isCommand(DELETE_COMMAND, DELETE_COMMAND_LONG)) {
                isUnkownCommand = false;
                doDelete(eil);
                saved = false;
            }
            if (eil.isCommand(VIEW_CLIPBOARD_COMMAND, VIEW_CLIPBOARD_COMMAND_LONG)) {
                isUnkownCommand = false;
                doViewClipboard(eil);
            }
            if (eil.isCommand(VERBOSE_COMMAND, VERBOSE_COMMAND_LONG)) {
                isUnkownCommand = false;
                if (showHelp(eil)) {
                    verboseHelp();
                } else {
                    verboseOn = !verboseOn;
                    say("verbose mode is now " + (verboseOn ? "ON" : "OFF"));
                }
            }
            if (isUnkownCommand) {
                say("Sorry, unknown command. Type ? for help");
            }
        }
        say("done!");

    }

    protected void doFindHelp() {
        say(FIND_COMMAND + "|" + FIND_COMMAND_LONG + " [x,y] regex = search through lines in a range");
        say("        listing all the lines that match the regex. Omitting the range implies search the whole buffer");
    }

    protected void doFind(EditorInputLine eil) {
        if (showHelp(eil)) {
            doFindHelp();
            return;
        }
        int[] range = getRange(eil);
        int count = 0;
        for (int i = range[0]; i <= range[1]; i++) {
            if (getBuffer().get(i).matches(eil.getLastArg())) {
                say(i + ": " + getBuffer().get(i));
                count++;
            }
        }
        sayv(count + " lines matched");
    }

    protected void doSubstituteHelp() {
        say(SUBSITUTE_COMMAND + "|" + SUBSITUTE_COMMAND_LONG + " [x,y] regex value");
        say("    This will use the regex to match each line (this does not match over line breaks) and ");
        say("    substitute the given value");
        say("    Omitting the range implies carrying this out over the entire buffer.");
        say("E.g. to remove all the white space in a line");
        say(SUBSITUTE_COMMAND + " \\\\s \"\"");
    }

    protected boolean showHelp(EditorInputLine eil) {
        return eil.hasArg(HELP_COMMAND);
    }

    protected int[] getRange(EditorInputLine eil) {
        int[] range = new int[2];
        range[0] = 0;
        range[1] = getBuffer().size() - 1;
        if (eil.hasIndices()) {
            if (eil.getIndexSize() == 1) {
                // then this a single line
                range[0] = eil.getIndices()[0];
                range[1] = eil.getIndices()[0];
            } else {
                range[0] = eil.getIndices()[0];
                range[1] = Math.min(range[1], eil.getIndices()[1]);
            }
        }
        return range;
    }

    protected void doSubstitute(EditorInputLine eil) {
        if (showHelp(eil)) {
            doSubstituteHelp();
            return;
        }
        int range[] = getRange(eil);
        for (int i = range[0]; i <= range[1]; i++) {
            String newValue = getBuffer().get(i).replaceAll(eil.getArg(1), eil.getArg(2));
            getBuffer().set(i, newValue);
        }
        sayv("done. " + (range[1] - range[0] + 1) + " lines processed.");

    }

    protected void doCutHelp() {
        say("cut [x,y] = copy the given range to the clipboard, then remove them from the buffer.");
    }

    protected void doCut(EditorInputLine eil) {
        if (showHelp(eil)) {
            doCutHelp();
            return;
        }
        doCopy(eil);
        doDelete(eil);
    }

    protected void doDeleteHelp() {
        say(DELETE_COMMAND + "|" + DELETE_COMMAND_LONG + " [x,y] = deletes the lines from x to y inclusive. Note that omitting this argument causes no action.");
    }


    protected void doDelete(EditorInputLine eil) {
        if (showHelp(eil)) {
            doDeleteHelp();
            return;
        }

        if (!eil.hasIndices()) {
            say("sorry, no range of lines specified, so no delete has been done.");
            return;
        }
        int[] range = getRange(eil);
        for (int i = 0; i <= range[1] - range[0]; i++) {
            getBuffer().remove(range[0]); // whittle it down. Indices change after each deletion, so keep pointer here
        }
        sayv("done! Removed " + (range[1] - range[0] + 1) + " lines");
    }

    protected void doMoveHelp() {
        say(MOVE_COMMAND + "|" + MOVE_COMMAND_LONG + " [x,y,z] = move the range from x,y and insert it before z in the current buffer.");
        say("    Omitting z means append to the end of the buffer.");
    }

    protected void doMove(EditorInputLine eil) {
        if (showHelp(eil)) {
            doMoveHelp();
            return;
        }
        if (!eil.hasIndices()) {
            say("sorry, no range specified. Cannot perform the operation");
            return;
        }
        // case 0: no target, just append
        if (eil.getIndexSize() == 2) {
            doCut(eil);
            getBuffer().addAll(clipboard);
            sayv("moved " + clipboard.size() + " lines to the end of the buffer.");
            return;
        }

        // case 1: target before cut. No need to recalculate insertion index
        if (eil.getIndices()[2] < eil.getIndices()[0]) {
            doCopy(eil);
            getBuffer().addAll(eil.getIndices()[2], clipboard);
            sayv("moved " + clipboard.size() + " lines.");
            return;
        }
        // case 2: target inside cut range. Do nothing.
        if (eil.getIndices()[0] <= eil.getIndices()[2] && eil.getIndices()[2] <= eil.getIndices()[1]) {
            sayv("The insertion point for is inside the lines to be moved. No action needs to be done.");
            return;
        }
        // case 3: target after cut range, have to recalcuate insertion point.
        doCut(eil);

        // so the insertion point would be after the initial point of the cut and the file reshuffles.
        int targetIndex = eil.getIndices()[2] - (eil.getIndices()[1] - eil.getIndices()[0] + 1);
        getBuffer().addAll(targetIndex, clipboard);
        sayv("moved " + clipboard.size() + " lines to before line number " + eil.getIndices()[2]);


        // trick is finding where z, the target index,  is in the new buffer
        // if z lies in the cut range, then just append the result to the end of the buffer post delete.
    }

    protected void doViewClipboardHelp() {
        say(VIEW_CLIPBOARD_COMMAND + "|" + VIEW_CLIPBOARD_COMMAND_LONG + " = view the contents of the clipboard");
    }

    protected void doViewClipboard(EditorInputLine eil) {
        if (showHelp(eil)) {
            doViewClipboardHelp();
            return;
        }

        if (clipboard.isEmpty()) {
            say("(empty)");
            return;
        }
        for (String x : clipboard) {
            say(x);
        }
        sayv(clipboard.size() + " lines.");
    }

    protected void doWriteHelp() {
        say(WRITE_COMMAND + "|" + WRITE_COMMAND_LONG + " [x,y] file_path = write the lines x through y to the given file, overwriting any contents.");
        say("      Note that if the range is omitted, the entire buffer is saved.");
    }

    protected void doWrite(EditorInputLine eil) {
        if (showHelp(eil)) {
            doWriteHelp();
            return;
        }

        List<String> list = getBuffer();
        try {
            File f = new File(eil.getLastArg());
            if (f.isDirectory()) {
                say("Sorry, that is a directory.");
                return;
            }
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String x : list) {
                bw.write(x);
                bw.newLine();
            }
            bw.flush();
            bw.close();
            say("done writing file \"" + f.getAbsolutePath() + "\"");
            sayv("wrote " + getBuffer().size() + " lines.");
        } catch (IOException x) {
            say("There was an error. File not written. \"" + x.getMessage() + "\"");
        }
    }


    protected void doAppendtHelp() {
        say(APPEND_COMMAND + "|" + APPEND_COMMAND_LONG + " append to the end of the buffer.");
        say("        Input continues until a single \"" + END_COMMAND + "\" is entered");
        say("        No arguments are accepted, it is just a convenience.");
    }

    protected void doAppend(EditorInputLine eil) throws IOException {
        if (showHelp(eil)) {
            doAppendtHelp();
            return;
        }
        eil = new EditorInputLine(INSERT_COMMAND); // This just causes it to insert at the end.
        doInsert(eil);
    }

    protected void doInsertHelp() {
        say(INSERT_COMMAND + "|" + INSERT_COMMAND_LONG + " [x] = insert starting (optionally) at line x");
        say("        Input continues until a single \"" + END_COMMAND + "\" is entered");
        say("        Note that this pushes every line in the buffer after it down.");
        say("        No argument means append to the end of the buffer.");
    }

    /**
     * Insert lines until a single period is entered.
     *
     * @param eil
     */
    protected void doInsert(EditorInputLine eil) throws IOException {
        if (showHelp(eil)) {
            doInsertHelp();
            return;
        }

        String lineIn = readline();
        LinkedList<String> tempBuffer = new LinkedList<>();
        while (!lineIn.trim().equals(END_COMMAND)) {
            tempBuffer.add(lineIn);
            lineIn = readline();
        }
        if (eil.hasIndices()) {
            getBuffer().addAll(eil.getIndices()[0], tempBuffer);
        } else {
            getBuffer().addAll(tempBuffer);
        }
        sayv("inserted " + tempBuffer.size() + " lines.");
    }

    protected void doEditLinesHelp() {
        say(EDIT_A_LINE_COMMAND + "|" + EDIT_A_LINE_COMMAND_LONG + " [x,y] edit a range of lines");
        say("       This allows you to *almost* edit a range of lines. The issue is that nature of Java from the command line");
        say("       This will print the current line (with line number), then a return and let you type what you want. It is not really possible");
        say("       to print an editable line of text in Java generally, though your shell may allow for that. For instance");
        say("       many terminals do not even allow a backspace or delete, but will insert control characters at the end of the line.");
        say("       Sorry, this is the best general solution so far...");
    }

    protected void doEditLines(EditorInputLine eil) {
        if (showHelp(eil)) {
            doEditLinesHelp();
            return;
        }
        int[] range = getRange(eil);
        for (int i = range[0]; i <= range[1]; i++) {
            say(i + ": " + getBuffer().get(i));
            say2(PROMPT);
            try {
                String lineIn = readline();
                getBuffer().set(i, lineIn);
            } catch (IOException e) {
                say("Sorry, an error occurred. aborting...");
                sayv("Message was \"" + e.getMessage() + "\"");
            }
        }
        say("done");
    }

    protected void verboseHelp() {
        say(VERBOSE_COMMAND + "|" + VERBOSE_COMMAND_LONG + " = toggles verbose mode on or off. This also will cause extra prompting for actions.");
        say("        The default is OFF");
    }

    protected void doPrintHelp() {
        say(PRINT_COMMAND + "|" + PRINT_COMMAND_LONG + " [x,y] [" + PRINT_NO_LINE_NUMBERS_FLAG + "]= print lines x through y inclusive, with or without line numbers. " +
                "If you give a single number, a single line is printed");
        say("          If you omit any numbers, the entire buffer is printed. Omitting the flag means to print line numbers");
        say("          Giving the flag means just the contents of the buffer are printed.");
    }

    String PRINT_NO_LINE_NUMBERS_FLAG = "-noNumber";
    protected void doPrint(EditorInputLine eil) {
        if (showHelp(eil)) {
            doPrintHelp();
            return;
        }
        boolean showLineNumbers = !eil.hasArg(PRINT_NO_LINE_NUMBERS_FLAG);
        int[] range = getRange(eil);
        for (int i = range[0]; i <= range[1]; i++) {
            String head = showLineNumbers?(i + ": "):("");
            say(head  + getBuffer().get(i));
        }
    }

    protected void doReadHelp() {
        say(READ_COMMAND + "|" + READ_COMMAND_LONG + " [n] \"file_path\" = read the fully qualified file into the buffer, inserting it at the given line");
        say("                  Note that the index is missing, the file is simply appended");
        say("                  If the index is past the end of the buffer, the file is simply appended");
    }

    /**
     * Read a file into this buffer at a possible starting index.
     *
     * @param eil
     */
    protected void doRead(EditorInputLine eil) {
        if (showHelp(eil)) {
            doReadHelp();
            return;
        }

        int targetIndex = getBuffer().size();
        if (eil.hasIndices()) {
            targetIndex = Math.min(targetIndex, eil.getIndices()[0]); // don't overshoot end of buffer.
        }
        File f = new File(eil.getLastArg());
        if (!f.exists()) {
            say("sorry, the file \"" + f.getAbsolutePath() + "\" does not exist");
            return;
        }
        if (!f.isFile()) {
            say("sorry but \"" + f.getAbsolutePath() + "\" is not a file");
            return;
        }
        if (!f.canRead()) {
            say("sorry, but you cannot read \"" + f.getAbsolutePath() + "\".");
            return;
        }
        LinkedList<String> tempBuffer = new LinkedList<>();
        int size = 0;
        try {
            FileReader fis = new FileReader(f);
            BufferedReader br = new BufferedReader(fis);
            String linein = br.readLine();
            while (linein != null) {
                size = size + linein.length();
                tempBuffer.add(linein);
                linein = br.readLine();
            }
            br.close();
        } catch (Throwable t) {
            say("sorry, but an exception was encountered:\"" + t.getMessage() + "\". No changes.");
        }
        // now we add the lines we got since it worked.
        for (String x : tempBuffer) {
            getBuffer().add(targetIndex, x);
        }
        sayv(tempBuffer.size() + " lines inserted, " + size + " characters.");
    }

    LinkedList<String> clipboard = new LinkedList<>();

    protected void doCopyHelp() {
        say(COPY_COMMAND + "|" + COPY_COMMAND_LONG + " [x,y] = copies the lines x through y to the clipboard. ");
        say("         A single number copies a single line. No arguments cause the entire buffer to be copied.");

    }

    protected void doCopy(EditorInputLine eil) {
        if (showHelp(eil)) {
            doCopyHelp();
            return;
        }

        int[] range = getRange(eil);
        clipboard = new LinkedList<>();
        for (int i = range[0]; i <= range[1]; i++) {
            clipboard.add(getBuffer().get(i));
        }
        sayv((range[1] - range[0]) + " lines copied to the clipboard.");
    }

    protected void doPasteHelp() {
        say(PASTE_COMMAND + "|" + PASTE_COMMAND_LONG + " [n] = paste the contents of the clipboard at index n. If the index is omitted, ");
        say("     append to the end of the buffer.");
    }

    protected void doPaste(EditorInputLine eil) {
        if (showHelp(eil)) {
            doPasteHelp();
            return;
        }

        int index = getBuffer().size() - 1;
        if (eil.hasIndices()) {
            index = Math.min(index, eil.getIndices()[0]);
            getBuffer().addAll(index, clipboard);
            sayv(clipboard.size() + " lines inserted at line number " + index);

        } else {
            getBuffer().addAll(clipboard);
            sayv(clipboard.size() + " lines appended");

        }
    }

    /**
     * The form of the line is command [x,y,z] arg0 arg1 arg2...
     * So for instance<br/>
     * <pre>
     *     >m [2,5,15]
     * </pre>
     * would move lines 2,3,4 and 5 to before line 15, this implies that lines 2,3,4 and 5 are removed from their original location.
     * Another example might be
     * <pre>
     *     >s [1,6] "fnord" "baz"
     * </pre>
     * Replaces every instance of "baz" by "fnord" in the range of lines 1 through 6.
     *
     * @param lineIn
     */
    protected void parseLine(String lineIn) {

    }

    public BufferedReader getBufferedReader() {
        if (bufferedReader == null) {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return bufferedReader;
    }

    BufferedReader bufferedReader;

    protected String readline() throws IOException {
        return getBufferedReader().readLine();
    }

    protected EditorInputLine toInputLine(String x) {
        return new EditorInputLine(CLT.tokenize(x));
    }

    /**
     * For use with informational messages.
     *
     * @param x
     */
    protected void say(String x) {
        System.out.println(x);
    }

    /**
     * Used for spitting out extra messages in verbose mode.
     *
     * @param x
     */
    protected void sayv(String x) {
        if (verboseOn) {
            say(x);
        }
    }

    protected void say2(String x) {
        System.out.print(x);
    }

    boolean isDone = false;

    public static void main(String[] args) {
        LineEditor lineEditor = new LineEditor(new LinkedList<String>());
        lineEditor.say("attempting to read arguments as files to insert...");
        for (String arg : args) {
            EditorInputLine eil = new EditorInputLine(READ_COMMAND + " \"" + arg + "\"");
            try {
                lineEditor.doRead(eil);
            } catch (Throwable t) {
                lineEditor.say("skipped file named \"" + arg + "\"");
            }
        }
        // ok, now try to run it with whatever ended up in the buffer.
        try {
            lineEditor.execute();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
