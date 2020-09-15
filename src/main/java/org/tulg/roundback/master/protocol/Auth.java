package org.tulg.roundback.master.protocol;

import org.tulg.roundback.core.StringTokenizer;

import java.time.Instant;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.objects.Session;
import org.tulg.roundback.core.objects.User;
import org.tulg.roundback.master.MasterProtocol;

/**
 * Class to handle 'auth' commands to the master.
 *
 * @author Jason Williams <jasonw@tulg.org>
 */
public class Auth {

    /**
     * parser for the auth command.
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
        String userName="";
        String subCommand = parser.nextToken();
        switch (subCommand.toLowerCase()) {
            case "check":
                if (mp.checkSession()) {
                    // got a valid user in our session.
                    mp.println("TRUE");
                    return true;
                }
                mp.println("FALSE");
                mp.getSession().deleteSession(mp.getSession().getRbdbf_uuid());
                return true;
            case "logout": 
                mp.getSession().deleteSession(mp.getSession().getRbdbf_uuid());
                mp.setSession(new Session());
                mp.println("OK");
                return true;
            case "login":
                
                if(!parser.hasMoreTokens()){
                    mp.println("Err: Missing Required Arguement");
                    return true;
                }
                userName = parser.nextToken().strip();
                if(!parser.hasMoreTokens()){
                    mp.println("Err: Missing Required Argument");
                    return true;
        
                }
                String passwordIn = parser.nextToken();
                User rbUser = new User();
                
                // Check the authentication.
                if(!rbUser.authenticateUser(userName, passwordIn)){
                    Logger.log(Logger.LOG_LEVEL_WARN, "Invalid login for " + userName + " from " + mp.getClientAddress());
                    mp.println("Err: Invalid login");

                } else {
                    mp.getSession().setRbdbf_userid(rbUser.getUuid());
                    mp.getSession().save();
                    Logger.log(Logger.LOG_LEVEL_INFO, "Successful login for " + userName + " from " + mp.getClientAddress());
                    mp.println("OK");
                    if(rbUser.isAdmin()){
                        mp.adminSession(true);
                        mp.adminSessionStart(Instant.now().getEpochSecond());
                    }
                }
                return true;
            case "chpassword":
                if(!parser.hasMoreTokens()){
                    mp.println("Err: Missing Required Arguement");
                    return true;
                }
                userName = parser.nextToken().strip();
                if(!parser.hasMoreTokens()){
                    mp.println("Err: Missing Required Argument");
                    return true;
        
                }
                String passwordIn2 = parser.nextToken();
                if(!parser.hasMoreTokens()){
                    mp.println("Err: Missing Required Arguement");
                    return true;
                }
                String passwordNew = parser.nextToken();
                User rbUser2 = new User();
                if(!rbUser2.authenticateUser(userName, passwordIn2)){
                    Logger.log(Logger.LOG_LEVEL_WARN, "Invalid login for " + userName + " from " + mp.getClientAddress());
                    mp.println("Err: Invalid login");

                } else {
                    // we autheneticated, so let's update the password
                    if(rbUser2.updateUser(passwordNew)){
                        Logger.log(Logger.LOG_LEVEL_INFO, "Password updated for " + userName);
                        mp.println("OK Please reauth.");

                    }
                }
                return true;

            default:
                mp.println("Err: Unrecognized Sub Command.");
                parser.last();

        }

        return true;


    
    }


     /**
     * Returns a String of BASE64 encoded characters that represents the encrypted data.
     * <p>
     * Useful for network communications, and small encryption that is terminated by newlines.
     *
     * @param  mp       the MasterProtocol Object that called this one.
     * @param  password the password to check
     * @return              true auth passed, false it failed
     */
    private static boolean authenticateAdmin(MasterProtocol mp, String password) {
        // TODO: For now, admin pass can be the same as encryption key.
        String key = mp.getRoundBackConfig().getEncryptionKey();
        if(password.equals(key)) {
            mp.adminSessionStart(System.currentTimeMillis() / 1000L);
            mp.adminSession(true);
            Logger.log(Logger.LOG_LEVEL_INFO, "Accepted Admin Pass from " + mp.getClientAddress());
            return true;
        }
        Logger.log(Logger.LOG_LEVEL_WARN, "Incorrect password from " + mp.getClientAddress());
        return false;
    }

}