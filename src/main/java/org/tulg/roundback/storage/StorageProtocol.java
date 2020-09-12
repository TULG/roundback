package org.tulg.roundback.storage;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.NetIOHandler;
import org.tulg.roundback.core.RoundBackConfig;

import java.io.IOException;
import org.tulg.roundback.core.StringTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jasonw on 4/26/2017.
 */
public class StorageProtocol {

    private NetIOHandler netIOHandler = null;
    private boolean adminSession = false;
    private long adminSessionStart = 0;
    private RoundBackConfig rBackConfig;
    private boolean connectionClosing = false;
    private StringTokenizer parser;


    public StorageProtocol(NetIOHandler netIOHandler) {
     

        this.netIOHandler = netIOHandler;


    }

    public boolean process(String inputLine) throws IOException {
        if(connectionClosing){
            return false;
        }
        // TODO: Add in a session check.
        if (inputLine.compareTo("") == 0) {
            netIOHandler.println("OK");

            return true;
        }

        // split the line into words.
        parser = new StringTokenizer(inputLine);
        String command = parser.nextToken();
        // Process incoming commands.

        // bye command
        switch (command.toLowerCase()) {
            case "bye":
                System.out.println("Connection to: " + netIOHandler.getClientAddress() + " closed.");

                return false;
            
            default:
                // commands that need to call other objects will be separate classes,
                // pulled in here.
                try {
                    // protocol processors should be named "protocolName" where name is the
                    // top-level
                    // command to invoke the class for.
                    Class<?> protoCls = Class.forName("org.tulg.roundback.storage.protocol."
                            + command.substring(0, 1).toUpperCase() + command.substring(1));
                    Method protoMethod = protoCls.getMethod("parse", StorageProtocol.class, StringTokenizer.class);
                    Object protoReturn = protoMethod.invoke(null, this, parser);
                    if((Boolean) protoReturn) {
                        // parser returned success, let's see if it wants us to try to keep parsing.
                        if(parser.hasMoreTokens()){
                            // recurse and lets see what happens.
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

        }
        return true;
    }

    public boolean checkForMoreTokens() {
        if(this.parser == null){
            this.println("Err: Parser error");
            return false;
        }
        if(!this.parser.hasMoreTokens()){
            this.println("Err: Missing Required Argument");
            return false;
        }
        return true;
    }

    public RoundBackConfig getStorageConfig() {
        return rBackConfig;
    }

    public void setRoundBackConfig(RoundBackConfig rBackConfig) {
        this.rBackConfig = rBackConfig;
    }


    /**
     * Close the open connection.
     */
    private void closeConnection() {
        netIOHandler.flush();
        connectionClosing = true;
    }
    /**
     * Used to send a line of text back to the client.
     * 
     * @param  line     text to send
     * 
     */
    public void println(String line){
        try {
/*             if(session != null){
                if(!session.checkSession()){
                    session.createSession(null);
                    this.setSession(session);
                }
                line =  "sess " + this.session.getRbdbf_uuid() + " " + line;
            } */
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