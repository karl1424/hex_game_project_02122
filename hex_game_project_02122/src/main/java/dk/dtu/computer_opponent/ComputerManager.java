package dk.dtu.computer_opponent;

import dk.dtu.main.GUI;
import dk.dtu.main.GameBoard;

public class ComputerManager {
    private GameBoard gameBoard;
    private int playerNumber;
    private GUI gui;
    private SmallBoardStrategy smallBoardStrategy;
    private MCTS mcts;
    private int difficulty;
    private boolean useSmallBoardStrategyHard;

    public ComputerManager(GameBoard gameBoard, int playerNumber, GUI gui, int difficulty) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
        this.difficulty = difficulty;
        smallBoardStrategy = new SmallBoardStrategy(gameBoard, playerNumber, gui);
        mcts = new MCTS(gameBoard, playerNumber, gui, difficulty);
        useSmallBoardStrategyHard = (gameBoard.getBoardM() == 3 && gameBoard.getBoardN() == 3 && difficulty == 10000);
    }

    public void makeMove() {
        if (gameBoard.getWinner() != 0) {
            System.out.println("Game is won by Player " + gameBoard.getWinner());
            return;
        }
        if (useSmallBoardStrategyHard && playerNumber == 1) {
            smallBoardStrategy.makeMove();
        } else {
            mcts.makeMove();
        }
    }

    public void setLastHumanMove(int x, int y) {
        smallBoardStrategy.setLastHumanMove(x, y);
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

}
