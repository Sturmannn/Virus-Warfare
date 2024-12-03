package com.virus_warfare.sockets;

import com.virus_warfare.sockets.client.controllers.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainClient extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameController.class.getResource("/com/virus_warfare/views/sockets/connect.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);
        stage.setTitle("Virus Warfare");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}