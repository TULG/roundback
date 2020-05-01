package org.tulg.roundback.master;

import org.sqlite.JDBC;
//import org.tulg.roundback.core.BackupStatus;
import org.tulg.roundback.core.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jasonw on 10/9/2016.
 *
 * MasterDB - interface to the main database.
 *
 */
public class MasterDB extends JDBC {
    private Connection dbConn;
    private Statement stmt;
    private boolean isOpen;
    private final String dbPath;
    private final String table;

    public MasterDB(String table) {
        this.table = table;
        isOpen = false;
        dbPath = System.getProperty("user.home") + File.separator + ".roundback" + File.separator + "RoundBack.db";
        if (!Files.isDirectory(Paths.get(System.getProperty("user.home") + File.separator + ".roundback"))) {
            if (!Files.exists(Paths.get(System.getProperty("user.home") + File.separator + ".roundback"))) {
                // no directory, try to create it.
                File dataDir = new File(System.getProperty("user.home") + File.separator + ".roundback");
                dataDir.mkdir();
            } else {
                Logger.log(Logger.LOG_LEVEL_CRITICAL, "Unable to create database directory: "
                        + System.getProperty("user.home") + File.separator + ".roundback");
                System.exit(4);
            }
        }

    }
    /**
     * Open the database for access
     *
     * @return    true on success, false on failure.
     */
    public boolean open() {
        try {
            dbConn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (SQLException e) {
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e.getStackTrace().toString());
            isOpen = false;
            dbConn = null;
            return false;
        }
        isOpen = true;
        Logger.log(Logger.LOG_LEVEL_INFO, "Opened database: " + dbPath);
        return true;
    }

    /**
     * Close the database.
     *
     */
    public void close() {
        if (dbConn != null) {
            try {
                dbConn.close();
            } catch (SQLException e) {
                // ignore sql exception on close.
            }
            dbConn = null;
            isOpen = false;
        }

    }

    /**
     * Check if a table already exists in the database.
     *
     * @param  table    The table to look for.
     * @return          True if it is there, false if not.
     */
    public boolean tableExists(String table) {
        if (!isOpen) {
            open();
        }

        try {
            DatabaseMetaData md = dbConn.getMetaData();
            ResultSet rs = md.getTables(null, null, table, null);
            rs.next(); // should be only one
            return rs.getRow() > 0;
        } catch (SQLException e) {
            Logger.log(Logger.LOG_LEVEL_DEBUG, e.getMessage());
        }

        return false;
    }

    /**
     * Runs a query on the server, should return a result set, so 
     * best to use for SELECT type queries.
     *
     * @param    sqlString  the query to run
     * @return              ResultSet for the query run
     */
    public ResultSet query(String sqlString) {

        return null;
    }
    /**
     * Runs an update on the table.
     * <p>
     * data arg is a HashMap of HashMap<String, String>
     * where the first string is the field, and the second string is the value
     * <p>
     * where arg is also a HashMap of HashMap<String, String>
     * where the first string is the field, and the second string is the value
     *
     * @param   data    the fields to update and their values.
     * @param   where   the fields to use in a where clause 
     */
    public void update(HashMap<String, String> data, HashMap<String, String> where) {

    }

    /**
     * Runs a delete on the table.
     * <p>
     * where arg is also a HashMap of HashMap<String, String>
     * where the first string is the field, and the second string is the value
     *
     * @param   where   the fields to use in a where clause 
     */
    public void delete(HashMap<String, String> where) {

        if(where.size()<1) {
            Logger.log(Logger.LOG_LEVEL_WARN, "You passed an empty where to the DB Delete. You probably don't want");
            Logger.log(Logger.LOG_LEVEL_WARN, "to empty the table, so I didn't run the query.  If you did want to");
            Logger.log(Logger.LOG_LEVEL_WARN, "empty the table, you should call deleteAll() instead.");
            return;
        }
    }

    /**
     * DANGER: Delete ALL entries in the table, you probably don't want this.
     * 
     */
    public void deleteAll() {

    }

    /**
     * Create a table in the database, takes a HashMap as the arg,
     * should be typed to HashMap<String, String> where the first
     * string is the field name and the second string is the 
     * field type.
     *
     * @param  fields       The HashMap mentioned above for the fields in the db
     * @return              true on success and false on failure.
     */
    public boolean createTable(HashMap<String, String> fields) {
        String sql = "CREATE TABLE " + this.table + " ( ";
        for (Map.Entry mapElement : fields.entrySet()) {
            String key = (String) mapElement.getKey();
            String value = (String) mapElement.getValue();

            sql = sql + key + " " + value + ", ";

        }

        sql = sql.substring(0, sql.length()-2) + ");";

        Statement statement;
        try {
            statement = dbConn.createStatement();
            return statement.execute(sql);
        } catch (SQLException e) {
            Logger.log(Logger.LOG_LEVEL_CRITICAL, "There was an error creating the table, exeption message follows:");
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e.getMessage());
            return false;
        }
        
    }
/*
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
    }*/
}
