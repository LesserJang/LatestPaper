package com.jih10157.latestpaper;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

final class PaperAPI {

    private static final String baseUrl = "https://papermc.io/api/v1/paper";

    private final JSONParser parser = new JSONParser();

    public Version getLatestPaperVersion() throws IOException {
        URL url = new URL(baseUrl);
        JSONObject object;
        try {
            object = (JSONObject) parser.parse(new InputStreamReader(url.openStream()));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Version("0.0.0");
        }
        @SuppressWarnings("unchecked")
        List<String> versions = (List<String>) object.get("versions");
        return new Version(versions.get(0));
    }

    public long getLatestPaperBuild(String v) throws IOException {
        URL url = new URL(baseUrl + "/" + v);
        JSONObject object;
        try {
            object = (JSONObject) parser.parse(new InputStreamReader(url.openStream()));
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return (Long) ((JSONObject) object.get("builds")).get("latest");
    }

    public static void downloadPaper(File target, String v) throws IOException {
        System.out.println("Downloading " + v + " " + target.getName());
        URL url = new URL(baseUrl + "/" + v + "/latest/download");
        Files.copy(url.openStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
