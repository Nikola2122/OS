package javaNetworking;
import java.io.*;
import java.net.*;
import java.util.Scanner;

class WorkerThread extends Thread{

    private Socket socket;

    public WorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            System.out.println("Connected with " + socket.getInetAddress() + ":" + socket.getPort());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line = null;

            while(!(line=reader.readLine()).isEmpty()){
                System.out.println(line);

                writer.write("Okay i got that i send u something back...\n");
                writer.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (writer != null){
                writer.close();
            }
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

class TCPServer extends Thread{
    private int port;

    public TCPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        System.out.println("TCP server is starting...");

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Socket failed");
            return;
        }

        System.out.println("Socket is started, tcp server is running.");
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                WorkerThread t = new WorkerThread(socket);
                t.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class TCPClient extends Thread{
    private String hostname;
    private int port;

    public TCPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        PrintWriter writer = null;
        BufferedReader reader = null;
        Scanner sc = new Scanner(System.in);

        try {
            socket = new Socket(hostname,port);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(true){
                String line = sc.nextLine();
                writer.write(line + "\n");
                writer.flush();

                line=reader.readLine();
                System.out.println(line);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
public class tcp1 {

}
