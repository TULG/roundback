package org.tulg.roundback.core.objects;

import org.tulg.roundback.master.MasterDB;

public class StorageEndpoint extends NetEndpoint {

    public StorageEndpoint(){
        super();
        this.type = NetEndpoint.STORAGE;
    }
}