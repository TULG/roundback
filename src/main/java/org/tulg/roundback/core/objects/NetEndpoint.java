package org.tulg.roundback.core.objects;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.tulg.roundback.core.Logger;
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
    public String rbdbf_currentAuthToken; // The current randomly generated auth token. (A UUID)  

    public String rbdbf_backing_store; // if storage, what backing stores it supports
    public boolean rbdbf_enabled; // if this server is enabled or not
    public boolean rbdbf_online; // if this server is online or not
    public long rbdbf_hb_time; // the last time we recieved a heartbeat from this endpoint

    public NetEndpoint(){
        this.table = "netendpoints";
        this.db = new MasterDB(this.table);
        this.rbdbf_uuid = null;

    }

    public boolean registerEndpoint(String ipAddress, String hostname, int epType){
        // first let's see if we have a host by this IP and hostname
        HashMap<String, String>where = new HashMap<String,String>();
        where.put("rbdbf_ip", ipAddress);
        where.put("rbdbf_hostname", hostname);
        NetEndpoint dbRes = (NetEndpoint)this.db.getItem(where, this.getClass().getSimpleName());
        if(dbRes != null){
            Logger.warn("Attempt to re-register " + hostname + "(" + ipAddress + ")");
            return false;
        }
        // Let's set some defaults now
        this.rbdbf_backing_store = "";
        this.rbdbf_enabled = true;
        this.rbdbf_online = true;
        this.rbdbf_hb_time = Instant.now().getEpochSecond();
        this.rbdbf_hostname = hostname;
        this.rbdbf_ip = ipAddress;
        this.rbdbf_port = "";
        this.rbdbf_uuid = UUID.randomUUID().toString();
        this.rbdbf_currentAuthToken = UUID.randomUUID().toString();
        this.rbdbf_type = epType;

        // and save it to the db
        return this.db.insertIfNotExist(this);
    }

	public boolean getEndpointByIp(String hostAddress) {
        HashMap<String, String>where = new HashMap<String,String>();
        where.put("rbdbf_ip", hostAddress);
        NetEndpoint dbRes = (NetEndpoint)this.db.getItem(where, this.getClass().getSimpleName());
        if(dbRes != null){
            // we got a valid result, let's set our variables.
            this.rbdbf_backing_store = dbRes.rbdbf_backing_store;
            this.rbdbf_enabled = dbRes.rbdbf_enabled;
            this.rbdbf_hb_time = dbRes.rbdbf_hb_time;
            this.rbdbf_hostname = dbRes.rbdbf_hostname;
            this.rbdbf_ip = dbRes.rbdbf_ip;
            this.rbdbf_online = dbRes.rbdbf_online;
            this.rbdbf_port = dbRes.rbdbf_port;
            this.rbdbf_type = dbRes.rbdbf_type;
            this.rbdbf_uuid = dbRes.rbdbf_uuid;
            this.rbdbf_currentAuthToken = dbRes.rbdbf_currentAuthToken;
            dbRes = null;
            return true;
        } else {
            Logger.warn("Unable to find host " + hostAddress +" did you properly register it?");
        }
        return false;

    }
    
    public String processHeartBeat() {
        if(this.rbdbf_uuid == null){
            Logger.err("processHeartBeat() called on bad object. You need to load the object first.");
            return "";
        }
        if(this.rbdbf_hb_time > Instant.now().getEpochSecond()){
            Logger.err("Host "+this.rbdbf_hostname + " time sync problem in db.  Last heartbeat was in the future?");
            return "";
        }
        // TODO: Cycle the token, if needed.  Probably need a master cmdline opt to specify token life.
        return this.rbdbf_currentAuthToken;
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