package javaNetworking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

class RequestProcessor {
    String command;
    String uri;
    HashMap<String,String> hashMap;

    public RequestProcessor(String [] lines) {
        String [] parts = lines[0].split(" ");
        command = parts[0];
        uri = parts[1];
        hashMap = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            parts = lines[i].split(": ");
            hashMap.put(parts[0], parts[1]);
        }
    }
}
class HTTPThread extends Thread{

    private Socket socket;

    public HTTPThread(Socket socket) {
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
            StringBuilder builder = new StringBuilder();
            String line;

            while(!(line=reader.readLine()).isEmpty()){
                builder.append(line).append('\n');
            }
            RequestProcessor processor = new RequestProcessor(builder.toString().split("\n"));
            writer.write("HTTP/1.1 200 OK\n\n");

            if (processor.command.equals("GET") && processor.uri.equals("/")){
                reader = new BufferedReader(new FileReader("Auditoriski\\src\\javaNetworking\\PROJECT.html"));
                while(!(line=reader.readLine()).isEmpty()){
                    writer.write(line+"\n");
                    writer.flush();
                }
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

class TCPServerBrowser extends Thread{
    private int port;

    public TCPServerBrowser(int port) {
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
                HTTPThread t = new HTTPThread(socket);
                t.start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

public class tcp2 {

}
