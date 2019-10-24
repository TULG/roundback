package org.tulg.roundback.rbadmin;

import org.tulg.roundback.client.ClientNetwork;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * Created by jasonw on 9/24/2016.
 */
class MainWindow extends JFrame implements MouseListener {

    private JPanel panel;
    private JTree tree1;
    private JButton button1;
    private JButton button2;
    private JPanel infoPanel;
    private JList backupList;
    private final ClientNetwork clientNetwork;
    private final Preferences preferences;
    private final PreferencesWindow preferencesWindow;
    private boolean isConnected;
    private JMenuItem connItem;
    private final DefaultMutableTreeNode top;


    public MainWindow() {
        isConnected = false;
        //GridLayout gbl = (GridLayout) infoPanel.getLayout();



        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("Test 1");
        listModel.addElement("Test 2");
        listModel.addElement("Test 3");
        backupList.setModel(listModel);


        top = new DefaultMutableTreeNode("...");
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
        /*
        top.add(new DefaultMutableTreeNode("Test 1"));

        DefaultMutableTreeNode category = new DefaultMutableTreeNode("Test 2");
        category.add(new DefaultMutableTreeNode("Test 3"));
        top.add(category);
        */

        clientNetwork = new ClientNetwork();
        this.preferences = Preferences.userRoot().node("org.tulg.roundback.rbadmin");
        preferencesWindow = new PreferencesWindow(this);


        // load preferences.
        loadAndCheckPrefs();

        // Attempt to connect to the master server.
        isConnected = clientNetwork.connect();
        if(isConnected){
            try {
                clientNetwork.recvRaw();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            loadHosts(top);
        }


        model.setRoot(top);
        URL url = ClassLoader.getSystemClassLoader().getResource("RoundBack.png");

        setIconImage(new ImageIcon(url).getImage());


        GuiTreeRenderer guiTreeRenderer = new GuiTreeRenderer();
        tree1.setCellRenderer(guiTreeRenderer);

        tree1.addMouseListener(this);


        this.setTitle("Round Back");

        this.setJMenuBar(buildMainWindowMenu());
        this.add(panel);


        setBounds(-1, -1, 800, 600);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0); // An Exit Listener
            }
        });


    }

    private void loadHosts(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode hostsCategory = new DefaultMutableTreeNode("Backed Up Hosts");
        HostTreeNode tmpNode;
        for (String hostname : clientNetwork.getHosts()) {
            String[] parts = hostname.split(" ");

            tmpNode = new HostTreeNode(parts[2], parts[1]);
            hostsCategory.add(tmpNode);
        }
        top.add(hostsCategory);
    }

    public Preferences getPreferences() {
        return preferences;
    }

    private JMenuBar buildMainWindowMenu() {

        JMenuBar menuBar = new JMenuBar();
        // File Menu
        JMenu fileMenu = new JMenu("File");
        {
            JMenuItem prefsItem = new JMenuItem("Preferences");
            prefsItem.setAccelerator(KeyStroke.getKeyStroke("p"));
            prefsItem.addActionListener(e -> {
                //open the prefs window.
                PreferencesWindow dialog = preferencesWindow;

                dialog.pack();
                dialog.setVisible(true);
                // reload all the prefs
                loadAndCheckPrefs();
                if(isConnected){
                    clientNetwork.disconnect();
                    isConnected=false;
                    connItem.setText("Connect");
                }
            });
            fileMenu.add(prefsItem);

            fileMenu.addSeparator();

            String connectedMenu="Disconnect";
            if(!isConnected) {
                connectedMenu="Connect";

            }
            connItem = new JMenuItem(connectedMenu);
            connItem.setAccelerator(KeyStroke.getKeyStroke("c"));
            connItem.addActionListener(e -> {
                // Disconnect or connect.
                if(isConnected) {
                    clientNetwork.disconnect();
                    isConnected=false;
                    connItem.setText("Connect");
                    top.removeAllChildren();

                } else {
                    clientNetwork.connect();
                    loadHosts(top);
                    isConnected=true;
                    connItem.setText("Disconnect");
                }
                DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
                model.reload();
            });
            fileMenu.add(connItem);

            fileMenu.addSeparator();

            JMenuItem closeItem = new JMenuItem("Exit");
            closeItem.setAccelerator(KeyStroke.getKeyStroke("x"));
            closeItem.addActionListener(e -> System.exit(2));
            fileMenu.add(closeItem);

        }

        menuBar.add(fileMenu);



        return menuBar;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private void loadAndCheckPrefs(){
        clientNetwork.setServer(preferences.get("masterServer",""));

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        TreePath tp = tree1.getSelectionPath();
        //TreePath tp = tree1.getPathForLocation(me.getX(), me.getY());
        if(tp != null) {
            Object tmpObj = tp.getLastPathComponent();
            if(tmpObj.getClass() == HostTreeNode.class) {
                HostTreeNode tn = (HostTreeNode) tp.getLastPathComponent();
                int hid = tn.getHid();
                if(hid > 0 ) {
                    infoPanel.setVisible(true);
                    DefaultListModel listModel = new DefaultListModel();

                    for (String backupStr : clientNetwork.getBackups(hid)) {
                        String[] parts = backupStr.split(" ");
                        Date date = new Date();
                        date.setTime(Long.valueOf(parts[3]));
                        listModel.addElement("Start Time: " + date.toString() + " (Status: " + parts[7] + ")");


                    }/*
                    listModel.addElement("hid: " + hid + "Test 1");
                    listModel.addElement("hid: " + hid + "Test 2");
                    listModel.addElement("hid: " + hid + "Test 3");*/
                    backupList.setModel(listModel);
                } else {
                    infoPanel.setVisible(false);
                }
            } else {
                infoPanel.setVisible(false);
            }
        } else {
            infoPanel.setVisible(false);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
