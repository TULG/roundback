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
    public String rbdbf_uuid; // unique identifier for the entry
    public String rbdbf_jobid; // what job this item came from
    public String rbdbf_path; // what was the original full path
    public int rbdbf_type; // item type 
    public int rbdbf_dev_major; // if a device, it's major num
    public int rbdbf_dev_minor; // if a device, it's minor num
    public String rbdbf_link_dest; // if a link, it's target file or inode number
    public int rbdbf_file_mode; // linux/unix mode permissions
    public int rbdbf_user_id; // linux/unix owner
    public int rbdbf_group_id; // linux/unix group
    public String rbdbf_media_id; // what media is this file kept on 
    public int rbdbf_media_start_block; // where is the start block for this file
    public int rbdbf_media_end_block; // where is the end block of this file
    public String rbdbf_media_server; // id of what server was this file stored on
    public int rbdbf_size; // the file's original size.
    public String rbdbf_crc; // a quick crc for the file, performed on the client before sending.
    public int rbdbf_mtime; // future: mod time
    public int rbdbf_atime; // future: if enabled, access time
    public int rbdbf_ctime; // future: if enabled, create time

    public BackupItem () {
        this.table = "backup_items";
        this.db = new MasterDB(this.table);
    }
    
}