package com.virus_warfare.soap.server;



import com.virus_warfare.soap.shared.CellValue;
import com.virus_warfare.soap.shared.Player;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlType(name = "Response")
@XmlRootElement(name = "GameResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response {
    @XmlElementWrapper(name = "grid")
    @XmlElement
    private List<List<CellValue>> grid;
//    private CellValue[][] grid;
    @XmlElement
    private ResponseAction action;
    @XmlElement
    private String message = null;
    @XmlElement
    private List<Player> players;

    public Response(){
        System.err.println("Empty Response constructor");
    }


    public Response(ResponseAction action, List<List<CellValue>> grid, boolean isGrid) {
        this.action = action;
        this.grid = grid;
    }

    public Response(ResponseAction action, List<List<CellValue>> grid, Player player) {
        this.action = action;
        this.grid = grid;
        this.players = List.of(player);
    }


    public Response(ResponseAction action) {
        this.action = action;
        switch (action) {
            case WAITING_SECOND_PLAYER:
                message = "Waiting for the second player";
                break;
            case GAME_FULL:
                message = "Game is already full";
                break;
            case GAME_OVER_WIN_X:
                message = "Game over X wins";
                break;
            case GAME_OVER_WIN_O:
                message = "Game over O wins";
                break;
            case GAME_RESTART:
                message = "Game restarted";
                break;
            case MAKE_MOVE:
                message = "Make your move"; // И здесь
                break;
            case SKIP_MOVE:
                message = "Skip your move"; // ХЗ вот здесь
                break;
            case PLAYER_SYMBOL:
                message = "You are ";
                break;
            case ERROR_POLLING:
                message = "Error while polling";
                break;
            default:
                System.err.println("Unknown action in Response constructor");
        }
    }

    public Response(ResponseAction action, List<Player> players) {
        this.action = action;
        this.players = players;
    }

    public Response(ResponseAction action, Player player) {
        this.action = action;
        this.players = List.of(player);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<List<CellValue>> getGrid() {
        return grid;
    }

    public void setCell(int x, int y, CellValue value) {
        grid.get(x).set(y, value);
    }

    public CellValue getCell(int x, int y) {
        return grid.get(x).get(y);
    }

    public ResponseAction getAction() {
        return action;
    }
    public String getStringMessage() {
        return message;
    }
}
