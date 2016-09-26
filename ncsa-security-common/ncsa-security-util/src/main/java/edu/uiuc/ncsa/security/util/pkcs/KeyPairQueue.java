package edu.uiuc.ncsa.security.util.pkcs;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.QueueWithSpare;

import java.security.KeyPair;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/11/14 at  12:14 PM
 */
public class KeyPairQueue extends QueueWithSpare<KeyPair> {
       @Override
       public KeyPair pop() {
           KeyPair x = super.pop();
           if (x != null) {
               return x;
           }
           try {
               KeyPair keyPair = KeyUtil.generateKeyPair();
               setSpare(keyPair);
               return keyPair;
           } catch (Exception e) {
               throw new GeneralException("Error generating key pair", e);
           }

       }
   }
