package org.tulg.roundback.master;

/**
 * Created by jasonw on 9/24/2016.
 */
class Main {
    public static void main(String[] args) {
        MasterConfig masterConfig = new MasterConfig();
        MasterCommandLine masterCommandLine = new MasterCommandLine(args, masterConfig);
        MasterNetwork masterNetwork = new MasterNetwork(masterConfig);
        masterNetwork.listen();

    }
}
