package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.dtu.main.Coordinate;

//Click on continue when VSCode says build failed

public class SmallBoardTest {
    private static final int GAMES = 10000;
    private static final int ITERATIONS = 5000;
    private static final int BOARD_SIZE = 3;

    public static void main(String[] args) {
        int player1Wins = 0;
        int player2Wins = 0;

        long batchStart = System.nanoTime(); // Starttid for de første 100 spil

        for (int i = 0; i < GAMES; i++) {
            int winner = playGame();

            if (winner == 1) {
                player1Wins++;
            } else if (winner == 2) {
                player2Wins++;
            }

            if ((i + 1) % 100 == 0) {
                long batchEnd = System.nanoTime(); // Sluttid for batch
                long durationNs = batchEnd - batchStart;
                double durationMs = durationNs / 1_000_000.0;
                double avgTimePerGame = durationMs / 100.0;

                int gamesCompleted = i + 1;
                System.out.println("Completed " + gamesCompleted + " games:");
                System.out.println("Player 1 wins: " + player1Wins + " (" +
                        String.format("%.1f", (player1Wins * 100.0 / gamesCompleted)) + "%)");
                System.out.println("Player 2 wins: " + player2Wins + " (" +
                        String.format("%.1f", (player2Wins * 100.0 / gamesCompleted)) + "%)");
                System.out.println("Time for last 100 games: " + String.format("%.1f", durationMs) + " ms");
                System.out.println("Average time per game: " + String.format("%.2f", avgTimePerGame) + " ms\n");

                batchStart = System.nanoTime(); // Genstart tiden til næste batch
            }
        }

        System.out.println("Final Results after " + GAMES + " games:");
        System.out.println("Player 1 wins: " + player1Wins + " (" +
                String.format("%.1f", (player1Wins * 100.0 / GAMES)) + "%)");
        System.out.println("Player 2 wins: " + player2Wins + " (" +
                String.format("%.1f", (player2Wins * 100.0 / GAMES)) + "%)");
    }

    private static int playGame() {
        SimulationGame game = new SimulationGame(BOARD_SIZE, BOARD_SIZE);

        List<int[]> playerMoves = new ArrayList<>();

        int currentPlayer = 1;
        List<Coordinate> availableMoves = game.getAvailableMoves();
        int totalMoves = availableMoves.size();
        int moveCount = 0;
        SmallBoardStrategy smallBoardStrategy = new SmallBoardStrategy(game, 1);
        MCTS mctsPlayer2 = new MCTS((SimulationGame) null, 2, ITERATIONS);

        while (moveCount < totalMoves) {
            Coordinate moveMade = null;
            if (currentPlayer == 1) {
                moveMade = smallBoardStrategy.makeMoveInTest();
            } else if (currentPlayer == 2) {
                mctsPlayer2.setSimulationGame(game);
                moveMade = mctsPlayer2.makeMoveInTest();
                smallBoardStrategy.setLastHumanMove(moveMade.getX(),moveMade.getY());
            }
            game.makeMove(moveMade, currentPlayer);
            availableMoves.remove(moveMade);
            moveCount++;

            playerMoves.add(new int[] { moveMade.getX(), moveMade.getY(), currentPlayer });

            currentPlayer = (currentPlayer == 1) ? 2 : 1;
        }
        game.checkWin();

        if (game.winner == 2 & BOARD_SIZE == 3) {
            game.printBoard();
            System.out.println();
            for (int[] c : playerMoves) {
                System.out.println(Arrays.toString(c));
            }
        }

        // System.out.println();
        // System.out.println("Player " + game.winner + " wins");
        return game.winner;
    }
}
