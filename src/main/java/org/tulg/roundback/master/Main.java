package org.tulg.roundback.master;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.RoundBack;
import org.tulg.roundback.core.RoundBackConfig;

/**
 * The Main class for the master server.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 */
class Main {
    public static void main(String[] args) {
        // set the instance type
        RoundBack.setInstanceType(RoundBack.MASTER);
        Logger.log(Logger.LOG_LEVEL_INFO, "RoundBack Version " + 
            RoundBack.getVersion() + " on " + RoundBack.getFullOString());

        // load the config and parse the commandline.
        RoundBackConfig rBackConfig = new RoundBackConfig();
        
        // before we start up the network, init the Objects tables in the DB
        Main.initObjects();

        // start up the network
        MasterCommandLine.parseToConfig(args, rBackConfig);
        MasterNetwork masterNetwork = new MasterNetwork(rBackConfig);
        masterNetwork.listen();

    }

    /**
     * Initialize all the objects in the MasterDB
     *
     * @return              true on success, false on failure
     */
    private static boolean initObjects() {
        try {
            // see if we can get an array of our objects
            Class[] classes = Main.getClasses("org.tulg.roundback.core.objects");
            for (Class initClass : classes) {
                Method initMethod = null;
                Object initObject = null;
                try {
                    // instantiate
                    String clazzName = initClass.getName();
                    Class<?> clazz = Class.forName(clazzName);
                    Constructor<?> constructor = clazz.getConstructor();
                    initObject = constructor.newInstance((Object[])null);
                    initMethod = initObject.getClass().getMethod("initializeDB", (Class<?>[]) null);
                } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException e) {
                    continue;
                }

                try {
                    if (initMethod != null && initObject != null) {
                        initMethod.invoke(initObject, (Object[]) null);
                    }
                } catch ( SecurityException |  IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException e) {
                    continue;
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            Logger.log(Logger.LOG_LEVEL_WARN, "Unable to find any objects.");
            return false;
        }

        return true;
    }


    /**
    * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
    *
    * @param packageName The base package
    * @return The classes
    * @throws ClassNotFoundException
    * @throws IOException
    * @see https://dzone.com/articles/get-all-classes-within-package
    */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     * @see https://dzone.com/articles/get-all-classes-within-package
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}