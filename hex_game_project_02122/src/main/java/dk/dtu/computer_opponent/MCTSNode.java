package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GameBoard;

public class MCTSNode {
    private Coordinate move;
    private MCTSNode parent;
    private List<MCTSNode> children;
    private List<Coordinate> unexpandedMoves;

    private int visits;
    private int wins;

    private int playerNumber;
    private boolean fullyExpanded;

    private static final double EXPLORATION_PARAMETER = Math.sqrt(2);
    private Random rand;

    public MCTSNode(MCTSNode parent, Coordinate move, int playerNumber) {
        this.move = move;
        this.parent = parent;
        this.playerNumber = playerNumber;
        children = new ArrayList<>();
        visits = 0;
        wins = 0;
        rand = new Random();

    }

    public MCTSNode selection() {
        MCTSNode bestChild = null;
        double currentBestScore = Double.MIN_VALUE;

        for (MCTSNode child : children) {
            double exploitationTerm = child.wins / child.visits;
            double explorationTerm = EXPLORATION_PARAMETER * Math.sqrt(Math.log(this.visits) / child.visits);
            double uctScore = exploitationTerm + explorationTerm;

            if (uctScore > currentBestScore) {
                currentBestScore = uctScore;
                bestChild = child;
            }
        }
        return bestChild;
    }

    // public MCTSNode expansion(GameBoard gameBoard) {
    // List<Coordinate> availableMoves = getAvailableMoves(gameBoard);

    // if (availableMoves.isEmpty()) {
    // this.fullyExpanded = true;
    // return null;
    // }
    // }

    public int simulation(GameBoard gameBoard) {
        GameBoard simulationBoard = cloneGameBoard(gameBoard);

        if(move != null && parent != null) {
            simulationBoard.updateSpot(move.getX(), move.getY(), parent.getPlayerNumber());
        }

        int currentPlayerNumber = playerNumber;
        List<Coordinate> availableMoves = new ArrayList<>();

        while(simulationBoard.getWinner() == 0) {
            availableMoves = getAvailableMoves(gameBoard);
            int i = rand.nextInt(availableMoves.size());
            Coordinate spot = availableMoves.get(i);
            simulationBoard.updateSpot(spot.getX(), spot.getY(), currentPlayerNumber);
            currentPlayerNumber = (currentPlayerNumber == 1) ? 2 : 1;
        }

        return simulationBoard.getWinner();
    }

    public void backpropagation(int result) {
        incrementVisit();

        if(result == playerNumber) {
            addWin();
        }

        if(parent != null) {
            parent.backpropagation(result);
        }
    }

    private List<Coordinate> getAvailableMoves(GameBoard gameBoard) {
        List<Coordinate> availableMoves = new ArrayList<>();
        for (int x = 0; x < gameBoard.getBoardM(); x++) {
            for (int y = 0; y < gameBoard.getBoardN(); y++) {
                if (gameBoard.getBoard()[x][y].getState() == 0) {
                    availableMoves.add(new Coordinate(x, y, 0));
                }
            }
        }
        return availableMoves;
    }

    private GameBoard cloneGameBoard(GameBoard gameBoard) {
        GameBoard clone = new GameBoard(gameBoard.getBoardM(), gameBoard.getBoardN(), null);
        for (int x = 0; x < gameBoard.getBoardM(); x++) {
            for (int y = 0; y < gameBoard.getBoardN(); y++) {
                clone.getBoard()[x][y].setState(gameBoard.getBoard()[x][y].getState());
            }
        }
        return clone;
    }

    public int getVisits() {
        return visits;
    }

    public double getWins() {
        return wins;
    }

    public MCTSNode getParent() {
        return parent;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void addChild(MCTSNode child) {
        children.add(child);
    }

    public void incrementVisit() {
        visits++;
    }

    public void addWin() {
        wins++;
    }

}
