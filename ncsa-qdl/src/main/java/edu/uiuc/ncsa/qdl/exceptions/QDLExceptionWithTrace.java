package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * An exception when evaluating a set of statements. This gives the statement number
 * (not the line number, which is close to impossible to determine given how Antlr
 * handles whitespace).
 * <p>Created by Jeff Gaynor<br>
 * on 11/3/21 at  4:08 PM
 */
public class QDLExceptionWithTrace extends QDLException{

    public boolean hasStatement(){
        return statement != null;
    }
    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    Statement statement;

    public QDLExceptionWithTrace(Statement statement) {
        this.statement = statement;
    }

    public QDLExceptionWithTrace(Throwable cause, Statement statement) {
        super(cause);
        this.statement = statement;    }

    public QDLExceptionWithTrace(String message, Statement statement) {
        super(message);
        this.statement = statement;    }

    public QDLExceptionWithTrace(String message, Throwable cause, Statement statement) {
        super(message, cause);
        this.statement = statement;    }
    boolean script = false;
    String scriptName;

    public boolean isScript() {
        return script;
    }

    public void setScript(boolean script) {
        this.script = script;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }
}
