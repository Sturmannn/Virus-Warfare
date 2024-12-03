package com.virus_warfare.soap.server;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ResponseAction")
@XmlEnum
public enum ResponseAction {
    @XmlEnumValue("WAITING_SECOND_PLAYER")
    WAITING_SECOND_PLAYER,
    @XmlEnumValue("GAME_FULL")
    GAME_FULL,
    @XmlEnumValue("GAME_START")
    GAME_START,
    @XmlEnumValue("GAME_OVER_WIN_X")
    GAME_OVER_WIN_X,
    @XmlEnumValue("GAME_OVER_WIN_O")
    GAME_OVER_WIN_O,
    @XmlEnumValue("GAME_RESTART")
    GAME_RESTART,
    @XmlEnumValue("MAKE_MOVE")
    MAKE_MOVE,
    @XmlEnumValue("SKIP_MOVE")
    SKIP_MOVE,
    @XmlEnumValue("PLAYER_SYMBOL")
    PLAYER_SYMBOL,
    @XmlEnumValue("PLAYERS_INFO")
    PLAYERS_INFO,
    @XmlEnumValue("ERROR_POLLING")
    ERROR_POLLING,
    @XmlEnumValue("ALREADY_CONNECTED")
    ALREADY_CONNECTED
}