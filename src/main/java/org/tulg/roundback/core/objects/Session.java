package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

public class Session extends RoundBackObject{


    private String rbdbf_uuid; // unique id in the db
    private String rbdbf_hid; // the id of the endpoint in the netendpoints table.
    private int rbdbf_start; // unix timestamp the session started
    private String rbdbf_userid; // the id of the user from Users table

    public Session(){
        this.table = "rb_sessions";
        this.db = new MasterDB(this.table);
    }
    
}