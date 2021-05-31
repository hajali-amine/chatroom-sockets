package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Client{
    private String appName = "ChatRoom";

    private Client cli;
    private JFrame newFrame = new JFrame(appName);
    private JTextField messageBox;
    private JTextArea chatBox;
    private JTextField usernameBox;
    private JFrame preFrame;

    private String serverName;
    private int serverPort;
    private String id;
    private Socket s;
    private DataInputStream is;
    private DataOutputStream os;

    String  username;

    //constructeur
    public Client(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //instance du client
                Client cli = new Client("localhost", 4444);
                //La fenetre
                cli.preDisplay(cli);
            }
        });
    }

    private void connect() {
        try {
            this.s = new Socket(serverName, serverPort);
            System.out.println("client port est " + s.getLocalPort());
            this.is = new DataInputStream(s.getInputStream());
            this.os = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preDisplay(Client cli) {
        //premiere fenetre: choisir le pseudonyme
        newFrame.setVisible(false);
        preFrame = new JFrame(appName);
        preFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        usernameBox = new JTextField(15);
        JLabel chooseUsernameLabel = new JLabel("Votre Pseudonyme:");
        JButton enterServer = new JButton("Enter ChatRoom");
        enterServer.addActionListener(new EnterServerButtonListener(cli));
        //activation de la deuxieme fenetre
        JPanel prePanel = new JPanel(new GridBagLayout());
        GridBagConstraints preRight = new GridBagConstraints();
        preRight.insets = new Insets(0, 0, 0, 10);
        preRight.anchor = GridBagConstraints.EAST;
        GridBagConstraints preLeft = new GridBagConstraints();
        preLeft.anchor = GridBagConstraints.WEST;
        preLeft.insets = new Insets(0, 10, 0, 10);
        preRight.fill = GridBagConstraints.HORIZONTAL;
        preRight.gridwidth = GridBagConstraints.REMAINDER;

        prePanel.add(chooseUsernameLabel, preLeft);
        prePanel.add(usernameBox, preRight);
        preFrame.add(BorderLayout.CENTER, prePanel);
        preFrame.add(BorderLayout.SOUTH, enterServer);
        preFrame.setSize(300, 100);
        preFrame.setLocation(500, 500);
        preFrame.setVisible(true);
    }

    public void display() {
        //preparation de la deuxieme fenetre
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JPanel southPanel = new JPanel();
        southPanel.setBackground(Color.BLACK);
        southPanel.setLayout(new GridBagLayout());
        //message par defaut dans la zone ou vous pouvez ecrire les message
        messageBox = new JTextField("écrivez votre message ici",30 );
        messageBox.requestFocusInWindow();
        //pour permettre d'envoyer le message en cliquant sur entrée
        messageBox.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                messageBox.setText("");
            }
            public void focusLost(FocusEvent e) {
            }
        });

        chatBox = new JTextArea();
        chatBox.setBackground(Color.LIGHT_GRAY);
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Robotto", Font.PLAIN, 13));
        chatBox.setForeground(Color.BLACK);
        chatBox.setLineWrap(true);
        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;
        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;
        southPanel.add(messageBox, left);
        mainPanel.add(BorderLayout.SOUTH, southPanel);
        newFrame.add(mainPanel);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(600, 400);
        newFrame.setTitle(this.appName + " - " + this.username);
        newFrame.setVisible(true);

        //thread pour l'ecriture des messages
        Writer w = new Writer();
        w.start();
        //thread pour lire les messages
        Reader r = new Reader();
        r.start();
    }

    class Writer extends Thread{
        @Override
        public void run() {
            messageBox.addActionListener(new SendMessageButtonListener());
        }
    }

    class Reader extends Thread{
        @Override
        public void run() {
            String line;
            while(true){
                try {
                    line = is.readUTF();
                    chatBox.append(line + "\n");
                } catch (IOException e) { }
            }
        }
    }

    class EnterServerButtonListener implements ActionListener {
        private Client cli;
        public EnterServerButtonListener(Client cli){
            this.cli = cli;
        }
        public void actionPerformed(ActionEvent event) {
            username = usernameBox.getText();
            if (username.length() < 1) {
                System.out.println("No!");
            } else {
                try {
                    //connection des I/O avec le serveur
                    cli.connect();
                    os.writeUTF(username);
                    id = username;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                preFrame.setVisible(false);
                //deuxieme fenetre
                display();
                try {
                    chatBox.append( is.readUTF() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class SendMessageButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                os.writeUTF(id +"> "+ messageBox.getText());
                if (messageBox.getText().equalsIgnoreCase("Quit")){
                    s.close();
                    is.close();
                    os.close();
                    System.exit(0);
                }
            } catch (SocketException se){} catch (Exception ex) {
                System.err.println("you're disconnected");
            }

            chatBox.append(id +"> "+ messageBox.getText()+"\n");
            messageBox.setText("");
            messageBox.requestFocusInWindow();
        }
    }
}