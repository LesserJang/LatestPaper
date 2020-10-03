package com.jih10157.latestpaper;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PaperClip {

    private static final Pattern paperPattern = Pattern.compile("paper-(\\d+).jar");

    private final File file;
    private final JarFile jar;
    private final Version mcVersion;
    private final int version;

    PaperClip(File file) throws IOException {
        this.file = file;
        this.jar = new JarFile(file);
        if (!isPaperClipName(file.getName()) && !isPaperClip()) {
            throw new IllegalArgumentException(file.getName()+" is not paperclip");
        }
        Properties properties = new Properties();
        properties.load(jar.getInputStream(jar.getEntry("patch.properties")));
        mcVersion = new Version(properties.getProperty("version"));
        Matcher matcher = paperPattern.matcher(file.getName());
        matcher.find();
        version = Integer.parseInt(matcher.group(1));
        jar.close();
    }

    private boolean isPaperClip() throws IOException {
        String mainClass = jar.getManifest().getMainAttributes().getValue("Main-Class");
        return "io.papermc.paperclip.Paperclip".equals(mainClass);
    }

    Version getMCVersion() {
        return mcVersion;
    }

    int getVersion() {
        return version;
    }

    File getFile() {
        return file;
    }

    static boolean isPaperClipName(String name) {
        return paperPattern.matcher(name).matches();
    }
}
