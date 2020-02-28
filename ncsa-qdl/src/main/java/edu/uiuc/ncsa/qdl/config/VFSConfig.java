package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/20 at  12:49 PM
 */
public interface VFSConfig {
    String getType();
    String getScheme();
    String getMountPoint();
    boolean canRead();
    boolean canWrite();
    }
