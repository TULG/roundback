package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;
import org.tulg.roundback.master.MasterDB;

/**
 * Handles storage and retrieval of BackupItem objects
 * in the db and/or via a connection to a master server.
 *
 * @see RoundBackObject
 * @author Jason Williams <jasonw@tulg.org>
 */
public class BackupItem extends RoundBackObject{
    public static int SYMLINK_SOFT=0;
    public static int SYMLINK_HARD=1;
    public static int DIRECTORY=2;
    public static int DEVICE=3;
    public static int FILE=4;

    // these fields will automagically be added to the db when the table is created.
    // see RoundBackObject class.
    private String uuid; // unique identifier for the entry
    private String jobid; // what job this item came from
    private String path; // what was the original full path
    private int type; // item type 
    private int dev_major; // if a device, it's major num
    private int dev_minor; // if a device, it's minor num
    private String link_dest; // if a link, it's target file or inode number
    private int file_mode; // linux/unix mode permissions
    private int user_id; // linux/unix owner
    private int group_id; // linux/unix group
    private String media_id; // what media is this file kept on 
    private int media_start_block; // where is the start block for this file
    private int media_end_block; // where is the end block of this file
    private String media_server; // id of what server was this file stored on
    private int size; // the file's original size.
    private String crc; // a quick crc for the file, performed on the client before sending.
    private int mtime; // future: mod time
    private int atime; // future: if enabled, access time
    private int ctime; // future: if enabled, create time

    public BackupItem () {
        this.table = "backup_items";
        this.db = new MasterDB(this.table);
    }
    
}