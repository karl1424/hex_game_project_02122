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

    private Map<String,Hexagon> hexagonMap = new HashMap<>();

    public GUI(int GRID_WIDTH, int GRID_HEIGHT, GameBoard gameBoard, GamePanel gamePanel) {
        this.gameBoard = gameBoard;
        this.gamePanel = gamePanel;
        HEX_RADIUS = MAX_DIMENSION / (Math.sqrt(3) * GRID_WIDTH);
        setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        double boardOffset = SCREEN_WIDTH / 2;

        for (int row = -GRID_HEIGHT / 2; row <= GRID_HEIGHT / 2; row++) {
            for (int col = -GRID_WIDTH / 2; col <= GRID_WIDTH / 2; col++) {
                int xCor = col + GRID_HEIGHT / 2;
                int yCor = row + GRID_WIDTH / 2;
                String key = yCor + "," + xCor;
                Hexagon hexagon = new Hexagon(col, row, boardOffset, HEX_RADIUS, xCor, yCor, gamePanel, gameBoard);
                getChildren().add(hexagon.getHexGroup());

                hexagonMap.put(key, hexagon);
            }
        }
    }

    public void updateHexagonColor(String key, Color color) {
        Hexagon hex = hexagonMap.get(key);
        if (hex != null) {
            hex.setFill(color);
            System.out.println("Updated hexagon color at " + key);
        } else {
            System.out.println("Warning: No hexagon found at key " + key);
            System.out.println("Available keys: " + hexagonMap.keySet());
        }
    }

    public void setComputerOpponent(ComputerOpponent comp) {
        this.computerOpponent = comp;
    }
}