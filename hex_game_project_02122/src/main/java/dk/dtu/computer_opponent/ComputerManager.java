package dk.dtu.computer_opponent;

import dk.dtu.game_components.GameBoard;
import dk.dtu.main.NativeWrapper;

public class ComputerManager {
    private GameBoard gameBoard;
    private int playerNumber;
    private SmallBoardStrategy smallBoardStrategy;
    private MCTS mcts;
    private int difficulty;
    private boolean useSmallBoardStrategyHard;
    private NativeWrapper wrapper;
    private boolean nativeTest = false;

    public ComputerManager(GameBoard gameBoard, int playerNumber, int difficulty) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.difficulty = difficulty;
        smallBoardStrategy = new SmallBoardStrategy(gameBoard, playerNumber);
        mcts = new MCTS(gameBoard, playerNumber, difficulty);
        useSmallBoardStrategyHard = (gameBoard.getBoardM() == 3 && gameBoard.getBoardN() == 3 && difficulty == 10000);
        if (nativeTest)
            wrapper = new NativeWrapper();
    }

    public void makeMove() {
        if (gameBoard.getWinner() != 0) {
            System.out.println("Game is won by Player " + gameBoard.getWinner());
            return;
        }
        if (useSmallBoardStrategyHard && playerNumber == 1) {
            smallBoardStrategy.makeMove();
        } else if (nativeTest) {
            int[][] tempBoard = new int[gameBoard.getBoardN()][gameBoard.getBoardM()];
            for (int i = 0; i < gameBoard.getBoardN(); i++) {
                for (int j = 0; j < gameBoard.getBoardM(); j++) {
                    tempBoard[i][j] = gameBoard.getBoard()[i][j].getState();
                }
            }

            long startTime = System.currentTimeMillis();
            int[] move = wrapper.runAlgorithm(tempBoard, playerNumber, difficulty);
            System.out.println("FROM C++ : x: " + move[0] + ", y: " + move[1]);
            long endTime = System.currentTimeMillis();
            System.out.println(
                    "MCTS with C++ completed " + difficulty + " iterations in " + (endTime - startTime) + "ms");
            gameBoard.updateSpot(move[0], move[1], playerNumber);
        } else {
            long startTime = System.currentTimeMillis();
            mcts.makeMove();
            long endTime = System.currentTimeMillis();
            System.out.println("MCTS completed " + difficulty + " iterations in " + (endTime - startTime) + "ms");
        }
    }

    public void setLastHumanMove(int x, int y) {
        smallBoardStrategy.setLastHumanMove(x, y);
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

}
