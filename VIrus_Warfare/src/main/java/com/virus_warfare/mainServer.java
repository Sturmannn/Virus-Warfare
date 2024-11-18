package com.virus_warfare;

import com.virus_warfare.server.models.ServerSocketWrapper;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class mainServer {
    private final int port = 3124;
    InetAddress ip = null;

    public void StartServer() {
        try {
            ip = InetAddress.getLocalHost();
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started IP: " + ip.getHostAddress() + " Port: " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                new ServerSocketWrapper(clientSocket);
            }
        } catch (Exception e) {
            System.err.println("Error while starting server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        mainServer server = new mainServer();
        server.StartServer();
    }
}
