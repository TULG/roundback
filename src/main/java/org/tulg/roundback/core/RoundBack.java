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

    // default to master.
    private static int instanceType = RoundBack.MASTER;

    public static int getInstanceType() {
        return RoundBack.instanceType;
    }

    public static void setInstanceType(int instanceType){
        RoundBack.instanceType = instanceType;
    }

}