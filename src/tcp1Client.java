package javaNetworking;

public class tcp1Client {
    public static void main(String[] args) {
        TCPClient client = new TCPClient("localhost",9000);
        client.start();
    }
}
