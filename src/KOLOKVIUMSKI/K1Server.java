package KOLOKVIUMSKI;

import javaNetworking.KOLOKVIUMSKI.Server;

public class K1Server {
    public static void main(String[] args) {
        Server server = new Server(7391);
        server.start();
    }
}
