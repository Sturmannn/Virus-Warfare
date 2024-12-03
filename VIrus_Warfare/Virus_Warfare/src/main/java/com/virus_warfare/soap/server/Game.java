package com.virus_warfare.soap.server;

import com.virus_warfare.soap.shared.CellValue;
import com.virus_warfare.soap.shared.Player;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Game {
    private static volatile Game instance = null;
    private final CellValue[][] grid;
    private final int rows;
    private final int cols;

    private final List<Player> players;
    private final GameServiceImpl gameService; // Ссылка на сервис SOAP

    private int currentPlayer = 1; // 1 - X, 2 - O
    private int movesLeft = 3;    // Ходы, оставшиеся у текущего игрока

    private boolean hasXMadeFirstMove = false;
    private boolean hasOMadeFirstMove = false;


    // Синхронизация потоков
    private final Lock lock = new ReentrantLock();

    private Game(int rows, int cols, GameServiceImpl gameService) {
        if (rows < 3 || cols < 3) {
            System.err.println("Wrong grid size");
            System.exit(1);
        }
        this.rows = rows;
        this.cols = cols;
        grid = new CellValue[rows][cols];
        players = new ArrayList<>();
        this.gameService = gameService;
        clear();
    }

    public static Game getInstance(int rows, int cols, GameServiceImpl gameService) {
        if (instance == null) {
            synchronized (Game.class) {
                if (instance == null) {
                    instance = new Game(rows, cols, gameService);
                }
            }
        }
        return instance;
    }

    public List<List<CellValue>> getGrid() {
        lock.lock();
        try {
            return convertArrayToList(grid);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(int x, int y, CellValue value) {
        lock.lock();
        try {
            if (x < 0 || x >= rows || y < 0 || y >= cols) {
                System.err.println("Wrong cell coordinates (SetCell)");
                return;
            }
            grid[x][y] = value;
        } finally {
            lock.unlock();
        }
    }

    public CellValue getCell(int x, int y) {
        lock.lock();
        try {
            return grid[x][y];
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            for (CellValue[] cellValues : grid) Arrays.fill(cellValues, CellValue.EMPTY);
            currentPlayer = 1;
            movesLeft = 3;
            hasXMadeFirstMove = false; // Сброс флага первого хода X
            hasOMadeFirstMove = false; // Сброс флага первого хода O
        } finally {
            lock.unlock();
        }
    }


    public void skipMove() {
        lock.lock();
        try {
            switchPlayerTurn();
        } finally {
            lock.unlock();
        }
    }

    public void addPlayer(Player player /*,ServerSocketWrapper clientSocket*/) {
        lock.lock();
        try {
            for (Player p : players) {
                if (p.getName().equals(player.getName())) {
                    System.err.println("Player already exists: " + player.getName());
                    return;
                }
            }
            assignPlayerSymbol(player);
        } finally {
            lock.unlock();
        }
    }

    public void removePlayer(Player player/*,ServerSocketWrapper clientSocket*/) {
        lock.lock();
        try {
            if (player == null || player.getName() == null) {
                System.err.println("Player or player name is null (removePlayer): " + player);
                return;  // Выход из метода, если игрок или имя пустое
            }

            if (!players.remove(player)) {
                System.err.println("Player not found (removePlayer): " + player.getName());
            }
        } finally {
            lock.unlock();
        }
    }


    public List<Player> getPlayers() {
        lock.lock();
        try {
            return new ArrayList<>(players);
        } finally {
            lock.unlock();
        }
    }

    private void assignPlayerSymbol(Player player) {
        lock.lock();
        try {
            if (players.isEmpty()) {
                player.setSymbol(CellValue.X);
            } else if (players.size() == 1) {
                player.setSymbol(CellValue.O);
            } else {
                System.err.println("assignPlayerSymbol: Game is already full");
                throw new IllegalStateException("Game is already full");
            }
            players.add(player);
        } finally {
            lock.unlock();
        }
    }

    public boolean makeMove(Player player, int x, int y) {
        lock.lock();
        try {
            if (x < 0 || x >= rows || y < 0 || y >= cols) {
                System.err.println("Wrong cell coordinates (MakeMove)");
                return false;
            }

            if (!isPlayerTurn(player)) {
                System.err.println("It's not the player's turn.");
                return false;
            }


            CellValue playerSymbol = player.getSymbol();
            CellValue zombifiedSymbol = playerSymbol == CellValue.X ? CellValue.ZO : CellValue.ZX;
            CellValue opponentSymbol = playerSymbol == CellValue.X ? CellValue.O : CellValue.X;

            if ((player.getSymbol() == CellValue.X) && !hasXMadeFirstMove) {
                if (x != 0 || y != 0) {
                    System.err.println("First move for X must be in the top-left corner (0, 0).");
                    return false;
                }
                grid[x][y] = playerSymbol;
                movesLeft--;
                hasXMadeFirstMove = true;
                return true;
            } else if ((player.getSymbol() == CellValue.O) && !hasOMadeFirstMove) {
                if (x != rows - 1 || y != cols - 1) {
                    System.err.println("First move for O must be in the bottom-right corner (" + (rows - 1) + ", " + (cols - 1) + ").");
                    return false;
                }
                grid[x][y] = playerSymbol;
                movesLeft--;
                hasOMadeFirstMove = true;
                return true;
            }


            if (isCellAccessible(x, y, player.getSymbol())) {
                if (grid[x][y] == CellValue.EMPTY) {
                    grid[x][y] = playerSymbol;
                    movesLeft--;
                } else if (grid[x][y] == opponentSymbol) {
                    grid[x][y] = zombifiedSymbol;
                    movesLeft--;
                } else if (grid[x][y] == zombifiedSymbol) {
                    grid[x][y] = playerSymbol;
                    return false;
                }

                if (hasXMadeFirstMove && hasOMadeFirstMove && isGameOver(opponentSymbol)) {
                    sendGameOverMessage();
                    return false;
                }

                if (movesLeft == 0) {
                    switchPlayerTurn();
                }

                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }


    private boolean isPlayerTurn(Player player) {
        // Проверка, текущий ли игрок ходит
        return (currentPlayer == 1 && player.getSymbol() == CellValue.X) ||
                (currentPlayer == 2 && player.getSymbol() == CellValue.O);
    }

    private void switchPlayerTurn() {
        // Переключение текущего игрока и сброс счётчика ходов
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        movesLeft = 3;
    }

    private boolean isGameOver(CellValue opponentSymbol) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isCellAccessible(i, j, opponentSymbol)) {
                    return false; // Противник может сделать хотя бы один ход
                }
            }
        }
        return true; // У противника нет доступных ходов
    }

    private void sendGameOverMessage() {
        System.out.println("Game over!!! " + currentPlayer);
        ResponseAction winner = currentPlayer == 1 ? ResponseAction.GAME_OVER_WIN_X : ResponseAction.GAME_OVER_WIN_O;

        Response response = new Response(winner, convertArrayToList(grid), true);
        gameService.notifyAllPlayers(response);
    }


    private boolean isCellAccessible(int x, int y, CellValue playerSymbol) {
        // Клетка доступна, если соседствует с клеткой игрока или через цепочку зомбированных клеток противника
        return isAdjacentToSymbol(x, y, playerSymbol) || isAccessibleThroughZombifiedChain(x, y, playerSymbol);
    }

    // Проверка наличия соседа для игрока с заданным символом
    private boolean isAdjacentToSymbol(int x, int y, CellValue symbol) {
        CellValue opponentSymbol = symbol == CellValue.X ? CellValue.O : CellValue.X;
        if (grid[x][y] != CellValue.EMPTY && grid[x][y] != opponentSymbol)
            return false;
        for (int dx = -1; dx <= 1; ++dx)
            for (int dy = -1; dy <= 1; ++dy) {
                int nx = x + dx; // Соседняя клетка по X
                int ny = y + dy; // Соседняя клетка по Y

                if (isInsideGrid(nx, ny) && grid[nx][ny] == symbol)
                    return true;
            }
        return false;
    }

    // Используется BFS для поиска пути по зомбированным клеткам
    private boolean isAccessibleThroughZombifiedChain(int x, int y, CellValue playerSymbol) {

        // X - крестик, O - нолик, ZX - зомбированный крестик, ZO - зомбированный нолик
        // Игрок с крестиком может ходить по ZO, а игрок с ноликом - по ZX.

        // Определяется зомбированный символ противника, по которому может двигаться игрок
        CellValue zombifiedSymbol = playerSymbol == CellValue.X ? CellValue.ZO : CellValue.ZX;
        CellValue opponentSymbol = playerSymbol == CellValue.X ? CellValue.O : CellValue.X;

        if (grid[x][y] != CellValue.EMPTY && grid[x][y] != opponentSymbol) {
            return false;
        }

        // Матрица посещенных клеток
        boolean[][] visited = new boolean[rows][cols];

        // Очередь для BFS
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(x, y)); // Добавляется начальная клетка

        // BFS для поиска пути к клетке игрока
        while (!queue.isEmpty()) {
            Pair<Integer, Integer> cell = queue.poll();
            int cx = cell.getKey(); // Текущая клетка (X)
            int cy = cell.getValue(); // Текущая клетка (Y)

            // Если клетка уже посещена, она пропускается
            if (visited[cx][cy]) {
                continue;
            }
            visited[cx][cy] = true;

            // Проверка соседей текущей клетки
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue; // Пропуск самой клетку

                    int nx = cx + dx;
                    int ny = cy + dy;

                    // Проверка, что соседняя клетка находится в пределах поля
                    if (!isInsideGrid(nx, ny)) {
                        continue;
                    }

                    // Если нашлась клетка игрока, цепочка существует
                    if (grid[nx][ny] == playerSymbol) {
                        return true;
                    }

                    // Если клетка зомбированная, она добавляется её в очередь
                    if (grid[nx][ny] == zombifiedSymbol && !visited[nx][ny]) {
                        queue.add(new Pair<>(nx, ny));
                    }
                }
            }
        }
        // Если не удалось найти цепочку, возвращается false
        return false;
    }

    private boolean isInsideGrid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    public static List<List<CellValue>> convertArrayToList(CellValue[][] array) {
        List<List<CellValue>> list = new ArrayList<>();
        for (CellValue[] row : array) {
            List<CellValue> rowList = new ArrayList<>();
            Collections.addAll(rowList, row);
            list.add(rowList);
        }
        return list;
    }
}
