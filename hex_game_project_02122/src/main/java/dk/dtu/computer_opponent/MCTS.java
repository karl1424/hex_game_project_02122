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
    
    //Hi Bjarke
    private static final int MAX_ITERATIONS = 10000;

    public MCTS(GameBoard gameBoard, int playerNumber, GUI gui) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
    }

    public void makeMove() {
        MCTSNode rootNode = new MCTSNode(null, null, playerNumber);
        
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

    public int getPlayerNumber() {
        return playerNumber;
    }
}