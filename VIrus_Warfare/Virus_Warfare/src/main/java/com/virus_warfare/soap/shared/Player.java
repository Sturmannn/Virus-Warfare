package com.virus_warfare.soap.shared;


import java.util.UUID;
import com.virus_warfare.soap.shared.CellValue;

import javax.xml.bind.annotation.*;

@XmlType(name = "Player")
@XmlRootElement(name = "Player")
@XmlAccessorType(XmlAccessType.FIELD)
public class Player {
    @XmlElement
    private String nickname;
    @XmlElement
    private UUID id;
    @XmlElement
    private CellValue symbol = null;

    public Player() {
        System.err.println("Empty Player constructor");
    }

    public Player(String nickname, CellValue symbol) {
        this.nickname = nickname;

        if (symbol != CellValue.X && symbol != CellValue.O) {
            if (symbol == null) {
                System.out.println("Symbol is null for Player constructor");
            } else {
                System.err.println("Wrong symbol for Player constructor");
            }
        }
        this.symbol = symbol;
    }

    public Player (String nickname, UUID id, CellValue symbol) {
        this.nickname = nickname;
        this.id = id;
        this.symbol = symbol;
    }

    public String getName() {
        return nickname;
    }

    public UUID getID() {
        return id;
    }

    public void setID(UUID id) {
        this.id = id;
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