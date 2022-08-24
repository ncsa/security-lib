package edu.uiuc.ncsa.sat.thing;

import edu.uiuc.ncsa.sat.SATConstants;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  10:51 AM
 */
public class ExecuteAction extends Action{
    public ExecuteAction() {
        super(SATConstants.ACTION_EXECUTE);
    }

    public String getArg() {
        return arg;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }

    String arg;


}
