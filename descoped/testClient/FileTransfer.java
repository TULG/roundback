package org.tulg.roundback.testClient;

import org.tulg.roundback.client.ClientNetwork;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclFileAttributeView;
import java.util.*;

public class FileTransfer extends JDialog {
    private final TestCliConfig testCliConfig;
    private final SimpleAttributeSet boldText;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtStorageServer;
    private JTextField txtPort;
    private JTextField txtBackupID;
    private JTextField txtTarget;
    private JButton btnBrowse;
    private JEditorPane consoleStorage;
    private JTextField txtCommand;
    private JButton btnSend;
    private JButton btnConnect;
    private Thread recvThread;
    private boolean connected;
    private ClientNetwork clientNetwork;
    private StyledDocument styledDocument;
    private SimpleAttributeSet defaultStyle;


    public FileTransfer(TestCliConfig testCliConfig) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnSend);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                //super.windowClosing(e);
                if(connected) {
                    if(clientNetwork != null) {
                        clientNetwork.disconnect();
                    }
                    connected=false;
                    btnConnect.setText("Connect");
                    recvThread.interrupt();
                }
            }
        });

        btnBrowse.setMnemonic('b');
        buttonOK.setMnemonic('o');
        buttonCancel.setMnemonic('c');

        consoleStorage.setBackground(Color.BLACK);
        consoleStorage.setForeground(Color.GREEN);
        consoleStorage.setCaretColor(Color.GREEN);
        consoleStorage.getCaret().setVisible(true);
        consoleStorage.getCaret().setBlinkRate(500);

        styledDocument = (StyledDocument) consoleStorage.getDocument();
        defaultStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(defaultStyle, "Monospace");
        StyleConstants.setForeground(defaultStyle, Color.GREEN);
        StyleConstants.setBackground(defaultStyle, Color.BLACK);

        boldText = new SimpleAttributeSet(defaultStyle);
        StyleConstants.setBold(boldText, true);

        resetThread();

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());
        btnBrowse.addActionListener(e -> onBrowse());



        btnConnect.addActionListener(e -> onConnect());
        btnSend.addActionListener(e -> onSend());

        this.testCliConfig = testCliConfig;
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

    private void onBrowse() {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int retval = fc.showOpenDialog(this);
        if(retval == JFileChooser.APPROVE_OPTION) {
            // user selected a file
            File chosenFile = fc.getSelectedFile();
            String filename = chosenFile.getAbsolutePath();
            txtTarget.setText(filename);
            int len = filename.length();
            getFilePerms();
        }
    }

    private void getFilePerms() {
        AclFileAttributeView aclView = Files.getFileAttributeView(Paths.get(txtTarget.getText()), AclFileAttributeView.class);
        try {
            java.util.List<AclEntry> aclEntryList = aclView.getAcl();
            for(AclEntry entry : aclEntryList) {
                System.out.format("Principal: %s%n", entry.principal());
                System.out.format("Type: %s%n", entry.type());
                System.out.format("Permissions:%n");
                Set<AclEntryPermission> permissions = entry.permissions();
                for (AclEntryPermission p : permissions) {
                    System.out.format("%s %n", p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void onSend() {
        // Send the entered command
        if(clientNetwork != null) {
            try {
                styledDocument.insertString(styledDocument.getLength(),
                        ">>> " + txtCommand.getText() + "\n", boldText  );
                //editorPane1.setText(editorPane1.getText() + "<b>>>> " + txtCommand.getText()+ "</b>\n");
                clientNetwork.sendRaw(txtCommand.getText());
                txtCommand.setText("");
            } catch (IOException | NullPointerException | BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private void onConnect() {
        // Connect to the master
        if(! connected ) {
            clientNetwork = new ClientNetwork();
            clientNetwork.setEncryption(testCliConfig.getEncrypted());
            clientNetwork.setEncryptionKey(testCliConfig.getEncryptionKey());
            clientNetwork.setServer(txtStorageServer.getText(), txtPort.getText());
            if (clientNetwork.connect()) {
                connected = true;
                btnConnect.setText("Disconnect");
                contentPane.setPreferredSize(contentPane.getPreferredSize());
                recvThread.start();
            }
        } else {
            connected = false;
            btnConnect.setText("Connect");
            clientNetwork.disconnect();
            //clientNetwork = null;
            recvThread.interrupt();
        }
    }

    private void onOK() {
        // TODO: add code to try to send the file
        // make sure it updates the txt file in the connectDialog box.
        // should also hide this window, or we need a new console perhaps on
        // this window.
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }



    public void setConnectDialog(ConnectDialog connectDialog) {
        ConnectDialog connectDialog1 = connectDialog;
    }


    private void resetThread(){
        // set up a new thread to do the recv
        recvThread = new Thread(() -> {
            // need to work on this.
            if(connected) {
                while(connected) {
                    // if we ar econnected
                    try {
                        String tmpString = clientNetwork.recvRaw();
                        if(tmpString == null)
                            throw new IOException("Disconnected.");
                        if(!tmpString.equals("")) {
                            styledDocument.insertString(styledDocument.getLength(), tmpString + "\n", defaultStyle);
                            //editorPane1.setText(editorPane1.getText() + tmpString + "\n");
                        }
                    } catch (IOException e) {
                        connected = false;
                        btnConnect.setText("Connect");
                        clientNetwork.disconnect();
                        clientNetwork = null;
                    } catch (InterruptedException e) {
                        connected = false;
                        btnConnect.setText("Connect");
                        if(clientNetwork != null) {
                            clientNetwork.disconnect();
                            clientNetwork = null;
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Storage receive thread exit.");
            resetThread();
        });
    }
}
