package edu.uiuc.ncsa.security.delegation.storage;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.DateUtils;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;

import java.util.Date;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkEquals;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/12/16 at  4:32 PM
 */
public class BaseClient  extends IdentifiableImpl {
    public BaseClient(Identifier identifier) {
        super(identifier);
    }

    @Override
     public BaseClient clone() {
         BaseClient c = new BaseClient(getIdentifier());
         c.setCreationTS(getCreationTS());
         c.setEmail(getEmail());
         c.setName(getName());
         c.setSecret(getSecret());
         return c;
     }

     public String getSecret() {
         return secret;
     }

     public void setSecret(String secret) {
         this.secret = secret;
     }

     String secret;

     String name;
     Date creationTS;
     String email;


     public String getEmail() {
         return email;
     }

     public void setEmail(String email) {
         this.email = email;
     }

     public String getName() {
         return name;
     }

     public void setName(String name) {
         this.name = name;
     }

     public Date getCreationTS() {

         return creationTS;
     }

     public void setCreationTS(Date creationTS) {
         this.creationTS = creationTS;
     }


     @Override
     public boolean equals(Object obj) {
         if(!super.equals(obj)) return false;
         Client c = (Client) obj;
         if (!checkEquals(getSecret(), c.getSecret())) return false;
         if (!checkEquals(getName(), c.getName())) return false;
         if (!checkEquals(getEmail(), c.getEmail())) return false;
         if (!DateUtils.equals(getCreationTS(), c.getCreationTS())) return false;
         return true;
     }


     @Override
     public String toString() {
         return getClass().getSimpleName() + "[name=\"" + getName() +
                 "\", id=\"" + getIdentifierString() +
                 "\", email=\"" + getEmail() +
                 "\", secret=" + (getSecret()==null?"(none)":getSecret().substring(0,25)) +
                 "]";
     }

}
