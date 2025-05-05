package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GUI;
import dk.dtu.main.GameBoard;

public class MCTS {
    private GameBoard gameBoard;
    private int playerNumber;
    private GUI gui;
    
    private static final int MAX_ITERATIONS = 10000;

    public MCTS(GameBoard gameBoard, int playerNumber, GUI gui) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
    }

    public void makeMove() {
        List<Coordinate> availableMoves = getAvailableMoves();
        
        MCTSNode rootNode = new MCTSNode(null, null, playerNumber);
        
        for (Coordinate move : availableMoves) {
            MCTSNode child = new MCTSNode(rootNode, move, playerNumber == 1 ? 2 : 1);
            rootNode.addChild(child);
        }
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            rootNode.selectAction(gameBoard);
        }
        long endTime = System.currentTimeMillis();
        
        System.out.println("MCTS completed " + MAX_ITERATIONS + " iterations in " +  (endTime - startTime) + "ms");
        
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
            System.out.println("Computer (Player " + playerNumber + ") chooses move at: " + bestMove.getX() + ", " + bestMove.getY() + 
            " (Visits: " + bestChild.getVisits() + 
            ", Win ratio: " + (bestChild.getWins() / (double)bestChild.getVisits()) + ")");
        } 
    }
    
    private List<Coordinate> getAvailableMoves() {
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