package edu.uiuc.ncsa.security.installer;

/**
 * Models a version. So the name is what the user specifies in the -version
 * flag, the file is the actual resource name and the description is shown to the
 * user when listing versions.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 6/16/24 at  6:38 AM
 */
public class VersionEntry {
    public VersionEntry(String name, String resource, String description, String notes) {
        this.name = name;
        this.resource = resource;
        this.description = description;
        this.notes = notes;
    }

    String name;
    String resource;
    String description;
    String notes;
}
