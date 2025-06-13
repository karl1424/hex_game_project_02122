package dk.dtu.computer_opponent;

import java.util.List;

import dk.dtu.main.Coordinate;

public class MCTStest {
    private static final int GAMES = 500;
    private static final int ITERATIONS = 10000;
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

        int currentPlayer = 1;
        List<Coordinate> availableMoves = game.getAvailableMoves();
        int totalMoves = availableMoves.size();
        int moveCount = 0;

        while (moveCount < totalMoves) {
            SimulationGame sim = new SimulationGame(game);
            MCTS mcts = new MCTS(sim, currentPlayer, ITERATIONS);
            Coordinate moveMade = mcts.makeMoveInTest();
            sim.makeMove(moveMade, currentPlayer);
            availableMoves.remove(moveMade);

            // System.out.println("Computer (Player " + currentPlayer + ") chooses move at:
            // " + moveMade.getX() + ", "
            // + moveMade.getY());
            moveCount++;
            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            game = sim;
        }
        game.checkWin();

        // game.printBoard();
        // System.out.println();
        // System.out.println("Player " + game.winner + " wins");
        return game.winner;
    }
}