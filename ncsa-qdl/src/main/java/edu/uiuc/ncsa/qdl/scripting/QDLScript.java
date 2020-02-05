package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.util.scripting.ScriptInterface;
import edu.uiuc.ncsa.security.util.scripting.StateInterface;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/4/20 at  5:09 PM
 */
public class QDLScript implements ScriptInterface {

    public QDLScript(Reader script, XProperties properties) {
        this.rawScript = script;
        this.properties = properties;
    }

    State state;

    public Reader getRawScript() {
        return rawScript;
    }

    public void setRawScript(Reader rawScript) {
        this.rawScript = rawScript;
    }

    String text = null; // the text representation

    /**
     * Returns a text representation. <b>NOTE:</b> reading a {@link Reader} generally only works once.
     * This will  be converted to a {@link StringReader}
     * when this method is called unless it is one. No way around it with a general reader since they don't support
     * mark() and reset() generally. Sorry. Best we can do...
     * @return
     */
    public String getText() {
        if(text == null) {
            try {
                char[] arr = new char[8 * 1024];
                StringBuilder buffer = new StringBuilder();
                int numCharsRead;
                while ((numCharsRead = rawScript.read(arr, 0, arr.length)) != -1) {
                    buffer.append(arr, 0, numCharsRead);
                }
                rawScript.close();
                text = buffer.toString();

                if (!(rawScript instanceof StringReader)) {
                    // ok, once you read a reader it cannot be re-read.
                    // Convert it to a StringReader or attempts to run this
                    // multiple times will fail
                    rawScript = new StringReader(text);
                }
            } catch (IOException ox) {
                throw new QDLException("Error: Could not read the script:" + ox.getMessage());
            }
        }
        return text;
    }

    Reader rawScript;

    /**
     * These properties are for external systems that must manage when or how the scripts are run.
     * For instance, if there is a version of the script. QDL does not care what version the author
     * has of this, but it must be preserved. These are set and managed externally -- QDL itself never
     * touches these or cares about them.
     * @return
     */
    public XProperties getProperties() {
        return properties;
    }

    public void setProperties(XProperties properties) {
        this.properties = properties;
    }

    XProperties properties;


    public void execute(StateInterface state)  {
        QDLParser parser = new QDLParser((State)state);
        try {
            parser.execute(rawScript);
        }catch(Throwable t){
            if(t instanceof RuntimeException){
                throw (RuntimeException)t;
            }
            throw new QDLException("Error: Could not execute script: " + t.getMessage(), t);
        }
    }

    /**
     * Generally if at all possible, run this with {@link #execute(StateInterface)} rather than setting the state and trying to run it.
     * If you set the state then run it, you may end up with stale state at some point which can
     * make bugs that are very hard to track down.
     */
  /*  public void execute() {
        try {
            if (state == null) {
                throw new QDLException("Error: The state has not been set prior to execution");
            }
            execute(state);
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Error: Could not execute script: " + t.getMessage());
        }

    }
*//*
    public void execute(File file) {
        try {
            rawScript = new FileReader(file);
        } catch (Throwable t) {
            throw new QDLException("Error: could not load file \"" + file.getAbsolutePath() + "\":" + t.getMessage());
        }
        execute();
    }

    public void execute(List<String> commands) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String x : commands) {
            stringBuffer.append(x + "\n");
        }
        rawScript = new StringReader(stringBuffer.toString());
        execute();
    }

    public void execute(Reader reader) {
        rawScript = reader;
        execute();
    }*/

    @Override
    public String toString() {
        return "QDLScript{" +
                "code=\n'" + getText() + '\'' +
                ", \nproperties=" + properties +
                "\n}";
    }
}
