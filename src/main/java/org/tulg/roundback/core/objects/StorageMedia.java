package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

public class StorageMedia extends RoundBackObject{

    public static int MEDIA_RBIMAGE=0;
    public static int MEDIA_TAPE1;

    public String rbdbf_uuid; // unique identifier for the entry
    public String rbdbf_label; // name of the piece of media
    public int rbdbf_type; // type of media
    public int rbdbf_size; // size of the media
    public int rbdbf_used; // amount of used space on the media

    public StorageMedia(){
        this.table = "storage_media";
        this.db = new MasterDB(this.table);
    }

    
}