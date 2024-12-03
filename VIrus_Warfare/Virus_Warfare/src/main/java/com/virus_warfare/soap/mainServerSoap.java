package com.virus_warfare.soap;

import com.virus_warfare.soap.server.GameServiceImpl;

import javax.xml.ws.Endpoint;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class mainServerSoap {
    public static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Hello from mainServerSoap!");
        GameServiceImpl gameService = new GameServiceImpl();
        URI uri;
        URL url = null;
        try {
            uri = new URI("http", null, "localhost", PORT, "/game", null, null);
            url = uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            System.err.println("Error while creating URI (mainServerSoap)");
        }
        System.out.println("Publishing game service at: " + (url != null ? url.toString() : null));

        Endpoint.publish(url.toString(), gameService);
    }
}