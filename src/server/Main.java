package server;

public class Main {
    public static void main(String[] args){
        //lancement de serveur
        Server s = new Server(4444);
        s.start();
    }
}
