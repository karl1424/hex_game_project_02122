package dk.dtu.main;

import dk.dtu.computer_opponent.ComputerManager;
import dk.dtu.computer_opponent.SmallBoardStrategy;
import dk.dtu.connection.Client;
import dk.dtu.menu.MenuManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GamePanel extends Pane {
    private boolean isPlayerOneTurn = true;
    private GUI gui;
    private GameBoard gameBoard;
    private int gridSize;
    private ComputerManager computerOpponent;
    private int computerPlayer;
    private Stage primaryStage;
    private MenuManager menuManager;
    private Client client;

    public GamePanel(Stage primaryStage) {
        this.client = new Client();
        this.primaryStage = primaryStage;
        this.menuManager = new MenuManager(this, client, primaryStage);
    }

    public void gameInit(int gridSize, int computerPlayer) {
        this.getChildren().clear();
        this.gridSize = gridSize;
        this.computerPlayer = computerPlayer;
        isPlayerOneTurn = true;
        startGame();
    }

    private void startGame() {
        gameBoard = new GameBoard(gridSize, gridSize, this);
        gui = new GUI(gridSize, gridSize, gameBoard, this);
        if (computerPlayer != 0) {
            computerOpponent = new ComputerManager(gameBoard, computerPlayer, gui);
            gui.setComputerOpponent(computerOpponent);

            // Let computer go first if it's player 1
            if (computerOpponent.getPlayerNumber() == 1) {
                computerOpponent.makeMove();
                changeTurn();
            }
        } else {
            computerOpponent = null;
            gui.setComputerOpponent(null);
        }

        getChildren().add(gui);
        System.out.println("Initial empty board:");
        gameBoard.printBoard();
    }

    public void checkGameOver() {
        if (gameBoard.getWinner() != 0) {
            Color winnerColor = gameBoard.getWinner() == 1 ? Color.RED : Color.BLUE;

            gui.animateWinningPath(gameBoard.getWinningPath(), winnerColor);

            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
                menuManager.showGameOver(gameBoard.getWinner());
            }));
            delay.setCycleCount(1);
            delay.play();
        }
    }

    public boolean getTurn() {
        return isPlayerOneTurn;
    }

    public void changeTurn() {
        isPlayerOneTurn = !isPlayerOneTurn;
    }

    public ComputerManager getComputerOpponent() {
        return computerOpponent;
    }

    public int getComputerPlayer() {
        return computerPlayer;
    }

    public void resetGame() {
        gameInit(gridSize, computerPlayer);
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }
}