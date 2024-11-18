package com.virus_warfare.client.models;

public class Message {
    private final MessageAction action;
    private final Player player;
    int x; // Строка
    int y; // Столбец

    // Конструктор для подключения к игре и перезапуска игры
    public Message(MessageAction action, Player player) {
        if (MessageAction.CONNECT != action && MessageAction.RESTART != action) {
            System.err.println("Wrong action for Message constructor");
        }
        this.action = action;
        this.player = player;
        this.x = 0;
        this.y = 0;
    }

    public Message(MessageAction action, Player player, int x, int y) {
        this.action = action;
        this.player = player;
        if (action == MessageAction.MAKE_MOVE) {
            this.x = x;
            this.y = y;
        } else {
            this.x = 0;
            this.y = 0;
        }
    }

    public MessageAction getAction() {
        return action;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Player getPlayer() {
        return player;
    }

    public String getNickname() {
        return player.getName();
    }

}
