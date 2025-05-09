package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GameBoard;

public class RolloutPattern {
    private Random rand;

    public RolloutPattern() {
        this.rand = new Random();
    }

    public Coordinate getHeuristicMove(List<Coordinate> availableMoves, SimulationGame simGame, int playerNumber) {

        List<Coordinate> connectingMoves = findConnectingMoves(availableMoves, simGame, playerNumber);

        if (!connectingMoves.isEmpty()) {
            return connectingMoves.get(rand.nextInt(connectingMoves.size()));
        }

        return getRandomMove(availableMoves);
    }

    public Coordinate findWinningMoveOnBoard(GameBoard gameBoard, int playerNumber) {
        List<Coordinate> availableMoves = getAvailableMoves(gameBoard);

        for (Coordinate move : availableMoves) {
            gameBoard.getBoard()[move.getX()][move.getY()].setState(playerNumber);

            boolean isWinningMove = checkForWinOnBoard(move, playerNumber, gameBoard);

            gameBoard.getBoard()[move.getX()][move.getY()].setState(0);

            if (isWinningMove) {
                return move;
            }
        }

        return null;
    }

    private boolean checkForWinOnBoard(Coordinate start, int turn, GameBoard gameBoard) {
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

    private Coordinate getRandomMove(List<Coordinate> availableMoves) {
        if (availableMoves == null || availableMoves.isEmpty()) {
            return null;
        }
        return availableMoves.get(rand.nextInt(availableMoves.size()));
    }

    private List<Coordinate> findConnectingMoves(List<Coordinate> availableMoves, SimulationGame simGame,
            int playerNumber) {
        List<Coordinate> connectingMoves = new ArrayList<>();
        int[] directionsX = { 0, 0, -1, 1, -1, 1 };
        int[] directionsY = { 1, -1, 0, 0, 1, -1 };

        for (Coordinate move : availableMoves) {
            boolean isConnecting = false;

            for (int i = 0; i < 6 && !isConnecting; i++) {
                int nx1 = move.getX() + directionsX[i];
                int ny1 = move.getY() + directionsY[i];

                if (nx1 < 0 || nx1 >= simGame.boardM || ny1 < 0 || ny1 >= simGame.boardN)
                    continue;

                if (simGame.board[nx1][ny1].getState() == 3 - playerNumber) {
                    isConnecting = true;
                    break;
                }

                for (int j = 0; j < 6; j++) {
                    int nx2 = nx1 + directionsX[j];
                    int ny2 = ny1 + directionsY[j];

                    if (nx2 < 0 || nx2 >= simGame.boardM || ny2 < 0 || ny2 >= simGame.boardN)
                        continue;

                    if (simGame.board[nx2][ny2].getState() == 3 - playerNumber) {
                        isConnecting = true;
                        break;
                    }
                }
            }

            if (isConnecting) {
                connectingMoves.add(move);
            }
        }

        return connectingMoves;
    }

}