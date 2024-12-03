package com.virus_warfare.soap.client.controllers;


import com.virus_warfare.soap.server.Response;
import com.virus_warfare.soap.server.ResponseAction;
import com.virus_warfare.soap.shared.GameService;
import com.virus_warfare.soap.shared.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectController implements ResponseHandler {
    @FXML
    private Label statusConnect;
    @FXML
    private TextField nameField;
    @FXML
    private Button connectButton;

    public static final int serverPort = 8080;
    GameService clientProxy = null; // Клиентский прокси для общения с сервером

    Player player = null;
    private boolean isClientConnected = false;

    Thread longPollingThread;

    private volatile boolean isControllerSwitched = false;

    private volatile ResponseHandler responseHandler = this;

    @FXML
    public void initialize() {
        nameField.setOnAction(event -> onConnectButtonClick());
    }

    private void connect() {
        URL url = null;
        try {
            // WSDL сервиса
            url = new URL(String.format("http://localhost:%d/game?wsdl", serverPort));
            System.out.println("WSDL URL (ConnectController): " + url);
        } catch (MalformedURLException e) {
            System.err.println("Error while creating URL (ConnectController)" + e.getMessage());
        }
        QName qName = new QName("http://server.soap.virus_warfare.com/", "GameServiceImplService");
        Service webService = Service.create(url, qName);
        clientProxy = webService.getPort(new QName("http://server.soap.virus_warfare.com/", "GameServiceImplPort"), GameService.class);
    }

    @FXML
    private void onConnectButtonClick() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            Platform.runLater(() -> statusConnect.setText("Enter your name."));
            return;
        }
        if (clientProxy != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Already connected to the server.");
            alert.showAndWait();
            return;
        }
        connect();
        if (clientProxy == null) {
            Platform.runLater(() -> statusConnect.setText("Failed to connect to server."));
            return;
        }
        isClientConnected = true;
        System.out.println("Connected to server! (clientProxy available)");

        handleResponse(clientProxy.connect(name));
    }

    private void openGameWindow(Response response) {
        Platform.runLater(() -> {
            try {
                // Закрытие текущего окна
                Stage currentStage = (Stage) connectButton.getScene().getWindow();
                currentStage.close();

                // Открытие нового окна
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/virus_warfare/views/soap/game.fxml"));
                Stage gameStage = new Stage();
                gameStage.setScene(new Scene(loader.load(), 475, 700));

                // Передача клиентского прокси в игровой контроллер
                GameController gameController = loader.getController();
                gameController.setPlayer(player);
                gameController.setClientProxy(clientProxy);
                gameController.handleResponse(response);

                responseHandler = gameController;

                gameStage.setTitle("Virus Warfare");
                gameStage.show();

                isControllerSwitched = true;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error while opening game window (openGameWindow)" + e.getMessage());
                statusConnect.setText("Failed to open game window.");
            }
        });
    }


    @Override
    public void handleResponse(Response response) {
        ResponseAction action = response.getAction();
        System.out.println("Response action (handleResponse, connectController): " + action);
        if (action == null) {
            System.err.println("Response action is null (handleResponse)");
            return;
        }
        switch (action) {
            case GAME_FULL:
                Platform.runLater(() -> statusConnect.setText("Game is full."));
                stop();
                break;
            case ALREADY_CONNECTED:
                Platform.runLater(() -> statusConnect.setText("The player is already connected."));
                stop();
                break;
            case WAITING_SECOND_PLAYER:
                Platform.runLater(() -> statusConnect.setText("Waiting for the second player..."));
                this.player = response.getPlayers().get(0);
                startLongPolling();
                break;
            case GAME_START:
                Platform.runLater(() -> statusConnect.setText("Game started!"));
                if (this.player == null) {
                    this.player = response.getPlayers().get(0);
                }
                openGameWindow(response);
                startLongPolling();
                break;
            default:
                Platform.runLater(() -> statusConnect.setText("Unknown server response."));
        }
    }

    private void startLongPolling() {
        if (longPollingThread != null) {
            return;
        }
        longPollingThread = new Thread(() -> {
            while (isClientConnected) {
                Response response = clientProxy.longPolling(player.getID());
                if (response.getAction() != ResponseAction.PLAYERS_INFO) {
                    responseHandler.handleResponse(response);
                } else {
                    while (!isControllerSwitched) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            System.err.println("Thread was interrupted: " + e.getMessage());
                        }
                    }
                    responseHandler.handleResponse(response);
                }
            }
        });
        longPollingThread.setDaemon(true);
        longPollingThread.start();
    }


    public void stop() {
        if (clientProxy != null) {
            clientProxy = null;
        }
    }
}
