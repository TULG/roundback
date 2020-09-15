package org.tulg.roundback.core;

import java.io.IOException;
import java.net.Socket;

/**
 * Class to provide a connection to the master server.
 * 
 */
public class MasterConnection {
    private String masterAddress;
    private String masterPort;
    private Socket socket;
    protected final NetIOHandler netIOHandler;
    private String sessionId;

    public MasterConnection() {
        netIOHandler = new NetIOHandler();
        netIOHandler.setEncrypted(false);
    }

    public MasterConnection(String masterAddress, String masterPort) {
        this.masterAddress = masterAddress;
        this.masterPort = masterPort;
        netIOHandler = new NetIOHandler();
        netIOHandler.setEncrypted(false);
    }

    public MasterConnection(String masterAddress, String masterPort, boolean encrypted) {
        this.masterAddress = masterAddress;
        this.masterPort = masterPort;
        netIOHandler = new NetIOHandler();
        netIOHandler.setEncrypted(encrypted);
    }

    public void setEncryption(boolean encryption) {
        netIOHandler.setEncrypted(encryption);
    }

    public void setEncryptionKey(String key) {
        netIOHandler.setEncryptionKey(key);
    }

    public void setServer(String serverString) {
        // parse serverString
        if (serverString.equals("")) {
            this.masterAddress = "locahost";
            this.masterPort = "2377";
            return;
        }
        int portSep = serverString.indexOf(':');
        masterAddress = serverString.substring(0, portSep);
        masterPort = serverString.substring(portSep + 1);

    }

    public void setServer(String serverAddress, String serverPort) {
        this.masterAddress = serverAddress;
        this.masterPort = serverPort;
    }

    public boolean connect() {
        try {
            socket = new Socket(masterAddress, Integer.parseInt(masterPort));
            netIOHandler.setIn(socket.getInputStream());
            netIOHandler.setOut(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Cannot connect to " + masterAddress + ":" + masterPort);
            return false;
        }

        // wait for an OK prompt.
        String inString="";
        try {
            inString = netIOHandler.readLineWithTimeout(60);
       
            while(!inString.endsWith("OK")) {
                Logger.debg(inString);
                inString = netIOHandler.readLineWithTimeout(60); 
            }
        } catch (Exception e) {
            if(e.getMessage() == "Timeout" ){
                Logger.crit("Timeout waiting for master.");
            } else {
                Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
            }
            System.exit(1);
        }
        // set our session id.
        inString = inString.replace("sess ", "");
        inString = inString.replace(" OK", "");
        this.sessionId = inString;
      return true;

  }
  public void disconnect(){
      try {
          netIOHandler.println("bye");
          socket.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public void sendRaw(String rawString) throws IOException {
      netIOHandler.println(rawString);
  }

  public String recvRaw() throws IOException, InterruptedException {

      /*// wait for reply...
      int timer=0;
      while(!netIOHandler.inReady()){
              Thread.sleep(1000);
              timer++;
              if(timer > 15){
                  break;
              }

      }
      if(netIOHandler.inReady()) {
      */
          return netIOHandler.readLine();
      //}

      //return "";
  }

  public boolean sendHeartBeat() throws IOException{
    String hbresult = this.sendCmd("heartbeat");
    if(hbresult.endsWith("OK") ) {
        return true;
    }
    // If we got here, our heartbeat failed.
    Logger.crit("Error sending heartbeat.  Master said: " + hbresult);
    return false;
  }

  public boolean login(String user, String password){
    String loginCmd = "auth login " + user + " " + password;

    if(!this.sendCmd(loginCmd).endsWith("OK")){
        return false;
    }
    return true;
  }
  private String sendCmd(String commmand) {
    String tmpCommand = "sess " + this.sessionId + " " + commmand;

    try {
        netIOHandler.println(tmpCommand);
        return(netIOHandler.readLine());
    } catch (IOException e) {
        Logger.crit("Error logging in.");
        Logger.log(Logger.LOG_LEVEL_CRITICAL, e);
        return null;
    }

  }

  public boolean register(int instanceType, String hostname) {
    String command = "register ";
    switch (instanceType) {
        case RoundBack.CLIENT:
            command = command + "client";
            break;
        case RoundBack.STORAGE: 
            command = command + "server";
            break;
        default:
            Logger.crit("Unrecognized instance type!");
            return false;
            
    }
    command = command + " " + hostname;
    if(!this.sendCmd(command).endsWith("OK")){
        return false;
    }
    return true;
  }

}
