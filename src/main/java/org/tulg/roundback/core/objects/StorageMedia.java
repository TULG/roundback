package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

public class StorageMedia extends RoundBackObject{

    public static int MEDIA_RBIMAGE=0;
    public static int MEDIA_TAPE1;

    private String uuid; // unique identifier for the entry
    private String label; // name of the piece of media
    private int type; // type of media
    private int size; // size of the media
    private int used; // amount of used space on the media

    public StorageMedia(){
        this.table = "storage_media";
        this.db = new MasterDB(this.table);
    }

    
}