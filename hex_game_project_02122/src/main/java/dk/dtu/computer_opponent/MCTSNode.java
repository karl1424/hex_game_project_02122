package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GameBoard;

public class MCTSNode {
    private Coordinate move;
    private MCTSNode parent;
    private List<MCTSNode> children;
    private boolean expanded;

    private int visits;
    private double wins;

    private int playerNumber;
    private static final double EXPLORATION_PARAMETER = 0.7;
    private static final double EPSILON = 1e-6;
    private Random rand;
    private static RolloutPattern rolloutPattern;

    public MCTSNode(MCTSNode parent, Coordinate move, int playerNumber) {
        this.move = move;
        this.parent = parent;
        this.playerNumber = playerNumber;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0;
        this.expanded = false;
        this.rand = new Random();
        if (rolloutPattern == null) {
            rolloutPattern = new RolloutPattern();
        }
    }

    public void selectAction(GameBoard gameBoard) {
        List<MCTSNode> visited = new LinkedList<>();
        MCTSNode current = this;
        visited.add(current);

        while (!current.isLeaf()) {
            current = current.selection();
            visited.add(current);
        }

        if (!current.expanded) {
            current.expansion(gameBoard);
            current.expanded = true;
        }

        MCTSNode nodeToRollout = current;
        double value = simulation(nodeToRollout, gameBoard);

        for (MCTSNode node : visited) {
            node.backpropagation(value);
        }
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void expansion(GameBoard gameBoard) {
        List<Coordinate> availableMoves = getAvailableMoves(gameBoard);

        int nextPlayer = (playerNumber == 1) ? 2 : 1;
        for (Coordinate availableMove : availableMoves) {
            MCTSNode child = new MCTSNode(this, availableMove, nextPlayer);
            children.add(child);
        }
    }

    private MCTSNode selection() {
        MCTSNode selected = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (MCTSNode child : children) {
            double exploitationTerm = child.wins / (child.visits + EPSILON);
            double explorationTerm = EXPLORATION_PARAMETER
                    * Math.sqrt(Math.log(this.visits + 1) / (child.visits + EPSILON)) + rand.nextDouble() * EPSILON;
            double uctScore = exploitationTerm + explorationTerm;

            if (uctScore > bestValue) {
                selected = child;
                bestValue = uctScore;
            }
        }

        return selected;
    }

    private double simulation(MCTSNode node, GameBoard gameBoard) {
        SimulationGame simGame = new SimulationGame(gameBoard.getBoardM(), gameBoard.getBoardN());

        for (int x = 0; x < gameBoard.getBoardM(); x++) {
            for (int y = 0; y < gameBoard.getBoardN(); y++) {
                simGame.board[x][y].setState(gameBoard.getBoard()[x][y].getState());
            }
        }

        applyMovesToSimulation(node, simGame);

        int currentPlayer = node.playerNumber;

        while (simGame.winner == 0) {
            List<Coordinate> availableMoves = getAvailableMoves(simGame);

            Coordinate spot = rolloutPattern.getHeuristicMove(availableMoves, simGame, currentPlayer);
            if (spot == null) {
                spot = availableMoves.get(rand.nextInt(availableMoves.size()));
            }
            simGame.board[spot.getX()][spot.getY()].setState(currentPlayer);

            if (simGame.checkForWin(spot, currentPlayer)) {
                simGame.winner = currentPlayer;
            }

            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }

        return (simGame.winner == this.playerNumber) ? 1.0 : 0.0;
    }

    private void applyMovesToSimulation(MCTSNode node, SimulationGame simGame) {
        List<MCTSNode> pathToRoot = new ArrayList<>();
        MCTSNode current = node;

        while (current.parent != null) {
            pathToRoot.add(0, current);
            current = current.parent;
        }

        for (MCTSNode pathNode : pathToRoot) {
            if (pathNode.move != null && pathNode.parent != null) {
                Coordinate move = pathNode.move;
                int player = pathNode.parent.playerNumber;
                simGame.board[move.getX()][move.getY()].setState(player);
            }
        }
    }

    public void backpropagation(double value) {
        visits++;
        wins += value;
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

    public boolean isFullyExpanded() {
        return expanded;
    }

    public int getVisits() {
        return visits;
    }

    public double getWins() {
        return wins;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public List<MCTSNode> getChildren() {
        return children;
    }

    public Coordinate getMove() {
        return move;
    }

    public void addChild(MCTSNode child) {
        children.add(child);
    }
}