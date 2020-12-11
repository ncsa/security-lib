package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

import javax.inject.Provider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

/**
 * Creates Identifiers for use by {@link edu.uiuc.ncsa.security.core.Identifiable} objects.
 * Override for specific semantics. These are created for the various token types when
 * the system is loaded.
 * <p>Created by Jeff Gaynor<br>
 * on 4/3/12 at  1:54 PM
 */
public abstract class IdentifierProvider<V extends Identifier> implements Provider<V> {
    public static String SCHEME = "myproxy";
    public static String SCHEME_SPECIFIC_PART = "oa4mp,2012:"; // NB trailing colon is needed by us
     public static String VERSION_2_0 = "v2.0";

    public static void setScheme(String SCHEME) {
        IdentifierProvider.SCHEME = SCHEME;
    }

    public static void setSchemeSpecificPart(String SCHEME_SPECIFIC_PART) {
        if (SCHEME_SPECIFIC_PART == null || SCHEME_SPECIFIC_PART.isEmpty()) {
            IdentifierProvider.SCHEME_SPECIFIC_PART = null;
            return;
        }
        if (!SCHEME_SPECIFIC_PART.endsWith(":")) {
            SCHEME_SPECIFIC_PART = SCHEME_SPECIFIC_PART + ":";
        }
        IdentifierProvider.SCHEME_SPECIFIC_PART = SCHEME_SPECIFIC_PART;
    }

   String versionString = null;
    protected IdentifierProvider(URI uri,
                                 String component,
                                 boolean useTimestamps) {
        this(uri, component, null, useTimestamps);
    }

    protected IdentifierProvider(URI uri,
                                 String component,
                                 String versionString,
                                 boolean useTimestamps) {
        uriScheme = uri.getScheme();
        schemeSpecificPart = uri.getSchemeSpecificPart();
        this.component = component;
        this.useTimestamps = useTimestamps;
        this.versionString = versionString;
    }

    protected IdentifierProvider(String component) {
        this(component, true);
    }

    protected IdentifierProvider(String component, boolean useTimestamps) {
        this(SCHEME, SCHEME_SPECIFIC_PART, component, useTimestamps);
    }

    /**
     * The main constructor. The component is appended after the caput. If time stamps are
     * enabled, then the last component will be a the current time in milliseconds.
     *
     * @param scheme
     * @param schemeSpecificPart
     * @param component
     * @param useTimestamps
     */
    protected IdentifierProvider(String scheme,
                                 String schemeSpecificPart,
                                 String component,
                                 boolean useTimestamps) {
        this.useTimestamps = useTimestamps;
        this.schemeSpecificPart = schemeSpecificPart;
        this.uriScheme = scheme;
        this.component = component;
    }

    protected String uriScheme;

    protected String schemeSpecificPart;

    /**
     * Creates the caput (="head") from the uri scheme and scheme specific part. This will be of the form<br><br></br></br>
     * scheme : scheme specific part :
     * <br><br>
     * Note that this will not add the final ":" if the SSP ends with a colon or slash. E.g. if
     * <UL>
     * <li>scheme = myproxy</li>
     * <li>SPP = oa4mp,2012</li>
     * </UL>
     * then the caput is myproxy:oa4mp,2012:
     *
     * <p>Note that this does allow for empty scheme specific parts vs. null ones. In the former, it is assumed the user is
     * actively suppressing this component, where as in the null case the default is used.</p>
     * @return
     */
    protected String getCaput() {
        if (caput == null) {
            URI x = null;
            try {
                if(schemeSpecificPart == null){
                    if(uriScheme.endsWith(":")){
                        return uriScheme;
                    }
                   return uriScheme + ":";
                }else {
                    x = new URI(uriScheme, schemeSpecificPart, null);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new GeneralException("Error: Could not create uri for identifiers.", e);
            }
            caput = x.toString();
        }
        return caput;
    }

    String caput;

    /**
     * Creates identifiers of the form
     * <br><br>
     * caput + head + hexString + t                <br><br>
     * with forward slashes added between components as needed.<br><br>
     * E.g. if the caput =  myproxy:oa4mp,2012:<br><br>
     * and the head is "client" and the tail is null, then one result might be<br><br>
     * <p/>
     * myproxy:oa4mp,2012:/client/1d1158e470589f64a816b040b669ba07
     * <p/>Note that if timestamps are enabled, then a last component consisting of a
     * time stamp will be added.
     *
     * @return
     */
    protected URI uniqueIdentifier() {
        String h = "";
        if (component != null) {
            h = component.startsWith("/") ? "" : "/" + component;
            h = h + (component.endsWith("/") ? "" : "/");
        }
        String t = "";
        if (useTimestamps) {
            t = "/" + Long.toString(new Date().getTime());
        }
        return Identifiers.uniqueIdentifier(getCaput() + (versionString==null?"":versionString+"/") + h, t);
        //return URI.create(getCaput() + h + getHexString() + t);
    }

    protected boolean useTimestamps;
    protected String component;

    @Override
    public V get() {
        return (V) new BasicIdentifier(uniqueIdentifier());
    }
}
