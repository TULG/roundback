package org.tulg.roundback.storage;

import org.tulg.roundback.core.RoundBackConfig;

/**
 * Created by jasonw on 9/24/2016.
 */
class Main {
    public static void main(String[] args) {
        RoundBackConfig roundBackConfig = new RoundBackConfig();
        StorageCommandLine storageCommandLine = new StorageCommandLine(args,roundBackConfig);
        StorageNetwork storageNetwork = new StorageNetwork(roundBackConfig);
        storageNetwork.listen();
        //storageConfig.printConfig();
    }
}
