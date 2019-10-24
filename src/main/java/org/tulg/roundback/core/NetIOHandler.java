package org.tulg.roundback.core;

import java.io.*;
import java.util.Objects;

/**
 * Created by jasonw on 5/25/2017.
 */
public class NetIOHandler {
    private PrintStream out = null;
    private BufferedReader in = null;
    private boolean isEncrypted = false;
    private String encryptionKey = "";
    private String clientAddress;

    public NetIOHandler(OutputStream outputStream, InputStream inputStream) {
        out = new PrintStream(outputStream,true);
        in = new BufferedReader(new InputStreamReader(inputStream));

    }
    public NetIOHandler(){

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

    public void println(String string) throws IOException {
        if(out == null) {
            throw new IOException("Cannot open output stream");
        }
        if(isEncrypted) {
            if(Objects.equals(encryptionKey, "")){
                System.err.println("Error: encryptionKey not set but encryption requested.");
                System.err.println("Error: network writes will fail!");
            } else {
                out.println(Encrypter.encrypt(encryptionKey, Encrypter.getIVBytes(), string));
            }
        } else {
            out.println(string);
        }

    }

    public String readLine() throws IOException {
        if(in == null) {
            throw new IOException("Cannot open input  stream");
        }
        try {
            if (isEncrypted) {
                if(Objects.equals(encryptionKey, "")) {
                    System.err.println("Error: encryptionKey not set but encryption requested.");
                    System.err.println("Error: network reads will fail!");
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
            //System.err.println("Error: Error reading input stream. Empty string returned.");
            throw new IOException("Input Stream Disconnected");
        }
        return "";
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
