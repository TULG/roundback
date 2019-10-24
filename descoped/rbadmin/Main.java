package org.tulg.roundback.rbadmin;

import javax.swing.*;

/**
 * Created by jasonw on 9/24/2016.
 */
class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e ) {
            e.printStackTrace();
        }

        // write your code here
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        mainWindow.toFront();


    }
}
