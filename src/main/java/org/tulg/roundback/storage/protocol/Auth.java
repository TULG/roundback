package org.tulg.roundback.storage.protocol;

import org.tulg.roundback.core.StringTokenizer;
import org.tulg.roundback.storage.StorageProtocol;

/**
 * Class to handle 'auth' commands to the storage server.
 *
 * @author Jason Williams <jasonw@tulg.org>
 */
public class Auth {

  /**
   * parser for the auth command.
   *
   * @param  sp       the StorageProtocol object that called us
   * @param  parser   the parser for the incoming line of data
   * @return          true to keep the connection open, false to close it. (NOT an error indicator.)
   */
  static public boolean parse (StorageProtocol sp, StringTokenizer parser) {
    if(!sp.checkForMoreTokens()){
      return true;
    }
    String subCommand = parser.nextToken();
    sp.println("TODO: Implement to query master for session");
    /* switch (subCommand.toLowerCase()) {
        case "check":
            if (isAdminSession()) {
                sp.println("TRUE");
            } else {
                sp.println("FALSE");
            }
            return true;
        case "password":
            if(!parser.hasMoreTokens()){
                netIOHandler.println("Err: Missing Required Arguement");
                return true;
            }
            String passwordIn = parser.nextToken();
            if(!authenticateAdmin(passwordIn)){
                netIOHandler.println("Err: Invalid login");

            } else {
                netIOHandler.println("OK");

            }

            return true;

        default:
            netIOHandler.println("Err: Unrecognized Sub Command.");

    } */

    return true;
  }

  protected boolean authenticateClientSession(String sessId){
    // TODO: Authenticate client session with the master server.
    return false;
  }

}
