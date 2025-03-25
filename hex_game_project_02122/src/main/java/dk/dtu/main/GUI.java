package dk.dtu.main;

import javafx.scene.layout.Pane;

public class GUI extends Pane {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = SCREEN_WIDTH;
    private static final int MAX_DIMENSION = 350;
    private static double HEX_RADIUS;

    private GameBoard gameBoard;
    private GamePanel gamePanel;

    public GUI(int GRID_WIDTH, int GRID_HEIGHT, GameBoard gameBoard, GamePanel gamePanel) {
        this.gameBoard = gameBoard;
        this.gamePanel = gamePanel;
        HEX_RADIUS = MAX_DIMENSION / (Math.sqrt(3) * GRID_WIDTH);
        setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        double boardOffset = SCREEN_WIDTH / 2;

        for (int row = -GRID_HEIGHT / 2; row <= GRID_HEIGHT / 2; row++) {
            for (int col = -GRID_WIDTH / 2; col <= GRID_WIDTH / 2; col++) {
                Hexagon hexagon = new Hexagon(col, row, boardOffset, HEX_RADIUS, (col + GRID_HEIGHT / 2),
                        (row + GRID_WIDTH / 2), gamePanel, gameBoard);
                getChildren().add(hexagon.getHexGroup());
            }
        }
    }

}
