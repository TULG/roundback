package org.tulg.roundback.core;

import java.util.prefs.Preferences;

/**
 * RoundBackConfig
 */
public class RoundBackConfig {

    // TODO: Merge all configs to one.

    private Preferences preferences;
    // master conifg
    private String masterPort;
    private int sessionTimeout;    


    // storage conifig
    private String storagePort;
    private int maxThreads;
    private int minDataPort;
    private int maxDataPort;
    private int maxImgSize;
    private String backupStorePath;

    // core config
    private boolean encrypted;
    private int defaultLogLevel;


    public RoundBackConfig(){
        preferences = Preferences.userRoot().node("org.tulg.roundback");
        // master config
        masterPort = preferences.get("master.port", "2377");
        sessionTimeout = preferences.getInt("master.sessionTimeout", 3600);

        // storage config
        storagePort = preferences.get("storage.port", "2278");
        maxThreads = preferences.getInt("storage.maxThreads", 2);
        minDataPort = preferences.getInt("storage.minDataPort", 50000);
        maxDataPort = preferences.getInt("storage.maxDataPort", 51000);
        maxImgSize = preferences.getInt("storage.maxImgSize", 1024);
        backupStorePath = preferences.get("storage.backupStorePath", System.getProperty("user.home"));

        // core config
        encrypted = preferences.getBoolean("UseEncryption", true);
        defaultLogLevel = preferences.getInt("core.defaultLogLevel", Logger.LOG_LEVEL_INFO);
    }

    public void save (){
                //  save the current config to prefs

        preferences.put("master.port", masterPort);
        preferences.putInt("master.sessionTimeout", sessionTimeout);

        preferences.put("storage.port", storagePort);
        preferences.putInt("storage.maxThreads", maxThreads);
        preferences.putInt("storage.minDataPort", minDataPort);
        preferences.putInt("storage.maxDataPort", maxDataPort);
        preferences.putInt("storage.maxImgSize", maxImgSize);
        preferences.putBoolean("storage.UseEncryption", encrypted);

        preferences.putBoolean("UseEncryption", encrypted);
        preferences.putInt("core.defaultLogLevel", defaultLogLevel);
    }

    public String getMasterPort() {
        return masterPort;
    }

    public boolean getEncrypted() {
        return encrypted;
    }

    public String getEncryptionKey() {
        // pulled from prefs, might need to make sure we find a way to
        // only pull the key from disk when needed without caching it in
        // a var in RAM.
        return preferences.get("EncryptionKey", "");
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void setMasterPort(String masterPort) {
        this.masterPort = masterPort;
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
        preferences.put("EncryptionKey", encryptionKey);
    }

    public String getStoragePort() {
        return storagePort;
    }
   
/*
    public void printConfig(){
        System.out.println("port: " + port);
        System.out.println("maxThreads: " + maxThreads);
        System.out.println("minDataPort: " + minDataPort);
        System.out.println("maxDataPort: " + maxDataPort);
        System.out.println("maxImgSize: " + maxImgSize);

        // TODO: Expand this.

    }*/

    public void setpreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void setStoragePort(String storagePort) {
        this.storagePort = storagePort;
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

    public String getBackupStorePath() {
        return backupStorePath;
    }

    public void setBackupStorePath(String backupStorePath) {
        this.backupStorePath = backupStorePath;
    }


    public int getSessionTimeout() {
        return this.sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getDefaultLogLevel() {
        return this.defaultLogLevel;
    }

    public void setDefaultLogLevel(int defaultLogLevel) {
        this.defaultLogLevel = defaultLogLevel;
    }

}
