package dk.dtu.main;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;

public class GamePanel extends Pane {
    private boolean isPlayerOneTurn = true;
    private GUI gui;
    private GameBoard gameBoard;
    private int gridSize;

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
        getChildren().add(gui);
        gameBoard.printBoard(gameBoard.board);

        // gameBoard.printBoard(gameBoard.board);
        // gameBoard.pickSpot("1,1", 1, 1, 2); // Get this input somewhere else...
        // gameBoard.printBoard(gameBoard.board);
        // gameBoard.pickSpot("3,1", 3, 1, 2); // Get this input somewhere else...
        // gameBoard.printBoard(gameBoard.board);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean getTurn() {
        return isPlayerOneTurn;
    }

    public void changeTurn() {
        isPlayerOneTurn = !isPlayerOneTurn;
    }
}
