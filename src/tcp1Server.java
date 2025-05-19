package javaNetworking;

public class tcp1Server {
    public static void main(String[] args) {
        TCPServer server = new TCPServer(9000);
        server.start();
    }
}
