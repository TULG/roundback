package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

public class User extends RoundBackObject {

    public String rbdbf_uuid;
    public String rbdbf_name;
    public String rbdbf_uname;
    public String rbdbf_email;
    public int rbdbf_isAdmin;
    public String rbdbf_password;

    public boolean l_isAuthenticated;

    public User() {
        this.table = "rb_users";
        this.db = new MasterDB(this.table);
        this.l_isAuthenticated = false;
    }

    public User(String infoString) {
        // Args in order: username, password, email, isadmin(1|0), Full Name

        // parse this into an array.
        String[] userInfo = infoString.split(", ");
        this.table = "rb_users";
        this.db = new MasterDB(this.table);
        this.l_isAuthenticated = false;
        try{
            // see if this user exists.
            if(this.getUserByUsername(userInfo[0]) != null ){
                return;
            }
            this.rbdbf_uname = userInfo[0];
            this.rbdbf_password = BCrypt.hashpw(userInfo[1], BCrypt.gensalt(8)); // TODO: Make this salt rounds
            this.rbdbf_email = userInfo[2];
            this.rbdbf_isAdmin = Integer.parseInt(userInfo[3]);
            this.rbdbf_name = userInfo[4];
            this.rbdbf_uuid = UUID.randomUUID().toString();
            db.insertIfNotExist(this);
        }catch(Exception e){
            Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
        }
    }

	public boolean authenticateUser(String username, String password) {

        this.l_isAuthenticated = false;

        User tmpUser = this.getUserByUsername(username);

        if(tmpUser != null) {
            String passChk = tmpUser.getPassword();
            if(BCrypt.checkpw(password, passChk)){
                // authentication passed
                this.setEmail(tmpUser.getEmail());
                this.setName(tmpUser.getName());
                this.setUname(tmpUser.getUname());
                this.setUuid(tmpUser.getUuid());
                this.l_isAuthenticated=true;
                return true;
            }
        }

        return false;
    }

    public boolean updateUser(String password) {
        if (this.l_isAuthenticated) {
            if(this.getUuid() == null){
                return false;
            }
            db.update("rbdbf_password='" + BCrypt.hashpw(password, BCrypt.gensalt(8)) + "'", "rbdbf_uuid='" + this.getUuid() + "'");
            return true;
        }
        return false;
    }

    @Override
    public boolean initializeDB(){
        boolean constructorRes = super.initializeDB();
        if(!constructorRes){
            return false;
        }
        return createDefaultUser();
    }

    private boolean createDefaultUser() {

        String adminUUID = "f83dab72-8aa6-4219-ba68-4e936142f2dc";
        User tmpUser = this.getUserByUUID(adminUUID);
        if(tmpUser != null) {
            // user already exists, no need to recreate.
            Logger.log(Logger.LOG_LEVEL_DEBUG, "User admin already exists, not adding.");
            db.close();
            return true;
        }
        this.setUname("admin");
        this.setUuid(adminUUID);
        this.setName("Super User");
        this.setIsAdmin(1);
        this.setEmail("nobody@nowhere.com");
        this.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt(8))); // TODO: Make this salt rounds
                                                                                  // configurable

        return db.insertIfNotExist(this);
    }

    public User getUserByUUID(String UUID){
        HashMap<String, String> where = new HashMap<String, String>();
        where.put("rbdbf_uuid", UUID);
        User tmpUser = (User)db.getItem(where, "User");
        return tmpUser;
        
    }

    public User getUserByUsername(String userName){
        HashMap<String, String> where = new HashMap<String, String>();
        where.put("rbdbf_uname", userName);
        User tmpUser = (User)db.getItem(where, "User");

        return tmpUser;
    }

    public boolean saveUser(){
        if (this.l_isAuthenticated) {
            if(this.getUuid() == null){
                return false;
            }
            db.update(
                "rbdbf_email='" + this.rbdbf_email + "'" +
                "rbdbf_isadmin='" + this.rbdbf_isAdmin + "'" +
                "rbdbf_name='" + this.rbdbf_name + "'"
                ,
                "rbdbf_uuid='" + this.getUuid() + "'");
            
            return true;
        }
        return false;
    }

    public String getUuid() {
        return this.rbdbf_uuid;
    }

    public void setUuid(String uuid) {
        this.rbdbf_uuid = uuid;
    }

    public String getName() {
        return this.rbdbf_name;
    }

    public void setName(String name) {
        this.rbdbf_name = name;
        this.saveUser();
    }

    public String getUname() {
        return this.rbdbf_uname;
    }

    public void setUname(String uname) {
        this.rbdbf_uname = uname;
    }

    public String getEmail() {
        return this.rbdbf_email;
    }

    public void setEmail(String email) {
        this.rbdbf_email = email;
        this.saveUser();
    }

    public boolean isAdmin() {
        return (this.rbdbf_isAdmin == 1) ? true : false;
    }

    public void setIsAdmin(int isAdmin) {
        this.rbdbf_isAdmin = isAdmin;
    }

    public String getPassword() {
        return this.rbdbf_password;
    }

    public void setPassword(String password) {
        this.rbdbf_password = password;
    }

    public boolean isL_isAuthenticated() {
        return this.l_isAuthenticated;
    }

    public boolean getL_isAuthenticated() {
        return this.l_isAuthenticated;
    }

    public void setL_isAuthenticated(boolean l_isAuthenticated) {
        this.l_isAuthenticated = l_isAuthenticated;
    }

	public boolean deleteUser() {
        if(!this.rbdbf_uuid.equals("")){
            db.delete("rbdbf_uuid = '"+ this.rbdbf_uuid + "'");
            return true;
        }
        return false;
    }


}