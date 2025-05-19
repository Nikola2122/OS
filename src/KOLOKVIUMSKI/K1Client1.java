package KOLOKVIUMSKI;

import javaNetworking.KOLOKVIUMSKI.Client;

public class K1Client1 {
    public static void main(String[] args) {
        Client klient1 = new Client(7391,"localhost");
        klient1.start();
    }
}
