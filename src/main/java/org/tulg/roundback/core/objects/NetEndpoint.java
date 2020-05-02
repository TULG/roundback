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

    protected String uuid; // unique identifier for the entry
    protected String hostname ; // the FQDN of this enpoint.
    protected String ip; // the address of this endopoint.
    protected String port; // the port this server runs on
    protected int type; // what type of endpoint is this  

    protected String backing_store; // if storage, what backing stores it supports
    protected boolean enabled; // if this server is enabled or not
    protected boolean online; // if this server is online or not
    protected int hb_time; // the last time we recieved a heartbeat from this endpoint

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