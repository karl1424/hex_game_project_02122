package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GameBoard;

public class MCTSNode {
    private static int counter = 0;
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

    private static long totalSelectionTime = 0;
    private static long totalExpansionTime = 0;
    private static long totalSimulationTime = 0;
    private static long totalBackpropagationTime = 0;
    private static int runCount = 0;

    public MCTSNode(MCTSNode parent, Coordinate move, int playerNumber) {
        this.move = move;
        this.parent = parent;
        this.playerNumber = playerNumber;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0;
        this.expanded = false;
        this.rand = new Random();
    }

    public void selectAction(GameBoard gameBoard) {
        long startTime, endTime;

        List<MCTSNode> visited = new LinkedList<>();
        MCTSNode current = this;
        visited.add(current);

        startTime = System.nanoTime();
        while (!current.isLeaf()) {
            current = current.selection();
            visited.add(current);
        }
        endTime = System.nanoTime();
        totalSelectionTime += (endTime - startTime);

        if (!current.expanded) {
            startTime = System.nanoTime();
            current.expansion(gameBoard);
            current.expanded = true;
            endTime = System.nanoTime();
            totalExpansionTime += (endTime - startTime);
        }

        startTime = System.nanoTime();
        MCTSNode nodeToRollout = current;
        double value = simulation(nodeToRollout, gameBoard);
        endTime = System.nanoTime();
        totalSimulationTime += (endTime - startTime);

        startTime = System.nanoTime();
        for (MCTSNode node : visited) {
            node.backpropagation(value);
        }
        endTime = System.nanoTime();
        totalBackpropagationTime += (endTime - startTime);

        runCount++;
    }

    public static void printTimingStats() {
        System.out.println("After " + runCount + " runs:");
        System.out.println("Avg Selection Time:      " + (totalSelectionTime / runCount) / 1_000.0 + " µs");
        System.out.println("Avg Expansion Time:      " + (totalExpansionTime / runCount) / 1_000.0 + " µs");
        System.out.println("Avg Simulation Time:     " + (totalSimulationTime / runCount) / 1_000.0 + " µs");
        System.out.println("Avg Backpropagation Time:" + (totalBackpropagationTime / runCount) / 1_000.0 + " µs");
        long total = totalSelectionTime + totalExpansionTime + totalSimulationTime + totalBackpropagationTime;
        System.out.println("Avg Total Time Per Iteration: " + (double)(total / runCount) / 1_000.0 + " µs\n");
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void expansion(GameBoard gameBoard) {
        List<Coordinate> availableMoves = gameBoard.getAvailableMoves();

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
                    * Math.sqrt(Math.log(this.visits + 1) / (child.visits + EPSILON));
            double uctScore = exploitationTerm + explorationTerm;

            if (uctScore > bestValue) {
                selected = child;
                bestValue = uctScore;
            }
        }

        return selected;
    }

    private double simulation(MCTSNode node, GameBoard gameBoard) {
        SimulationGame simGame = new SimulationGame(gameBoard);

        applyMovesToSimulation(node, simGame);

        int currentPlayer = node.playerNumber;

        List<Coordinate> availableMoves = simGame.getAvailableMoves();
        int totalMoves = availableMoves.size();
        int moveCount = 0;

        while (moveCount < totalMoves) {
            int i = rand.nextInt(availableMoves.size());
            Coordinate spot = availableMoves.get(i);
            simGame.makeMove(spot, currentPlayer);

            availableMoves.remove(spot);

            moveCount++;
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }
        simGame.checkWin();

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