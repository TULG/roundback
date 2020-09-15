package org.tulg.roundback.core.objects;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

public class Session extends RoundBackObject{



    public String rbdbf_uuid; // unique id in the db
    public String rbdbf_hid; // the id of the endpoint in the netendpoints table.
    public long rbdbf_start; // unix timestamp the session started
    public String rbdbf_userid; // the id of the user from Users table

    private int timeout = 120; // timeout in seconds


    public Session(){
        this.table = "rb_sessions";
        this.db = new MasterDB(this.table);
    }

    public void gcSessions(){

        // perform garbage collection on Sessions.
        long timeout = Instant.now().getEpochSecond() - this.timeout;
        String where = "rbdbf_start <  " + String.valueOf(Instant.now().getEpochSecond() - this.timeout);
        this.db.delete(where);


    }
    public void heartbeat(){
        gcSessions();
        this.rbdbf_start = Instant.now().toEpochMilli();
        this.save();
    }
    public void upateUserSession(String username){
        gcSessions();
    }

    public boolean save(){
        db.update(
                "rbdbf_hid='" + this.rbdbf_hid + "', " +
                "rbdbf_start='" + this.rbdbf_start + "', " +
                "rbdbf_userid='" + this.rbdbf_userid + "' "
                ,
                "rbdbf_uuid='" + this.getRbdbf_uuid() + "'");
            
        return true;
    }
    public boolean checkSession(String sId){
        gcSessions();

        HashMap<String, String> where = new HashMap<String, String>();
        where.put("rbdbf_uuid", sId);
        Session chkSession = (Session)db.getItem(where, "Session");
        if(chkSession != null) {
            if(chkSession.rbdbf_uuid.equals(sId)) {
                return true;
            }
        }
        return false;
    }

    public void deleteSession(String sId) {
        db.delete("rbdbf_uuid = '" + sId + "'");
    }

    public void destroy() {
        if(this.rbdbf_uuid != ""){
            this.deleteSession(this.rbdbf_uuid);
        }
    }

	public String createSession(String hid) {
        gcSessions();
        if(hid == null ){
            // this is a temporary session, perhaps.
            UUID hUuid = UUID.randomUUID();
            HashMap<String, String> where = new HashMap<String, String>();
            where.put("rbdbf_uuid", hUuid.toString());

            Session sessionCheck = (Session)db.getItem(where, "Session");
            if(sessionCheck == null){
                // no existing session, which is expected here, so let's make one.
                this.rbdbf_uuid = hUuid.toString();
                this.rbdbf_hid = hUuid.toString();
                this.rbdbf_start = Instant.now().getEpochSecond();
                this.rbdbf_userid = "";
                db.insertIfNotExist(this);
                return this.rbdbf_uuid;  
            } else {
                Logger.log(Logger.LOG_LEVEL_CRITICAL, "Error: Session UUID collision");
                return null;
            }

        }
		return null;
	}

    public String getRbdbf_uuid() {
        return this.rbdbf_uuid;
    }

    public void setRbdbf_uuid(String rbdbf_uuid) {
        this.rbdbf_uuid = rbdbf_uuid;
    }

    public String getRbdbf_hid() {
        return this.rbdbf_hid;
    }

    public void setRbdbf_hid(String rbdbf_hid) {
        this.rbdbf_hid = rbdbf_hid;
    }

    public long getRbdbf_start() {
        return this.rbdbf_start;
    }

    public void setRbdbf_start(long rbdbf_start) {
        this.rbdbf_start = rbdbf_start;
    }

    public String getRbdbf_userid() {
        return this.rbdbf_userid;
    }

    public void setRbdbf_userid(String rbdbf_userid) {
        this.rbdbf_userid = rbdbf_userid;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

	public boolean checkSession() {
		return checkSession(this.rbdbf_uuid);
	}
    
}