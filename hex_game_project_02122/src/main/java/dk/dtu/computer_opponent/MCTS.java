package dk.dtu.computer_opponent;

import java.util.List;

import dk.dtu.game_components.Coordinate;
import dk.dtu.game_components.GameBoard;

public class MCTS {
    private GameBoard gameBoard;
    private SimulationGame simBoard;
    private int playerNumber;

    private int iterations;

    private boolean debug = false;

    public MCTS(GameBoard gameBoard, int playerNumber, int iterations) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.iterations = iterations;
    }

    public MCTS(SimulationGame simBoard, int playerNumber, int iterations) {
        this.simBoard = simBoard;
        this.playerNumber = playerNumber;
        this.iterations = iterations;
    }

    public void makeMove() {
        Coordinate winningMove = findWinningMove();

        if (winningMove != null) {
            gameBoard.updateSpot(winningMove.getX(), winningMove.getY(), playerNumber);
            return;
        }

        MCTSNode rootNode = new MCTSNode(null, null, playerNumber);

        for (int i = 0; i < iterations; i++) {
            rootNode.selectAction(gameBoard);
        }

        // MCTSNode.printTimingStats();

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
            if (debug)
                System.out.println("Computer (Player " + playerNumber + ") chooses move at: " + bestMove.getX() + ", "
                        + bestMove.getY() +
                        " (Visits: " + bestChild.getVisits() +
                        ", Win ratio: " + (bestChild.getWins() / (double) bestChild.getVisits()) + ")");
        }
    }

    public Coordinate makeMoveInTest() {
        Coordinate winningMove = findWinningMoveInTest();

        if (winningMove != null) {
            return winningMove;
        }

        MCTSNode rootNode = new MCTSNode(null, null, playerNumber);

        for (int i = 0; i < iterations; i++) {
            rootNode.selectAction(simBoard);
        }

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
            return bestMove;
        }
        return null;
    }

    public Coordinate findWinningMoveInTest() {
        List<Coordinate> availableMoves = simBoard.getAvailableMoves();

        for (Coordinate move : availableMoves) {
            if (simBoard.checkForWin(move, playerNumber)) {
                return move;
            }
        }
        return null;
    }

    public void setSimulationGame(SimulationGame simBoard) {
        this.simBoard = simBoard;
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
