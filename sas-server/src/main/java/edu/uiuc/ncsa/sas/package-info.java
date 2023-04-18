/**
 * SAS = Subject-action service. This is a web-based service that allows users to run remote
 * sessions on a server. It is not a REST API though it does use HTTP. The basic operation is
 * that a client registers, sending their public key. They have an SAS-aware client that can
 * log them in and all traffic uses pretty good privacy (initial exchange uses public/private keys,
 * then a shared symmetric key is created). All exchanges are encrypted, so this can even be
 * done over HTTP rather than HTTPS.
 *
 * <p>On the server side, sessions are managed and the user may run multiple sessions. This means
 * that your server can run headless but you can still get nice GUI or other sessions locally.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/23 at  9:07 AM
 */
package edu.uiuc.ncsa.sas;