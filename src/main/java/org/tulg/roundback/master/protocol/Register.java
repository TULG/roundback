package org.tulg.roundback.master.protocol;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.StringTokenizer;
import org.tulg.roundback.core.objects.NetEndpoint;
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
            parser.last();
            return true;
        }
        if(!parser.hasMoreTokens()) {
            mp.println("Err: Missing Required argument");
            parser.last();
            return true;
        }

        String subCommand = parser.nextToken();
        if(!parser.hasMoreTokens()) {
            mp.println("Err: Missing Required Argument");
            return true;
        }
        
        String regHost = parser.nextToken();
        NetEndpoint newEndpoint = new NetEndpoint();
        switch (subCommand.toLowerCase()) {
            case "server":
                // server should always be a storage server
                if(newEndpoint.registerEndpoint(mp.getClientAddress(), regHost, NetEndpoint.STORAGE)){
                    mp.println("OK");
                    return true;
                } else {
                    Logger.err("Unable to regisgter host " + regHost);
                    mp.println("Err: Unable to register.");
                }

                break;
            case "client":
                // register a client
                if(newEndpoint.registerEndpoint(mp.getClientAddress(), regHost, NetEndpoint.CLIENT)){
                    mp.println("OK");
                    return true;
                } else {
                    Logger.err("Unable to regisgter host " + regHost);
                    mp.println("Err: Unable to register.");
                }

                break;
            default:
                mp.println("Err: Unknown register type.");
        }
        return true;
    }

}