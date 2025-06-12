package dk.dtu.computer_opponent;

import java.util.List;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GUI;
import dk.dtu.main.GameBoard;

public class MCTS {
    private GameBoard gameBoard;
    private int playerNumber;
    private GUI gui;

    private int iterations;

    public MCTS(GameBoard gameBoard, int playerNumber, GUI gui, int iterations) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
        this.iterations = iterations;
    }

    public void makeMove() {
        Coordinate winningMove = findWinningMove();

        if (winningMove != null) {
            gameBoard.updateSpot(winningMove.getX(), winningMove.getY(), playerNumber);
            System.out.println("Computer (Player " + playerNumber + ") found winning move at: " +
                    winningMove.getX() + ", " + winningMove.getY());
            return;
        }

        MCTSNode rootNode = new MCTSNode(null, null, playerNumber);

        for (int i = 0; i < iterations; i++) {
            rootNode.selectAction(gameBoard);
        }

        //MCTSNode.printTimingStats();
        
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

    private Coordinate findWinningMove() {
        List<Coordinate> availableMoves = gameBoard.getAvailableMoves();

        for (Coordinate move : availableMoves) {
            if (gameBoard.checkWinningMove(move, playerNumber)) {
                return move;
            }
        }

        return null;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}