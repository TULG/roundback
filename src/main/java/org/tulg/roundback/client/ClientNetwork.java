package org.tulg.roundback.client;

import org.tulg.roundback.core.MasterConnection;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jasonw on 10/6/2016.
 */
public class ClientNetwork extends MasterConnection {

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
