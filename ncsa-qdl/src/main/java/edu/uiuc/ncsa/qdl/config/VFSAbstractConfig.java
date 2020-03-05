package edu.uiuc.ncsa.qdl.config;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/4/20 at  3:13 PM
 */
public abstract class VFSAbstractConfig implements VFSConfig {
    public VFSAbstractConfig(String scheme, String mountPoint, boolean readable, boolean writeable) {
        this.scheme = scheme;
        this.mountPoint = mountPoint;
        this.readable = readable;
        this.writeable = writeable;
    }

    String scheme;
     String mountPoint;
     boolean readable = false;
     boolean writeable = false;
     @Override
     public String getScheme() {
         return scheme;
     }

     @Override
     public String getMountPoint() {
         return mountPoint;
     }



     @Override
     public boolean canRead() {
         return readable;
     }

     @Override
     public boolean canWrite() {
         return writeable;
     }

    @Override
    public String toString() {
        return "VFSAbstractConfig{" +
                "type=\'" + getType() + '\'' +
                "scheme='" + scheme + '\'' +
                ", mountPoint='" + mountPoint + '\'' +
                ", readable=" + readable +
                ", writeable=" + writeable +
                '}';
    }
}
