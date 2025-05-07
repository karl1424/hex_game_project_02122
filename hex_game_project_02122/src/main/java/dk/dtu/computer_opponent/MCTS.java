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

    private Coordinate findWinningMove() {
        List<Coordinate> availableMoves = getAvailableMoves(gameBoard);
        
        for (Coordinate move : availableMoves) {
            gameBoard.getBoard()[move.getX()][move.getY()].setState(playerNumber);
            
            boolean isWinningMove = checkForWin(move, playerNumber);
            
            gameBoard.getBoard()[move.getX()][move.getY()].setState(0);
            
            if (isWinningMove) {
                return move;
            }
        }
        
        return null;
    }

    private boolean checkForWin(Coordinate start, int turn) {
        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[gameBoard.getBoardM()][gameBoard.getBoardN()];

        queue.add(start);
        visited[start.getX()][start.getY()] = true;

        boolean reachedStartEdge = false;
        boolean reachedEndEdge = false;

        int[] directionsX = { 0, 0, -1, 1, -1, 1 };
        int[] directionsY = { 1, -1, 0, 0, 1, -1 };

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            if (turn == 2) {
                if (current.getY() == 0) {
                    reachedStartEdge = true;
                }
                if (current.getY() == gameBoard.getBoardN() - 1) {
                    reachedEndEdge = true;
                }
            } else {
                if (current.getX() == 0) {
                    reachedStartEdge = true;
                }
                if (current.getX() == gameBoard.getBoardM() - 1) {
                    reachedEndEdge = true;
                }
            }

            if (reachedStartEdge && reachedEndEdge) {
                return true;
            }

            for (int i = 0; i < 6; i++) {
                int neighborX = current.getX() + directionsX[i];
                int neighborY = current.getY() + directionsY[i];

                if (neighborX >= 0 && neighborX < gameBoard.getBoardM() &&
                        neighborY >= 0 && neighborY < gameBoard.getBoardN()) {
                    if (!visited[neighborX][neighborY] &&
                            gameBoard.getBoard()[neighborX][neighborY].getState() == turn) {
                        queue.add(gameBoard.getBoard()[neighborX][neighborY]);
                        visited[neighborX][neighborY] = true;
                    }
                }
            }
        }

        return false;
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

    public int getPlayerNumber() {
        return playerNumber;
    }
}