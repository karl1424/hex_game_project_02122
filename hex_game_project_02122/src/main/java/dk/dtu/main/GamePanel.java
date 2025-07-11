package dk.dtu.main;

import dk.dtu.computer_opponent.ComputerManager;
import dk.dtu.game_components.GameBoard;
import dk.dtu.menu.MenuManager;
import dk.dtu.network.Client;
import dk.dtu.user_interface.GUI;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GamePanel extends Pane {
    public GUI gui;
    private GameBoard gameBoard;
    private int gridSize;
    private ComputerManager computerOpponent;
    private int computerPlayer;
    private MenuManager menuManager;
    private Client client;
    private int difficulty;

    public boolean isOnline = false;
    private int playerNumber;
    private int curentPlayerTurn;

    public GamePanel(Stage primaryStage) {
        this.client = new Client(this);
        this.menuManager = new MenuManager(this, client, primaryStage);
    }

    public void gameInit(int gridSize, int computerPlayer, int playerNumber, int difficulty) {
        this.getChildren().clear();
        this.gridSize = gridSize;
        this.computerPlayer = computerPlayer;
        this.playerNumber = playerNumber;
        this.difficulty = difficulty;
        startGame();
    }

    private void startGame() {
        curentPlayerTurn = 1;
        gameBoard = new GameBoard(gridSize, gridSize, this);
        gui = new GUI(gridSize, gridSize, gameBoard, this);
        getChildren().add(gui);
        // gameBoard.printBoard();
        if (computerPlayer != 0) {
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(0.2), _ -> {
                computerOpponent = new ComputerManager(gameBoard, computerPlayer, difficulty);

                // Let computer go first if it's player 1
                if (computerOpponent.getPlayerNumber() == 1) {
                    computerOpponent.makeMove();
                    changeTurn();
                }
            }));
            delay.setCycleCount(1);
            delay.play();
        } else {
            computerOpponent = null;
        }
    }

    public void checkGameOver() {
        if (gameBoard.getWinner() != 0) {
            Color winnerColor = gameBoard.getWinner() == 1 ? Color.RED : Color.BLUE;

            gui.animateWinningPath(gameBoard.getWinningPath(), winnerColor);

            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), _ -> {
                menuManager.showGameOver(gameBoard.getWinner());
            }));
            delay.setCycleCount(1);
            delay.play();

            if (isOnline) {
                menuManager.gameOverPanel.setOnline();
                if (!menuManager.getClient().getClientState().isHost()) {
                    menuManager.getClient().receiveToLobby();
                }
            } else {
                menuManager.gameOverPanel.setLocal();
            }
        }
    }

    public boolean getTurn() {
        return curentPlayerTurn == playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public int getCurrentPlayerTurn() {
        return curentPlayerTurn;
    }

    public void changeTurn() {
        curentPlayerTurn = curentPlayerTurn == 1 ? 2 : 1;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public ComputerManager getComputerOpponent() {
        return computerOpponent;
    }

    public int getComputerPlayer() {
        return computerPlayer;
    }

    public void resetGame() {
        gameInit(gridSize, computerPlayer, playerNumber, difficulty);
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public void sendCoordinates(int x, int y, int player) throws InterruptedException {
        client.getGameCommunicationHandler().sendSpot(x, y, player);
    }

    public void beginGettingCoordinates() {
        client.getGameCommunicationHandler().getSpot(this, spot -> {
            getGameBoard().updateSpot((int) spot[0], (int) spot[1], playerNumber == 1 ? 2 : 1);
            changeTurn();
        });
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Client getClient() {
        return client;
    }
}
