package dk.dtu.main;

import java.util.*;

import javafx.application.Platform;

public class GameBoard {
    public boolean BFSDebug = true;
    public Coordinate[][] board;
    public int boardM; // 5
    public int boardN; // 5
    private int winner = 0;
    private GamePanel gamePanel;

    GameBoard(int boardM, int boardN, GamePanel gamePanel) {
        this.boardM = boardM;
        this.boardN = boardN;
        this.gamePanel = gamePanel;
        initializeBoard();
    }

    public void initializeBoard() {
        board = new Coordinate[boardM][boardN];
        for (int x = 0; x < boardM; x++) {
            for (int y = 0; y < boardN; y++) {
                board[x][y] = new Coordinate(x, y, 0);
            }
        }

        // #region TEST!
        /*
         * board.put("0,1", new Coordinate(0, 1, 2));
         * board.put("1,0", new Coordinate(1, 0, 2));
         * board.put("0,2", new Coordinate(0, 2, 2));
         * board.put("1,2", new Coordinate(1, 2, 2));
         * board.put("2,0", new Coordinate(2, 0, 2));
         * board.put("2,1", new Coordinate(2, 1, 2));
         * //board.put("3,1", new Coordinate(3, 1, 2));
         * 
         * board.put("4,0", new Coordinate(4, 0, 1));
         * board.put("4,1", new Coordinate(4, 1, 2));
         * board.put("3,2", new Coordinate(3, 2, 1));
         * board.put("2,3", new Coordinate(2, 3, 1));
         */
        // pickSpot("1,4", 1, 4, 1);
        // #endregion

        // pickSpot("1,1", 1, 1, 2); // Get this input somewhere else...
        // pickSpot("3,1", 3, 1, 2); // Get this input somewhere else...
        // printBoard(board);

    }

    public void printBoard() {
        for (int y = 0; y < boardN; y++) {
            for (int x = 0; x < boardN; x++) {
                System.out.print(board[x][y].getState() + " ");
            }
            System.out.println();
        }
    }

    /*
     * public void printBoard(Map<String, Coordinate> board) {
     * // Find max x,y of board size
     * int maxX = 0, maxY = 0;
     * for (Coordinate coord : board.values()) {
     * maxX = Math.max(maxX, coord.getX());
     * maxY = Math.max(maxY, coord.getY());
     * }
     * 
     * // Create 2D array with default spots
     * int[][] grid = new int[maxX + 1][maxY + 1];
     * for (int i = 0; i <= maxY; i++) {
     * Arrays.fill(grid[i], -1);
     * }
     * 
     * // Populate the 2D array with values from the board Map
     * for (Coordinate coord : board.values()) {
     * grid[coord.getX()][coord.getY()] = coord.getState();
     * }
     * 
     * // Print the 2D board
     * System.out.println("Game Board:");
     * for (int y = 0; y <= maxY; y++) {
     * for (int x = 0; x <= maxX; x++) {
     * if (grid[x][y] == -1) {
     * System.out.print(". "); // Print empty space as "."
     * } else {
     * System.out.print(grid[x][y] + " "); // Print state (0, 1, 2)
     * }
     * }
     * System.out.println();
     * }
     * }
     */

    public void pickSpot(String spot, int x, int y, int turn) {
        board[x][y] = new Coordinate(x, y, turn);
        Coordinate start = board[x][y]; // The chosen spot

        // Check win condition
        boolean win = exploreNeighbors(start, turn);
        if (win) {
            this.winner = turn;
            System.out.println("Player " + turn + " wins!");
        } else {
            System.out.println("No winning path");
        }
        Platform.runLater(() -> {
            gamePanel.checkGameOver();
        });

    }

    public boolean exploreNeighbors(Coordinate start, int turn) {
        // Neighbour ruleset
        int[] directionsX = { 0, 0, -1, 1, -1, 1 };
        int[] directionsY = { 1, -1, 0, 0, 1, -1 };

        // Queue for the BFS
        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[boardM][boardN];
        queue.add(start);
        visited[start.getX()][start.getY()] = true;

        boolean reachedStartEdge = false;
        boolean reachedEndEdge = false;

        // Running through the queue
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            // Determine the winning condition based on the player's state
            if (turn == 2) { // Player 2: Check Top-to-Bottom (X-based)
                if (current.getY() == 0)
                    reachedStartEdge = true;
                if (current.getY() == boardN - 1)
                    reachedEndEdge = true;
            } else if (turn == 1) { // Player 1: Check Left-to-Right (Y-based)
                if (current.getX() == 0)
                    reachedStartEdge = true;
                if (current.getX() == boardM - 1)
                    reachedEndEdge = true;
            }

            if (BFSDebug)
                System.out.println("Processing: " + current);

            // If both conditions are met, a winning path is found
            if (reachedStartEdge && reachedEndEdge) {
                System.out.println("Winning Path Found!");
                return true;
            }

            // Check all 6 neighbors
            for (int i = 0; i < 6; i++) {
                int neighborX = current.getX() + directionsX[i];
                int neighborY = current.getY() + directionsY[i];

                // Ensure the neighbor exists, is not visited and belongs to the same player
                if (neighborX >= 0 && neighborX < boardM && neighborY >= 0 && neighborY < boardN) {
                    if (!visited[neighborX][neighborY] && board[neighborX][neighborY].getState() == turn) { // Check only player's own spots
                        queue.add(board[neighborX][neighborY]);
                        visited[neighborX][neighborY] = true;
                        if (BFSDebug)
                            System.out.println("Adding neighbor: " + board[neighborX][neighborY]);
                    }
                }
            }
        }
        return false; // No winning path found
    }

    public int getWinner() {
        return winner;
    }

    public Coordinate[][] getBoard() {
        return board;
    }
}
