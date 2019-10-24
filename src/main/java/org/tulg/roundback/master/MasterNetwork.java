package org.tulg.roundback.master;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jasonw on 10/7/2016.
 */
class MasterNetwork {
    private ServerSocket socket;
    private int port;
    private final boolean quitting;
    private final MasterConfig masterConfig;

    public MasterNetwork(MasterConfig config){
        port=2377;

        port = Integer.parseInt(config.getPort(),10);
        if(port == 0) {
            port = 2377;
        }
        quitting=false;

        masterConfig = config;

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
        while(!quitting) {
            try {
                clientSock = socket.accept();

                // Someone connected, start a thread to handle the connection
                ExecutorService executor = Executors.newFixedThreadPool(5); // TODO: this probably goes outside the loop

                // Set up the thread.
                MasterThread clientThread = new MasterThread(clientSock);
                clientThread.setMasterConfig(masterConfig);

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
