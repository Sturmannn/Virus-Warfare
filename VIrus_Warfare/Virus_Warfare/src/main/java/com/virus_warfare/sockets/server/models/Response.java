package com.virus_warfare.sockets.server.models;

import com.virus_warfare.sockets.client.models.Player;
import com.virus_warfare.sockets.shared.CellValue;

import java.util.List;

public class Response {
    private CellValue[][] grid;
    private final ResponseAction action;
    private String message = null;

    private List<Player> players;


    public Response(ResponseAction action, CellValue[][] grid) {
        this.action = action;
        this.grid = grid;
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
            default:
                System.err.println("Unknown action in Response constructor");
        }
    }

    public Response(ResponseAction action, List<Player> players) {
        this.action = action;
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public CellValue[][] getGrid() {
        return grid;
    }

    public void setCell(int x, int y, CellValue value) {
        grid[x][y] = value;
    }

    public CellValue getCell(int x, int y) {
        return grid[x][y];
    }

    public ResponseAction getAction() {
        return action;
    }

    public String getStringMessage() {
        return message;
    }
}
