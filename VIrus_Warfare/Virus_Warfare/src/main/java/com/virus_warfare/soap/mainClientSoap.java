package com.virus_warfare.soap;

import com.virus_warfare.soap.shared.GameService;
import com.virus_warfare.soap.client.controllers.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class mainClientSoap extends Application {
    public static final int PORT = 8080;

    @Override
    public void start(Stage stage) throws IOException {
//        Здесь по-моему не GameController должен быть
        FXMLLoader fxmlLoader = new FXMLLoader(GameController.class.getResource("/com/virus_warfare/views/soap/connect.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 200);
        stage.setTitle("Virus Warfare");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
//        URL url = null;
//        try {
//            // WSDL сервиса
//            url = new URL(String.format("http://localhost:%d/game?wsdl", PORT));
//            System.out.println("WSDL URL: " + url);
//        } catch (MalformedURLException e) {
//            System.err.println("Error while creating URL (mainClientSoap)");
//        }
//        QName qName = new QName("http://server.soap.virus_warfare.com/", "GameServiceImplService");
//        Service webService = Service.create(url, qName);
//        GameService clientProxy = webService.getPort(new QName("http://server.soap.virus_warfare.com/", "GameServiceImplPort"), GameService.class);
//        System.out.println(clientProxy.connect("BIBA"));
        launch();
    }
}
