package org.tulg.roundback.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tulg.roundback.core.Logger;
import org.tulg.roundback.core.RoundBackConfig;

/**
 * Handles listening for and accepting connections 
 * along with starting MasterThreads for new connections.
 */
class MasterNetwork {
    private ServerSocket socket;
    private int port;
    private final boolean quitting;
    private final RoundBackConfig rBackConfig;

    public MasterNetwork(RoundBackConfig config){
        port=2377;

        port = Integer.parseInt(config.getMasterPort(),10);
        if(port == 0) {
            port = 2377;
        }
        quitting=false;

        rBackConfig = config;

    }
    /**
     * Starts the main listening loop for incoming network connections.
     */
    public void listen(){
        try {
            // create a new socket
            socket = new ServerSocket(port);
        } catch (IOException e) {
            Logger.log(Logger.LOG_LEVEL_DEBUG, e.getStackTrace().toString());
            Logger.log(Logger.LOG_LEVEL_CRITICAL, "Unable to create socket on port " + port);
            System.exit(1);
        }

        // Start listening.
        Socket clientSock;
        Logger.log(Logger.LOG_LEVEL_INFO, "Server Started on port: " + port);
        ExecutorService executor = Executors.newFixedThreadPool(5); 

        while(!quitting) {
            try {
                clientSock = socket.accept();

                // Someone connected, start a thread to handle the connection
                
                // Set up the thread.
                MasterThread clientThread = new MasterThread(clientSock, rBackConfig);
                // clientThread.setMasterConfig(masterConfig);

                executor.execute(clientThread);
                Logger.log(Logger.LOG_LEVEL_INFO, "Accepted connection: " +
                        clientSock.getInetAddress().getHostAddress() + ":" +
                        clientSock.getPort()
                );

            } catch (IOException e) {
                Logger.log(Logger.LOG_LEVEL_DEBUG, e.getStackTrace().toString());
                Logger.log(Logger.LOG_LEVEL_CRITICAL, "socket error on accept()");
                System.exit(1);
            }
        }
    }
}
