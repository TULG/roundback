package org.tulg.roundback.storage;


import java.util.prefs.Preferences;

/**
 * Created by jasonw on 9/24/2016.
 */
class StorageConfig {
    private Preferences storagePrefs;
    private String port;
    private int maxThreads;
    private int minDataPort;
    private int maxDataPort;
    private int maxImgSize;
    private boolean encrypted;
    private String backupStorePath;


    public Preferences getStoragePrefs() {
        return storagePrefs;
    }

    public String getPort() {
        return port;
    }

    public StorageConfig () {
        storagePrefs = Preferences.userRoot().node("org.tulg.roundback.storage");

        port = storagePrefs.get("port", "2278");
        maxThreads = storagePrefs.getInt("maxThreads", 2);
        minDataPort = storagePrefs.getInt("minDataPort", 50000);
        maxDataPort = storagePrefs.getInt("maxDataPort", 51000);
        maxImgSize = storagePrefs.getInt("maxImgSize", 1024);
        encrypted = storagePrefs.getBoolean("UseEncryption", true);
        backupStorePath = storagePrefs.get("backupStorePath", System.getProperty("user.home"));

    }

    public void printConfig(){
        System.out.println("port: " + port);
        System.out.println("maxThreads: " + maxThreads);
        System.out.println("minDataPort: " + minDataPort);
        System.out.println("maxDataPort: " + maxDataPort);
        System.out.println("maxImgSize: " + maxImgSize);

        // TODO: Expand this.

    }

    public void save() {
        //  save the current config to prefs
        storagePrefs.put("port", port);
        storagePrefs.putInt("maxThreads", maxThreads);
        storagePrefs.putInt("minDataPort", minDataPort);
        storagePrefs.putInt("maxDataPort", maxDataPort);
        storagePrefs.putInt("maxImgSize", maxImgSize);
        storagePrefs.putBoolean("UseEncryption", encrypted);

    }

    public void setStoragePrefs(Preferences storagePrefs) {
        this.storagePrefs = storagePrefs;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public int getMinDataPort() {
        return minDataPort;
    }

    public void setMinDataPort(int minDataPort) {
        this.minDataPort = minDataPort;
    }

    public int getMaxDataPort() {
        return maxDataPort;
    }

    public void setMaxDataPort(int maxDataPort) {
        this.maxDataPort = maxDataPort;
    }

    public int getMaxImgSize() {
        return maxImgSize;
    }

    public void setMaxImgSize(int maxImgSize) {
        this.maxImgSize = maxImgSize;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    public void setEncryptionKey(String encryptionKey) {
        // only var that is synced to disk when set.
        //  might need to make sure we find a way to
        // only push the key directly disk when needed without caching it in
        // a var in RAM.
        // TODO: Need to find a better way to store the key, or take it in from the user at start up and keep a hashed verion in memory.
        // TODO: Update Encrypter.java and all other *Config.java files.  Perhaps
        // TODO: Merge all *Config.java files to one main Config.java class.
        storagePrefs.put("EncryptionKey", encryptionKey);
    }

    public boolean getEncrypted() {
        return encrypted;
    }

    public String getEncryptionKey() {
        // pulled from prefs, might need to make sure we find a way to
        // only pull the key from disk when needed without caching it in
        // a var in RAM.
        return storagePrefs.get("EncryptionKey", "");
    }

    public String getBackupStorePath() {
        return backupStorePath;
    }

    public void setBackupStorePath(String backupStorePath) {
        this.backupStorePath = backupStorePath;
    }
}
