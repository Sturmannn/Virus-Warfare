package com.virus_warfare.client.controllers;

import com.virus_warfare.client.models.*;
import com.virus_warfare.server.models.Response;
import com.virus_warfare.shared.CellValue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;

public class ConnectController implements ServerResponseListener {

    @FXML
    private Label statusConnect;
    @FXML
    private TextField nameField;
    @FXML
    private Button connectButton;

    private ClientSocketWrapper clientSocketWrapper;

    private final InetAddress serverIP = null; // localhost
    private final int serverPort = 3124;

    @FXML
    public void initialize() {
        nameField.setOnAction(event -> onConnectButtonClick());
    }

    @FXML
    private void onConnectButtonClick() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            Platform.runLater(() -> statusConnect.setText("Enter your name."));
            return;
        }
        if (clientSocketWrapper != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Already connected to the server.");
            alert.showAndWait();
            return;
        }
        try {
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Connecting to server...");
            clientSocketWrapper = new ClientSocketWrapper(ip, serverPort, this);
            if (clientSocketWrapper.getClientSocket() == null) {
                Platform.runLater(() -> statusConnect.setText("Failed to connect to server."));
                stop();
                return;
            }
            System.out.println("Connected to server!");
            clientSocketWrapper.startListening();
            System.out.println("Listening for responses...");

            Player player = new Player(name, serverIP, serverPort, null); // is 1st player - X or 2nd player - O
            clientSocketWrapper.setPlayer(player);

            clientSocketWrapper.sendMessage(new Message(MessageAction.CONNECT, clientSocketWrapper.getPlayer()));
            Platform.runLater(() -> statusConnect.setText("Connecting..."));
        } catch (Exception e) {
            stop();
            Platform.runLater(() -> statusConnect.setText("Failed to connect to server."));
            System.err.println("Failed to connect to server." + e.getMessage());
            System.err.println("Error while sending name to server (onConnectButtonClick)" + e.getMessage());
        }
    }

    @Override
    public void onResponseReceived(Response response) {
        System.out.println("In ConnectionResponseReceived" + response.getAction());
        Platform.runLater(() -> {
            if (response.getAction() == null) {
                statusConnect.setText("Unknown server response.");
                return;
            }
            switch (response.getAction()) {
                case WAITING_SECOND_PLAYER:
                    statusConnect.setText("Waiting for the second player...");
                    clientSocketWrapper.getPlayer().setSymbol(CellValue.X);
                    break;
                case GAME_START:
                    if (clientSocketWrapper.getPlayer().getSymbol() == null) {
                        clientSocketWrapper.getPlayer().setSymbol(CellValue.O);
                    }
                    statusConnect.setText("Game started!");
                    openGameWindow(response);
                    break;
                default:
                    statusConnect.setText("Unknown server response.");
            }
        });
    }

    private void openGameWindow(Response response) {
        try {
            // Закрытие текущего окно
            Stage currentStage = (Stage) connectButton.getScene().getWindow();
            currentStage.close();

            // Открытие нового окна
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/virus_warfare/views/game.fxml"));
            Stage gameStage = new Stage();
            gameStage.setScene(new Scene(loader.load(), 475, 700));

            // Передача сокета в игровой контроллер
            GameController gameController = loader.getController();
            gameController.setClientSocketWrapper(clientSocketWrapper);
            gameController.onResponseReceived(response);

            gameStage.setTitle("Virus Warfare");
            gameStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error while opening game window (openGameWindow)" + e.getMessage());
            statusConnect.setText("Failed to open game window.");
        }
    }

    public void stop() {
        if (clientSocketWrapper != null) {
            clientSocketWrapper.stop();
            clientSocketWrapper = null;
        }
    }
}
