package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.security.util.cli.IOInterface;

import java.io.IOException;

/**
 * An IO class backed by strings. This is used in e.g. web apps as a dummy where the
 * actual control flow is managed by something else. Call {@link #flush()} between
 * calls to reset everything.
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  2:14 PM
 */
public class StringIO implements IOInterface {

    public StringIO(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    String input;

    public StringBuilder getOutput() {
        return output;
    }

    public void setOutput(StringBuilder output) {
        this.output = output;
    }

    StringBuilder output = new StringBuilder();

    @Override
    public String readline(String prompt) throws IOException {
        return input;
    }

    @Override
    public String readline() throws IOException {
        return input;
    }

    @Override
    public void print(Object x) {
        output.append(x);
    }

    @Override
    public void println(Object x) {
        output.append(x + "\n");
    }

    @Override
    public void flush() {
        input = "";
        output = new StringBuilder();
    }

    @Override
    public void clearQueue() {

    }

    @Override
    public boolean isQueueEmpty() {
        return false;
    }

    @Override
    public void setBufferingOn(boolean bufferOn) {

    }

    @Override
    public boolean isBufferingOn() {
        return false;
    }
}
