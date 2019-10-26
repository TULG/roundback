package org.tulg.roundback.storage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jasonw on 4/25/2017.
 */
class StorageNetwork {

    private ServerSocket socket;
    private int port;
    private final boolean quitting;
    private final StorageConfig storageConfig;

    public StorageNetwork(StorageConfig config){
        port=2378;

        port = Integer.parseInt(config.getPort(),10);
        if(port == 0) {
            port = 2378;
        }
        quitting=false;

        storageConfig = config;

    }

    public void listen(){
        try {
            // create a new socket
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: Unable to create socket on port " + port);
            System.exit(1);
        }

        // Start listening.
        Socket clientSock;
        System.out.println("Server Started on port: " + port);
        
        ExecutorService executor = Executors.newFixedThreadPool(5); 
        while(!quitting) {
            try {
                clientSock = socket.accept();


                // Set up the thread.
                StorageThread clientThread = new StorageThread(clientSock);
                clientThread.setStorageConfig(storageConfig);
                // Someone connected, start a thread to handle the connection
                executor.execute(clientThread);
                System.out.println("Accepted connection: " +
                        clientSock.getInetAddress().getHostAddress() + ":" +
                        clientSock.getPort()
                );

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error: socket error on accept()");
                System.exit(1);
            }
        }
    }
}
