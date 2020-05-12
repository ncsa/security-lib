package edu.uiuc.ncsa.security.util.cli;

import java.io.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/11/20 at  6:58 AM
 */
public class BasicIO implements IOInterface {
    
    public BasicIO() {
        // let lazy initialization take care of this.
    }

    PrintStream printStream;
    InputStream inputStream;

    public BasicIO(InputStream inputStream, PrintStream printStream) {
        this.printStream = printStream;
        this.inputStream = inputStream;
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public BufferedReader getBufferedReader() {
        if(bufferedReader == null){
            bufferedReader = new BufferedReader(new InputStreamReader(getInputStream()));
        }
        return bufferedReader;
    }

    BufferedReader bufferedReader = null;

    @Override
    public String readline(String prompt) throws IOException {
        if(prompt != null) {
            print(prompt);
        }
        return getBufferedReader().readLine();
    }

    @Override
    public String readline() throws IOException {
        return readline(null);
    }

    public PrintStream getPrintStream() {
        if(printStream == null){
            printStream = System.out;
        }
        return printStream;
    }

    public void setPrintStream(PrintStream printStream) {
        this.printStream = printStream;
    }

    public InputStream getInputStream() {
        if(inputStream == null){
            inputStream = System.in;
        }
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void print(Object x) {
        getPrintStream().print(x);
    }

    @Override
    public void println(Object x) {
        getPrintStream().println(x);
    }

    @Override
    public void flush() {
            getPrintStream().flush();
    }
}
