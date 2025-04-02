package dk.dtu.main;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;

public class GamePanel extends Pane {
    private boolean isPlayerOneTurn = true;
    private GUI gui;
    private GameBoard gameBoard;
    private int gridSize;
    private ComputerOpponent computerOpponent;
    private int computerPlayer;

    public GamePanel() {
        this.gridSize = 0;

        chooseBoardSize();
        choosePlayerNumber();
        startGame();
    }

    private void choosePlayerNumber() {
        List<String> choices = Arrays.asList("Player 1", "Player 2");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Player 1", choices);
        dialog.setTitle("Choose Player");
        dialog.setHeaderText("HEX Game");
        dialog.setContentText("Select your player:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            String selectedPlayer = result.get();
            this.computerPlayer = selectedPlayer.equals("Player 1") ? 2 : 1;
        } else {
            System.exit(0);
        }
    }

    private void chooseBoardSize() {
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

                    if (gridSize >= 3 && gridSize <= 99 && gridSize % 2 != 0) {
                        validInput = true;
                    } else {
                        showError("The number must be an odd integer bigger or equal than 3 and smaller than 100. Try again.");
                    }
                } catch (NumberFormatException e) {
                    showError("The number must be an odd integer bigger or equal than 3. Try again.");
                }
            } else {
                System.exit(0);
                return;
            }
        }
    }

    private void startGame() {
        gameBoard = new GameBoard(gridSize, gridSize, this);
        gui = new GUI(gridSize, gridSize, gameBoard, this);
        computerOpponent = new ComputerOpponent(gameBoard, computerPlayer, gui);
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

    public void checkGameOver() {
        if (gameBoard.getWinner() != 0) {
            showGameOver(gameBoard.getWinner());
        }
    }

    private void showGameOver(int winner) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Finished");
        alert.setContentText("Player " + winner + " (" + (winner == 1 ? "Red" : "Blue") + ") has won the game!");
        alert.showAndWait();
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
}