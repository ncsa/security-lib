package edu.uiuc.ncsa.qdl.config;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/12/21 at  8:09 AM
 */
public class QDLEditor {
    public String name;
    public String exec;
    public boolean clearScreen;
    public List<QDLEditorArg> args = new ArrayList<>();
}
