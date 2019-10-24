package org.tulg.roundback.master;

import java.util.prefs.Preferences;

/**
 * Created by jasonw on 9/24/2016.
 */
class MasterConfig {
    private Preferences preferences;
    private String port;
    private boolean encrypted;


    public MasterConfig(){
        preferences = Preferences.userRoot().node("org.tulg.roundback.master");
        port = preferences.get("port", "2377");
        encrypted = preferences.getBoolean("UseEncryption", true);

    }

    public void save (){
        preferences.put("port", port);
        preferences.putBoolean("UseEncryption", encrypted);


    }

    public String getPort() {
        return port;
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

    public void setPort(String port) {
        this.port = port;
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


}
