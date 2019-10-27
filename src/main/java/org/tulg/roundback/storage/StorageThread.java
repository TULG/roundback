package org.tulg.roundback.storage;

import org.tulg.roundback.core.NetIOHandler;
import org.tulg.roundback.core.RoundBackConfig;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by jasonw on 6/17/2017.
 */
public class StorageThread implements  Runnable {
    private Socket clientSock;
    private boolean quitting;
    private final StorageProtocol sProto;
    private NetIOHandler netIOHandler = null;
    private RoundBackConfig rBackConfig = null;

    public StorageThread(Socket clientSock){
        this.clientSock = clientSock;
        quitting = false;
        netIOHandler = new NetIOHandler();
        sProto = new StorageProtocol(netIOHandler);

    }

    public void setClientSock(Socket clientSock) {
        this.clientSock = clientSock;
    }


    public void setRoundBackConfig(RoundBackConfig rBackConfig) {
        this.rBackConfig = rBackConfig;
        sProto.setRoundBackConfig(rBackConfig);

    }

    @Override
    public void run() {
        netIOHandler.setEncrypted(rBackConfig.getEncrypted());
        netIOHandler.setEncryptionKey(rBackConfig.getEncryptionKey());
        netIOHandler.setClientAddress(clientSock.getInetAddress().getHostAddress());
        try {
            netIOHandler.setIn(clientSock.getInputStream());
            netIOHandler.setOut(clientSock.getOutputStream());
            netIOHandler.println("RoundBack Storage Server");
        } catch (IOException e) {
            System.err.println("Error: Cannot open input stream");
            quitting = true;
        }


        String inputLine;
        // listen until we quit
        while(!quitting) {
            try {

                inputLine = netIOHandler.readLine();
                if(!sProto.process(inputLine)){
                    quitting=true;
                }

            } catch (IOException e) {
                System.err.println("Exception: " + e.getMessage());
                quitting = true;
            }


        }
        netIOHandler.close();
        try {
            clientSock.close();
        } catch (IOException e) {
            // ignore exception on close.
        }

    }
}
