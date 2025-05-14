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
        SimulationGame simGame = new SimulationGame(gameBoard);

        applyMovesToSimulation(node, simGame);

        int currentPlayer = node.playerNumber;

        while (simGame.winner == 0) {
            List<Coordinate> availableMoves = simGame.getAvailableMoves();

            if (availableMoves.isEmpty()) {
                break;
            }

            int i = rand.nextInt(availableMoves.size());
            Coordinate spot = availableMoves.get(i);
            // Coordinate spot = chooseHeuristicMove(availableMoves,simGame,currentPlayer);
            simGame.makeMove(spot, currentPlayer);

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

    private Coordinate chooseHeuristicMove(List<Coordinate> moves, SimulationGame simGame, int player) {
        int M = simGame.boardM;
        int N = simGame.boardN;
        int centerX = M / 2;
        int centerY = N / 2;
        int opponent = (player == 1) ? 2 : 1;

        List<ScoredMove> scored = new ArrayList<>();
        int[] directionsX = { 0, 0, -1, 1, -1, 1 };
        int[] directionsY = { 1, -1, 0, 0, 1, -1 };

        for (Coordinate mv : moves) {
            // 1) Center value
            double dist = Math.hypot(mv.getX() - centerX, mv.getY() - centerY);
            double centerScore = (1.0 / (1.0 + dist)) * 2;

            // 2) chain pattern(Friendly adjacency)
            int adj = 0;
            for (int k = 0; k < directionsX.length; k++) {
                int nx = mv.getX() + directionsX[k], ny = mv.getY() + directionsY[k];
                if (nx >= 0 && nx < M && ny >= 0 && ny < N && simGame.board[nx][ny].getState() == player) {
                    adj++;
                }
            }
            double adjScore = adj * 1;

            // 3) Opponent adjacency (blocking)
            int oppAdj = 0;
            for (int k = 0; k < directionsX.length; k++) {
                int nx = mv.getX() + directionsX[k];
                int ny = mv.getY() + directionsY[k];
                if (nx >= 0 && nx < M && ny >= 0 && ny < N && simGame.board[nx][ny].getState() == opponent) {
                    oppAdj++;
                }
            }
            double blockScore = oppAdj * 3;

            // 4) Bridge pattern
            int bridgeCount = 0;
            int[][] cross = {
                    { -1, 0, 0, -1 },
                    { -1, 0, 0, 1 },
                    { 1, 0, 0, -1 },
                    { 1, 0, 0, 1 } };
            for (int i = 0; i < cross.length; i++) {
                int dx1 = cross[i][0], dy1 = cross[i][1];
                int dx2 = cross[i][2], dy2 = cross[i][3];
                int nx1 = mv.getX() + dx1, ny1 = mv.getY() + dy1;
                int nx2 = mv.getX() + dx2, ny2 = mv.getY() + dy2;
                if (nx1 >= 0 && nx1 < M && ny1 >= 0 && ny1 < N
                        && nx2 >= 0 && nx2 < M && ny2 >= 0 && ny2 < N
                        && simGame.board[nx1][ny1].getState() == player
                        && simGame.board[nx2][ny2].getState() == player) {
                    bridgeCount++;
                }
            }
            double bridgeScore = bridgeCount * 1;

            // 5) Ladder escape & ladder breaker patterns
            int escapeCount = 0;
            int breakerCount = 0;
            int[][] escapePatterns = {
                { 2, 1, 1, 2 },
                { 2, -1, 1, -2 },
                { -2, 1, -1, 2 },
                { -2, -1, -1, -2 }
            };
            for (int[] pat : escapePatterns) {
                int sx = mv.getX() + pat[0], sy = mv.getY() + pat[1];
                int ex = mv.getX() + pat[2], ey = mv.getY() + pat[3];
                if (sx >= 0 && sx < M && sy >= 0 && sy < N
                        && ex >= 0 && ex < M && ey >= 0 && ey < N
                        && simGame.board[sx][sy].getState() == player
                        && simGame.board[ex][ey].getState() == 0) {
                    escapeCount++;
                }
            }
            int[][] breakerPatterns = {
                { 1, 2, 2, 1 },
                { 1, -2, 2, -1 },
                { -1, 2, -2, 1 },
                { -1, -2, -2, -1 }
            };
            for (int[] pat : breakerPatterns) {
                int ox = mv.getX() + pat[0], oy = mv.getY() + pat[1];
                int jx = mv.getX() + pat[2], jy = mv.getY() + pat[3];
                if (ox >= 0 && ox < M && oy >= 0 && oy < N
                        && jx >= 0 && jx < M && jy >= 0 && jy < N
                        && simGame.board[ox][oy].getState() == opponent
                        && simGame.board[jx][jy].getState() == 0) {
                    breakerCount++;
                }
            }
            double escapeScore = escapeCount * 2;
            double breakerScore = breakerCount * 2;

            double totalScore = centerScore + blockScore + bridgeScore + adjScore + escapeScore + breakerScore;
            scored.add(new ScoredMove(mv, totalScore));
        }

        scored.sort((a, b) -> Double.compare(b.score, a.score));
        int limit = Math.min(3, scored.size());
        int choice = rand.nextInt(limit);
        return scored.get(choice).move;
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

    private static class ScoredMove {
        Coordinate move;
        double score;

        ScoredMove(Coordinate m, double s) {
            move = m;
            score = s;
        }
    }
}