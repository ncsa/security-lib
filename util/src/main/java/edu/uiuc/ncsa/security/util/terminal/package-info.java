/**
 * An implementation of an ISO 6429 terminal. This allows basic cursor addressing in any Java
 * only command line application. There are some gotcha because, well, Java's IO has a lot
 * of limitations. However, all the real grunt work of reading each character and doing something
 * with it is supported and extending this is not hard at all. Works great with the cil package.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 8/3/22 at  8:05 AM
 */
package edu.uiuc.ncsa.security.util.terminal;