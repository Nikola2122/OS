package javaNetworking;

import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP1Client {
    public static void main(String[] args) throws SocketException, UnknownHostException {
        UDPClient client = new UDPClient("localhost",9000);
        client.start();
    }
}
