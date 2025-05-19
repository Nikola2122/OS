package KOLOKVIUMSKI;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Semaphore;


class Worker extends Thread {
    Socket socket;
    ArrayList<String> array;
    Semaphore semaphore;

    public Worker(Socket socket, ArrayList<String> array, Semaphore semaphore) {
        this.socket = socket;
        this.array = array;
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        PrintWriter writer2 = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String str = reader.readLine();
                if (str != null && str.equals("HANDSHAKE")) {
                    System.out.println(str);
                    writer.write("Logged In " + socket.getInetAddress() + "\n");
                    writer.flush();
                }
                else if (str != null && str.equals("STOP")) {
                    semaphore.acquire();
                    writer.write("This is the current number of words " + array.size() + "\n");
                    writer.flush();
                    semaphore.release();
                    break;
                }
                else {
                    System.out.println(str);
                    semaphore.acquire();
                    int idx = array.indexOf(str);
                    if (idx == -1) {
                        writer.write(str + " NEMA\n");
                        writer.flush();
                        LocalDateTime now = LocalDateTime.now();
                        writer2 = new PrintWriter(new FileWriter("C:\\Users\\Nikola Iliev\\IdeaProjects\\OS\\Auditoriski\\src\\javaNetworking\\KOLOKVIUMSKI\\kolokviumska1File.txt",true));
                        writer2.write(str + " Time: " + now + " IP: " + socket.getInetAddress() + "\n");
                        writer2.close();
                        array.add(str);
                    }
                    else{
                        writer.write(str + " IMA\n");
                        writer.flush();
                    }
                    semaphore.release();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
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

class Server extends Thread {
    int port;
    Semaphore semaphore;
    ArrayList<String> array;

    public Server(int port) {
        this.port = port;
        semaphore = new Semaphore(1);
        array = new ArrayList<>();
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");
            while (true) {
                Socket newConnection = serverSocket.accept();
                new Worker(newConnection,array,semaphore).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Client extends Thread {
    int port;
    String hostname;

    public Client(int port, String hostname) {
        this.port = port;
        this.hostname = hostname;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            socket = new Socket(hostname,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("HANDSHAKE\n");
            writer.flush();
            Scanner sc = new Scanner(System.in);

            while (true) {
                String str = reader.readLine();
                System.out.println(str);
                if (str.startsWith("This is the current")){
                    break;
                }
                String inp = sc.nextLine();
                writer.write(inp+"\n");
                writer.flush();
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

public class kolokviumska1 {
}
