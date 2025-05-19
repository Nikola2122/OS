package KOLOKVIUMSKA2;

public class ClientMain2 {
    public static void main(String[] args) {
        FileClient cl2 = new FileClient(9000,"localhost");
        cl2.start();
    }
}
