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
    private GUI gui;
    private GameBoard gameBoard;
    private int gridSize;
    private ComputerManager computerOpponent;
    private int computerPlayer;
    private Stage primaryStage;
    private MenuManager menuManager;
    private Client client;

    public boolean isOnline = false;
    private int playerNumber;
    private int curentPlayerTurn;

    public GamePanel(Stage primaryStage) {
        this.client = new Client();
        this.primaryStage = primaryStage;
        this.menuManager = new MenuManager(this, client, primaryStage);
    }

    public void gameInit(int gridSize, int computerPlayer, int playerNumber) {
        this.getChildren().clear();
        this.gridSize = gridSize;
        this.computerPlayer = computerPlayer;
        this.playerNumber = playerNumber;
        //isPlayerOneTurn = true;
        startGame();
    }

    private void startGame() {
        curentPlayerTurn = 1;
        gameBoard = new GameBoard(gridSize, gridSize, this);
        gui = new GUI(gridSize, gridSize, gameBoard, this);
        if (computerPlayer != 0) {
            computerOpponent = new ComputerManager(gameBoard, computerPlayer, gui);
            gui.setComputerOpponent(computerOpponent);

            // Let computer go first if it's player 1
            if (computerOpponent.getPlayerNumber() == 1) {
                computerOpponent.makeMove();
                //changeTurn();
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
        return curentPlayerTurn == playerNumber;
    }

    public int getPlayerNumber(){
        return playerNumber;
    }

    public int getCurrentPlayerTurn(){
        return curentPlayerTurn;
    }

    public void changeTurn() {
        curentPlayerTurn = curentPlayerTurn == 1 ? 2 : 1;
    }

    public boolean getIsOnline(){
        return isOnline;
    }

    public ComputerManager getComputerOpponent() {
        return computerOpponent;
    }

    public int getComputerPlayer() {
        return computerPlayer;
    }

    public void resetGame() {
        gameInit(gridSize, computerPlayer, playerNumber);
    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public void sendCoordinates(int x, int y, int player){
        client.sendSpot(x, y, player);
    }

    public void beginGettingCoordinates(){
        client.getSpot(playerNumber, spot -> {
            System.out.println("Got spot: " + spot[0] + ", " + spot[1]);
            changeTurn();
        });
        
    }
}