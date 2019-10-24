package org.tulg.roundback.storage;

import org.tulg.roundback.core.NetIOHandler;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by jasonw on 4/26/2017.
 */
class StorageProtocol {

    private NetIOHandler netIOHandler = null;
    private boolean adminSession = false;
    private long adminSessionStart = 0;
    private StorageConfig storageConfig;


    public StorageProtocol(NetIOHandler netIOHandler) {


        this.netIOHandler = netIOHandler;


    }

    public boolean process(String inputLine) throws IOException {

        if (inputLine.compareTo("") == 0) {
            netIOHandler.println("OK");

            return true;
        }

        // split the line into words.
        StringTokenizer parser = new StringTokenizer(inputLine);
        String commannd = parser.nextToken();
        // Process incoming commands.

        // bye command
        switch (commannd.toLowerCase()) {
            case "bye":
                System.out.println("Connection to: " + netIOHandler.getClientAddress() + " closed.");

                return false;
            case "auth":
                return proecessAuthCommand(parser);
            case "store":
                return processStoreCommand(parser);
            // XXX: Add new commands here.
            default:
                netIOHandler.println("ERR: Unsupported Command");

        }
        return true;
    }

    private boolean checkForMoreTokens(StringTokenizer parser) throws IOException {
        if(!parser.hasMoreTokens()){
            netIOHandler.println("Err: Missing Required Argument");
            return false;
        }
        return true;
    }

    private boolean processStoreCommand(StringTokenizer parser) throws IOException {
        if(!parser.hasMoreTokens()) {
            netIOHandler.println("Err: Missing Required Argument");
            return true;
        }
        // store command syntax:
        //      store <backupID> <file|directory> <checksum> <owner> <group> <perms> <timestamp> <full_path_name>
        String backupID, type, checkSum, owner, group, perms, timestamp, fullPathName;
        backupID = parser.nextToken();
        if(!checkForMoreTokens(parser))
            return true;
        type=parser.nextToken();
        if(!checkForMoreTokens(parser))
            return true;
        checkSum = parser.nextToken();
        if(!checkForMoreTokens(parser))
            return true;
        owner = parser.nextToken();
        if(!checkForMoreTokens(parser))
            return true;
        group = parser.nextToken();
        if(!checkForMoreTokens(parser))
            return true;
        perms = parser.nextToken();
        if(!checkForMoreTokens(parser))
            return true;
        timestamp = parser.nextToken();
        if(!checkForMoreTokens(parser))
            return true;
        fullPathName = parser.nextToken();
        // TODO: Store to the DB on master and spawn the receiver thread.


        // TODO: after successful insert, spawn the receiver thread.
        StorageReceiverThread storageReceiverThread = new StorageReceiverThread(netIOHandler.getClientAddress());


        return true;
    }


    public StorageConfig getStorageConfig() {
        return storageConfig;
    }

    public void setStorageConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
    }


    private boolean proecessAuthCommand(StringTokenizer parser) throws IOException {
        if(!parser.hasMoreTokens()){
            netIOHandler.println("Err: Missing Required Argument");

            return true;

        }
        String subCommand = parser.nextToken();
        switch (subCommand.toLowerCase()) {
            case "check":
                if (isAdminSession()) {
                    netIOHandler.println("TRUE");
                } else {
                    netIOHandler.println("FALSE");
                }
                return true;
            case "password":
                if(!parser.hasMoreTokens()){
                    netIOHandler.println("Err: Missing Required Arguement");
                    return true;
                }
                String passwordIn = parser.nextToken();
                if(!authenticateAdmin(passwordIn)){
                    netIOHandler.println("Err: Invalid login");

                } else {
                    netIOHandler.println("OK");

                }

                return true;

            default:
                netIOHandler.println("Err: Unrecognized Sub Command.");

        }

        return true;


    }

    public boolean authenticateAdmin(String password) {

        // TODO: For now, admin pass can be the same as encryption key.
        String key = storageConfig.getEncryptionKey();
        if(password.equals(key)) {
            adminSessionStart = System.currentTimeMillis() / 1000L;
            adminSession = true;
            return true;
        }
        return false;
    }

    public boolean isAdminSession () {
        // Check to see if this connection has admin rights.
        // First check if admin session is true.
        if(adminSession) {
            //  check that adminSessionStart + adminSessionLength < now()
            int adminSessionLength = 60;
            if(adminSessionStart + adminSessionLength < System.currentTimeMillis() / 1000L ){
                adminSessionStart = 0;
                adminSession = false;
                return false;

            } else {
                // everything looks ok, so we have an admin session
                return true;
            }
        }
        return false;
    }

}