package edu.uiuc.ncsa.security.storage.data;

import edu.uiuc.ncsa.security.core.Identifiable;

import java.util.Date;

/**
 * Interface for {@link edu.uiuc.ncsa.security.core.Identifiable} (or other) objects that
 * need to have their creation, access and last modified times monitored.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  8:13 AM
 */
public interface Monitored extends Identifiable {
    public Date getLastModifiedDate();
    public void setLastModifiedDate(Date date);
    public Date getLastAccessedDate();
    public void setLastAccessedDate(Date d);
    public Date getCreationDate();
    public void setCreationDate(Date d);
}
