package org.tulg.roundback.storage.protocol;

import org.tulg.roundback.core.StringTokenizer;
import org.tulg.roundback.storage.StorageProtocol;
import org.tulg.roundback.storage.StorageReceiverThread;

/**
 * Class to handle 'store' commands to the storage server.
 *
 * @author Jason Williams <jasonw@tulg.org>
 */
public class Store {
  
  /**
   * parser for the store command.
   *
   * @param  sp       the StorageProtocol object that called us
   * @param  parser   the parser for the incoming line of data
   * @return          true to keep the connection open, false to close it. (NOT an error indicator.)
   */
  static public boolean parse (StorageProtocol sp, StringTokenizer parser) {
    if(!sp.checkForMoreTokens()) {
      return true;
    }
    // store command syntax:
    //      store <backupID> <file|directory> <checksum> <owner> <group> <perms> <timestamp> <full_path_name>
    String backupID, type, checkSum, owner, group, perms, timestamp, fullPathName;
    backupID = parser.nextToken();
    if(!sp.checkForMoreTokens())
        return true;
    type=parser.nextToken();
    if(!sp.checkForMoreTokens())
        return true;
    checkSum = parser.nextToken();
    if(!sp.checkForMoreTokens())
        return true;
    owner = parser.nextToken();
    if(!sp.checkForMoreTokens())
        return true;
    group = parser.nextToken();
    if(!sp.checkForMoreTokens())
        return true;
    perms = parser.nextToken();
    if(!sp.checkForMoreTokens())
        return true;
    timestamp = parser.nextToken();
    if(!sp.checkForMoreTokens())
        return true;
    fullPathName = parser.nextToken();
    // TODO: Store to the DB on master and spawn the receiver thread.


    // TODO: after successful insert, spawn the receiver thread.
    //StorageReceiverThread storageReceiverThread = new StorageReceiverThread(sp.getClientAddress());
    sp.println("TODO: Implement store command");

    return true;
  }
}
