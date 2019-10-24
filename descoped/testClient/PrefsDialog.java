package org.tulg.roundback.testClient;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.*;

class PrefsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtMaster;
    private JCheckBox chkEncyption;
    private JPasswordField txtEncKey;
    private final TestCliConfig testCliConfig;

    public PrefsDialog(TestCliConfig testCliConfig) {
        this.testCliConfig = testCliConfig;
        setContentPane(contentPane);
        setModal(true);

        getRootPane().setDefaultButton(buttonOK);

        // load the prefs into the widgets.
        if(testCliConfig.getEncrypted()) {
            chkEncyption.setSelected(true);
            txtEncKey.setEnabled(true);
            txtEncKey.setText(testCliConfig.getEncryptionKey());
        } else {
            chkEncyption.setSelected(false);
            txtEncKey.setEnabled(false);
            txtEncKey.setText("");
        }

        txtMaster.setText(testCliConfig.getHostname() + ":" + testCliConfig.getPort());

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // change listener for encryption Checkbox
        ChangeListener encListener = e -> {
            AbstractButton abstractButton = (AbstractButton) e.getSource();
            ButtonModel buttonModel = abstractButton.getModel();
            if(buttonModel.isSelected()) {
                txtEncKey.setEnabled(true);
            } else {
                txtEncKey.setEnabled(false);
            }

        };
        chkEncyption.addChangeListener(encListener);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // save all the changes to the prefs and reload the prefs.
        testCliConfig.setEncrypted(chkEncyption.isSelected());
        String tmpString2 = new String(txtEncKey.getPassword());
        testCliConfig.setEncryptionKey(tmpString2);
        String tmpString = txtMaster.getText();
        String hostname = tmpString.substring(0, tmpString.indexOf(":"));
        String port = tmpString.substring(tmpString.indexOf(":") + 1);
        testCliConfig.setHostname(hostname);
        testCliConfig.setPort(port);
        testCliConfig.save();
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
