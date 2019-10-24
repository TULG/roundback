package org.tulg.roundback.storage;

/**
 * Created by jasonw on 9/24/2016.
 */
class Main {
    public static void main(String[] args) {
        StorageConfig storageConfig = new StorageConfig();
        StorageCommandLine storageCommandLine = new StorageCommandLine(args,storageConfig);
        StorageNetwork storageNetwork = new StorageNetwork(storageConfig);
        storageNetwork.listen();
        //storageConfig.printConfig();
    }
}
