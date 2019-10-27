package org.tulg.roundback.master;

import org.tulg.roundback.core.BackupStatus;
import org.tulg.roundback.core.Encrypter;
import org.tulg.roundback.core.NetIOHandler;
import org.tulg.roundback.core.RoundBackConfig;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by jasonw on 10/9/2016.
 *
 * MasterProtocol - Used to process the incoming/outgoing data
 *      to/from the master server.
 */
class MasterProtocol {

    private final MasterDB db;
    private NetIOHandler netIOHandler = null;
    private boolean adminSession = false;
    private long adminSessionStart = 0;
    private RoundBackConfig rBackConfig;


    public MasterProtocol (NetIOHandler netIOHandler){

        db = new MasterDB();
        db.open();
        db.close();
        this.netIOHandler = netIOHandler;

    }

    public boolean process(String inputLine) throws IOException {

        if(inputLine.compareTo("") == 0){
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
                closeConnection(db);
                return false;

            case "list":
                return processListCommand(parser, db);
            case "backup":
                return processBackupCommand(parser, db);
            case "test" :
                return processTestCommand(parser, db);
            case "register":
                return processRegisterCommand(parser, db);
            case "auth":
                return proecessAuthCommand(parser, db);
            // XXX: Add new commands here.
            default:
                netIOHandler.println("ERR: Unsupported Command");
                closeConnection(db);
        }
        return true;
    }

    private boolean proecessAuthCommand(StringTokenizer parser, MasterDB db) throws IOException {
        if(!parser.hasMoreTokens()){
            netIOHandler.println("Err: Missing Required Argument");
            closeConnection(db);
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
                    closeConnection(db);
                    return true;
                }
                String passwordIn = parser.nextToken();
                if(!authenticateAdmin(passwordIn)){
                    netIOHandler.println("Err: Invalid login");

                } else {
                    netIOHandler.println("OK");

                }
                closeConnection(db);
                return true;

            default:
                netIOHandler.println("Err: Unrecognized Sub Command.");

        }

