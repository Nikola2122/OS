package KOLOKVIUMSKI;

import javaNetworking.KOLOKVIUMSKI.Client;

public class K1Client2 {
    public static void main(String[] args) {
        Client klient2 = new Client(7391,"localhost");
        klient2.start();
    }
}
