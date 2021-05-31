package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerThread extends Thread {
    private Socket socket;
    private Server server;
    private String id;
    private DataOutputStream os;

    public ServerThread(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            try {
                broadcast("serveur> " + id + " est deconnecte");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                int k = server.getServerThreads().indexOf(this);
                boolean remove = server.getServerThreads().remove(this);
                System.out.println(id + " a quitte le chat \n \n");
            }
        }
    }

    private void handleClientSocket() throws IOException {
        DataInputStream is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
        //capter l'ID du client
        id = is.readUTF();
        String line = null;
        //afficher le message de welcoming
        handleLogin();
        //informer d'un nouveau client
        broadcast("serveur> " + id + " est connecte");
        //looper pour recevoir le messages
        while (true) {
            line = is.readUTF();
            if ((id + "> Quit").equalsIgnoreCase(line)) {
                broadcast("serveur> " + id + " est deconnecte");
                int k = server.getServerThreads().indexOf(this);
                ServerThread remove = server.getServerThreads().remove(k);
                System.out.println(id + " a quitte le chatroom \n \n");
                socket.close();
                is.close();
                os.close();
                break;
            }
            //broadcasting
            broadcast(line);
        }
    }

    public void handleLogin() throws IOException {
        String msg = "server> Bienvenue " + id + " vous etes bien connectes : ";
        System.out.println("user " + id + " is in \n \n");
        List<ServerThread> serverThreads = server.getServerThreads();
        if (serverThreads.size() <4){
            if (serverThreads.size() == 1){
                msg = msg +"y'a que toi connecte";
            };
            if(serverThreads.size() == 2){
                msg = msg + serverThreads.get(0).getid() + " est aussi connecte";
            }
            if (serverThreads.size() == 3){
                msg = msg +serverThreads.get(0).getid() + " et " + serverThreads.get(1).getid() + " sont aussi connectes";
            }
        }
        else{
            for (int i = 1; i<serverThreads.size()-1; i++) {
                msg = msg + " " + serverThreads.get(i).getid() + ", ";
            }
            msg = msg + " et " + serverThreads.get(serverThreads.size()-1).getid() + " sont aussi connectes";
        }
        os.writeUTF(msg);
    }

    public void update(String msg) throws IOException {
        os.writeUTF(msg);
    }

    public String getid() {
        return (id);
    }

    private void broadcast(String msg) throws IOException {
        List<ServerThread> serverThreads = server.getServerThreads();
        for (ServerThread t : serverThreads) {
            if (!id.equalsIgnoreCase(t.getid())) {
                t.update(msg);
            }
        }
    }
}



