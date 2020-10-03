package com.jih10157.latestpaper;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public final class Agent {

    private static Instrumentation inst = null;

    public static void agentmain(String agentArgs, Instrumentation instr) {
        inst = instr;
    }

    static void addToClassPath(File jarFile) {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        if (loader instanceof URLClassLoader) {
            try {
                Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addURL.setAccessible(true);
                addURL.invoke(loader, jarFile.toURI().toURL());
            } catch (NoSuchMethodException | MalformedURLException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                System.err.println("Failed to add paperclip to System ClassLoader");
                System.exit(1);
                throw new InternalError();
            }
        } else if (inst != null) {
            try {
                inst.appendToSystemClassLoaderSearch(new JarFile(jarFile));
                deliverInstToPaper(inst);
                inst = null;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load paperclip file");
                System.exit(1);
                throw new InternalError();
            }
        } else {
            System.err.println("Unable to add paperclip to System ClassLoader");
            System.exit(1);
            throw new InternalError();
        }
    }

    private static void deliverInstToPaper(Instrumentation inst) {
        try {
            Class<?> agent = Class.forName("io.papermc.paperclip.Agent");
            Method agentmain = agent.getMethod("agentmain", String.class, Instrumentation.class);
            agentmain.invoke(null, null, inst);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.err.println("Cannot deliver instrumentation to paperclip");
            System.exit(1);
            throw new InternalError();
        }
    }
}
