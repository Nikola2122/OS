package javaNetworking;

public class tcp2Server {
    public static void main(String[] args) {
        TCPServerBrowser serverBrowser = new TCPServerBrowser(9000);
        serverBrowser.start();
    }
}
