package org.tulg.roundback.core;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

/**
 * Handles the actual socket I/O for an established network connection.
 * 
 * @author Jason Williams
 */
public class NetIOHandler {
    private PrintStream out = null;
    private BufferedReader in = null;
    private boolean isEncrypted = false;
    private String encryptionKey = "";
    private String clientAddress;

    public NetIOHandler(OutputStream outputStream, InputStream inputStream) {
        out = new PrintStream(outputStream, true);
        in = new BufferedReader(new InputStreamReader(inputStream));

    }

    public NetIOHandler() {

    }

    public NetIOHandler(Socket remoteSocket, boolean isEncrypted) {

        try {
            out = new PrintStream(remoteSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(remoteSocket.getInputStream()));

        } catch (IOException e) {
            Logger.log(Logger.LOG_LEVEL_CRITICAL, "Unable to setup networking.");
            System.exit(1);
        }
        this.isEncrypted = isEncrypted;
    }

    public NetIOHandler(OutputStream outputStream, InputStream inputStream, boolean isEncrypted) {
        out = new PrintStream(outputStream,true);
        in = new BufferedReader(new InputStreamReader(inputStream));
        this.isEncrypted = isEncrypted;

    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public PrintStream getOut() {
        return out;
    }

    public void setOut(OutputStream outputStream) {
        this.out = new PrintStream(outputStream, true);
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(InputStream inputStream) {
        this.in = new BufferedReader(new InputStreamReader(inputStream));
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * prints a line back to the client, with optional encryption.
     *
     * @param  string   the data to send back to the client.
     * 
     */
    public void println(String string) throws IOException {
        if(out == null) {
            throw new IOException("Cannot open output stream");
        }
        if(isEncrypted) {
            if(Objects.equals(encryptionKey, "")){
                Logger.log(Logger.LOG_LEVEL_ERROR, "encryptionKey not set but encryption requested.");
                Logger.log(Logger.LOG_LEVEL_ERROR, "network writes will fail!");
                throw new IOException("Encryption requested, but no key set!");
            } else {
                out.println(Encrypter.encrypt(encryptionKey, Encrypter.getIVBytes(), string));
            }
        } else {
            out.println(string);
        }

    }

    /**
     * Read a line of data from the client
     *
     * @return  The data from the client, in a String
     */
    public String readLine() throws IOException {
        if(in == null) {
            throw new IOException("Cannot open input  stream");
        }
        try {
            if (isEncrypted) {
                if(Objects.equals(encryptionKey, "")) {
                    Logger.log(Logger.LOG_LEVEL_ERROR, "encryptionKey not set but encryption requested.");
                    Logger.log(Logger.LOG_LEVEL_ERROR, "network reads will fail!");
                    throw new IOException("Encryption requested, but no key set!");
                } else {
                    String inLine = in.readLine();
                    if(inLine == null)
                        return null;
                    return Encrypter.decrypt(encryptionKey, inLine);
                }
            } else {
                return in.readLine();
            }
        } catch (IOException e) {
            throw new IOException("Input Stream Disconnected");
        }
    }

    public void flush() {
        out.flush();
    }

    public boolean inReady() throws IOException {
        return in.ready();
    }


    public void close() {
        out.close();
        try {
            in.close();
        } catch (IOException e) {
            // ignore io exception on close.
        }
    }
}
