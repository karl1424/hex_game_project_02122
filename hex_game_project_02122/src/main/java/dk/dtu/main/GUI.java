package dk.dtu.main;

import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GUI extends Pane {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = SCREEN_WIDTH;
    private static final int MAX_DIMENSION = 350;
    private static double HEX_RADIUS;

    private Hexagon[][] hexagons;

    public GUI(int GRID_WIDTH, int GRID_HEIGHT, GameBoard gameBoard, GamePanel gamePanel) {
        if (gamePanel.isOnline) {
            Label label = new Label(gamePanel.getTurn() ? "You start" : "Opponent starts");
            label.setFont(Font.font("Arial", FontWeight.BOLD, 30));
            label.setLayoutY(20);

            label.layoutBoundsProperty().addListener((_, _, newVal) -> {
                label.setLayoutX((SCREEN_WIDTH - newVal.getWidth()) / 2);
            });

            getChildren().add(label);
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(_ -> getChildren().remove(label));
            delay.play();
        }
        hexagons = new Hexagon[gameBoard.getBoardM()][gameBoard.getBoardN()];
        HEX_RADIUS = MAX_DIMENSION / (Math.sqrt(3) * GRID_WIDTH);
        setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        double boardOffset = SCREEN_WIDTH / 2;

        for (int row = -GRID_HEIGHT / 2; row <= GRID_HEIGHT / 2; row++) {
            for (int col = -GRID_WIDTH / 2; col <= GRID_WIDTH / 2; col++) {
                int xCor = col + GRID_HEIGHT / 2;
                int yCor = row + GRID_WIDTH / 2;
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

    public void animateWinningPath(List<Coordinate> winningPath, Color playerColor) {
        if (winningPath == null || winningPath.isEmpty()) {
            return;
        }

        Color flashColor = Color.WHITE;
        Color originalColor = playerColor;

        Timeline timeline = new Timeline();
        int totalCycles = 6;
        Duration cycleDuration = Duration.millis(200);

        for (int i = 0; i < totalCycles; i++) {
            Duration time = cycleDuration.multiply(i);
            Color targetColor = (i % 2 == 0) ? flashColor : originalColor;

            for (Coordinate coord : winningPath) {
                int x = coord.getX();
                int y = coord.getY();

                KeyFrame keyFrame = new KeyFrame(time, _ -> {
                    if (hexagons[x][y] != null) {
                        hexagons[x][y].setFill(targetColor);
                    }
                });
                timeline.getKeyFrames().add(keyFrame);
            }
        }

        timeline.setCycleCount(1);
        timeline.play();
    }
}