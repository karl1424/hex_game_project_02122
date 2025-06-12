package dk.dtu.computer_opponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.dtu.main.Coordinate;

//Click on continue when VSCode says build failed

public class MCTStest {
    private static final int GAMES = 100;
    private static final int ITERATIONS = 5000;
    private static final int BOARD_SIZE = 3;

    public static void main(String[] args) {
        int player1Wins = 0;
        int player2Wins = 0;

        for (int i = 0; i < GAMES; i++) {
            int winner = playGame();

            if (winner == 1) {
                player1Wins++;
            } else if (winner == 2) {
                player2Wins++;
            }

            if ((i + 1) % 100 == 0) {
                int gamesCompleted = i + 1;
                System.out.println("Completed " + gamesCompleted + " games:");
                System.out.println("Player 1 wins: " + player1Wins + " (" +
                        String.format("%.1f", (player1Wins * 100.0 / gamesCompleted)) + "%)");
                System.out.println("Player 2 wins: " + player2Wins + " (" +
                        String.format("%.1f", (player2Wins * 100.0 / gamesCompleted)) + "%)");
                System.out.println();
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
        MCTS mctsPlayer1 = new MCTS( null, 1, ITERATIONS);
        MCTS mctsPlayer2 = new MCTS( null, 2, ITERATIONS);

        while (moveCount < totalMoves) {
            SimulationGame sim = new SimulationGame(game);
            MCTS mcts = (currentPlayer == 1) ? mctsPlayer1 : mctsPlayer2;
            mcts.setSimulationGame(sim);
            Coordinate moveMade = mcts.makeMoveInTest();
            sim.makeMove(moveMade, currentPlayer);
            availableMoves.remove(moveMade);
            moveCount++;

            playerMoves.add(new int[] { moveMade.getX(), moveMade.getY(), currentPlayer });

            currentPlayer = (currentPlayer == 1) ? 2 : 1;
            game = sim;
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