package com.virus_warfare.soap.server;


import com.virus_warfare.soap.shared.CellValue;
import com.virus_warfare.soap.shared.GameService;
import com.virus_warfare.soap.shared.Player;

import javax.jws.WebService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@WebService(endpointInterface = "com.virus_warfare.soap.shared.GameService")
public class GameServiceImpl implements GameService {
    private final int rows = 10;
    private final int cols = 10;

    private Game game;
    private final Map<UUID, Player> players = new ConcurrentHashMap<>();
    private final Map<UUID, Response> pendingResponses = new ConcurrentHashMap<>();


    public void notifyAllPlayers(Response response) {
        synchronized (pendingResponses) {
            players.forEach((id, player) -> pendingResponses.put(id, response));
            pendingResponses.notifyAll();
        }
    }

    @Override
    public Response connect(String nickname) {
        game = Game.getInstance(rows, cols, this);
        System.out.println("Player: " + nickname);
        if (game.getPlayers().size() == 2) {
            return new Response(ResponseAction.GAME_FULL);
        } else if (game.getPlayers().size() == 1) {
            System.out.println("Second player " + nickname);
            UUID secondPlayerID = UUID.nameUUIDFromBytes(nickname.getBytes());

            if (game.getPlayers().get(0).getID().equals(secondPlayerID))
                return new Response(ResponseAction.ALREADY_CONNECTED);

            // Второй игрок играет за "O"
            Player secondPlayer = new Player(nickname, secondPlayerID, CellValue.O);
            game.addPlayer(secondPlayer);
            players.put(secondPlayerID, game.getPlayers().get(1));

            // Первый подключившийся игрок ходит первым и играет за крестики
            notifyAllPlayers(new Response(ResponseAction.GAME_START, game.getGrid(), secondPlayer));
            try {
                Thread.sleep(100); // Задержка в 100 миллисекунд
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted: " + e.getMessage());
            }
            notifyAllPlayers(new Response(ResponseAction.PLAYERS_INFO, game.getPlayers()));
            return new Response(ResponseAction.GAME_START, game.getGrid(), secondPlayer);

        } else if (game.getPlayers().isEmpty()) {
            System.out.println("First player " + nickname);
            UUID playerID = UUID.nameUUIDFromBytes(nickname.getBytes());

            // Первый подключившийся игрок играет за "X"
            Player player = new Player(nickname, playerID, CellValue.X);
            game.addPlayer(player);
            players.put(playerID, game.getPlayers().get(0));

            Response response = new Response(ResponseAction.WAITING_SECOND_PLAYER, player);
            return response;
        }
        System.err.println("Unknown state in connect method");
        return null;
    }

    @Override
    public Response longPolling(UUID id) {
        synchronized (pendingResponses) {
            while (!pendingResponses.containsKey(id)) {
                try {
                    pendingResponses.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread was interrupted: " + e.getMessage());
                    return new Response(ResponseAction.ERROR_POLLING);
                }
            }
            return pendingResponses.remove(id);
        }
    }

    @Override
    public void makeMove(UUID id, int x, int y) {
        System.out.println("Make move");
        Player player = players.get(id);
        if (player == null) {
            return;
        }
        if (game.makeMove(player, x, y)) {
            notifyAllPlayers(new Response(ResponseAction.MAKE_MOVE, game.getGrid(), true));
        }
    }

    @Override
    public void restartGame(UUID id) {
        System.out.println("Restart game");
        game.clear();
        notifyAllPlayers(new Response(ResponseAction.GAME_RESTART, game.getGrid(), true));
    }

    @Override
    public void skipMove(UUID id) {
        System.out.println("Skip move");
        game.skipMove();
    }
}
