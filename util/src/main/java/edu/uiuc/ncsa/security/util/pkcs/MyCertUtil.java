package edu.uiuc.ncsa.security.util.pkcs;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Feb 23, 2011 at  11:45:07 AM
 */

import edu.uiuc.ncsa.security.util.crypto.CertUtil;

public class MyCertUtil extends CertUtil {
}
/*
  Useful OpenSSL command to create a self-signed cert and key (4096 bits) good for 10 years.
  This can be used to test this class. Fill in subj with something useful.

  openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -sha256 -days 3650 -nodes -subj "/C=XX/ST=StateName/L=CityName/O=CompanyName/OU=CompanySectionName/CN=CommonNameOrHostname"

  Key is PKCS 8 format.
 */