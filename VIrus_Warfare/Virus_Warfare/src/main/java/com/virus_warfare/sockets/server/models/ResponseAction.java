package com.virus_warfare.sockets.server.models;

public enum ResponseAction {
    WAITING_SECOND_PLAYER,
    GAME_FULL,
    GAME_START,
    GAME_OVER_WIN_X,
    GAME_OVER_WIN_O,
    GAME_RESTART,
    MAKE_MOVE,
    SKIP_MOVE,
    PLAYER_SYMBOL,
    PLAYERS_INFO
}
