package KOLOKVIUMSKA3;

import java.io.*;
import java.net.Socket;

public class FileCounterClient extends Thread {
    int port;
    String servername;

    public FileCounterClient(int port, String servername) {
        this.port = port;
        this.servername = servername;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            socket = new Socket(servername,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write("Connected with " + socket.getPort() + " " + socket.getInetAddress()+ "\n");
            writer.flush();

            writer.write("GET /hello 1.1\n");
            writer.write("User-Agent: Mozilla.com\n");
            writer.write("Content-Type: text/html\n");
            writer.println();
            writer.flush();

            String line = reader.readLine();
            System.out.println(line);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
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
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        FileCounterClient client = new FileCounterClient(1000, "localhost");
        client.start();
    }
}
