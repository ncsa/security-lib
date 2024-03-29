package edu.uiuc.ncsa.security.storage.monitored;

import edu.uiuc.ncsa.security.core.DateComparable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;

import java.util.Date;

/**
 * Interface for {@link edu.uiuc.ncsa.security.core.Identifiable} (or other) objects that
 * need to have their creation, access and last modified times monitored.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  8:13 AM
 */
public class Monitored extends IdentifiableImpl implements DateComparable {
    public Monitored(Identifier identifier) {
        super(identifier);
    }


    Date lastModifiedTS = null; // Note: never set this since the system manages it.

    // Note: never set this, since the system manages it. In particular, this is null of the
    // object has never been accessed, so is used in cleaning up unused objects.
    Date creationTS  = new Date();
    Date lastAccessed  = null;

    public Date getLastModifiedTS() {
        return lastModifiedTS;
    }

    public void setLastModifiedTS(Date lastModifiedTS) {
        this.lastModifiedTS = lastModifiedTS;
    }

    public Date getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Date getCreationTS() {
        return creationTS;
    }

    public void setCreationTS(Date creationTS) {
        this.creationTS = creationTS;
    }

    @Override
    public Monitored clone() {
        IdentifiableImpl ii = super.clone();
        Monitored mm = new Monitored(ii.getIdentifier());
        mm.setDescription(getDescription());
        mm.setCreationTS(getCreationTS());
        mm.setLastAccessed(getLastAccessed());
        mm.setLastModifiedTS(getLastModifiedTS());
        return mm;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Monitored)) return false;
        Monitored mm = (Monitored) obj;
        if(getCreationTS() == null){
            if(mm.getCreationTS() != null) return false;
        }else{
            if(!getCreationTS().equals(mm.getCreationTS())) return false;
        }
        if(getLastAccessed() == null){
            if(mm.getLastAccessed()!=null) return false;
        } else{

            if (!getLastAccessed().equals(mm.getLastAccessed())) return false;
        }
        if(getLastModifiedTS() == null){
              if(mm.getLastModifiedTS() != null) return false;
        } else{
            if (!getLastModifiedTS().equals(mm.getLastModifiedTS())) return false;
        }
        return true;

    }
}
