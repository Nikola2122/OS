package KOLOKVIUMSKA2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

class FileClient extends Thread {
    int port;
    String server;

    public FileClient(int port, String server) {
        this.port = port;
        this.server = server;
    }

    @Override
    public void run() {
        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        Scanner sc = new Scanner(System.in);

        try {
            socket = new Socket(server,port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));


            while(true) {
                String line = sc.nextLine();
                if(line.startsWith("DOWNLOAD")){
                    writer.write(line + "\n");
                    writer.flush();
                    while(true) {
                        line = reader.readLine();
                        System.out.println(line);
                        if (line.equals("END")){
                            break;
                        }
                    }
                }
                else if(line.startsWith("UPLOAD")){
                    writer.write(line + "\n");
                    String fileName = sc.nextLine();
                    writer.write("BEGIN " + fileName);
                    writer.flush();
                    while(true) {
                        line = sc.nextLine();
                        writer.write(line + "\n");
                        writer.flush();
                        if (line.equals("END")){
                            break;
                        }
                    }
                }
                else if(line.equals("LIST")){
                    writer.write(line + "\n");
                    writer.flush();
                    while(!(line = reader.readLine()).isEmpty()) {
                        System.out.println(line);
                    }
                }
                else{
                    break;
                }
            }
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
}


class FileWorker extends Thread {
    Socket socket;
    String path;
    Semaphore s1;

    public FileWorker(Socket socket, String path, Semaphore s1) {
        this.socket = socket;
        this.path = path;
        this.s1 = s1;
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String line = "";
            while(!(line = reader.readLine()).equals("DIS")) {
                if (line != null && line.equals("UPLOAD")){
                    s1.acquire();
                    System.out.println("UPLOADING...");
                    StringBuilder builder = new StringBuilder();
                    line = reader.readLine();
                    String [] parts = line.split(" "); // BEGIN imetoNaFajlo
                    String name = parts[1];
                    System.out.println(line);

                    while(!(line=reader.readLine()).equals("END")){
                        builder.append(line).append("\n");
                    }

                    File file = new File(path + "\\" + name + ".txt");
                    file.createNewFile();

                    PrintWriter writer2 = new PrintWriter(new FileWriter(file, true));
                    writer2.write(builder + "\n");
                    writer2.flush();
                    writer2.close();
                    s1.release();
                }
                else if(line != null && line.equals("LIST")){
                    s1.acquire();
                    System.out.println("LISTING...");
                    File file = new File(path);
                    File [] files = file.listFiles();
                    for (File f : files){
                        writer.write(f.getName() + " Length: " + f.length() + " Bytes\n");
                        writer.flush();
                    }
                    s1.release();
                }
                else if(line != null && line.startsWith("DOWNLOAD")){
                    s1.acquire();// donwload file1
                    System.out.println("DOWNLOADING...");
                    String [] parts = line.split(" ");
                    String name = parts[1];
                    File file = new File(path + "\\" + name + ".txt");
                    if (file.exists()){
                        BufferedReader reader2 = new BufferedReader(new FileReader(file));
                        writer.write("BEGIN\n");
                        writer.flush();
                        while ((line = reader2.readLine()) != null) {
                            writer.write(line + "\n");
                            writer.flush();
                        }
                        writer.write("END\n");
                        writer.flush();
                        reader2.close();
                    }
                    s1.release();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
        }
    }
}

public class FileServer extends Thread {
    int port;
    String path = "Auditoriski\\src\\javaNetworking\\KOLOKVIUMSKA2\\files";
    Semaphore s1 = new Semaphore(1);

    public FileServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            System.out.println("Server started on port: " + port + " and IP: " + socket.getInetAddress());
            while(true){
                Socket newConn = socket.accept();
                new FileWorker(newConn,path,s1).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public static void main(String[] args) {
        FileServer fs = new FileServer(9000);
        fs.start();
    }
}

