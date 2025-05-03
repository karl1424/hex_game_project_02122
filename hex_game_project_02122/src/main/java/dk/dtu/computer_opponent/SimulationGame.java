package dk.dtu.computer_opponent;

import java.util.LinkedList;
import java.util.Queue;

import dk.dtu.main.Coordinate;

public class SimulationGame {
    public Coordinate[][] board;
    public int boardM;
    public int boardN;
    public int winner = 0;
    
    public SimulationGame(int boardM, int boardN) {
        this.boardM = boardM;
        this.boardN = boardN;
        this.board = new Coordinate[boardM][boardN];
        
        for (int x = 0; x < boardM; x++) {
            for (int y = 0; y < boardN; y++) {
                board[x][y] = new Coordinate(x, y, 0);
            }
        }
    }
    
    public boolean checkForWin(Coordinate start, int turn) {
        int[] directionsX = {0, 0, -1, 1, -1, 1};
        int[] directionsY = {1, -1, 0, 0, 1, -1};

        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[boardM][boardN];

        queue.add(start);
        visited[start.getX()][start.getY()] = true;

        boolean reachedStartEdge = false;
        boolean reachedEndEdge = false;

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            if (turn == 2) {
                if (current.getY() == 0) {
                    reachedStartEdge = true;
                }
                if (current.getY() == boardN - 1) {
                    reachedEndEdge = true;
                }
            } else {
                if (current.getX() == 0) {
                    reachedStartEdge = true;
                }
                if (current.getX() == boardM - 1) {
                    reachedEndEdge = true;
                }
            }

            if (reachedStartEdge && reachedEndEdge) {
                return true;
            }

            for (int i = 0; i < 6; i++) {
                int neighborX = current.getX() + directionsX[i];
                int neighborY = current.getY() + directionsY[i];

                if (neighborX >= 0 && neighborX < boardM && neighborY >= 0 && neighborY < boardN) {
                    if (!visited[neighborX][neighborY] && board[neighborX][neighborY].getState() == turn) {
                        queue.add(board[neighborX][neighborY]);
                        visited[neighborX][neighborY] = true;
                    }
                }
            }
        }
        
        return false;
    }
}
