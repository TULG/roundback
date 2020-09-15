package org.tulg.roundback.master.protocol;

import org.tulg.roundback.core.StringTokenizer;
import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.objects.Session;
import org.tulg.roundback.master.MasterProtocol;

public class Sess {
        /**
     * parser for the register command.
     *
     * @param  mp       the MasterProtocol object that called us
     * @param  parser   the parser for the incoming line of data
     * @return          true to keep the connection open, false to close it. (NOT an error indicator.)
     */
    static public boolean parse (MasterProtocol mp, StringTokenizer parser) {
        if(!parser.hasMoreTokens()) {
            mp.println("Err: Missing Required Argument");
            return true;
        }
        String sId = parser.nextToken();
        /*if(!parser.hasMoreTokens()) {
            mp.println("Err: Missing Required Argument");
            return true;
        }*/
        
        Session session = new Session();
        session.setTimeout(mp.getRoundBackConfig().getSessionTimeout());
        if(!session.checkSession(sId)){
            session.createSession(null);
            mp.setSession(session);
            
            /*Logger.log(Logger.LOG_LEVEL_DEBUG, "Expired Session, " + sId);
            mp.println("ERR Session Expired, please reconnect");
            return false;*/
        }

        return true;
    }
}