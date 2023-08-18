package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.util.configuration.TemplateUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Tools for development, such as centralizing certain things for testing.
 * <p>Created by Jeff Gaynor<br>
 * on 8/18/23 at  9:19 AM
 */
public class DevUtils {
    /**
       * Test properties. These are in the local resources directory and will have
       * substitutions carried out for OS environment variables and Java
       * system properties. So a typical entry might be
       * <pre>
       *     sec-lib.jwt.keys=${NCSA_CONFIG_ROOT}/sas/keys.jwk
       * </pre>
       * If the value of NCSA_CONFIG_ROOT was set in the current (operating system) environment
     * then it will be resolved and used.
       * @param filename
       * @return
       */
      public static XProperties getTestProperties(String filename) throws IOException {
          Map<String,String> p = getAllProperties();
          XProperties xp = new XProperties();
          InputStream inputStream = DevUtils.class.getResourceAsStream(filename);
          if(inputStream == null){
              throw new FileNotFoundException("Could not locate resource \"" + filename+ "\"");
          }
          InputStreamReader isr = new InputStreamReader(inputStream);
          BufferedReader bufferedReader = new BufferedReader(isr);
          String linein = bufferedReader.readLine();
          StringBuilder stringBuilder = new StringBuilder();
          while(linein != null){
              stringBuilder.append(TemplateUtil.newReplaceAll(linein, p) + "\n");
              linein = bufferedReader.readLine();
          }
          bufferedReader.close();
          ByteArrayInputStream bais = new ByteArrayInputStream(stringBuilder.toString().getBytes());
          xp.load(bais);
          return xp;
      }
      static HashMap<String,String> allProperties = null;

      /**
       * A {@link java.util.Map} of all the OS and java properties for use resolving
       * configuration testing files. Java properties override OS properties.
       * Typically given to the JVM at startup with a -D prefix, so to set the
       * property named my.prop at startup for the application MyApp you'd write
       * <pre>
       *     java -Dmy.prop="my-value" MyApp
       * </pre>
       * @return
       */
      public static Map<String,String> getAllProperties(){
          if(allProperties == null ){
              allProperties = new HashMap<>();
              allProperties.putAll(System.getenv());
              for(String x : System.getProperties().stringPropertyNames()){
               allProperties.put(x, System.getProperty(x));
              }
          }
          return allProperties;
      }

      public static void main(String[] args) {
          try {
              System.out.println(getTestProperties("/sec-lib.xp"));
          }catch (Exception e){
              e.printStackTrace();
          }
      }
}
