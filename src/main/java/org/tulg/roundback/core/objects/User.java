package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

public class User extends RoundBackObject{

    private String uuid;
    private String name; 
    private String uname; 
    private String emai; 
    private int isAdmin;

    public User(){
        this.table = "rb_users";
        this.db = new MasterDB(this.table);
    }
    
}