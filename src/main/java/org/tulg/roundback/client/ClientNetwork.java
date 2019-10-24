package org.tulg.roundback.client;

import org.tulg.roundback.core.NetIOHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by jasonw on 10/6/2016.
 */
public class ClientNetwork {
    private String serverAddress;
    private String serverPort;
    private Socket socket;
    private final NetIOHandler netIOHandler;

    public ClientNetwork(){
        netIOHandler = new NetIOHandler();
    }

    public void setEncryption(boolean encryption) {
        netIOHandler.setEncrypted(encryption);
    }

    public void setEncryptionKey(String key) {
        netIOHandler.setEncryptionKey(key);
    }

    public void setServer(String serverString){
        //  parse serverString
        if(serverString.equals("")){
            this.serverAddress="locahost";
            this.serverPort="2377";
            return;
        }
        int portSep = serverString.indexOf(':');
        serverAddress = serverString.substring(0, portSep);
        serverPort = serverString.substring(portSep+1);

    }

    public void setServer(String serverAddress, String serverPort){
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public boolean connect(){
        try {
            socket = new Socket(serverAddress,Integer.parseInt(serverPort));
            netIOHandler.setIn(socket.getInputStream());
            netIOHandler.setOut(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Cannot connect to " + serverAddress + ":" + serverPort);
            return false;
        }

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

    public ArrayList<String> getHosts(){
        ArrayList<String> tmpArray = new ArrayList<>();
        try {

            // send the master server out request
            netIOHandler.println("list hosts");
            netIOHandler.flush();
            // wait for reply...
            int timer=0;
            while(!netIOHandler.inReady()){
                try {
                    Thread.sleep(1000);
                    timer++;
                    if(timer > 15){
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.err.println("Error: Interrupted while waiting for server.");
                    System.exit(1);
                }
            }
            if(netIOHandler.inReady()) {
                String tmpString = netIOHandler.readLine();
                while (!tmpString.isEmpty()) {
                    tmpArray.add(tmpString);
                    if(netIOHandler.inReady()) {
                        tmpString = netIOHandler.readLine();
                    } else {
                        tmpString = "";
                    }
                }
            } else {
                System.err.println("Error: Timeout waiting for list hosts from server");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpArray;
    }

    public ArrayList<String> getBackups(int hid) {
        ArrayList<String> tmpArray = new ArrayList<>();
        try {

            // send the master server out request
            netIOHandler.println("list backups hid " + hid);
            netIOHandler.flush();
            // wait for reply...
            int timer=0;
            while(!netIOHandler.inReady()){
                try {
                    Thread.sleep(1000);
                    timer++;
                    if(timer > 15){
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.err.println("Error: Interrupted while waiting for server.");
                    System.exit(1);
                }
            }
            while(netIOHandler.inReady()) {
                String tmpString = netIOHandler.readLine();
                if(tmpString.compareTo("") != 0){
                    String tmpString2 = tmpString.substring(0,3);
                    if (tmpString2.compareTo("ERR") != 0 ) {
                        tmpArray.add(tmpString);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmpArray;

    }

}
