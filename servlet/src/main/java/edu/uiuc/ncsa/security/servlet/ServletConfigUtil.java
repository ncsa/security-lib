package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.util.configuration.ConfigUtil;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/16/13 at  9:45 AM
 */
public class ServletConfigUtil extends ConfigUtil {
    /**
       * Looks for the configuration in the servlet context using the keys for the init parameters
       * @param servletContext
       * @param configFileKey
       * @param configName
       * @param topNodeTag
       * @return
       */

      public static ConfigurationNode findConfigurationNode(ServletContext servletContext,
                                                            String configFileKey,
                                                            String configName,
                                                            String topNodeTag) {
          String fileName = servletContext.getInitParameter(configFileKey);
          if(fileName == null || fileName.length() == 0){
              throw new MyConfigurationException("Error: No configuration file was specified in the servlet configuration.");
          }
          try {
              return findConfiguration(fileName, servletContext.getInitParameter(configName), topNodeTag);
          } catch (MyConfigurationException cx) {
              cx.printStackTrace();
              // plan B, maybe it's in the deployment itself? try to get as a resource
              URL url = null;
              try {
                  url = servletContext.getResource(fileName);
              } catch (MalformedURLException e) {
                  throw new MyConfigurationException("Error: Could not parse URL \"" + fileName + "\". Has a valid configuration been specified?", e);
              }
              if (url == null) {
                  //throw new MyConfigurationException("Error:No configuration found.");
                  // throw the original exception since this is not a resource.
                  throw cx;
              }
              XMLConfiguration cfg = Configurations.getConfiguration(url);
              return findNamedConfig(cfg, servletContext.getInitParameter(configName), topNodeTag);
          }
      }

}
