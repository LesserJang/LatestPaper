package com.jih10157.latestpaper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.JarFile;

public final class LatestPaper {

    public static void main(String[] args) {
        File file = process();
        Agent.addToClassPath(file);
        try {
            getMainMethod().invoke(null, (Object) args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static File process() {
        PaperClip paperClip = getCachedPaperClip();
        PaperAPI paperAPI = new PaperAPI();
        Version latestVersion;
        int latestPaperBuild;
        try {
            latestVersion = paperAPI.getLatestPaperVersion();
            latestPaperBuild = (int) paperAPI.getLatestPaperBuild(latestVersion.toString());
            if (paperClip != null) {
                if (paperClip.getMCVersion().isSmallerThan(latestVersion)
                    || paperClip.getVersion() < latestPaperBuild) {
                    paperClip = updatePaperClip(latestVersion, latestPaperBuild);
                }
            } else {
                paperClip = updatePaperClip(latestVersion, latestPaperBuild);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (paperClip == null) {
            System.err.println("Failed to download or load paperclip file");
            System.exit(1);
            throw new InternalError();
        }
        return paperClip.getFile();
    }

    private static PaperClip getCachedPaperClip() {
        PaperClip paperClip = null;
        File[] files = getCacheDir().listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    paperClip = new PaperClip(file);
                    break;
                } catch (IOException | IllegalArgumentException ignored) {
                }
            }
        }
        return paperClip;
    }

    private static PaperClip updatePaperClip(Version version, int buildNumber) throws IOException {
        File newPaperClip = new File(getCacheDir(), "paper-" + buildNumber + ".jar");
        PaperAPI.downloadPaper(newPaperClip, version.toString());
        File[] files = getCacheDir().listFiles((dir, name) ->
            PaperClip.isPaperClipName(name) && !name.equals("paper-" + buildNumber + ".jar"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        return new PaperClip(newPaperClip);
    }

    private static File getCacheDir() {
        File cacheDir = new File("./cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    private static Method getMainMethod() {
        try {
            return Class.forName("io.papermc.paperclip.Paperclip").getMethod("main", String[].class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Failed to find main method in paperclip");
            System.exit(1);
        }
        throw new InternalError();
    }
}
