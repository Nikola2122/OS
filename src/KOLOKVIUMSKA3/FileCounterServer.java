package KOLOKVIUMSKA3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.RandomAccess;
import java.util.concurrent.Semaphore;

public class FileCounterServer extends Thread {
    int port;
    Semaphore semaphore;
    String filePath;
    String counterPath;

    public FileCounterServer(int port, String filePath, String counterPath) {
        this.port = port;
        this.semaphore = new Semaphore(1);
        this.filePath = filePath;
        this.counterPath = counterPath;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        System.out.println("Server is starting...");

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started.");

            while (true) {
                Socket socket = serverSocket.accept();
                new Worker(socket).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Worker extends Thread {
        Socket socket;

        public Worker(Socket socket) {
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
                StringBuilder builder = new StringBuilder();
                System.out.println(line);

                while (!(line = reader.readLine()).isEmpty()) {
                    builder.append(line).append("\n");
                }

                Request request = new Request(builder);
                semaphore.acquire();
                PrintWriter writerToFile = new PrintWriter(new FileWriter(filePath, true));
                writerToFile.write(String.format("REQUEST: %s, VERSION: %s, URI: %s, User-Agent: %s\n",request.request,
                        request.version,request.uri,request.headers.get("User-Agent")));
                writerToFile.flush();
                writerToFile.close();

                RandomAccessFile file = new RandomAccessFile(counterPath, "rw");
                int count = 0;
                try {
                    count = file.readInt();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                file.seek(0);
                file.writeInt(count+1);
                file.close();
                semaphore.release();

                writer.write("Your connection has been monitored\n");
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finally {
                if ( writer != null ) {
                    writer.close();
                }
                if ( socket != null ) {
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
            }
        }
    }

    class Request{
        String request;
        String version;
        String uri;
        Map<String,String> headers;

        public Request(StringBuilder builder) {
            String [] parts = builder.toString().split("\n");
            String [] parts2 = parts[0].split(" ");
            this.request = parts2[0];
            this.version = parts2[1];
            this.uri = parts2[2];
            this.headers = new HashMap<>();

            for (int i = 1; i < parts.length; i++) {
                String[] parts3 = parts[i].split(": ");
                this.headers.put(parts3[0], parts3[1]);
            }
        }

    }

    public static void main(String[] args) {
        FileCounterServer server = new FileCounterServer(1000,"Auditoriski\\src\\javaNetworking\\" +
                "KOLOKVIUMSKA3\\connections.txt","Auditoriski\\src\\javaNetworking\\" +
                "KOLOKVIUMSKA3\\counter.bin");
        server.start();
    }
}
