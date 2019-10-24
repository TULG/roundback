package org.tulg.roundback.rbadmin;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by jasonw on 10/9/2016.
 */
class HostTreeNode extends DefaultMutableTreeNode {

    private int hid;
    public HostTreeNode(Object userObject) {
        super(userObject);
    }
    public HostTreeNode(Object userObject, String hid){
        super(userObject);
        this.hid = Integer.parseInt(hid);
    }

    public int getHid() {
        return hid;
    }
}
