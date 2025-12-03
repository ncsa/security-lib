package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.cf.CFBundle;
import edu.uiuc.ncsa.security.core.cf.CFLoader;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.util.configuration.TimeUtil;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/16/13 at  9:45 AM
 */
public class ServletTimeUtil extends TimeUtil {

    /**
     * The contract is that any file in the servlet  context if absolute is a file on
     * the system and if relative is loaded as a resource.
     * @param servletContext
     * @param configFileKey
     * @param configName
     * @param topNodeTag
     * @return
     */
    public static CFNode findCFConfigurationNode(ServletContext servletContext,
                                                 String configFileKey,
                                                 String configName,
                                                 String topNodeTag) {
        String fileName = servletContext.getInitParameter(configFileKey);
        if (fileName == null || fileName.length() == 0) {
            throw new MyConfigurationException("Error: No configuration file was specified in the servlet configuration.");
        }
            File configFile = new File(fileName);
            // If it's relative, assume it's a resource
            InputStream in = null;
            if (configFile.isAbsolute()) {
                if(!configFile.exists()) {
                    throw new MyConfigurationException("configuration file \"" + fileName + "\" does not exist.");
                }
                if(!configFile.canRead()) {
                    throw new MyConfigurationException("configuration file \"" + fileName + "\" cannot be read.");
                }
                if(!configFile.isFile()) {
                    throw new MyConfigurationException("configuration file \"" + fileName + "\" is not a file.");
                }
                try {
                    in = new FileInputStream(configFile);
                }catch(Throwable t) {
                    throw new MyConfigurationException("could not load configuration file \"" + fileName + "\" as a resource. Has a valid configuration been specified?", t);
                }

            } else {
                in = servletContext.getResourceAsStream(fileName);
            }
            if(in == null) {
                throw new MyConfigurationException("could not load configuration file \"" + fileName + "\" as a file. Has a valid configuration been specified?");
            }
            // Vastly simpler configuration loading!
            CFLoader cfLoader = new CFLoader();
            CFBundle bundle = cfLoader.loadBundle(in, topNodeTag);
            CFNode cfNode = bundle.getNamedConfig(servletContext.getInitParameter(configName));
            return cfNode;

    }


}
