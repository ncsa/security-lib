/**
 * Package for the Unused Client thread. This is the machinery to track down clients
 * that have not been used and delete them.
 * <p>
 *     This solves a specific problem for certain OA4MP servers that allow for public client
 *     registration and auto-approval. Since anyone can create a client and the only
 *     control is really if they can log in, unused clients can accumulate at a high rate.
 *     This is particularly a problem if automation is involved in requesting the clients
 *     and there is some issue --  unused clients numbering in the tens of thousands can happen.
 *     This thread, if enabled, will compare client's last accessed timestamp with their
 *     creation timestamp. The contract is that if the creation timestamp is less than
 *     a certain value (say 6 hours) and last accessed is zero or null, then the client
 *     is simply deleted.
 * </p>
 * <p>Created by Jeff Gaynor<br>
 * on 5/10/23 at  8:57 AM
 */
package edu.uiuc.ncsa.security.storage.uuc;