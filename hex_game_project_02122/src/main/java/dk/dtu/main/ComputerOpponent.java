package dk.dtu.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.paint.Color;

public class ComputerOpponent {
    private GameBoard gameBoard;
    private int playerNumber;
    private boolean isFirstMove = true;
    private GUI gui;
    private Coordinate lastHumanMove = null;
    private List<Coordinate> computerMoves = new ArrayList<>();

    public ComputerOpponent(GameBoard gameBoard, int playerNumber, GUI gui) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
    }

    public void makeMove() {
        if (gameBoard.getWinner() != 0) {
            System.out.println("Game is won by Player " + gameBoard.getWinner());
            return;
        }

        if (isFirstMove && playerNumber == 1) {
            int x = gameBoard.boardM / 2;
            int y = gameBoard.boardN / 2;
            String key = x + "," + y;
            if (gameBoard.board.containsKey(key) && gameBoard.board.get(key).getState() == 0) {
                gameBoard.pickSpot(key, x, y, playerNumber);
                System.out.println("Computer takes center move at: " + key);
                Color computerColor = (playerNumber == 1 ? Color.RED : Color.BLUE);
                gui.updateHexagonColor(key, computerColor);
                computerMoves.add(new Coordinate(x, y, playerNumber));
                System.out.println("Board after computer's first move:");
                gameBoard.printBoard(gameBoard.board);
                isFirstMove = false;
                return;
            }
        }

        if (playerNumber == 1 && gameBoard.boardM == 3 && gameBoard.boardN == 3) {
            List<Coordinate> candidateMoves = getCandidateMoves();
            List<Coordinate> priorityMoves = filterMovesNearLastHumanMove(candidateMoves);
            if (!priorityMoves.isEmpty() && placeFirstValidMove(priorityMoves)) {
                isFirstMove = false;
                return;
            }
        }

        else {
            makeRandomMove();
            isFirstMove = false;
            return;
        }

    }

    private void makeRandomMove() {
        if (gameBoard.getWinner() != 0) {
            return;
        }

        List<String> emptySpots = new ArrayList<>();
        for (int x = 0; x < gameBoard.boardM; x++) {
            for (int y = 0; y < gameBoard.boardN; y++) {
                String key = x + "," + y;
                if (gameBoard.board.containsKey(key) && gameBoard.board.get(key).getState() == 0) {
                    emptySpots.add(key);
                }
            }
        }

        if (!emptySpots.isEmpty()) {
            String randomKey = emptySpots.get(ThreadLocalRandom.current().nextInt(emptySpots.size()));
            String[] parts = randomKey.split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            gameBoard.pickSpot(randomKey, x, y, playerNumber);
            System.out.println("Computer placed random move at: " + randomKey);
            Color compColor = (playerNumber == 1 ? Color.RED : Color.BLUE);
            gui.updateHexagonColor(randomKey, compColor);
        }
    }

    /**
     * Scans the entire board (using x as the first coordinate and y as the second)
     * for empty cells that connects to at least one of computer opponent's move.
     */
    private List<Coordinate> getCandidateMoves() {
        List<Coordinate> candidates = new ArrayList<>();
        for (int x = 0; x < gameBoard.boardM; x++) {
            for (int y = 0; y < gameBoard.boardN; y++) {
                String key = x + "," + y;
                if (gameBoard.board.containsKey(key) && gameBoard.board.get(key).getState() == 0) {
                    if (isConnectedToComputerMove(x, y)) {
                        candidates.add(new Coordinate(x, y, playerNumber));
                    }
                }
            }
        }
        return candidates;
    }

    private boolean isConnectedToComputerMove(int x, int y) {
        int[] directionsX = { 0, 0, -1, 1, -1, 1 };
        int[] directionsY = { 1, -1, 0, 0, 1, -1 };

        for (Coordinate computerMove : computerMoves) {
            for (int i = 0; i < 6; i++) {
                int neighborX = x + directionsX[i];
                int neighborY = y + directionsY[i];

                if (neighborX == computerMove.getX() && neighborY == computerMove.getY()) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<Coordinate> filterMovesNearLastHumanMove(List<Coordinate> candidates) {
        List<Coordinate> filteredMoves = new ArrayList<>();

        if (lastHumanMove == null) {
            return candidates;
        }

        int[] directionsX = { 0, 0, -1, 1, -1, 1 };
        int[] directionsY = { 1, -1, 0, 0, 1, -1 };

        for (Coordinate candidate : candidates) {
            boolean isAdjacentToLastMove = false;

            for (int i = 0; i < 6; i++) {
                int neighborX = lastHumanMove.getX() + directionsX[i];
                int neighborY = lastHumanMove.getY() + directionsY[i];

                if (candidate.getX() == neighborX && candidate.getY() == neighborY) {
                    isAdjacentToLastMove = true;
                    break;
                }
            }

            if (isAdjacentToLastMove) {
                filteredMoves.add(candidate);
            }
        }

        System.out.println("Filtered " + filteredMoves.size() + " moves that are adjacent to the last human move");

        if (filteredMoves.isEmpty()) {
            System.out.println("No moves adjacent to last human move, returning all candidates");
            return candidates;
        }

        return filteredMoves;
    }

    private boolean placeFirstValidMove(List<Coordinate> moves) {
        if (gameBoard.getWinner() != 0) {
            return false;
        }

        int[][] priorityCoords = {
                { 0, 0 }, { 1, 0 }, { 2, 0 },
                { 0, 2 }, { 1, 2 }, { 2, 2 }
        };

        for (int[] coord : priorityCoords) {
            int priorityX = coord[0];
            int priorityY = coord[1];

            for (Coordinate move : moves) {
                if (move.getX() == priorityX && move.getY() == priorityY) {
                    String key = move.getX() + "," + move.getY();
                    if (gameBoard.board.containsKey(key) && gameBoard.board.get(key).getState() == 0) {
                        gameBoard.pickSpot(key, move.getX(), move.getY(), playerNumber);
                        System.out.println("Computer placed priority move at: " + key);
                        Color compColor = (playerNumber == 1 ? Color.RED : Color.BLUE);
                        gui.updateHexagonColor(key, compColor);
                        computerMoves.add(new Coordinate(move.getX(), move.getY(), playerNumber));
                        return true;
                    }
                }
            }
        }

        for (Coordinate move : moves) {
            String key = move.getX() + "," + move.getY();
            if (gameBoard.board.containsKey(key) && gameBoard.board.get(key).getState() == 0) {
                gameBoard.pickSpot(key, move.getX(), move.getY(), playerNumber);
                System.out.println("Computer placed regular move at: " + key);
                Color compColor = (playerNumber == 1 ? Color.RED : Color.BLUE);
                gui.updateHexagonColor(key, compColor);
                computerMoves.add(new Coordinate(move.getX(), move.getY(), playerNumber));
                return true;
            }
        }

        return false;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setLastHumanMove(int x, int y) {
        lastHumanMove = new Coordinate(x, y, playerNumber == 1 ? 2 : 1);
        System.out.println("Recorded last human move at: (" + x + "," + y + ")");
    }
}
