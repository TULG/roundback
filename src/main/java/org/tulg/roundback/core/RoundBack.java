package org.tulg.roundback.core;


/**
 * Base Class for this instance of RoundBack.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 */
public class RoundBack {
    public static int CLIENT=0;
    public static int MASTER=1;
    public static int STORAGE=2;

    public static int WIN=0;
    public static int NIX=1;
    public static int MAC=2;
    public static int OTH=3;

    // default to master.
    private static int instanceType = RoundBack.MASTER;
    private static int osType = 0;

    public static int getInstanceType() {
        return RoundBack.instanceType;
    }

    public static void setInstanceType(int instanceType){
        RoundBack.instanceType = instanceType;
        RoundBack.setOSType();
    }

    public static void setOSType(){
        String os = System.getProperty("os.name").toLowerCase();
        if(os.indexOf("win") >= 0){
            osType = RoundBack.WIN;
        } else if(os.indexOf("mac") >= 0){
            osType = RoundBack.MAC;
        } else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0 || os.indexOf("sunos") >= 0 ){
            osType = RoundBack.NIX;
        } else {
            osType = RoundBack.OTH;
        }
    }

}