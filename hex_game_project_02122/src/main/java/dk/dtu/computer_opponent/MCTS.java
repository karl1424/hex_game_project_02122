package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GUI;
import dk.dtu.main.GameBoard;

public class MCTS {
    private GameBoard gameBoard;
    private int playerNumber;
    private GUI gui;

    private int iterations;
    private RolloutPattern rolloutPattern;

    public MCTS(GameBoard gameBoard, int playerNumber, GUI gui, int iterations) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
        this.iterations = iterations;
        this.rolloutPattern = new RolloutPattern();
    }

    public void makeMove() {
        Coordinate winningMove = rolloutPattern.findWinningMoveOnBoard(gameBoard, playerNumber);

        if (winningMove != null) {
            gameBoard.updateSpot(winningMove.getX(), winningMove.getY(), playerNumber);
            System.out.println("Computer (Player " + playerNumber + ") found winning move at: " +
                    winningMove.getX() + ", " + winningMove.getY());
            return;
        }

        MCTSNode rootNode = new MCTSNode(null, null, playerNumber);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            rootNode.selectAction(gameBoard);
        }
        long endTime = System.currentTimeMillis();

        System.out.println("MCTS completed " + iterations + " iterations in " + (endTime - startTime) + "ms");

        MCTSNode bestChild = null;
        int mostVisits = -1;

        for (MCTSNode child : rootNode.getChildren()) {
            if (child.getVisits() > mostVisits) {
                mostVisits = child.getVisits();
                bestChild = child;
            }
        }

        if (bestChild != null && bestChild.getMove() != null) {
            Coordinate bestMove = bestChild.getMove();
            gameBoard.updateSpot(bestMove.getX(), bestMove.getY(), playerNumber);
            System.out.println("Computer (Player " + playerNumber + ") chooses move at: " + bestMove.getX() + ", "
                    + bestMove.getY() +
                    " (Visits: " + bestChild.getVisits() +
                    ", Win ratio: " + (bestChild.getWins() / (double) bestChild.getVisits()) + ")");
        }
    }

    
    public int getPlayerNumber() {
        return playerNumber;
    }
}