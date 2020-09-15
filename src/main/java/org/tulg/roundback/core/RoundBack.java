package org.tulg.roundback.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Base Class for this instance of RoundBack.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 */
public class RoundBack {
    public final static int CLIENT = 0;
    public final static int MASTER = 1;
    public final static int STORAGE = 2;

    public final static int WIN = 0;
    public final static int NIX = 1;
    public final static int MAC = 2;
    public final static int OTH = 3;

    public static int version_major = 0;
    public static int version_minor = 1;
    public static String version_release = "alpha";

    // default to master.
    private static int instanceType = RoundBack.MASTER;
    private static int osType = 0;

    public static int getInstanceType() {
        return RoundBack.instanceType;
    }

    public static void setInstanceType(int instanceType) {
        RoundBack.instanceType = instanceType;
        RoundBack.setOSType();
    }

    public static void setOSType() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            osType = RoundBack.WIN;
        } else if (os.indexOf("mac") >= 0) {
            osType = RoundBack.MAC;
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0
                || os.indexOf("sunos") >= 0) {
            osType = RoundBack.NIX;
        } else {
            osType = RoundBack.OTH;
        }
    }

    public static String getOString() {
        if (osType == RoundBack.WIN) {
            return "Windows";
        } else if (osType == RoundBack.MAC) {
            return "MacOS";
        } else if (osType == RoundBack.NIX) {
            return "'Nix Type";
        }
        return "Other (undetectable)";
    }

    public static String getFullOString() {
        return System.getProperty("os.name");
    }

    public static String getVersion() {
        return String.valueOf(RoundBack.version_major) + "." + String.valueOf(RoundBack.version_minor) + "-"
                + RoundBack.version_release;
    }

    public static String getInstanceString() {
        if (RoundBack.instanceType == RoundBack.CLIENT) {
            return "roundback-cli";
        }
        if (RoundBack.instanceType == RoundBack.MASTER) {
            return "master-server";
        }
        if (RoundBack.instanceType == RoundBack.STORAGE) {
            return "storage-server";
        }
        return "";
    }

    public static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else {
            String hostname;
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                return "Unknown";
            }
            int dotIdx = hostname.indexOf(".");
            return (dotIdx >= 0) ? hostname.substring(0, dotIdx) : hostname;
        }

    }
}