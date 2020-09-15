package org.tulg.roundback.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.MasterConnection;
import org.tulg.roundback.core.RoundBack;
import org.tulg.roundback.core.RoundBackConfig;

/**
 * Created by jasonw on 9/24/2016.
 */
class Main {
    public static void main(String[] args) {
        // set the instance type
        RoundBack.setInstanceType(RoundBack.STORAGE);
        Logger.log(Logger.LOG_LEVEL_INFO,
                "RoundBack Version " + RoundBack.getVersion() + " on " + RoundBack.getFullOString());

        // parse the commandline options to the config object

        RoundBackConfig roundBackConfig = new RoundBackConfig();
        StorageCommandLine storageCommandLine = new StorageCommandLine();
        storageCommandLine.parseToConfig(args, roundBackConfig);
        
        StorageNetwork storageNetwork = new StorageNetwork(roundBackConfig);
        // TODO: Attempt to connect and verify registration with the master server.
        // send heartbeat packet

        MasterConnection mc = new MasterConnection(roundBackConfig.getStorageMasterAddress(),
                roundBackConfig.getStorageMasterPort());
        if (!mc.connect()) {
            Logger.crit("Cannot connect to master server at '" + roundBackConfig.getStorageMasterAddress() + ":"
                    + roundBackConfig.getStorageMasterPort() + "'");
            System.exit(1);
        }

        
        if (storageCommandLine.register()) {
            int result = 0;
            // TODO: Call the register routine and exit.
            // Ask the user for a username and password.
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter an admin username: ");
            String username = "";
            String password = "";
            try {
                username = input.readLine();
                System.out.print("Enter the user's password: ");
                password = new String(System.console().readPassword());
            } catch (IOException e) {
                Logger.crit("Input/Output error");
                System.exit(1);
            }
            if(!mc.login(username, password)){
                Logger.crit("Login Error.");
                System.exit(1);
            }

            // we were logged in, let's try to register.
            if(!mc.register(RoundBack.getInstanceType(), RoundBack.getComputerName())){
                Logger.crit("Unable to register.");
                System.exit(1);
            }
            Logger.info("System Registered.");

            System.exit(result);
        }
        storageCommandLine = null;

        // we made a connection, attempt a heartbeat.
        try {
            if (!mc.sendHeartBeat()) {
                Logger.crit("Cannot send heartbeat to master.  Check Logs.");
                System.exit(1);
            }
        } catch (IOException e) {
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
            System.exit(1);
        }

        // we got this far, so we are good with the master.
        storageNetwork.setMasterConnection(mc);
        storageNetwork.listen();
        //storageConfig.printConfig();
    }
}
