package org.tulg.roundback.master.protocol;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.StringTokenizer;
import org.tulg.roundback.core.objects.User;
import org.tulg.roundback.master.MasterProtocol;

public class Admin {
    /**
     * parser for the admin command.
     *
     * @param  mp       the MasterProtocol object that called us
     * @param  parser   the parser for the incoming line of data
     * @return          true to keep the connection open, false to close it. (NOT an error indicator.)
     */
    static public boolean parse (MasterProtocol mp, StringTokenizer parser) {
        if(!parser.hasMoreTokens()){
            mp.println("Err: Missing Required Argument");
            return true;

        }
        if(mp.checkSession()){
            User checkUser = new User();
            checkUser = checkUser.getUserByUUID(mp.getSession().getRbdbf_userid());
            if(checkUser != null) {
                // got a valid user in our session.
                if(checkUser.isAdmin()==0){
                    // they are not an admin
                    mp.println("Err: Not Authorized");
                    mp.getSession().deleteSession(mp.getSession().getRbdbf_uuid());
                    Logger.crit("User " + checkUser.getUname() + " tried to access admin!");
                    return false; // kick the connection.
                }
            } else {
                // the user referenced by session doesn't exist anymore.  Kick 'em
                mp.println("Err: Not Authorized");
                mp.getSession().deleteSession(mp.getSession().getRbdbf_uuid());
                Logger.crit("Unauthenticated User tried to access admin!");
                return false; // kick the connection.
            }
        } else {
            // the session is expired.  Let them re-try
            mp.println("Err: Expired Session.");
            mp.getSession().deleteSession(mp.getSession().getRbdbf_uuid());
            return true;
        }
        String subCommand = parser.nextToken();
        String module = "";
        switch (subCommand.toLowerCase()) {
            case "create":
                if(!parser.hasMoreTokens()){
                    mp.println("Err: Missing Required Argument");
                    return true;
                }

                module = parser.nextToken();
                switch(module.toLowerCase()) {
                    case "user":
                        if(!parser.hasMoreTokens()){
                            mp.println("Err: Missing Required Argument");
                            return true;
                        }
                        // pass the rest of the string to the user object constructor and let it build the
                        // object.
                        // Args in order: username, password, email, isadmin(Y|N), Full Name
                        User newUser = new User(parser.fromCurrentToken());
                        parser.last();
                        if(newUser.getUserByUUID(newUser.getUuid())!=null){
                            Logger.log(Logger.LOG_LEVEL_INFO, "User " + newUser.getUname() + " created.");
                            mp.println("OK");
                        } else {
                            Logger.log(Logger.LOG_LEVEL_CRITICAL, "Error creating user " + newUser.getUname());
                            mp.println("Err: Unable to create user.");
                        }
                        return true;
                    default:
                        mp.println("Err: Unrecognized module to 'create' sub command.");
                }
            case "delete":
                if(!parser.hasMoreTokens()){
                    mp.println("Err: Missing Required Argument");
                    return true;
                }

                module = parser.nextToken();
                switch(module.toLowerCase()) {
                    case "user": 
                        if(!parser.hasMoreTokens()){
                            mp.println("Err: Missing Required Argument");
                            return true;
                        }
                        String uname = parser.nextToken();
                        if(uname.equals("")){
                            Logger.crit("Err: Missing required arg.");
                            return true;
                        }
                        User delUser = new User();
                        delUser = delUser.getUserByUsername(uname);
                        if(delUser == null) {
                            Logger.crit("Err: No user found.");
                            mp.println("Err: No User found.");
                            return true;
                        }
                        if(delUser.deleteUser()){
                            delUser = null;
                            mp.checkSession();
                            Logger.info("User " + uname + " removed.");
                            mp.println("OK User Removed");
                            
                        } else {
                            Logger.crit("Err: Unable to delete user " + uname + ".");
                            mp.println("Err: Unable to delete user");
                        }
                        return true;
                    default:
                        mp.println("Err: Unrecognized module to 'delete' sub command.");
                }
            default:
                mp.println("Err: Unrecognized Sub Command.");
        }

        return true; 
    }
}