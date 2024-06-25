package edu.uiuc.ncsa.security.installer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/18/24 at  11:14 AM
 */
public class ZipArchive implements FileEntryInterface {
    public ZipArchive(String sourceName) {
        this.sourceName = sourceName;
    }

    String sourceName;

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

}
