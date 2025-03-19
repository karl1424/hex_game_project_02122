package dk.dtu.main;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GamePanel extends Pane{
    private boolean isPlayerOneTurn = true;
    private GUI gui;
    private GameBoard gameBoard;
    private int gridSize = 5;

    public GamePanel(){
        startGame();
    }

    private void startGame(){
        gameBoard = new GameBoard(gridSize, gridSize);
        gui = new GUI(gridSize, gridSize, gameBoard, this);
        getChildren().add(gui);    
        gameBoard.printBoard(gameBoard.board);
        



        //gameBoard.printBoard(gameBoard.board);
        //gameBoard.pickSpot("1,1", 1, 1, 2); // Get this input somewhere else...
        //gameBoard.printBoard(gameBoard.board);
        //gameBoard.pickSpot("3,1", 3, 1, 2); // Get this input somewhere else...
        //gameBoard.printBoard(gameBoard.board); 
    }

    public boolean getTurn(){
        return isPlayerOneTurn;
    }

    public void changeTurn(){
        isPlayerOneTurn = !isPlayerOneTurn;
    }
}
