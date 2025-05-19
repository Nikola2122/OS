package javaNetworking;

import java.io.IOException;
import java.net.*;

class UDPClient extends Thread {
    String serverName;
    int port;
    InetAddress address;
    DatagramSocket socket;
    byte[] buffer;

    public UDPClient(String serverName, int port) throws UnknownHostException, SocketException {
        this.serverName = serverName;
        this.port = port;
        this.buffer = new byte[1024];

        this.address = InetAddress.getByName(serverName);
        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        buffer = "Hello World".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length,address,port);
        try {
            socket.send(packet);
            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length,address,port);
            socket.receive(receivedPacket);
            System.out.println(new String(receivedPacket.getData(),0,receivedPacket.getLength()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class UDPServer extends Thread {
    int port;
    byte [] buffer;


    public UDPServer(int port) {
        this.port = port;
        this.buffer = new byte[1024];
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();

                DatagramPacket back = new DatagramPacket(received.getBytes(), received.length(), address, port);
                socket.send(back);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
