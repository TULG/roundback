package org.tulg.roundback.rbadmin;

import javax.swing.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class PreferencesWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabbedPane1;
    private JTextField masterAddr;
    private Preferences preferences;

    public PreferencesWindow() {
        sharedConstructor();
    }

    private void sharedConstructor(){

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Round Back - Preferences");
        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        masterAddr.setText(preferences.get("masterServer",""));
    }

    public PreferencesWindow(MainWindow mainWindow) {
        this.preferences = mainWindow.getPreferences();
        sharedConstructor();
    }

    private void onOK() {
        // add your code here
        preferences.put("masterServer", masterAddr.getText());

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


}
