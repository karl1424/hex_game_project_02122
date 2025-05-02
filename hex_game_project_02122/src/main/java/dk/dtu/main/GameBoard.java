package dk.dtu.main;

import javafx.application.Platform;
import java.util.List;

public class GameBoard {
    private final Coordinate[][] board;
    private final int boardM;
    private final int boardN;
    private final GamePanel gamePanel;
    private final GameBoardLogic logic;
    private int winner = 0;

    public GameBoard(int boardM, int boardN, GamePanel gamePanel) {
        this.boardM = boardM;
        this.boardN = boardN;
        this.gamePanel = gamePanel;
        this.board = new Coordinate[boardM][boardN];
        initializeBoard();
        this.logic = new GameBoardLogic(this, board, boardM, boardN);
    }

    private void initializeBoard() {
        for (int x = 0; x < boardM; x++) {
            for (int y = 0; y < boardN; y++) {
                board[x][y] = new Coordinate(x, y, 0);
            }
        }
    }

    public void printBoard() {
        for (int y = 0; y < boardN; y++) {
            for (int x = 0; x < boardM; x++) {
                System.out.print(board[x][y].getState() + " ");
            }
            System.out.println();
        }
    }

    public void pickSpot(int x, int y, int turn) {
        board[x][y] = new Coordinate(x, y, turn);
        Coordinate start = board[x][y];
        getWinningPath().clear();

        boolean win = logic.exploreNeighbors(start, turn);
        if (win) {
            setWinner(turn);
            System.out.println("Player " + turn + " wins!");
        } else {
            System.out.println("No winning path");
        }
        Platform.runLater(() -> getGamePanel().checkGameOver());
    }

    public void hexagonHasBeenPressed(Hexagon hexagon) {
        logic.handleHexagonPressed(hexagon);
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public Coordinate[][] getBoard() {
        return board;
    }

    public int getBoardM() {
        return boardM;
    }

    public int getBoardN() {
        return boardN;
    }

    public List<Coordinate> getWinningPath() {
        return logic.getWinningPath();
    }

    public void updateSpot(int x, int y, int playerNumber) {
        logic.updateSpot(x, y, playerNumber);
    }
}
