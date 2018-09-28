package net.minecraftforge.installer.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraftforge.installer.DownloadUtils;

public class Install
{
    // Profile name to install and direct at this new version
    private String profile;
    // Version name to install to.
    private String version;
    // Vanilla version this is based off of.
    private String minecraft;
    // Version json to install into the client
    private String json;
    // Logo to be displayed on the installer GUI.
    private String logo;
    // Maven artifact path for the 'main' jar to install.
    private Artifact path;
    // Icon to use for the url button
    private String urlIcon;
    // Welcome message displayed on main install panel.
    private String welcome;
    // URL for mirror list, which needs to be a json file in the format of an array of Mirror
    private String mirrorList;
    //Hides an entry from the install UI
    private boolean hideClient, hideServer, hideExtract = false;
    // Extra libraries needed by processors, that may differ from the installer version's library list. Uses the same format as Mojang for simplicities sake.
    private Version.Library[] libraries;
    // Executable jars to be run after all libraries have been downloaded.
    private Processor[] processors;
    //Data files to be extracted during install, used for processor.
    private Map<String, DataFile> data;

    // non-serialized values
    private Mirror mirror;
    private boolean triedMirrors = false;

    public String getProfile() {
        return profile;
    }

    public String getVersion() {
        return version;
    }

    public String getMinecraft() {
        return minecraft;
    }

    public String getJson() {
        return json;
    }

    public String getLogo() {
        return logo;
    }

    public Artifact getPath() {
        return path;
    }

    public String getUrlIcon() {
        return urlIcon == null ? "/url.png" : urlIcon;
    }

    public String getWelcome() {
        return welcome == null ? "" : welcome;
    }

    public String getMirrorList() {
        return mirrorList;
    }

    public Mirror getMirror() {
        if (getMirrorList() == null)
            return null;
        if (!triedMirrors && mirror == null) {
            Mirror[] list = DownloadUtils.downloadMirrors(getMirrorList());
            mirror = list == null ? null : list[new Random().nextInt(list.length)];
        }
        return mirror;
    }

    public boolean hideClient() {
        return hideClient;
    }

    public boolean hideServer() {
        return hideServer;
    }

    public boolean hideExtract() {
        return hideExtract;
    }

    public Version.Library[] getLibraries() {
        return libraries == null ? new Version.Library[0] : libraries;
    }

    public Processor[] getProcessors() {
        return processors == null ? new Processor[0] : processors;
    }

    public Map<String, String> getData(boolean client) {
        if (data == null)
            return new HashMap<>();

        return data.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e -> client ? e.getValue().client : e.getValue().server));
    }

    public static class Processor {
        // The executable jar to run, The installer will run it in-process, but external tools can run it using java -jar {file}, so MANFEST Main-Class entry must be valid.
        private Artifact jar;
        // Dependency list of files needed for this jar to run. Aything listed here SHOULD be listed in {@see Install#libraries} so the installer knows to download it.
        private Artifact[] classpath;
        /*
         * Arguments to pass to the jar, can be in the following formats:
         * [Artifact] : A artifact path in the target maven style repo, where all libraries are downloaded to.
         * {DATA_ENTRY} : A entry in the Install#data map, extract as a file, there are a few extra specified values to allow the same processor to run on both sides:
         *   {MINECRAFT_JAR} - The vanilla minecraft jar we are dealing with, /versions/VERSION/VERSION.jar on the client and /minecraft_server.VERSION.jar for the server
         *   {SIDE} - Either the exact string "client" or "server" depending on what side we are installing.
         */
        private String[] args;

        public Artifact getJar() {
            return jar;
        }

        public Artifact[] getClasspath() {
            return classpath == null ? new Artifact[0] : classpath;
        }

        public String[] getArgs() {
            return args == null ? new String[0] : args;
        }
    }

    public static class DataFile {
        // The file to use when installing the client
        private String client;
        // The file to use when installing the server
        private String server;

        public String getClient() {
            return client;
        }
        public String getServer() {
            return server;
        }
    }
}
