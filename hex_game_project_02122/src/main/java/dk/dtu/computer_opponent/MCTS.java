package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GUI;
import dk.dtu.main.GameBoard;
import javafx.scene.paint.Color;

public class MCTS {
    private GameBoard gameBoard;
    private int playerNumber;
    private GUI gui;
    private Random rand;
    private Coordinate lastHumanMove;

    public MCTS(GameBoard gameBoard, int playerNumber, GUI gui) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
        this.rand = new Random();
    }

    public void makeMove() {
        makeRandomMove();
    }


    private void makeRandomMove() {
        if (gameBoard.getWinner() != 0) {
            return;
        }

        List<Coordinate> emptySpots = new ArrayList<>();
        for (int x = 0; x < gameBoard.boardN; x++) {
            for (int y = 0; y < gameBoard.boardM; y++) {
                if (gameBoard.getBoard()[x][y].getState() == 0) {
                    emptySpots.add(gameBoard.getBoard()[x][y]);
                }
            }
        }

        if (!emptySpots.isEmpty()) {
            int i = rand.nextInt(emptySpots.size());
            Coordinate spot = emptySpots.get(i);
            gameBoard.updateSpot(spot.getX(), spot.getY(), playerNumber);
            System.out.println("Computer placed random move at: " + spot.getX() + ", " + spot.getY());
        }
    }


    public int getPlayerNumber() {
        return playerNumber;
    }
}
