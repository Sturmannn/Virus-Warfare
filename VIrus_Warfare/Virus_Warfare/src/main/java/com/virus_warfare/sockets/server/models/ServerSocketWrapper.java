package com.virus_warfare.sockets.server.models;

import com.google.gson.Gson;
import com.virus_warfare.sockets.client.models.Message;
import com.virus_warfare.sockets.client.models.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ServerSocketWrapper {
    private final Socket clientSocket;
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Gson gson = new Gson();

    private Game game;
    private Player player;
    private final int rows = 10;
    private final int cols = 10;

    private volatile boolean isRunning = true;

    public ServerSocketWrapper(Socket clientSocket) {
        this.clientSocket = clientSocket;
        DataInputStream temp_dis = null;
        DataOutputStream temp_dos = null;
        try {
            temp_dis = new DataInputStream(clientSocket.getInputStream());
            temp_dos = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error while creating DataInputStream and DataOutputStream (SS constructor)" + e.getMessage());
        }
        dis = temp_dis;
        dos = temp_dos;
        new Thread(this::run).start();
    }

    private void run() {
        while (isRunning) {
            Message message = receiveMessage();
            if (message != null)
                System.out.println("Received message: " + message.getNickname() + " " + message.getAction());
            handleMessage(message);
        }
    }

    private void handleMessage(final Message message) {
        if (message == null || message.getAction() == null) {
            System.err.println("Received 'null' message");
            return; // Выход из метода, если сообщение равно null
        }

        switch (message.getAction()) {
            case CONNECT:
                game = Game.getInstance(rows, cols);
                System.out.println("Player: " + message.getPlayer().getName());
                if (game.getPlayers().size() == 2) {
                    Response response = new Response(ResponseAction.GAME_FULL);
                    sendResponse(response);
                    closeConnection();
                } else if (game.getPlayers().size() == 1) {
                    game.addPlayer(message.getPlayer(), this);
                    // Первый подключившийся игрок ходит первым и играет за крестики
                    Response response = new Response(ResponseAction.GAME_START, game.getGrid());
                    game.notifyAllPlayers(response);

                    try {
                        Thread.sleep(1000); // Пауза на 1 секунду для того, чтобы ResponseListener успел поменяться
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Восстановление состояния прерывания
                        System.err.println("Thread was interrupted: " + e.getMessage());
                    }

                    sendPlayersInfo(game.getPlayers());

                } else if (game.getPlayers().isEmpty()) {
                    game.addPlayer(message.getPlayer(), this);
                    Response response = new Response(ResponseAction.WAITING_SECOND_PLAYER);
                    System.out.println("Response-empty: " + response.getAction());
                    sendResponse(response);
                }
                break;
            case MAKE_MOVE:
                System.out.println("Make move");
                int x = message.getX();
                int y = message.getY();

                if (game.makeMove(message.getPlayer(), x, y))
                    game.notifyAllPlayers(new Response(ResponseAction.MAKE_MOVE, game.getGrid()));

                break;
            case RESTART:
                System.out.println("Restart game");
                game.clear();
                game.notifyAllPlayers(new Response(ResponseAction.GAME_RESTART, game.getGrid()));
                break;
            case SKIP_MOVE:
                System.out.println("Skip move");
                game.skipMove();
                break;
            default:
                System.err.println("Handle message error: unknown action");
                break;
        }
    }


    private Message receiveMessage() {
        try {
            System.out.println("Waiting for message...");
            String messageString = dis.readUTF();
            System.out.println("Received message: " + messageString);

            return gson.fromJson(messageString, Message.class);
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
            closeConnection();
        }
        return null;
    }

    public void sendResponse(final Response response) {
        try {
            dos.writeUTF(gson.toJson(response));
        } catch (IOException e) {
            System.err.println("Error while sending response to client (sendResponse)" + e.getMessage());
        }
    }

    private void sendPlayersInfo(List<Player> players) {
        Response response = new Response(ResponseAction.PLAYERS_INFO, game.getPlayers());
        game.notifyAllPlayers(response);
    }

    private void closeConnection() {
        System.out.println("Closing connection...");
        try {
            dis.close();
            dos.close();
            clientSocket.close();
            if (game != null) {
                game.removePlayer(player, this);
            }
            isRunning = false;
            System.out.println("Connection closed");
        } catch (IOException e) {
            System.err.println("Error while closing connection (closeConnection)" + e.getMessage());
        }
    }
}
