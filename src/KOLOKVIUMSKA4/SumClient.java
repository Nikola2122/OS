package KOLOKVIUMSKA4;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SumClient extends Thread{
    int port;
    String address;

    public SumClient(int port, String address) {
        this.port = port;
        this.address = address;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        Socket socket = null;

        try {
            socket = new Socket(address,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            Scanner sc = new Scanner(System.in);
            writer.write("HANDSHAKE\n");
            writer.flush();
            String line = reader.readLine();
            System.out.println(line);

            while(!(line=sc.nextLine()).equals("STOP")){
                writer.write(line+"\n");
                writer.flush();
            }
            writer.write("STOP\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(line);
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void main(String[] args) {
        SumClient client = new SumClient(1000,"localhost");
        client.start();
    }
}
