package edu.uiuc.ncsa.qdl.workspace;

import java.util.Date;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/21 at  6:50 AM
 */
public class AutosaveThread extends Thread {
    public AutosaveThread(WorkspaceCommands workspaceCommands) {
        this.workspaceCommands = workspaceCommands;
    }

    public boolean isStopThread() {
        return stopThread;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    boolean stopThread = true; // just in case,


    public WorkspaceCommands getWorkspaceCommands() {
        return workspaceCommands;
    }

    public void setWorkspaceCommands(WorkspaceCommands workspaceCommands) {
        this.workspaceCommands = workspaceCommands;
    }

    WorkspaceCommands workspaceCommands;


    protected void log(String x) {
        if (getWorkspaceCommands().getLogger() != null) {
            getWorkspaceCommands().getLogger().info(x);
        }
    }

    @Override
    public void run() {
        stopThread = false; // so if they call run, it runs.
        log("starting autosave thread at " + (new Date()));
        while (!isStopThread() && !this.isInterrupted()) {
            try {
                sleep(getWorkspaceCommands().getAutosaveInterval());
                if (workspaceCommands.currentWorkspace == null) {
                    workspaceCommands.say("Error: No file set as target of save. Autosave disabled.");
                    workspaceCommands.setAutosaveOn(false);
                    stopThread = true;
                } else {
                    String saveLine = WorkspaceCommands.SAVE_COMMAND; // Nothing fancy.
                    saveLine = saveLine + (getWorkspaceCommands().isAutosaveMessagesOn() ? WorkspaceCommands.SILENT_SAVE_FLAG : "");
                    getWorkspaceCommands().execute(saveLine);
                }
            } catch (InterruptedException e) {
                setStopThread(true); // just in case.
                if (getWorkspaceCommands().getLogger() != null) {
                    getWorkspaceCommands().getLogger().warn("Cleanup interrupted, stopping thread...");
                }
            }
        }

    }
}
