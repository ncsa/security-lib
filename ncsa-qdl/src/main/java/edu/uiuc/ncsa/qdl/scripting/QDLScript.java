package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.VThing;
import edu.uiuc.ncsa.qdl.vfs.FileEntry;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.util.scripting.ScriptInterface;
import edu.uiuc.ncsa.security.util.scripting.StateInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/4/20 at  5:09 PM
 */
public class QDLScript extends FileEntry implements ScriptInterface {
    /*
    Collision of names!!! The things that are run one the server are called "scripts" because that is what they
    are called in the delegation framework. Inside QDL a script is something run with a script_run/script_load command
    and has arguments. The scriptArgList here are the args for that and the isRunScript refers to whether
    this is to be run in QDL as a QDL script. Too darn many few words for the concepts...
     */
    public QDLScript(List<String> lines, XProperties xp) {
        super(lines, xp);
    }

    /**
     * Flag if this was loaded from a code block. Part of CIL-1302
     * @return
     */
    public boolean isFromCode() {
        return fromCode;
    }

    public void setFromCode(boolean fromCode) {
        this.fromCode = fromCode;
    }

    boolean fromCode = false;

    StemVariable scriptArglist = null;
    boolean isRunScript = false;

    public StemVariable getScriptArglist() {
        return scriptArglist;
    }

    public void setScriptArglist(StemVariable scriptArglist) {
        this.scriptArglist = scriptArglist;
    }

    public String getScriptArgName() {
        return scriptArgName;
    }

    public void setScriptArgName(String scriptArgName) {
        this.scriptArgName = scriptArgName;
    }

    public static String DEFAULT_ARG_NAME = "__args" + StemVariable.STEM_INDEX_MARKER;
    String scriptArgName = DEFAULT_ARG_NAME; // default

    public boolean isRunScript() {
        return isRunScript;
    }

    public void setRunScript(boolean runScript) {
        isRunScript = runScript;
    }

    public QDLScript(Reader script, XProperties properties) {
        super(properties);
        renderContent(script); // render asap since reader may be closed later by another process.
    }


    @Override
    public String getType() {
        return Scripts.SCRIPT;
    }

    /**
     * Returns the lines of text representation. Mostly the corresponding constructor is a convenience.
     *
     * @return
     */
    protected void renderContent(Reader rawScript) {
        if (!hasContent()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(rawScript);
                String line = bufferedReader.readLine();

                while (line != null) {
                    getLines().add(line);
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
            } catch (IOException ox) {
                throw new QDLException("Error: Could not read the script:" + ox.getMessage());
            }
        }
    }


    public void execute(StateInterface state) {
        QDLInterpreter parser = new QDLInterpreter((State) state);
        if (isRunScript()) {
            if (getScriptArglist() != null && !getScriptArglist().isEmpty()) {
                ((State) state).getVStack().put(new VThing(new XKey(getScriptArgName()), getScriptArglist()));
            }
        }
        try {
            parser.execute(getText(SHEBANG_REGEX));
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Error: Could not execute script: " + t.getMessage(), t);
        }
    }


    @Override
    public String toString() {
        String x = "QDLScript{" +
                Scripts.CODE + "=\n" + getText() +
                "properties=" + getProperties();
        return x + "\n}";
    }

}
