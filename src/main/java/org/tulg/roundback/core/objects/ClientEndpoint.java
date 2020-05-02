package org.tulg.roundback.core.objects;

import org.tulg.roundback.master.MasterDB;

public class ClientEndpoint extends NetEndpoint {

    public ClientEndpoint () {
        super();
        this.type = NetEndpoint.CLIENT;
    }
    
}