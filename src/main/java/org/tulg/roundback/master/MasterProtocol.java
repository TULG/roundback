package org.tulg.roundback.master;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.NetIOHandler;
import org.tulg.roundback.core.RoundBackConfig;
import org.tulg.roundback.core.StringTokenizer;
import org.tulg.roundback.core.objects.Session;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Used to process the incoming/outgoing data to/from the
 * master server.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 */
public class MasterProtocol {

    private NetIOHandler netIOHandler = null;
    private boolean adminSession = false;
    private long adminSessionStart = 0;
    private RoundBackConfig rBackConfig;
    private boolean connectionClosing = false;
    private Session session;

    public MasterProtocol(NetIOHandler netIOHandler, RoundBackConfig rBackConfig) {

        this.netIOHandler = netIOHandler;
        this.rBackConfig = rBackConfig;
        this.session = null;

    }

    /**
     * The main function for processing incoming commands.
     * 
     *
     * @param  inputLine    The data from the client.
     * @return              boolean, True means keep the connection open, false means close it.            
     */
    public boolean process(String inputLine) throws IOException {

        if(connectionClosing)
            return false;

        if (inputLine.compareTo("") == 0) {
            netIOHandler.println("OK");

            return true;
        }

        // split the line into words.
        StringTokenizer parser = new StringTokenizer(inputLine.trim());
        String command = parser.nextToken();
        // Process incoming commands.
        switch (command.toLowerCase()) {
            // commands that directly effect the network communications go here.
            case "bye":
                Logger.log(Logger.LOG_LEVEL_INFO, "Connection to: " + netIOHandler.getClientAddress() + " closed.");
                closeConnection();
                return false;
            case "heartbeat":
                // TODO: lookup ip
                // TODO: Call update session
                
            default:
                // commands that need to call other objects will be separate classes,
                // pulled in here.
                try {
                    // protocol processors should be named "protocolName" where name is the
                    // top-level
                    // command to invoke the class for.
                    Class<?> protoCls = Class.forName("org.tulg.roundback.master.protocol."
                            + command.substring(0, 1).toUpperCase() + command.substring(1));
                    Method protoMethod = protoCls.getMethod("parse", MasterProtocol.class, StringTokenizer.class);
                    Object protoReturn = protoMethod.invoke(null, this, parser);
                    if((Boolean) protoReturn) {
                        // parser returned success, let's see if it wants us to try to keep parsing.
                        if(parser.hasMoreTokens()){
                            // recurse and lets see what happens.
                            String str = parser.fromCurrentToken();
                            return this.process(parser.fromCurrentToken());
                        }
                    }
                    // return the result.
                    return (Boolean)protoReturn;

                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException e) {
                            
                    // ignore this and let it go to the error below.
                    // Log the exception at some point, help debugging poorly written parsers.
                    Logger.log(Logger.LOG_LEVEL_DEBUG, e.getClass().getName() + ": ", false);
                    Logger.log(Logger.LOG_LEVEL_DEBUG, e);
                    
                }

                netIOHandler.println("ERR: Unsupported Command");
                netIOHandler.flush();
                return true;
        }
    }


    /**
     * Close the open connection.
     */
    private void closeConnection() {
        netIOHandler.flush();
        connectionClosing = true;
    }

    /**
     * Check if this is an admin session
     *
     * @return        True or false.
     */
    public boolean isAdminSession () {
        // Check to see if this connection has admin rights.
        // First check if admin session is true.
        if(adminSession) {
            //  check that adminSessionStart + adminSessionLength < now()
            int adminSessionLength = 60;
            if(adminSessionStart + adminSessionLength < System.currentTimeMillis() / 1000L ){
                adminSessionStart = 0;
                adminSession = false;
                return false;

            } else {
                // everything looks ok, so we have an admin session
                return true;
            }
        }
        return false;
    }

    public RoundBackConfig getRoundBackConfig() {
        return rBackConfig;
    }
    public NetIOHandler getNetIOHandler() {
        return this.netIOHandler;
    }

    public boolean getAdminSession() {
        return this.adminSession;
    }

    public long getAdminSessionStart() {
        return this.adminSessionStart;
    }

    public MasterProtocol adminSession(boolean adminSession) {
        this.adminSession = adminSession;
        return this;
    }

    public MasterProtocol adminSessionStart(long adminSessionStart) {
        this.adminSessionStart = adminSessionStart;
        return this;
    }

    public Session getSession(){
        return session;
    }

    public void setSession(Session nSession){
        this.session = nSession;
    }


    /**
     * Used to send a line of text back to the client.
     * 
     * @param  line     text to send
     * 
     */
    public void println(String line){
        try {
            if(session != null){
                line = line + " sess " + this.session.getRbdbf_uuid();
            }
            netIOHandler.println(line);
        } catch (IOException e) {
            Logger.log(Logger.LOG_LEVEL_DEBUG, "Error sending to client. Closing Connection.");
            closeConnection();
        }
    }

    /**
     * Returns the connected client address
     * @return  a string representing the client's address.
     */
    public String getClientAddress(){
        return netIOHandler.getClientAddress();
    }

}
