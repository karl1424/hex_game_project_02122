package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.dtu.main.Coordinate;
import dk.dtu.main.GUI;
import dk.dtu.main.GameBoard;

public class SmallBoardStrategy {
    private GameBoard gameBoard;
    private int playerNumber;
    private GUI gui;
    private Coordinate lastHumanMove = null;
    private List<Coordinate> computerMoves = new ArrayList<>();
    private Random rand;

    public SmallBoardStrategy(GameBoard gameBoard, int playerNumber, GUI gui) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
        this.gui = gui;
        this.rand = new Random();
    }

    public void makeMove() {
        if (gameBoard.getBoard()[1][1].getState() == 0) {
            gameBoard.updateSpot(1, 1, playerNumber);
            System.out.println("Computer takes center move at: " + 1 + ", " + 1);
            computerMoves.add(new Coordinate(1, 1, playerNumber));
            System.out.println("Board after computer's first move:");
            gameBoard.printBoard();
            return;
        }
        List<Coordinate> candidateMoves = getCandidateMoves();
        List<Coordinate> priorityMoves = filterMovesNearLastHumanMove(candidateMoves);
        if (!priorityMoves.isEmpty() && placeFirstValidMove(priorityMoves)) {
            return;
        }
    }

    public void makeRandomMove() {
        if (gameBoard.getBoard()[1][1].getState() == 0) {
            gameBoard.updateSpot(1, 1, playerNumber);
            System.out.println("Computer takes center move at: " + 1 + ", " + 1);
            computerMoves.add(new Coordinate(1, 1, playerNumber));
            System.out.println("Board after computer's first move:");
            gameBoard.printBoard();
            return;
        }

        List<Coordinate> emptySpots = new ArrayList<>();
        for (int x = 0; x < gameBoard.getBoardM(); x++) {
            for (int y = 0; y < gameBoard.getBoardN(); y++) {
                if (gameBoard.getBoard()[x][y].getState() == 0) {
                    emptySpots.add(gameBoard.getBoard()[x][y]);
                }
            }
        }

        if (!emptySpots.isEmpty()) {
            int i = rand.nextInt(emptySpots.size());
            Coordinate spot = emptySpots.get(i);
            gameBoard.updateSpot(spot.getX(), spot.getY(), playerNumber);
            System.out.println("Computer placed random move at: " + spot.getX() + ", " + spot.getY());
        }
    }

    /**
     * Scans the entire board (using x as the first coordinate and y as the second)
     * for empty cells that connects to at least one of computer opponent's move.
     */
    private List<Coordinate> getCandidateMoves() {
        List<Coordinate> candidates = new ArrayList<>();
        for (int x = 0; x < gameBoard.getBoardN(); x++) {
            for (int y = 0; y < gameBoard.getBoardM(); y++) {
                if (gameBoard.getBoard()[x][y].getState() == 0) {
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
                { 0, 0 }, { 0, 1 }, { 0, 2 },
                { 2, 0 }, { 2, 1 }, { 2, 2 }
        };

        for (int[] coord : priorityCoords) {
            int priorityX = coord[0];
            int priorityY = coord[1];

            for (Coordinate move : moves) {
                if (move.getX() == priorityX && move.getY() == priorityY) {
                    if (gameBoard.getBoard()[move.getX()][move.getY()].getState() == 0) {
                        gameBoard.updateSpot(move.getX(), move.getY(), playerNumber);
                        computerMoves.add(new Coordinate(move.getX(), move.getY(), playerNumber));
                        return true;

                    }
                }
            }
        }

        for (Coordinate move : moves) {
            if (gameBoard.getBoard()[move.getX()][move.getY()].getState() == 0) {
                gameBoard.updateSpot(move.getX(), move.getY(), playerNumber);
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
