package org.tulg.roundback.master.protocol;

import java.util.StringTokenizer;

import org.tulg.roundback.master.MasterProtocol;

/**
 * Handles registering clients and storage servers.
 * @author Jason Williams <jasonw@tulg.org>
 *
 * 
 */
public class Register {

    /**
     * parser for the register command.
     *
     * @param  mp       the MasterProtocol object that called us
     * @param  parser   the parser for the incoming line of data
     * @return          true to keep the connection open, false to close it. (NOT an error indicator.)
     */
    static public boolean parse (MasterProtocol mp, StringTokenizer parser) {
        if(!mp.isAdminSession()) {
            mp.println("Err: NOAUTH: Authentication Required");
            return true;
        }
        if(!parser.hasMoreTokens()) {
            mp.println("Err: Missing Required argument");
            return true;
        }

        String subCommand = parser.nextToken();
        if(!parser.hasMoreTokens()) {
            mp.println("Err: Missing Required Argument");
            return true;
        }
        String regHost = parser.nextToken();
        switch (subCommand.toLowerCase()) {
            case "server":
                // server should always be a storage server
                mp.println("TODO: register remote as server: " + regHost);
                break;
            case "client":
                // register a client
                mp.println("TODO: register remote as client: " + regHost);
                break;
            default:
                mp.println("Err: Unknown register type.");
        }
        return true;
    }

}