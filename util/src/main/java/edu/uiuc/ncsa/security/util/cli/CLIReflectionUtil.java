package edu.uiuc.ncsa.security.util.cli;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/21/13 at  2:28 PM
 */
public class CLIReflectionUtil {
    static Method[] objectM = Object.class.getMethods(); // list of standard methods in java.lang.Object
    static Modifier mod = new Modifier(); // just need a dummy instance

    /**
     * Returns true if the methodName is one of the java.lang.Object basic methods (e.g. toString()).
     *
     * @param methodName
     * @param mods
     * @return
     */
    public static boolean isJavaObjectMethod(String methodName, int mods) {
        for (int i1 = 0; i1 < objectM.length; i1++) {
            int objMods = objectM[i1].getModifiers();
            if (!mod.isPublic(mods) || mod.isStatic(mods) || //
                    !mod.isPublic(objMods) || mod.isStatic(objMods) || //
                    methodName.equals(objectM[i1].getName())) {
                return true;
            } // end of comparing names with java general Object calls.
        } // end i1-loop
        return false;
    }

    /**
     * Invokes the named method on the object. This assumes that the object is an implementation of the {@link Commands}
     * interface.
     * @param obj
     * @param name
     * @param cliAV
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void invokeMethod(Commands obj, String name, InputLine cliAV) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
         Class[] al = new Class[1];
         al[0] = InputLine.class;
         Method M = obj.getClass().getMethod(name, al);
         Object[] z = new Object[1];
         z[0] = cliAV;
         M.invoke(obj, z);
     }
    /**
      * This snoops through the CCI and gets those methods that are not
      * in Java.lang.Object, are public and are not static. Note that this will
     * return all the unique names in the commands array. This method is not tasked
     * with dis-ambiguating them.
      *
      * @return java.lang.String[]
      */
     public static String[] getCommandsNameList(Commands[] commands) {
         HashMap<String, Integer> names = new HashMap<String, Integer>();
         // trick with the hash map to keep a running list of unique names, which are keys there.
         for(int i=0; i<commands.length ; i++){
             Method[] CCIM = commands[i].getClass().getMethods();
             for (int i0 = 0; i0 < CCIM.length; i0++) {
                 if (!isJavaObjectMethod(CCIM[i0].getName(), CCIM[i0].getModifiers()) && hasRightSignature(CCIM[i0])) {
                     names.put(CCIM[i0].getName(), 0);
                 }
             } // end i0-loop
         }
         return  names.keySet().toArray(new String[0]);
     }

    /**
      * Checks that the method has the correct signature for the contract in the Commands interface,
      * i.e. that takes a single InputLine as it argument, is public and not static.
      *
      * @param method
      * @return
      */
     public static boolean hasRightSignature(Method method) {
         int mods = method.getModifiers();
         Class pvec[] = method.getParameterTypes();
         return mod.isPublic(mods) && !mod.isStatic(mods)  && pvec.length == 1 && pvec[0].equals(InputLine.class);
     }

}
