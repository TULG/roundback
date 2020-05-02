package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

public class Session extends RoundBackObject{


    private String uuid; // unique id in the db
    private String hid; // the id of the endpoint in the netendpoints table.
    private int start; // unix timestamp the session started
    private String userid; // the id of the user from Users table

    public Session(){
        this.table = "rb_sessions";
        this.db = new MasterDB(this.table);
    }
    
}