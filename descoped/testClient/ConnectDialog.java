package org.tulg.roundback.testClient;

import org.tulg.roundback.client.ClientNetwork;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

class ConnectDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonConnect;
    private JButton buttonClose;
    private JButton btnPrefs;

    private JTextField txtCommand;
    private JButton btnSend;
    private JEditorPane editorPane1;
    private JButton btnFile;
    private final TestCliConfig testCliConfig;
    private StyledDocument styledDocument;
    SimpleAttributeSet defaultStyle;
    private SimpleAttributeSet boldText;
    private ClientNetwork clientNetwork;
    private boolean connected;
    private Thread recvThread;
    private static final String title = "RoundBack Test Client";

    /*
            new ImageIcon("icon_32.png").getImage(),
            new ImageIcon("icon_64.png").getImage());*/


    public ConnectDialog(TestCliConfig testCliConfig) {

        super(new TestClientFrame(title, null));
        ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("RoundBack.png"));
        Image image = imageIcon.getImage();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if(connected){
                    connected = false;
                    btnFile.setEnabled(false);
                    buttonConnect.setText("Connect");
                    if(clientNetwork != null)
                        clientNetwork.disconnect();
                    //clientNetwork = null;
                    recvThread.interrupt();


                }

                ((TestClientFrame)getParent()).dispose();
                System.exit(0);

            }
        });

        btnSend.setMnemonic('s');
        buttonConnect.setMnemonic('n');
        buttonClose.setMnemonic('c');
        btnPrefs.setMnemonic('p');


        editorPane1.setBackground(Color.BLACK);
        editorPane1.setForeground(Color.GREEN);
        editorPane1.setCaretColor(Color.GREEN);
        editorPane1.getCaret().setVisible(true);
        editorPane1.getCaret().setBlinkRate(500);

        styledDocument = (StyledDocument) editorPane1.getDocument();
        defaultStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(defaultStyle, "Monospace");
        StyleConstants.setForeground(defaultStyle, Color.GREEN);
        StyleConstants.setBackground(defaultStyle, Color.BLACK);

        boldText = new SimpleAttributeSet(defaultStyle);
        StyleConstants.setBold(boldText, true);

        ((TestClientFrame)getParent()).setIconImage(image);
        setIconImage(image);

        setTitle(title);
        connected = false;
        btnFile.setEnabled(false);
        this.testCliConfig = testCliConfig;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnSend);

        buttonConnect.addActionListener(e -> onConnect());
        buttonClose.addActionListener(e -> onClose());
        btnPrefs.addActionListener(e -> onPrefs());
        btnSend.addActionListener(e -> onSend());
        btnFile.addActionListener(e -> onFile());

        resetThread();


        // call onClose() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onClose() on ESCAPE
        contentPane.registerKeyboardAction(e -> onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


    }

    private void onFile() {
        FileTransfer fileTransfer = new FileTransfer(testCliConfig);
        fileTransfer.setConnectDialog(this);
        fileTransfer.pack();
        fileTransfer.setVisible(true);

    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible) {
            ((TestClientFrame)getParent()).dispose();
        }
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
                        btnFile.setEnabled(false);
                        buttonConnect.setText("Connect");
                        clientNetwork.disconnect();
                        clientNetwork = null;
                    } catch (InterruptedException e) {
                        connected = false;
                        btnFile.setEnabled(false);
                        buttonConnect.setText("Connect");
                        if(clientNetwork != null) {
                            clientNetwork.disconnect();
                            clientNetwork = null;
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Receive thread exit.");
            resetThread();
        });
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

    private void onPrefs() {
        // add code here to open prefs window.
        PrefsDialog dialog = new PrefsDialog(testCliConfig);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void onConnect() {
        // Connect to the master
        if(! connected ) {
            clientNetwork = new ClientNetwork();
            clientNetwork.setEncryption(testCliConfig.getEncrypted());
            clientNetwork.setEncryptionKey(testCliConfig.getEncryptionKey());
            clientNetwork.setServer(testCliConfig.getHostname(), testCliConfig.getPort());
            if (clientNetwork.connect()) {
                connected = true;
                btnFile.setEnabled(true);
                buttonConnect.setText("Disconnect");
                contentPane.setPreferredSize(contentPane.getPreferredSize());
                recvThread.start();
            }
        } else {
            connected = false;
            btnFile.setEnabled(false);
            buttonConnect.setText("Connect");
            clientNetwork.disconnect();
            //clientNetwork = null;
            recvThread.interrupt();
        }

    }

    private void onClose() {

        dispose();

    }

}

class TestClientFrame extends JFrame {
    TestClientFrame(String title, java.util.List<? extends Image> iconImages){
        super(title);
        setUndecorated(true);
        setVisible(true);
        setLocationRelativeTo(null);
        setIconImages(iconImages);
    }
}