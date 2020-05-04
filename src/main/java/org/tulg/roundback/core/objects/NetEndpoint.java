package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

/**
 * A base class for endpoints.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 * 
 */
public class NetEndpoint extends RoundBackObject {

    public static int CLIENT=0;
    public static int STORAGE=1;
    public static int MASTER=2;

    public String rbdbf_uuid; // unique identifier for the entry
    public String rbdbf_hostname ; // the FQDN of this enpoint.
    public String rbdbf_ip; // the address of this endopoint.
    public String rbdbf_port; // the port this server runs on
    public int rbdbf_type; // what type of endpoint is this  

    public String rbdbf_backing_store; // if storage, what backing stores it supports
    public boolean rbdbf_enabled; // if this server is enabled or not
    public boolean rbdbf_online; // if this server is online or not
    public int rbdbf_hb_time; // the last time we recieved a heartbeat from this endpoint

    public NetEndpoint(){
        this.table = "netendpoints";
        this.db = new MasterDB(this.table);
    }

    /*
    @Override
    public boolean initializeDB(){
        if(this.type == NetEndpoint.MASTER){
            // master objects don't do any db init;
            return true;
        }
        return super.initializeDB();
    }*/

}