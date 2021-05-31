package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private ArrayList<ServerThread> serverThreads = new ArrayList<ServerThread>();
    private int port;
    public Server(int port){
        this.port=port;
    }
    public List<ServerThread> getServerThreads(){
        return serverThreads;
    }
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("attente de client");
                Socket clientSocket = serverSocket.accept();
                System.out.println("la connexion est accepter" + clientSocket);
                //lancement de thread qui va s'occuper de client
                ServerThread t = new ServerThread(this, clientSocket);
                serverThreads.add(t);
                t.start();
            }
        } catch (Exception e) {}
    }
}