package javaNetworking.LAB5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.Semaphore;


class ChatClient extends Thread {
    int port;
    String address;

    public ChatClient(int port, String address) {
        this.port = port;
        this.address = address;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        Scanner sc = new Scanner(System.in);


        try {
            socket = new Socket(address,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String serverMsg = "";

            while(true){
                String msg = sc.nextLine();
                writer.write(msg+"\n");
                writer.flush();
                serverMsg = reader.readLine();
                if(serverMsg==null || serverMsg.startsWith("You haven't connected")) break;

                System.out.println(serverMsg);
                if (msg.equals("END")){
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
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
}

class ChatWorker extends Thread {
    Socket socket;
    ArrayList<String> connectedClients;
    Semaphore semaphore;

    public ChatWorker(Socket socket, ArrayList<String> connectedClients, Semaphore semaphore) {
        this.socket = socket;
        this.connectedClients = connectedClients;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        System.out.println("Trying to log in with client: " + socket.getPort() + " " + socket.getInetAddress());
        String index = "";
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line;

            while((line = reader.readLine()) != null && !line.equals("END")){
                if (line.startsWith("login")){
                    index = line.split(":")[1];
                    if (index.length()!=6){
                        System.out.println("ERROR: BAD INDEX");
                        writer.write("Enter right index format\n");
                        writer.flush();
                    }
                    System.out.println("Logged in client with index: " + index);
                    writer.write("Waiting for hello message\n");
                    writer.flush();
                }
                else if(line.startsWith("hello") && index.length()==6 && line.split(":")[1].equals(index)){
                    semaphore.acquire();
                    connectedClients.add(index);
                    semaphore.release();
                    System.out.println(line);;
                    writer.write("You can communicate with other clients now\n");
                    writer.flush();
                }
                else{
                    if(index.length()!=6 || !connectedClients.contains(index)){
                        System.out.println("ERROR: NOT LOGGED IN");
                        writer.write("You haven't connected as you should, please log in\n");
                        writer.flush();
                        break;
                    }
                    else {
                        String receiver = line.split(":")[0];
                        semaphore.acquire();
                        if (connectedClients.contains(receiver)) {
                            PrintWriter writer1 = new PrintWriter(new FileWriter("Auditoriski\\src\\javaNetworking\\LAB5\\chatlog" + receiver + ".txt",true));
                            PrintWriter writer2 = new PrintWriter(new FileWriter("Auditoriski\\src\\javaNetworking\\LAB5\\chatlog" + index + ".txt",true));
                            writer1.write(line.split(":")[1] + " - RECEIVED\n");
                            writer1.flush();
                            writer2.write(line.split(":")[1] + " - SENT\n");
                            writer2.flush();
                            writer1.close();
                            writer2.close();

                            System.out.println("Sending message");
                            writer.write("Your message has been sent to " + receiver + "\n");
                            writer.flush();
                        }
                        else{
                            System.out.println("ERROR: RECEIVER NOT FOUND");
                            writer.write("Receiver not logged in\n");
                            writer.flush();
                        }
                        semaphore.release();
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (writer!=null && index.length()==6){
                writer.write("You are logged out "+ index + "\n");
                writer.flush();
                writer.close();
            }
            if (reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            connectedClients.remove(index);
            semaphore.release();
        }
    }
}


class ChatServer extends Thread{
    int port;
    Semaphore semaphore;
    ArrayList<String> connectedClients;

    public ChatServer() {
        this.port = 9753;
        semaphore = new Semaphore(1);
        connectedClients = new ArrayList<>();
    }

    @Override
    public void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            System.out.println("Server is started...");
            while (true) {
                Socket client = socket.accept();
                new ChatWorker(client, connectedClients, semaphore).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class index_233127 {
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
}
