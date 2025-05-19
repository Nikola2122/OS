package javaNetworking;

public class UDP1Server {
    public static void main(String[] args) {
        UDPServer server = new UDPServer(9000);
        server.start();
    }
}
