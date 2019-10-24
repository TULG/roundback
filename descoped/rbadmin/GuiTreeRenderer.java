package org.tulg.roundback.rbadmin;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.net.URL;

/**
 * Created by jasonw on 9/25/2016.
 */
class GuiTreeRenderer extends DefaultTreeCellRenderer {
    public GuiTreeRenderer () {

    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if(row == 0 ) {
            setIcon(null);
            return this;
        }
        URL url = ClassLoader.getSystemClassLoader().getResource("computer-2.png");

        //setIcon(new ImageIcon("assets/icons/file-doc.png"));

        if( !leaf ){

            if(expanded){
                url=ClassLoader.getSystemClassLoader().getResource("folder-open-2.png");
            } else {
                url = ClassLoader.getSystemClassLoader().getResource("folder.png");
            }
        }
        if(url!=null) {
            ImageIcon img = new ImageIcon(url);
            Image tmpImg = img.getImage().getScaledInstance(16, 16, 0);
            img.setImage(tmpImg);
            setIcon(img);
        }
        return this;
    }
}
