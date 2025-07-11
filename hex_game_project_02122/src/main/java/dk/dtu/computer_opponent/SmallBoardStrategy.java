package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.List;

import dk.dtu.game_components.Coordinate;
import dk.dtu.game_components.GameBoard;

public class SmallBoardStrategy {
    private GameBoard gameBoard;
    private int playerNumber;
    private Coordinate lastHumanMove = null;
    private List<Coordinate> computerMoves = new ArrayList<>();

    public SmallBoardStrategy(GameBoard gameBoard, int playerNumber) {
        this.gameBoard = gameBoard;
        this.playerNumber = playerNumber;
    }

    public void makeMove() {
        if (gameBoard.getBoard()[1][1].getState() == 0) {
            gameBoard.updateSpot(1, 1, playerNumber);
            computerMoves.add(new Coordinate(1, 1, playerNumber));
            // gameBoard.printBoard();
            return;
        }
        List<Coordinate> candidateMoves = possibleMoves();
        List<Coordinate> priorityMoves = nearHuman(candidateMoves);
        if (!priorityMoves.isEmpty() && firstValidMove(priorityMoves)) {
            return;
        }
    }

    // Scans the entire board (using x as the first coordinate and y as the second)
    // for empty cells that connects to at least one of computer opponent's move.
    private List<Coordinate> possibleMoves() {
        List<Coordinate> candidates = new ArrayList<>();
        for (int x = 0; x < gameBoard.getBoardN(); x++) {
            for (int y = 0; y < gameBoard.getBoardM(); y++) {
                if (gameBoard.getBoard()[x][y].getState() == 0) {
                    if (isLinkedToAI(x, y)) {
                        candidates.add(new Coordinate(x, y, playerNumber));
                    }
                }
            }
        }
        return candidates;
    }

    private boolean isLinkedToAI(int x, int y) {
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

    private List<Coordinate> nearHuman(List<Coordinate> candidates) {
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
        return filteredMoves;
    }

    private boolean firstValidMove(List<Coordinate> moves) {
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
    }
}
