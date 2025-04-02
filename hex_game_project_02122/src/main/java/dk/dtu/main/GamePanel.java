package dk.dtu.main;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;

public class GamePanel extends Pane {
    private boolean isPlayerOneTurn = true;
    private GUI gui;
    private GameBoard gameBoard;
    private int gridSize;
    private ComputerOpponent computerOpponent;

    public GamePanel() {
        this.gridSize = 0;
        boolean validInput = false;

        while (!validInput) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Input boardsize");
            dialog.setHeaderText("HEX Game");
            dialog.setContentText("Input an odd integer:");

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                try {
                    gridSize = Integer.parseInt(result.get());

                    if (gridSize >= 3 && gridSize % 2 != 0) {
                        validInput = true;
                    } else {
                        showError("The number must be an odd integer bigger or equal than 3. Try again.");
                    }
                } catch (NumberFormatException e) {
                    showError("The number must be an odd integer bigger or equal than 3. Try again.");
                }
            } else {
                System.exit(0);
                return;
            }
        }
        startGame();
    }

    private void startGame() {
        gameBoard = new GameBoard(gridSize, gridSize);
        gui = new GUI(gridSize, gridSize, gameBoard, this);
        computerOpponent = new ComputerOpponent(gameBoard, 2, gui);
        gui.setComputerOpponent(computerOpponent);
        
        getChildren().add(gui);
        System.out.println("Initial empty board:");
        gameBoard.printBoard(gameBoard.board);
        
        // Let computer go first if it's player 1
        if (computerOpponent.getPlayerNumber() == 1) {
            computerOpponent.makeMove();
            changeTurn();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // public void showGameOver(int winner) {
    //     Alert alert = new Alert(AlertType.INFORMATION);
    //     alert.setTitle("Game Over");
    //     alert.setHeaderText("Game Finished");
    //     alert.setContentText("Player " + winner + " (" + (winner == 1 ? "Red" : "Blue") + ") has won the game!");
    //     alert.showAndWait();
    // }

    public boolean getTurn() {
        return isPlayerOneTurn;
    }

    public void changeTurn() {
        isPlayerOneTurn = !isPlayerOneTurn;
    }

    public ComputerOpponent getComputerOpponent() {
        return computerOpponent;
    }
}