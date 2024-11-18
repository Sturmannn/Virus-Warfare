package com.virus_warfare.client.models;

import com.google.gson.Gson;
import com.virus_warfare.server.models.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientSocketWrapper {
    private final InetAddress serverIp;
    private final int serverPort;
    private Socket clientSocket;
    private Player player;
    private DataInputStream dis;
    private DataOutputStream dos;
    private volatile boolean isRunning = true;
    private final Gson gson = new Gson();

    private volatile ServerResponseListener responseListener;

    public ClientSocketWrapper(InetAddress serverIp, int serverPort, ServerResponseListener responseListener) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.responseListener = responseListener;
        Socket temp_socket = null;
        DataInputStream temp_dis = null;
        DataOutputStream temp_dos = null;
        try {
            temp_socket = new Socket(serverIp, serverPort);
            temp_dis = new DataInputStream(temp_socket.getInputStream());
            temp_dos = new DataOutputStream(temp_socket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Error while creating DataInputStream and DataOutputStream (CS constructor)" + e.getMessage());
            return;
        }
        clientSocket = temp_socket;
        dis = temp_dis;
        dos = temp_dos;
    }


    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setPlayer(Player player) {
        if (player == null) {
            System.err.println("Player is null in ClientSocketWrapper (setPlayer)");
        }
        this.player = player;
    }

    public Player getPlayer() {
        if (player == null) {
            System.err.println("Player is null in ClientSocketWrapper (getPlayer)");
        }
        return player;
    }

    public void setResponseListener(ServerResponseListener responseListener) {
        this.responseListener = responseListener;
        System.out.println("Using responseListener (setResponse): " + responseListener.getClass().getSimpleName());
    }

    public void startListening() {
        Thread thread = new Thread(() -> {
            while (isRunning) {
                try {
                    Response response = receiveResponse();
                    if (response != null && response.getAction() != null) {
                        System.out.println("Received response: " + response.getAction());
                    } else {
                        System.out.println("Received response: null");
                    }

                    if (responseListener != null) {
                        responseListener.onResponseReceived(response);
                    }
                } catch (Exception e) {
                    System.err.println("Error while reading response from server (listenForResponses): " + e.getMessage());
                    stop();
                    break;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private Response receiveResponse() {
        try {
            String responseJson = dis.readUTF();
            System.out.println("Received response from server: " + responseJson);
            return gson.fromJson(responseJson, Response.class);
        } catch (Exception e) {
            System.err.println("Error while receiving response from server (receiveResponse)" + e.getMessage());
            return null;
        }
    }

    public void sendMessage(Message message) {
        try {
            String messageJson = gson.toJson(message);
            dos.writeUTF(messageJson);
            dos.flush();
            System.out.println("Sent message to server: " + messageJson);
        } catch (Exception e) {
            System.err.println("Error while sending message to server (sendMessage)" + e.getMessage());
        }
    }

    public void stop() {
        System.out.println("Stopping client socket...");
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (clientSocket != null) clientSocket.close();
            isRunning = false;
            System.out.println("Client socket stopped");
        } catch (Exception e) {
            System.err.println("Error while closing connection (closeConnection)" + e.getMessage());
        }
    }
}
