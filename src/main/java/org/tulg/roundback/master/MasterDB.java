package org.tulg.roundback.master;

import org.sqlite.JDBC;
import org.tulg.roundback.core.BackupStatus;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

/**
 * Created by jasonw on 10/9/2016.
 *
 * MasterDB - interface to the main database.
 *
 */
class MasterDB extends JDBC {
    private Connection dbConn;
    private Statement stmt;
    private boolean isOpen;
    private final String dbPath;

    public MasterDB (){
        isOpen=false;
        dbPath=System.getProperty("user.home") + File.separator + ".roundback" + File.separator + "RoundBack.db";
        if (!Files.isDirectory(Paths.get(System.getProperty("user.home") + File.separator + ".roundback"))) {
            if(!Files.exists(Paths.get(System.getProperty("user.home") + File.separator + ".roundback" ))){
                // no directory, try to create it.
                File dataDir = new File(System.getProperty("user.home") + File.separator + ".roundback");
                dataDir.mkdir();
            } else {
                System.err.println("Error: Unable to create database directory: " +
                        System.getProperty("user.home") + File.separator + ".roundback");
                System.exit(4);
            }
        }



    }

    public boolean open(){
        try {
            dbConn = DriverManager.getConnection("jdbc:sqlite:"+dbPath);
        } catch (SQLException e) {
            e.printStackTrace();
            isOpen = false;
            dbConn=null;
            return false;
        }
        isOpen = true;
        System.out.println("Opened database: " + dbPath);
        return true;
    }

    public void close(){
        if(dbConn!=null){
            try {
                dbConn.close();
            } catch (SQLException e) {
                // ignore sql exception on close.
            }
            dbConn=null;
            isOpen=false;
        }

    }

    public ResultSet getHosts(){
        if(!isOpen){
            open();
        }
        ResultSet rs=null;
        try {
            stmt = dbConn.createStatement();
            rs = stmt.executeQuery("SELECT id,hostname FROM hosts;");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;

    }

    public boolean createBackupSession(String backupUUID, int hostID) {
        if(!isOpen) {
            open();
        }
        try {
             long startTime = System.currentTimeMillis() / 1000L;
             String sql = "INSERT INTO backups (uuid, status, start_time, hid) VALUES('" +
                     backupUUID + "', '0', '" +
                     startTime + "', '" + hostID + "')";
             System.out.println(sql);
            stmt = dbConn.createStatement();
            stmt.executeUpdate(sql);

             // TODO: make sure the entry was created.

        } catch (SQLException e ) {
            e.printStackTrace();
        }
        return true;
    }

    public int getHostId(String remoteHost) {
        if(!isOpen) {
            open();
        }
        ResultSet rs;
        try {
            stmt = dbConn.createStatement();
            rs = stmt.executeQuery("SELECT id FROM hosts WHERE hostname='" + remoteHost + "';");
            if(rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public ResultSet getBackups() {
        if(!isOpen){
            open();
        }
        ResultSet rs=null;
        try {
            stmt = dbConn.createStatement();
            rs = stmt.executeQuery("SELECT uuid,hid,status,start_time FROM backups;");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;


    }

    public ResultSet getBackups(int hid) {
        if(!isOpen){
            open();
        }
        ResultSet rs=null;
        try {
            stmt = dbConn.createStatement();
            rs = stmt.executeQuery("SELECT uuid,hid,status,start_time FROM backups WHERE hid=" + hid + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;


    }

    public int getBackupStatus(String backupID) {
        if(!isOpen){
            open();
        }
        ResultSet rs;
        try {
            stmt = dbConn.createStatement();
            rs = stmt.executeQuery("SELECT status FROM backups WHERE uuid='" + backupID + "';");
            if(rs!=null) {
                return rs.getInt("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return -1;
    }

    public void setBackupStatus(String backupID, String status) {

        if(!isOpen) {
            open();
        }
        try {
            String sql = "UPDATE backups SET status='" + status + "' WHERE uuid='" + backupID + "';";
            System.out.println(sql);
            stmt = dbConn.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e ) {
            e.printStackTrace();
        }

    }

    public void purgeOldBackups(int hid) {

        // set all old backups to status ERROR

        int status = BackupStatus.toInt(BackupStatus.Status.ERROR);
        if(!isOpen) {
            open();
        }
        try {
            String sql = "UPDATE backups SET status='" + status + "' WHERE hid='" + hid + "';";
            System.out.println(sql);
            stmt = dbConn.createStatement();
            stmt.executeUpdate(sql);


        } catch (SQLException e ) {
            e.printStackTrace();
        }
    }

    public void deleteBackup(String backupId) {
        if(!isOpen) {
            open();
        }
        try {
            String sql = "DELETE FROM backups WHERE uuid='" + backupId + "';";
            System.out.println(sql);
            stmt = dbConn.createStatement();
            stmt.executeUpdate(sql);


        } catch (SQLException e ) {
            e.printStackTrace();
        }
    }
}