        return true;


    }

    private boolean processRegisterCommand(StringTokenizer parser, MasterDB db) throws IOException {
        if(!isAdminSession()) {
            netIOHandler.println("NOAUTH: Err: Authentication Required");
            closeConnection(db);
            return true;
        }
        if(!parser.hasMoreTokens()) {
            netIOHandler.println("Err: Missing Required argument");
            closeConnection(db);
            return true;
        }

        String subCommand = parser.nextToken();
        if(!parser.hasMoreTokens()) {
            netIOHandler.println("Err: Missing Required Argument");
            closeConnection(db);
            return true;
        }
        String regHost = parser.nextToken();
        switch (subCommand.toLowerCase()) {
            case "server":
                // server should always be a storage server
                netIOHandler.println("TODO: register remote as server" + regHost);
                // TODO: Storage Server Register
                break;
            case "client":
                // register a client
                netIOHandler.println("TODO: register remote as client" + regHost);
                // TODO: client register
                break;
            default:
                netIOHandler.println("Err: Unknown register type for host: " + regHost);
        }
        closeConnection(db);
        return true;
    }

    private boolean processTestCommand(StringTokenizer parser, MasterDB db) throws IOException {
        if(!parser.hasMoreTokens()){
            netIOHandler.println("OK");
            closeConnection(db);
            return true;
        }
        String subCommand = parser.nextToken();
        switch(subCommand.toLowerCase()) {
            case "encrypt":
                if(parser.hasMoreTokens()) {
                    String stringToEncrypt = parser.nextToken("").substring(1);
                    netIOHandler.println(Encrypter.encrypt(netIOHandler.getEncryptionKey(), Encrypter.getIVBytes(), stringToEncrypt));
                }else {
                    netIOHandler.println("ERR: Missing argument");
                }
                break;
            case "decrypt":
                if(parser.hasMoreTokens()){
                    String encString = parser.nextToken();
                    netIOHandler.println(Encrypter.decrypt(netIOHandler.getEncryptionKey(),encString));

                }else {
                    netIOHandler.println("ERR: Missing argument");
                }
                break;
            default:
                netIOHandler.println("ERR: Unrecognized subcommand");
        }
        closeConnection(db);
        return true;
    }

    private boolean processBackupCommand(StringTokenizer parser, MasterDB db) throws IOException {
        if(!parser.hasMoreTokens()) {
            netIOHandler.println("ERR: Protcol Error. Sub Command Required.");
            closeConnection(db);
            return true;
        }
        String subCommand = parser.nextToken();
        //  backup delete <backupid> commanad
        switch (subCommand.toLowerCase()) {
            case "delete":
                if(parser.hasMoreTokens()) {
                    String backupId = parser.nextToken();
                    db.deleteBackup(backupId);
                } else {
                    netIOHandler.println("ERR: Missing required argument.");
                }
                break;
            case "request":
                if(parser.hasMoreTokens()) {
                    String remoteHost = parser.nextToken();
                    int hostId = db.getHostId(remoteHost);

                    // check for any backups that are started or running and
                    // set them to error since the client is trying to start a new backup,
                    // assume previously incomplete backups died erroneously
                    db.purgeOldBackups(hostId);

                    //  generate a new backup session
                    String backupID = UUID.randomUUID().toString();
                    db.createBackupSession(backupID, hostId);
                    netIOHandler.println("backup id " + backupID + " server localhost:2378");
                } else {
                    netIOHandler.println("ERR: Missing required arguement.");
                }
                break;
            case "verify":
                if(parser.hasMoreTokens()) {
                    String backupId = parser.nextToken();
                    // verify the backup id.
                    if (BackupStatus.fromInt(db.getBackupStatus(backupId)) == BackupStatus.Status.PENDING) {

                        // set the backup id status to 'started"
                        db.setBackupStatus(backupId, BackupStatus.toString(BackupStatus.Status.STARTED));

                        netIOHandler.println("backup verified");
                    } else {
                        netIOHandler.println("backup invalid");
                    }
                } else {
                    netIOHandler.println("ERR: Missing required arguement.");
                }
                break;
            case "status":
                if(parser.hasMoreTokens()) {
                    String backupID = parser.nextToken();
                    if (parser.hasMoreTokens()) {
                        String argVal = parser.nextToken();
                        if (argVal.compareTo("set") == 0) {
                            if(parser.hasMoreTokens()) {
                                // we are going to set the status
                                db.setBackupStatus(backupID, parser.nextToken());
                            } else {
                                netIOHandler.println("ERR: Missing value to argument.");
                            }
                        } else {
                            netIOHandler.println("ERR: Unsupported argument.");
                        }
                    } else {
                        //  get the status
                        int status = db.getBackupStatus(backupID);
                        netIOHandler.println("backup id " + backupID + " status " + BackupStatus.toString(BackupStatus.fromInt(status)));
                    }
                } else {
                    netIOHandler.println("ERR: Missing required arguement.");
                }
                break;
            default:
                netIOHandler.println("ERR: Unsupported Subcommand: " + subCommand);
        }
        closeConnection(db);
        return true;
    }

    private boolean processListCommand(StringTokenizer parser,  MasterDB db) throws IOException {
        if(!parser.hasMoreTokens()) {
            netIOHandler.println("ERR: Missing subcommand");
        } else {
            String subCommand = parser.nextToken();
            try {
                ResultSet rs = null;
                switch (subCommand.toLowerCase()) {
                    case "hosts":
                        System.out.println("Sending host list to " +netIOHandler.getClientAddress());

                        // Get a list of all the hosts available.
                        rs = db.getHosts();
                        while (rs.next()) {
                            netIOHandler.println("host " + rs.getInt("id") + " " + rs.getString("hostname"));
                        }
                        break;
                    case "backups":
                        // list backups hid <hid>
                        if (parser.hasMoreTokens()) {
                            String arg1 = parser.nextToken();
                            if (arg1.compareTo("hid") == 0) {
                                if (parser.hasMoreTokens()) {
                                    String hidStr = parser.nextToken();
                                    int hid = Integer.valueOf(hidStr);
                                    rs = db.getBackups(hid);
                                } else {
                                    netIOHandler.println("ERR: missing value to argument.");
                                }

                            } else {
                                netIOHandler.println("ERR: Unrecognized argument to subcommand");
                            }
                        } else {
                            // list all backups
                            rs = db.getBackups();
                        }

                        // if no results, send error.
                        if (rs != null) {
                            if (rs.isClosed()) {
                                netIOHandler.println("ERR: No data");
                            }
                            while (rs.next()) {

                                netIOHandler.println("backup: " + rs.getString("uuid") +
                                        " start_time: " + rs.getInt("start_time") +
                                        " hostid: " + rs.getInt("hid") +
                                        " status: " + BackupStatus.toString(BackupStatus.fromInt(rs.getInt("status"))));

                            }
                        } else {
                            netIOHandler.println("ERR: No data");
                        }
                        break;
                    // XXX: add new list sub commands here.
                    default:
                        netIOHandler.println("ERR: Unsupported subcommand: " + subCommand );

                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        closeConnection(db);
        return true;
    }

    private void closeConnection(MasterDB db) {
        netIOHandler.flush();
        db.close();
    }

    public boolean authenticateAdmin(String password) {

        // TODO: For now, admin pass can be the same as encryption key.
        String key = rBackConfig.getEncryptionKey();
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

    public RoundBackConfig getRoundBackConfig() {
        return rBackConfig;
    }

    public void setRoundBackConfig(RoundBackConfig rBackConfig) {
        this.rBackConfig = rBackConfig;
    }
}
