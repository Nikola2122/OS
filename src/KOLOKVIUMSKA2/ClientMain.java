package KOLOKVIUMSKA2;

import javaNetworking.KOLOKVIUMSKA2.FileClient;

public class ClientMain {
    public static void main(String[] args) {
        FileClient client = new FileClient(9000,"localhost");
        client.start();
    }
}
