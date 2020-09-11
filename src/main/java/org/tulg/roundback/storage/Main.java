package org.tulg.roundback.storage;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.RoundBack;
import org.tulg.roundback.core.RoundBackConfig;

/**
 * Created by jasonw on 9/24/2016.
 */
class Main {
    public static void main(String[] args) {
        // set the instance type
        RoundBack.setInstanceType(RoundBack.STORAGE);
        Logger.log(Logger.LOG_LEVEL_INFO, "RoundBack Version " + 
        RoundBack.getVersion() + " on " + RoundBack.getFullOString());
        
        // parse the commandline options to the config object
        
        RoundBackConfig roundBackConfig = new RoundBackConfig();
        StorageCommandLine storageCommandLine = new StorageCommandLine();
        storageCommandLine.parseToConfig(args, roundBackConfig);
        storageCommandLine = null;

        StorageNetwork storageNetwork = new StorageNetwork(roundBackConfig);
        storageNetwork.listen();
        //storageConfig.printConfig();
    }
}
