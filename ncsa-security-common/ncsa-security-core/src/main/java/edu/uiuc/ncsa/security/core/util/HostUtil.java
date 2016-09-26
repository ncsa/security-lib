package edu.uiuc.ncsa.security.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/22/13 at  11:48 AM
 */
public class HostUtil {

    /**
      * This is the *real* host. The user may set the host property but this should be used internally
      * since it will do any reverse lookups needed.
      *
      * @return
      * @throws java.net.UnknownHostException
      */
     public static String canonicalName(String hostName) throws UnknownHostException {
         InetAddress host = InetAddress.getByName(hostName);
         return reverseLookup(host.getAddress());
     }

    /**
     * The address is either 4 bytes (IpV 4) or 16 bytes (IpV 6). This returns the
     * name associated with the address or throws an exception if no such name is known
     * @param address
     * @return
     * @throws UnknownHostException
     */
    public static String reverseLookup(byte[] address) throws UnknownHostException{
        InetAddress lookup = InetAddress.getByAddress(address);
        if(lookup == null)
            return null;
        return lookup.getCanonicalHostName();
    }

    public static String reverseLookup(String dottedQuadAddress) throws UnknownHostException{
        byte[] addr = new byte[4];
        StringTokenizer st = new StringTokenizer(dottedQuadAddress,".",false);
        int i  = 0;
        while(st.hasMoreTokens()){
            try{
                addr[i++] = Byte.parseByte(st.nextToken());
            }catch(NumberFormatException nfx){
                // Assumption is that this the name of the server which may or may not be
                // local. Try to find a canonical name for it:
                return canonicalName(dottedQuadAddress);
            }
        }
        return reverseLookup(addr);
    }
}
