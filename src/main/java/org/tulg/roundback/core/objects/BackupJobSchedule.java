package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBack;
import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;
/**
 * Handles storage and retrieval of BackupJobSchedule objects
 * in the db and/or via a connection to a master server.
 *
 * @see RoundBackObject
 * @author Jason Williams <jasonw@tulg.org>
 */
public class BackupJobSchedule extends RoundBackObject{

    public static int SIMPLE_HOURLY=0;
    public static int SIMPLE_DAILY=1;
    public static int SIMPLE_WEEKLY=2;
    public static int SIMPLE_MONTHLY=3;

    public static int TYPE_FULL=0;
    public static int TYPE_INC=1;

   
    // these fields will automagically be added to the db when the table is created.
    // see RoundBackObject class.
    private String rbdbf_uuid; // unique identifier
    private String rbdbf_startEveryMinute; // future: cron type scheduling
    private String rbdbf_startEveryHour; // cron type scheduling, or simple scheduling hour, defaults to 2am
    private String rbdbf_startEveryDay; // future: cron type scheduling, day of the month
    private String rbdbf_startEveryMonth; // future: cron type scheduling, month of the year 1:jan through 12:dec
    private String rbdbf_startEveryWeekday; // future: cron type scheduling, day of the week.
    private int rbdbf_startEvery; // simple scheduling, from enum
       
    private int rbdbf_nextRun; // the server's best guess when next to run this BackupJobSchedule, unix timestamp
    private int rbdbf_backupType; // the type of backup to run
    private String rbdbf_hid; // the host id of the client to run for.

    
    public BackupJobSchedule(){
        rbdbf_uuid="";
        rbdbf_startEveryMinute="";
        rbdbf_startEveryHour="02:00";
        rbdbf_startEveryDay="";
        rbdbf_startEveryMonth="";
        rbdbf_startEveryWeekday = "";
        rbdbf_startEvery=BackupJobSchedule.SIMPLE_DAILY;
        this.table = "backup_job_schedule";
        this.db = new MasterDB(this.table);

    }


 


}