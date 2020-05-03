package org.tulg.roundback.core.objects;

import org.tulg.roundback.master.MasterDB;

/**
 * Class for specific functions around Master Endpoints.
 * 
 * @author Jason Williams <jasonw@tulg.org>
 * 
 */
public class MasterEndpoint extends NetEndpoint {

    public MasterEndpoint(){
        super();
        this.rbdbf_type = NetEndpoint.MASTER;
    }
}