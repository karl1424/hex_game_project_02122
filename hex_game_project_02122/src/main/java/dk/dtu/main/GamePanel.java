package dk.dtu.main;

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
    private ComputerOpponent computerOpponent;
    private int computerPlayer;
    private Stage primaryStage;
    private Menu menu;

    public GamePanel(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.menu = new Menu(this, primaryStage);
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
            computerOpponent = new ComputerOpponent(gameBoard, computerPlayer, gui);
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
                menu.showGameOver(gameBoard.getWinner(), this);
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

    public ComputerOpponent getComputerOpponent() {
        return computerOpponent;
    }

    public int getComputerPlayer() {
        return computerPlayer;
    }

    public void resetGame() {
        gameInit(gridSize, computerPlayer);
    }

    public Menu getMenu() {
        return menu;
    }
}