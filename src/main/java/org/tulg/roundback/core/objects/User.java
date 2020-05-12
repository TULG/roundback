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

        return true;
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
    }

    public int isAdmin() {
        return this.rbdbf_isAdmin;
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


}