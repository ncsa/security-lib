package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
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
    public QDLScript(List<String> lines, XProperties xp) {
        super(lines, xp);
    }

    public QDLScript(Reader script, XProperties properties) {
        super(properties);
        renderContent(script); // too many variables for finding the reader is closed later.
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
        QDLParser parser = new QDLParser((State) state);
        try {
            parser.execute(getText());
        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new QDLException("Error: Could not execute script: " + t.getMessage(), t);
        }
    }


    @Override
    public String toString() {
        return "QDLScript{" +
                Scripts.CODE + "=\n'" + getText() + '\'' +
                ", \nproperties=" + getProperties() +
                "\n}";
    }

}
