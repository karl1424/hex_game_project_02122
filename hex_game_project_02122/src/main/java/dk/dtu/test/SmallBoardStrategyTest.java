package dk.dtu.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dk.dtu.computer_opponent.SimulationGame;
import dk.dtu.game_components.Coordinate;

public class SmallBoardStrategyTest {
    private SimulationGame sim;
    private int playerNumber;
    private Coordinate lastHumanMove = null;
    private List<Coordinate> computerMoves = new ArrayList<>();
    private Random rand;

    public SmallBoardStrategyTest(SimulationGame sim, int playerNumber) {
        this.sim = sim;
        this.playerNumber = playerNumber;
        this.rand = new Random();
    }

    public Coordinate makeMoveInTest() {
        if (sim.getBoard()[1][1].getState() == 0) {
            computerMoves.add(new Coordinate(1, 1, playerNumber));
            return new Coordinate(1, 1, playerNumber);
        } else {
            List<Coordinate> candidateMoves = possibleMovesIntest();
            List<Coordinate> priorityMoves = nearHuman(candidateMoves);
            if (!priorityMoves.isEmpty()) {
                Coordinate move = firstValidMoveInTest(priorityMoves);
                if (move != null) {
                    return move;
                }
            }

            List<Coordinate> availableMoves = sim.getAvailableMoves();
            if (!availableMoves.isEmpty()) {
                int i = rand.nextInt(availableMoves.size());
                Coordinate move = availableMoves.get(i);
                computerMoves.add(new Coordinate(move.getX(), move.getY(), playerNumber));
                return move;
            }
        }
        return null;
    }

    // Scans the entire board (using x as the first coordinate and y as the second)
    // for empty cells that connects to at least one of computer opponent's move.
    private List<Coordinate> possibleMovesIntest() {
        List<Coordinate> candidates = new ArrayList<>();
        for (int x = 0; x < sim.getboardN(); x++) {
            for (int y = 0; y < sim.getboardN(); y++) {
                if (sim.getBoard()[x][y].getState() == 0) {
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

    private Coordinate firstValidMoveInTest(List<Coordinate> moves) {
        if (sim.getWinner() != 0) {
            return null;
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
                    if (sim.getBoard()[move.getX()][move.getY()].getState() == 0) {
                        computerMoves.add(new Coordinate(move.getX(), move.getY(), playerNumber));
                        return move;

                    }
                }
            }
        }

        for (Coordinate move : moves) {
            if (sim.getBoard()[move.getX()][move.getY()].getState() == 0) {
                computerMoves.add(new Coordinate(move.getX(), move.getY(), playerNumber));
                return move;
            }
        }
        return null;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setLastHumanMove(int x, int y) {
        lastHumanMove = new Coordinate(x, y, playerNumber == 1 ? 2 : 1);
    }
}
