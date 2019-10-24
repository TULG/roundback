package org.tulg.roundback.testClient;

import java.util.prefs.Preferences;

/**
 * Created by jasonw on 9/24/2016.
 */
class TestCliConfig {
    private Preferences preferences;
    private String port;
    private String hostname;
    private boolean encrypted;


    public TestCliConfig(){
        preferences = Preferences.userRoot().node("org.tulg.roundback.testClient");
        port = preferences.get("port", "2377");
        encrypted = preferences.getBoolean("UseEncryption", true);
        hostname = preferences.get("hostname", "localhost");

    }

    public void save (){
        preferences.put("port", port);
        preferences.putBoolean("UseEncryption", encrypted);
        preferences.put("hostname", hostname);

    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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
        String tmpString = preferences.get("EncryptionKey", "");
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
        // might need to make sure we find a way to
        // only push the key directly disk when needed without caching it in
        // a var in RAM.
        preferences.put("EncryptionKey", encryptionKey);
    }


}
