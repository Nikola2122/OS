package KOLOKVIUMSKA4;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class SumServer extends Thread{
    int port;
    int suma;
    String path = "Auditoriski\\src\\javaNetworking\\KOLOKVIUMSKA4\\SumServer.txt";
    Semaphore semaphore = new Semaphore(1);

    public SumServer(int port) {
        this.port = port;
        suma = 0;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server has started.");
            while(true){
                Socket socket = serverSocket.accept();
                new SumThread(socket).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    class SumThread extends Thread{
        Socket socket;

        public SumThread(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            PrintWriter writer = null;

            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                String line = reader.readLine();

                System.out.println("HANDSHAKE FROM CLIENT: " + line);
                writer.write("Logged in" + socket.getInetAddress() + "\n");
                writer.flush();

                while(!(line=reader.readLine()).equals("STOP")){
                    int num = Integer.parseInt(line);
                    semaphore.acquire();
                    suma += num;
                    PrintWriter toFile = new PrintWriter(new FileWriter(path,true));
                    Date date = new Date();
                    toFile.write(line + " " + socket.getInetAddress() + " " + date.getTime() + "\n");
                    toFile.flush();
                    toFile.close();
                    semaphore.release();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    semaphore.acquire();
                    writer.write("Logged Out, current sum: " + suma + "\n");
                    writer.flush();
                    semaphore.release();
                    if (writer != null) {
                        writer.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) {
        SumServer server = new SumServer(1000);
        server.start();
    }
}
