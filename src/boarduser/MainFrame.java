package boarduser;

import javax.json.JsonObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import Client.Communication;
import Protocol.Serialiser;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public static final String DATE_FORMAT_NOW = "HH:mm:ss:SSS";
    private JTabbedPane mainPanel;
    private ManagerPanel managerPanel;
    private UserPanel userPanel;

    private JScrollPane chatContainer;
    private DefaultListModel<String> chatRoomModel;
    private JList<String> chatRoom;
    private JScrollPane messagePanel;
    private JTextPane messageBox;
    private JButton sendButton;
    private JScrollPane userListPanel;

    private JButton kickButton;

    private ClientMns boss;
    private boolean isBoss;
    private String canvasName;
    private String userName;
    private int userID;
    private Communication channel;

    /*UserList Related*/
    private DefaultListModel<String> userListModel;
    private HashMap<Integer, String> userPool;
    private JList<String> userList;

    /**
     * @wbp.parser.constructor
     */
    public MainFrame(String canvasName, boolean isBoss, String username) {

        this.isBoss = isBoss;
        this.canvasName = canvasName;
        this.userName = username;
        initComponent();
    }

    public MainFrame(String canvasName, boolean isBoss, String username, ClientMns boss) {

        this.isBoss = isBoss;
        this.canvasName = canvasName;
        this.userName = username;
        this.boss = boss;
        initComponent();
    }

    public void initComponent() {
    	this.setResizable(false);
        chatRoomModel = new DefaultListModel<String>();
        chatRoom = new JList<String>(chatRoomModel);

        userPanel = new UserPanel(canvasName);//, channel);
        mainPanel = new JTabbedPane();

        userPool = new HashMap<Integer, String>();
        userListModel = new DefaultListModel<String>();

        getContentPane().add(mainPanel);
        // boss.readCanvasList();
        setupButtons();

        if (isBoss) {
            boss.setActiveCanvas(userPanel.getCurrentCanvas());
            managerPanel = new ManagerPanel(boss, userPanel);
            mainPanel.addTab("Manager Panel", managerPanel);
            updateUserList();

        }
        else {
        	kickButton.setEnabled(false);
        	kickButton.setBorderPainted(false);
        	kickButton.setText("");
        }

        mainPanel.addTab("User Panel", userPanel);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                myWindowClosing();

            }
        ;

        });
		Font font = new Font("Times New Roman", Font.PLAIN, 24);
        setFont(font);

        initialLayout();
        initialButtonFunction();

        /*User List Testing*/
 /*userPool.put(2, "Leo");
                userPool.put(6, "Dan");
                
                String useless = JsonConverter.converToJson(userPool);
                updateUserList(useless);*/
    }

    private void myWindowClosing() {

        channel.windowClose();

    }

    public void setupButtons() {

        chatContainer = new JScrollPane();
        messagePanel = new JScrollPane();
        userListPanel = new JScrollPane();
        messageBox = new JTextPane();
        sendButton = new JButton("Send");
        kickButton = new JButton("Kick");
        kickButton.setVisible(true);
        chatRoom = new JList<String>();
        userList = new JList<String>();

        chatContainer.setViewportView(chatRoom);

        messagePanel.setViewportView(messageBox);

        sendButton.setText("Send");

        userListPanel.setViewportView(userList);

        kickButton.setText("Kick");

    }

    private void initialButtonFunction() {

        sendButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                String messageToSend;
                Message message;
                if (!(messageToSend = messageBox.getText()).equals("")) {

                    messageBox.setText("");
                    message = new Message(userName, messageToSend);
                    chatRoomModel.addElement(message.toString());
                    chatRoom.setModel(chatRoomModel);
                    sendMessage(message);
                }

            }

        });

        kickButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String userName = userList.getSelectedValue();
                int userID = boss.denyAccess(userName);

                if (userID != -10) {
                    channel.sendDeny(userID);                
                    JOptionPane.showMessageDialog(null, "The user is removed.");
                    updateUserList();
                    System.out.println("[DEBUG] remaining user" + userPool.values());
                } else {
                    JOptionPane.showMessageDialog(null, "Remove failed.");
                }

            }
        }
        );

    }

    private void sendMessage(Message msg) {

        String message = JsonConverter.convertToJson(msg);
        channel.sendChat(message);

    }

    public void upodateMessage(String msg) {

        Message msessage = JsonConverter.convertFromJson(msg);

        chatRoomModel.addElement(msessage.toString());
        chatRoom.setModel(chatRoomModel);

    }

    /*Method for communication to update the user list*/
    public void updateUserList(String userPoolList) {

        userPool = JsonConverter.convertFromJsonUser(userPoolList);
        Set<Integer> keySet = userPool.keySet();

        Iterator<Integer> iteratorKey = keySet.iterator();
        userListModel.clear();

        while (iteratorKey.hasNext()) {
            int tempID = iteratorKey.next();
            if (tempID != userID) {
                userListModel.addElement(userPool.get(tempID));
            }
        }

        userList.setModel(userListModel);
    }

    /*Method for manager to update the user list*/
    public void updateUserList() {

        userPool = boss.getUserInfo();

        Set<Integer> keySet = userPool.keySet();
        
        Iterator<Integer> iteratorKey = keySet.iterator();
        userListModel.clear();

        while (iteratorKey.hasNext()) {
            int tempID = iteratorKey.next();
            if (tempID != userID) {
                userListModel.addElement(userPool.get(tempID));
            }
        }

        userList.setModel(userListModel);
    }

    public String getUserName(int ID)
    {
    	return this.userPool.get(ID);
    }
    
    /*Method to set the userID*/
    public void setUserID(int id) {
        userID = id;
    }

    public UserPanel getUserPanel() {
        return this.userPanel;
    }

    public void buildChannel(Communication service) {
        this.channel = service;
        this.userPanel.getCurrentCanvas().setChannel(service);
    }

    

    public void initialLayout() {

    	GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(mainPanel, GroupLayout.DEFAULT_SIZE, 705, GroupLayout.DEFAULT_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addComponent(messagePanel).addGap(18, 18, 18)
                                        .addComponent(sendButton, GroupLayout.PREFERRED_SIZE, 110,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addGap(28, 28, 28))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(chatContainer, GroupLayout.DEFAULT_SIZE, 410,
                                                GroupLayout.DEFAULT_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(userListPanel)
                                .addComponent(kickButton, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE))
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                		.addContainerGap(9, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        		.addGroup(layout.createSequentialGroup().addComponent(mainPanel, 
                        				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,GroupLayout.DEFAULT_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE, false)
                                                .addComponent(userListPanel,  GroupLayout.PREFERRED_SIZE, 638,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addGap(5)
                                                .addComponent(chatContainer,  GroupLayout.PREFERRED_SIZE, 638,
                                                        GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(messagePanel, GroupLayout.PREFERRED_SIZE, 128,
                                                                GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup().addGap(45, 45, 45)
                                                        .addComponent(sendButton, GroupLayout.PREFERRED_SIZE, 53,
                                                                GroupLayout.PREFERRED_SIZE))
                                                .addGroup(
                                                        layout.createSequentialGroup().addGap(12, 12, 12).addComponent(
                                                                kickButton, GroupLayout.PREFERRED_SIZE, 128,
                                                                GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(9, Short.MAX_VALUE)));

        pack();

    }
    // public static void main(String[] args) {
    //
    // MainFrame frame = new MainFrame("sb", true, "Leo",null);
    //
    // frame.setVisible(true);
    // }
}
