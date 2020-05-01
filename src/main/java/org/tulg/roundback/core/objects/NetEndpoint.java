package org.tulg.roundback.core.objects;

import org.tulg.roundback.core.RoundBackObject;

/**
 * A base class for endpoints.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 * 
 */
public class NetEndpoint extends RoundBackObject {

    static int CLIENT=0;
    static int STORAGE=1;
    static int MASTER=2;

    private String uuid; // unique identifier for the entry
    private String hostname ; // the FQDN of this enpoint.
    private String ip; // the address of this endopoint.
    private String port; // the port this server runs on
    private int type; // what type of endpoint is this  

    private String backing_store; // if storage, what backing stores it supports
    private boolean enabled; // if this server is enabled or not
    private boolean online; // if this server is online or not
    private int hb_time; // the last time we recieved a heartbeat from this endpoint

}