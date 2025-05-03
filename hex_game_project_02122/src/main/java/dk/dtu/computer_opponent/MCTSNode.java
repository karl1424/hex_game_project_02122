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
        SimulationGame simGame = new SimulationGame(gameBoard.getBoardM(), gameBoard.getBoardN());
        
        for (int x = 0; x < gameBoard.getBoardM(); x++) {
            for (int y = 0; y < gameBoard.getBoardN(); y++) {
                simGame.board[x][y].setState(gameBoard.getBoard()[x][y].getState());
            }
        }
        
        if (move != null && parent != null) {
            int x = move.getX();
            int y = move.getY();
            simGame.board[x][y].setState(parent.getPlayerNumber());
        }

        int currentPlayerNumber = playerNumber;
        List<Coordinate> availableMoves = new ArrayList<>();

        while (simGame.winner == 0) {
            availableMoves = getAvailableMoves(simGame);
            if (availableMoves.isEmpty())
                break;
            int i = rand.nextInt(availableMoves.size());
            Coordinate spot = availableMoves.get(i);
            
            simGame.board[spot.getX()][spot.getY()].setState(currentPlayerNumber);
            
            boolean win = simGame.checkForWin(spot, currentPlayerNumber);
            if (win) {
                simGame.winner = currentPlayerNumber;
            }
            currentPlayerNumber = (currentPlayerNumber == 1) ? 2 : 1;
        }
        return simGame.winner;
    }

    public void backpropagation(int result) {
        MCTSNode current = this;

        while (current != null) {
            current.incrementVisit();

            if (result == current.playerNumber) {
                current.addWin();
            }

            current = current.parent;
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

    private List<Coordinate> getAvailableMoves(SimulationGame simGame) {
        List<Coordinate> availableMoves = new ArrayList<>();
        for (int x = 0; x < simGame.boardM; x++) {
            for (int y = 0; y < simGame.boardN; y++) {
                if (simGame.board[x][y].getState() == 0) {
                    availableMoves.add(new Coordinate(x, y, 0));
                }
            }
        }
        return availableMoves;
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
