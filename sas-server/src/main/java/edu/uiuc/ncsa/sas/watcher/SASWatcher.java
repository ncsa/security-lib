package edu.uiuc.ncsa.sas.watcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * For watcher SAS.
 * <p>Created by Jeff Gaynor<br>
 * on 6/21/24 at  4:35 PM
 */
public class SASWatcher {
    File watchedDir;

    public void doIt() throws Throwable {
        init();
        boolean poll = true;
        while (poll) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                //System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
                process(new File(watchedDir, event.context().toString()));
            }
            poll = key.reset();
        }
    }

    protected void init() throws Throwable {
        watchedDir = new File("/home/ncsa/temp/watched");
        watchService = FileSystems.getDefault().newWatchService();
        watchedDir.toPath().register(watchService, ENTRY_CREATE);
    }

    WatchService watchService;


    protected void process(File f) throws Throwable {
                                      say(f);
    }
     protected void say(Object o){
        System.out.println(o.toString());
     }

    public static void main(String[] args) throws Throwable{
        SASWatcher sasWatcher=new SASWatcher();
        sasWatcher.doIt();
    }
    protected static void watchEG() throws IOException, InterruptedException {
        // Set up watcher
        String dir = "/home/ncsa/temp/watched";
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(dir);
        path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
        // Set up polling to look for changes
        boolean poll = true;
        while (poll) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
            }
            poll = key.reset();
        }
    }
}
