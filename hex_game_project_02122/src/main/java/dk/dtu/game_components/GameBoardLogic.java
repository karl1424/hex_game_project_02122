package dk.dtu.game_components;

import dk.dtu.computer_opponent.ComputerManager;
import dk.dtu.main.GamePanel;
import dk.dtu.user_interface.Hexagon;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import java.util.*;

public class GameBoardLogic {
    private final GameBoard gameBoard;
    private final Coordinate[][] board;
    private final int boardM, boardN;
    private final List<Coordinate> winningPath = new ArrayList<>();
    public boolean BFSDebug = false;

    public GameBoardLogic(GameBoard gameBoard, Coordinate[][] board, int boardM, int boardN) {
        this.gameBoard = gameBoard;
        this.board = board;
        this.boardM = boardM;
        this.boardN = boardN;
    }

    public void updateSpot(int x, int y, int playerNumber) {
        gameBoard.pickSpot(x, y, playerNumber);
        Color compColor = (playerNumber == 1 ? Color.RED : Color.BLUE);
        gameBoard.getGamePanel().gui.updateHexagonColor(x, y, compColor);
    }

    public boolean exploreNeighbors(Coordinate start, int turn) {

        // Hexagon neighbors
        int[] directionsX = { 0, 0, -1, 1, -1, 1 };
        int[] directionsY = { 1, -1, 0, 0, 1, -1 };

        Queue<Coordinate> queue = new LinkedList<>();
        boolean[][] visited = new boolean[boardM][boardN];
        Map<Coordinate, Coordinate> parentMap = new HashMap<>();

        queue.add(start);
        visited[start.getX()][start.getY()] = true;

        boolean reachedStartEdge = false;
        boolean reachedEndEdge = false;
        Coordinate startEdgeCoordinate = null;
        Coordinate endEdgeCoordinate = null;

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();
            if (turn == 2) {
                if (current.getY() == 0) {
                    reachedStartEdge = true;
                    startEdgeCoordinate = current;
                }
                if (current.getY() == boardN - 1) {
                    reachedEndEdge = true;
                    endEdgeCoordinate = current;
                }
            } else {
                if (current.getX() == 0) {
                    reachedStartEdge = true;
                    startEdgeCoordinate = current;
                }
                if (current.getX() == boardM - 1) {
                    reachedEndEdge = true;
                    endEdgeCoordinate = current;
                }
            }

            if (BFSDebug)
                System.out.println("Processing: " + current);

            for (int i = 0; i < 6; i++) {
                int neighborX = current.getX() + directionsX[i];
                int neighborY = current.getY() + directionsY[i];

                if (neighborX >= 0 && neighborX < boardM && neighborY >= 0 && neighborY < boardN) {
                    if (!visited[neighborX][neighborY] && board[neighborX][neighborY].getState() == turn) {
                        Coordinate neighbor = board[neighborX][neighborY];
                        queue.add(neighbor);
                        visited[neighborX][neighborY] = true;
                        parentMap.put(neighbor, current);
                        if (BFSDebug)
                            System.out.println("Adding neighbor: " + neighbor);
                    }
                }
            }

            if (reachedStartEdge && reachedEndEdge) {
                if (BFSDebug)
                    System.out.println("Winning Path Found!");
                reconstructPath(parentMap, startEdgeCoordinate, endEdgeCoordinate);
                return true;
            }
        }
        return false;
    }

    private void reconstructPath(Map<Coordinate, Coordinate> parentMap, Coordinate startNode, Coordinate endNode) {
        List<Coordinate> path = new ArrayList<>();

        Coordinate current = startNode;
        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }

        List<Coordinate> endPath = new ArrayList<>();
        current = endNode;
        while (current != null && !path.contains(current)) {
            endPath.add(current);
            current = parentMap.get(current);
        }

        winningPath.clear();
        winningPath.addAll(path);
        winningPath.addAll(endPath);
    }

    public List<Coordinate> getWinningPath() {
        return winningPath;
    }

    public void handleHexagonPressed(Hexagon hexagon) throws InterruptedException {
        GamePanel gamePanel = gameBoard.getGamePanel();
        if (gameBoard.getWinner() != 0 || board[hexagon.getXCor()][hexagon.getYCor()].getState() != 0)
            return;
        if (gamePanel.getTurn() || (gamePanel.getComputerPlayer() == 0 && !gamePanel.getIsOnline())) {
            if (gamePanel.getIsOnline()) {
                gameBoard.pickSpot(hexagon.getXCor(), hexagon.getYCor(), gamePanel.getPlayerNumber());
                gamePanel.sendCoordinates(hexagon.getXCor(), hexagon.getYCor(), gamePanel.getPlayerNumber());
            } else {
                gameBoard.pickSpot(hexagon.getXCor(), hexagon.getYCor(), gamePanel.getCurrentPlayerTurn());
            }
            hexagon.setFill(gamePanel.getCurrentPlayerTurn() == 1 ? Color.RED : Color.BLUE);
            ComputerManager comp = gamePanel.getComputerOpponent();
            if (comp != null) {
                comp.setLastHumanMove(hexagon.getXCor(), hexagon.getYCor());
            }

            // gameBoard.printBoard();
            gamePanel.changeTurn();

            if (gameBoard.getWinner() == 0 && comp != null) {
                if (!gamePanel.getTurn() && gameBoard.getWinner() == 0) {
                    new Thread(() -> {
                        comp.makeMove();
                        Platform.runLater(() -> {
                            // gameBoard.printBoard();
                            gamePanel.changeTurn();
                        });
                    }).start();
                }
            }
        }
    }

    public boolean checkWinningMove(Coordinate move, int playerNumber) {
        int originalState = board[move.getX()][move.getY()].getState();
        board[move.getX()][move.getY()].setState(playerNumber);
        boolean isWinning = exploreNeighbors(move, playerNumber);
        board[move.getX()][move.getY()].setState(originalState);
        winningPath.clear();
        return isWinning;
    }

    public List<Coordinate> getAvailableMoves() {
        List<Coordinate> availableMoves = new ArrayList<>();
        for (int x = 0; x < boardM; x++) {
            for (int y = 0; y < boardN; y++) {
                if (board[x][y].getState() == 0) {
                    availableMoves.add(new Coordinate(x, y, 0));
                }
            }
        }
        return availableMoves;
    }
}
