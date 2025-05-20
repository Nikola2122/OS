package javaNetworking.LAB5;

public class ChatClient1 {
    public static void main(String[] args) {
        ChatClient c1 = new ChatClient(9753,"localhost");
        c1.start();
    }
}
