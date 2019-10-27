package org.tulg.roundback.master;

import org.tulg.roundback.core.RoundBackConfig;

/**
 * Created by jasonw on 9/24/2016.
 */
class Main {
    public static void main(String[] args) {
        RoundBackConfig rBackConfig = new RoundBackConfig();
        MasterCommandLine.parseToConfig(args, rBackConfig);
        //MasterCommandLine masterCommandLine = new MasterCommandLine(args, masterConfig);
        MasterNetwork masterNetwork = new MasterNetwork(rBackConfig);
        masterNetwork.listen();

    }
}
