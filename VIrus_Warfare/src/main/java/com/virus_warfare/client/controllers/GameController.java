package com.virus_warfare.client.controllers;

import com.virus_warfare.client.models.*;
import com.virus_warfare.server.models.Response;
import com.virus_warfare.shared.CellValue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

public class GameController implements ServerResponseListener {
    private static final int BOARD_SIZE = 10; // Размер игрового поля
    @FXML
    private GridPane gameGrid;
    @FXML
    private Label statusLabel;
    @FXML
    private Label playerXLabel;
    @FXML
    private Label playerOLabel;
    private StackPane[][] cells;              // Хранение всех клеток игрового поля

    private ClientSocketWrapper clientSocketWrapper;
    private boolean isGameOver = false;

    @FXML
    public void initialize() {
        cells = new StackPane[BOARD_SIZE][BOARD_SIZE];
    }

    public void setClientSocketWrapper(ClientSocketWrapper clientSocketWrapper) {
        this.clientSocketWrapper = clientSocketWrapper;
        this.clientSocketWrapper.setResponseListener(this);
    }

    @Override
    public void onResponseReceived(Response response) {
        System.out.println("In GameonResponseReceived" + response.getAction());
        Platform.runLater(() -> {
            switch (response.getAction()) {
                case GAME_START:
                    statusLabel.setText("Game started.");
                    updateGameGrid(response);
                    break;
                case MAKE_MOVE:
                    statusLabel.setText("Move received.");
                    updateGameGrid(response);
                    break;
                case GAME_RESTART:
                    isGameOver = false;
                    statusLabel.setText("Game restarted.");
                    updateGameGrid(response);
                    break;
                case PLAYERS_INFO:
                    updatePlayersInfo(response);
                    break;
                case GAME_OVER_WIN_X:
                    updateGameGrid(response);
                    isGameOver = true;
                    statusLabel.setText("Игра окончена! " + playerXLabel.getText() + " победил!");
                    showAlert(playerXLabel.getText() + " победил!");
                    break;
                case GAME_OVER_WIN_O:
                    updateGameGrid(response);
                    isGameOver = true;
                    statusLabel.setText("Игра окончена! " + playerOLabel.getText() + " победил!");
                    showAlert(playerOLabel.getText() + " победил!");
                    break;
                default:
                    statusLabel.setText("Unknown server response.");
            }
        });
    }

    private void updateGameGrid(Response response) {
        CellValue[][] grid = response.getGrid();
        gameGrid.getChildren().clear(); // Очистка игровой сетки
        cells = new StackPane[BOARD_SIZE][BOARD_SIZE]; // Пересоздание массива клеток

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(40, 40);
                int finalRow = row;
                int finalCol = col;

                // Очистка ячейки перед добавлением
                cell.getChildren().clear();

                cell.setOnMouseClicked(_ -> handleCellClick(finalRow, finalCol));

                // Стили (фон и рама)
                Rectangle background = new Rectangle(40, 40);
                background.setFill(Color.BEIGE);
                background.setStroke(Color.BLACK);
                cell.getChildren().add(background);

                switch (grid[row][col]) {
                    case X:
                        drawX(cell);
                        break;
                    case O:
                        drawO(cell);
                        break;
                    case ZX:
                        drawZX(cell);
                        break;
                    case ZO:
                        drawZO(cell);
                        break;
                    case EMPTY:
                        drawEmpty(cell);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown cell value: " + grid[row][col]);
                }

                gameGrid.add(cell, col, row);
                cells[row][col] = cell;
            }
        }
    }


    private void handleCellClick(int x, int y) {
        if (isGameOver) {
            return;
        }

        System.out.println("Cell clicked: " + x + ", " + y);

        // Отправка ход на сервер
        Message moveMessage = new Message(MessageAction.MAKE_MOVE, clientSocketWrapper.getPlayer(), x, y);
        clientSocketWrapper.sendMessage(moveMessage);

        System.out.println("Move sent: " + x + ", " + y);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Игра окончена ");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void drawX(StackPane cell) {
        Text x = new Text("X");
        x.setFill(Color.BLUE);
        x.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Arial';");
        cell.getChildren().add(x);
    }

    private void drawO(StackPane cell) {
        Text o = new Text("O");
        o.setFill(Color.RED);
        o.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Arial';");
        cell.getChildren().add(o);
    }

    private void drawZX(StackPane cell) {
        // Установка красного фона для клетки
        Rectangle background = new Rectangle(40, 40);
        background.setFill(Color.RED);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);

        // Создание текста "X" с фиолетовым цветом
        Text text = new Text("X");
        text.setFill(Color.PURPLE);  // Фиолетовый цвет для буквы "X"
        text.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        // Подсветка (например, тенями)
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.VIOLET);
        shadow.setRadius(5);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        text.setEffect(shadow);

        // Добавление текста на клетку
        cell.getChildren().add(text);
    }

    private void drawZO(StackPane cell) {
        // Установка синего фона для клетки
        Rectangle background = new Rectangle(40, 40);
        background.setFill(Color.BLUE);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);

        // Создание текста "O" с ярким цветом (например, белый или светло-желтый)
        Text text = new Text("O");
        text.setFill(Color.YELLOW);  // Жёлтый цвет для буквы "O" (яркий, контрастный с синим)
        text.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Arial';");

        // Добавление эффекта подсветки (например, тенями)
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.GOLD);
        shadow.setRadius(5);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        text.setEffect(shadow);

        // Добавление текста на клетку
        cell.getChildren().add(text);
    }

    private void drawEmpty(StackPane cell) {
        Rectangle background = new Rectangle(40, 40);
        background.setFill(Color.BEIGE);
        background.setStroke(Color.BLACK);
        cell.getChildren().add(background);
    }

    public void updatePlayersInfo(Response response) {
        System.out.println("Players info received");
        List<Player> players = response.getPlayers();
        if (players.size() == 2) {
            playerXLabel.setText(players.get(0).getName());
            playerOLabel.setText(players.get(1).getName());
        }
    }

    // Рестарт игры
    @FXML
    private void restartGame() {
        isGameOver = false;
        Message restartMessage = new Message(MessageAction.RESTART, clientSocketWrapper.getPlayer());
        clientSocketWrapper.sendMessage(restartMessage);
    }


    // Пропуск хода
    @FXML
    private void skipTurn() {
        Message skipMessage = new Message(MessageAction.SKIP_MOVE, clientSocketWrapper.getPlayer());
        clientSocketWrapper.sendMessage(skipMessage);
    }

}

