package dk.dtu.main;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GUI extends Pane {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = SCREEN_WIDTH;
    private static final int MAX_DIMENSION = 350;
    private static double HEX_RADIUS;

    private GameBoard gameBoard;
    private GamePanel gamePanel;
    private ComputerOpponent computerOpponent;

    private Hexagon[][] hexagons;

    public GUI(int GRID_WIDTH, int GRID_HEIGHT, GameBoard gameBoard, GamePanel gamePanel) {
        this.gameBoard = gameBoard;
        this.gamePanel = gamePanel;
        hexagons = new Hexagon[gameBoard.boardM][gameBoard.boardN];
        HEX_RADIUS = MAX_DIMENSION / (Math.sqrt(3) * GRID_WIDTH);
        setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        double boardOffset = SCREEN_WIDTH / 2;

        for (int row = -GRID_HEIGHT / 2; row <= GRID_HEIGHT / 2; row++) {
            for (int col = -GRID_WIDTH / 2; col <= GRID_WIDTH / 2; col++) {
                int xCor = col + GRID_HEIGHT / 2;
                int yCor = row + GRID_WIDTH / 2;
                String key = xCor + "," + yCor;
                Hexagon hexagon = new Hexagon(col, row, boardOffset, HEX_RADIUS, xCor, yCor, gamePanel, gameBoard);
                getChildren().add(hexagon.getHexGroup());

                hexagons[xCor][yCor] = hexagon;
            }
        }
    }

    public void updateHexagonColor(int x, int y, Color color) {
        Hexagon hex = hexagons[x][y];
        if (hex != null) {
            hex.setFill(color);
            System.out.println("Updated hexagon color at " + x + ", " + y);
        } else {
            System.out.println("Warning: No hexagon found at key " + x + ", " + y);
        }
    }

    public void setComputerOpponent(ComputerOpponent comp) {
        this.computerOpponent = comp;
    }
}