package com.virus_warfare.client.models;

import com.virus_warfare.shared.CellValue;

import java.net.InetAddress;

public class Player {
    private final String name;
    private final InetAddress ip;
    private final int port;

    private CellValue symbol = null;

    public Player(String name, InetAddress ip, int port, CellValue symbol) {
        this.name = name;
        this.ip = ip;
        this.port = port;

        if (symbol != CellValue.X && symbol != CellValue.O) {
            if (symbol == null) {
                System.out.println("Symbol is null for Player constructor");
            } else {
                System.err.println("Wrong symbol for Player constructor");
            }
        }
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public CellValue getSymbol() {
        return symbol;
    }

    public void setSymbol(CellValue symbol) {
        if (symbol != CellValue.X && symbol != CellValue.O) {
            System.err.println("Wrong symbol for Player constructor");
        }
        this.symbol = symbol;
    }
}
