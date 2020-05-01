package org.tulg.roundback.master.protocol;

import java.util.StringTokenizer;

import org.tulg.roundback.core.Encrypter;
import org.tulg.roundback.master.MasterProtocol;

public class Test {
    static public boolean parse (MasterProtocol mp, StringTokenizer parser) {
        if(!parser.hasMoreTokens()){
            mp.println("OK");
            return true;
        }
        String subCommand = parser.nextToken();
        switch(subCommand.toLowerCase()) {
            case "encrypt":
                if(parser.hasMoreTokens()) {
                    String stringToEncrypt = parser.nextToken("").substring(1);
                    mp.println(Encrypter.encrypt(mp.getRoundBackConfig().getEncryptionKey(), Encrypter.getIVBytes(), stringToEncrypt));
                }else {
                    mp.println("ERR: Missing argument");
                }
                break;
            case "decrypt":
                if(parser.hasMoreTokens()){
                    String encString = parser.nextToken();
                    mp.println(Encrypter.decrypt(mp.getRoundBackConfig().getEncryptionKey(),encString));

                }else {
                    mp.println("ERR: Missing argument");
                }
                break;
            default:
                mp.println("ERR: Unrecognized subcommand");
        }
        return true;
    }
}