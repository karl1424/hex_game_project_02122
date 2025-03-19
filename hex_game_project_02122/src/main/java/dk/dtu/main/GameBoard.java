package dk.dtu.main;
import java.util.*;

public class GameBoard {
    public static boolean BFSDebug = true;
    public static Map<String, Coordinate> board;
        public static int boardM; //5
        public  static int boardN; // 5
    
    
        GameBoard (int boardM, int boardN) {
            GameBoard.boardM = boardM;
            GameBoard.boardN = boardN;
            initializeBoard();
        }
    
        public static void initializeBoard(){
            board = new HashMap<>();
        for (int m = 0; m < boardM; m++){
            for (int n = 0; n < boardN; n++){
                String spot = m + "," + n;  // Use consistent format
                board.put(spot, new Coordinate(m, n, 0));
            }
        }
        
        //#region TEST!  
        board.put("0,1", new Coordinate(0, 1, 2));  
        board.put("1,0", new Coordinate(1, 0, 2));  
        board.put("0,2", new Coordinate(0, 2, 2)); 
        board.put("1,2", new Coordinate(1, 2, 2)); 
        board.put("2,0", new Coordinate(2, 0, 2));
        board.put("2,1", new Coordinate(2, 1, 2)); 
        //board.put("3,1", new Coordinate(3, 1, 2)); 

        board.put("4,0", new Coordinate(4, 0, 1)); 
        board.put("4,1", new Coordinate(4, 1, 2)); 
        board.put("3,2", new Coordinate(3, 2, 1)); 
        board.put("2,3", new Coordinate(2, 3, 1));  
        //pickSpot("1,4", 1, 4, 1);
        //#endregion

        //pickSpot("1,1", 1, 1, 2); // Get this input somewhere else...
        //pickSpot("3,1", 3, 1, 2); // Get this input somewhere else...
        //printBoard(board);

    }

    public void printBoard(Map<String, Coordinate> board) {
        // Find max x,y of board size
        int maxX = 0, maxY = 0;
        for (Coordinate coord : board.values()) {
            maxX = Math.max(maxX, coord.getX());
            maxY = Math.max(maxY, coord.getY());
        }
    
        // Create 2D array with default spots
        int[][] grid = new int[maxY + 1][maxX + 1];
        for (int i = 0; i <= maxY; i++) {
            Arrays.fill(grid[i], -1);
        }
    
        // Populate the 2D array with values from the board Map
        for (Coordinate coord : board.values()) {
            grid[coord.getX()][coord.getY()] = coord.getState();
        }
    
        // Print the 2D board
        System.out.println("Game Board:");
        for (int y = 0; y <= maxY; y++) {
            for (int x = 0; x <= maxX; x++) {
                if (grid[y][x] == -1) {
                    System.out.print(". ");  // Print empty space as "."
                } else {
                    System.out.print(grid[y][x] + " ");  // Print state (0, 1, 2)
                }
            }
            System.out.println();
        }
    }

    public void pickSpot(String spot, int x, int y, int turn) {
        board.put(spot, new Coordinate(x, y, turn));
        Coordinate start = board.get(spot);  // The chosen spot
        
        //Check win condition
        boolean win = exploreNeighbors(start, board, turn);
        if (win) {
            System.out.println("Player " + turn + " wins!");
        } else {
            System.out.println("No winning path");
        }
    }
    
    public static boolean exploreNeighbors(Coordinate start, Map<String, Coordinate> board, int turn) {
        // Neighbour ruleset
        int[] directionsX = {-1, 0, -1, 1, 0, 1};  
        int[] directionsY = {-1, -1, 0, 0, 1, 1};  
    
        // Queue for the BFS
        Queue<Coordinate> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(start);
        visited.add(start.getX() + "," + start.getY());
    
        boolean reachedStartEdge = false;
        boolean reachedEndEdge = false;
    
        // Running through the queue
        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
    
            // Determine the winning condition based on the player's state
            if (turn == 2) {  // Player 2: Check Top-to-Bottom (X-based)
                if (current.getX() == 0) reachedStartEdge = true;
                if (current.getX() == boardM - 1) reachedEndEdge = true;
            } else if (turn == 1) {  // Player 1: Check Left-to-Right (Y-based)
                if (current.getY() == 0) reachedStartEdge = true;
                if (current.getY() == boardN - 1) reachedEndEdge = true;
            }
    
            if (BFSDebug) System.out.println("Processing: " + current);
    
            // If both conditions are met, a winning path is found
            if (reachedStartEdge && reachedEndEdge) {
                System.out.println("Winning Path Found!");
                return true;
            }
    
            // Check all 6 neighbors
            for (int i = 0; i < 6; i++) {
                int neighborX = current.getX() + directionsX[i];
                int neighborY = current.getY() + directionsY[i];
                String neighborKey = neighborX + "," + neighborY;
    
                // Ensure the neighbor exists, is not visited and belongs to the same player
                if (board.containsKey(neighborKey) && !visited.contains(neighborKey)) {
                    Coordinate neighbor = board.get(neighborKey);
                    if (neighbor.getState() == turn) {  // Check only player's own spots
                        queue.add(neighbor);
                        visited.add(neighborKey);
                        if (BFSDebug) System.out.println("Adding neighbor: " + neighbor);
                    }
                }
            }
        }
        return false;  // No winning path found
    }
}
