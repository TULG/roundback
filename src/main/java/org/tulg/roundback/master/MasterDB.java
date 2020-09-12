package org.tulg.roundback.master;


import org.sqlite.JDBC;
import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.RoundBackObject;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @return true on success, false on failure.
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
        Logger.log(Logger.LOG_LEVEL_DEBUG, "Opened database: " + dbPath);
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
            Logger.log(Logger.LOG_LEVEL_DEBUG, "DB Closed.");
            dbConn = null;
            isOpen = false;
        }

    }

    /**
     * Check if a table already exists in the database.
     *
     * @param table The table to look for.
     * @return True if it is there, false if not.
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
            Logger.log(Logger.LOG_LEVEL_DEBUG, e);
        }

        return false;
    }

    /**
     * Runs a query on the server, should return a result set, so best to use for
     * SELECT type queries.
     *
     * @param sqlString the query to run
     * @return ResultSet for the query run
     */
    public ResultSet query(String sqlString) {
        if(!this.isOpen){
            this.open();
        }

        try{
            Statement statement = dbConn.createStatement();
            ResultSet result = statement.executeQuery(sqlString);
            return result;
        }catch(SQLException e){
            Logger.log(Logger.LOG_LEVEL_CRITICAL, "There was an error executing SQL, exeption message follows:");
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
            Logger.log(Logger.LOG_LEVEL_DEBUG, "SQL Statement: " + sqlString);
            this.close();
            return null;
        }
        
    }

    /**
     * Runs a query on the server, should return exactly one valid RoundBackObject
     * All params are required, errors if multiples are returned.
     *
     * @param where a Hash of key/value pairs to look for.
     * @param objClass the class of this object to map the returned data to
     * @return RoundBackObject representing the data returned
     */
    public RoundBackObject getItem(HashMap<String, String> where, String objClass) {
        if (where != null) {
            String sql = "SELECT * FROM " + this.table + " WHERE ";
            for (Map.Entry<String, String> entry : where.entrySet()) { // .getFieldsFiltered().entrySet()) {
                sql = sql + entry.getKey() + "='" + entry.getValue() + "', ";
            }
            sql = sql.substring(0, sql.length() - 2) + ";";
            ResultSet rs = this.query(sql);
            if(rs == null) {
                Logger.log(Logger.LOG_LEVEL_DEBUG, "Empty ResultSet in MasterDB.getItem()");
                return null;
            }
            // set a limit of 3 here, just in case we get multiples, we can detect it.
            List<HashMap<String, Object>> results = null;
            try {
                results = this.resultSetToArrayList(rs, 3);
                this.close();
            } catch (SQLException e) {
                Logger.log(Logger.LOG_LEVEL_DEBUG, "SQL: " + sql);
                Logger.log(Logger.LOG_LEVEL_CRITICAL, "SQL Error: ");
                Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
                return null;
            }
            if (results != null) {
                if (results.size() > 1) {
                    // multiple rows returned.
                    Logger.log(Logger.LOG_LEVEL_CRITICAL,
                            "Multiple rows returned, single row expected.  DB corruption is likely.");
                    System.exit(1);
                }
                if(results.size()<1){
                    return null;
                }
            }
            // if we get one row, let's convert it to a RoundBack object.
            Class<?> retClass;
            try {
                retClass = Class.forName("org.tulg.roundback.core.objects." + objClass);

                Constructor<?> retConst = retClass.getConstructor();
                RoundBackObject retObj = (RoundBackObject) retConst.newInstance((Object[]) null);
                for (Map.Entry<String, Object> entry : results.get(0).entrySet()) {
                    Field fSet = retObj.getClass().getField(entry.getKey());
                    if (fSet != null) {
                        fSet.set(retObj, entry.getValue());
                    }
                }
                // if we get here, we somehow successfully set all the values.
                return retObj;

            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | NoSuchFieldException e) {
                Logger.log(Logger.LOG_LEVEL_CRITICAL, "Error grabbing row to object");
                Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
                return null;
            }

        }
        return null;
    }

    /**
     * Returns a List<HashMap<String, Object>> representing a list of DB rows.
     * 
     *  *Note: This function can potentially use enormous amounts of memory!
     *
     * @param rs    the result set to processs.
     * @return      List<HashMap<String, Object>>, a list of hashes where they key is the column name.
     */
    private List<HashMap<String, Object>> resultSetToArrayList(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(50);
        while (rs.next()){
           HashMap<String, Object> row = new HashMap<String, Object>(columns);
           for(int i=1; i<=columns; ++i){           
            row.put(md.getColumnName(i),rs.getObject(i));
           }
            list.add(row);
        }
      
       return list;
      }

    /**
     * Returns a List<HashMap<String, Object>> representing a list of DB rows, but with a limit
     * 
     *  *Note: This function can potentially use enormous amounts of memory!
     *
     * @param rs    the result set to processs.
     * @param limit an upper limit to return, function stops at this upper limit.
     * @return      List<HashMap<String, Object>>, a list of hashes where they key is the column name.
     */
    private List<HashMap<String, Object>> resultSetToArrayList(ResultSet rs, int limit) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(50);
        int counter=0;
        while (rs.next()){
           HashMap<String, Object> row = new HashMap<String, Object>(columns);
           for(int i=1; i<=columns; ++i){           
            row.put(md.getColumnName(i),rs.getObject(i));
           }
            list.add(row);
            if(counter >= limit){
                return list;
            }
        }
      
       return list;
      }

    /**
     * Runs an update on the table.
     * <p>
     * data arg is a HashMap of HashMap<String, String> where the first string is
     * the field, and the second string is the value
     * <p>
     * where arg is also a HashMap of HashMap<String, String> where the first string
     * is the field, and the second string is the value
     *
     * @param data  the fields to update and their values.
     * @param where the fields to use in a where clause
     */
    public void update(HashMap<String, String> data, HashMap<String, String> where) {

    }

    public void update(String data, String where){
        String sql = "UPDATE " + this.table + " SET " + data + " WHERE " + where;
        this.execute(sql);
    }

    /**
     * Runs a delete on the table.
     * <p>
     * where arg is a string representation of the desired where
     *
     * @param where the fields to use in a where clause
     */
    public void delete(String where) {
        
        if (where.equals("") || where == null ) {
            Logger.log(Logger.LOG_LEVEL_WARN, "You passed an empty where to the DB Delete. You probably don't want");
            Logger.log(Logger.LOG_LEVEL_WARN, "to empty the table, so I didn't run the query.  If you did want to");
            Logger.log(Logger.LOG_LEVEL_WARN, "empty the table, you should call deleteAll() instead.");
            return;
        }
        String sql = "DELETE FROM " + this.table + " WHERE " + where + ";";
        this.execute(sql);
    }

    /**
     * DANGER: Delete ALL entries in the table, you probably don't want this.
     * 
     */
    public void deleteAll() {

    }

    /**
     * inserts a record into the database if it does not already exist.
     * 
     * @param fields
     * @return
     */
    public boolean insertIfNotExist(RoundBackObject dataObj) {
        String sql = "INSERT OR IGNORE INTO " + dataObj.getTable() + "(";
        String values = " VALUES (";
        /// INSERT OR IGNORE INTO bookmarks(users_id, lessoninfo_id) VALUES(123, 456)
        for (Map.Entry<String, String> entry : dataObj.getFieldsFiltered().entrySet()) {
            sql = sql + entry.getKey() + ", ";
            try {
                Class<?> dataObjClass = dataObj.getClass();
                Field dataField = dataObjClass.getField(entry.getKey());
                values = values + "'" + (String) dataField.get(dataObj).toString()
                        + "', ";
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                Logger.log(Logger.LOG_LEVEL_DEBUG, "SQL Statement: " + sql);
                Logger.log(Logger.LOG_LEVEL_CRITICAL, "Error processing data for db.");
                Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
                this.close();
                return false;
            }
        }
        values = values.substring(0, values.length()-2) + ");";
        sql = sql.substring(0, sql.length()-2) + ") " + values;
        boolean result = this.execute(sql);
        this.close();
        return result;

    }

    public boolean execute(String sql){
        if(!this.isOpen){
            this.open();
        }

        try{
            Statement statement = dbConn.createStatement();
            boolean result = statement.execute(sql);
            this.close();
            return result;
        }catch(SQLException e){
            Logger.log(Logger.LOG_LEVEL_CRITICAL, "There was an error executing SQL, exeption message follows:");
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
            Logger.log(Logger.LOG_LEVEL_DEBUG, "SQL Statement: " + sql);
            this.close();
            return false;
        }
        
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
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
            return false;
        }
        
    }

    public HashMap<String, String> getData(RoundBackObject dataObj){

        HashMap<String, String> data = dataObj.getFieldsFiltered();
        for (Map.Entry mapElement : data.entrySet()) {
            try {
                data.replace((String) mapElement.getKey(),
                        (String) dataObj.getClass().getDeclaredField((String) mapElement.getKey()).get(dataObj));
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                Logger.log(Logger.LOG_LEVEL_CRITICAL, "Error processing fields to database.");
                Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
                return null;
            }

        }
        if(data.size()<1){
            Logger.log(Logger.LOG_LEVEL_CRITICAL, "Error processing fields to database.");
            return null;
        }
        return data;
    }
}
