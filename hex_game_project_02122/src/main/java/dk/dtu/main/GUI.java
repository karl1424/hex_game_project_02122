package dk.dtu.main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class GUI extends Application {
    private static final int GRID_WIDTH = 13;
    private static final int GRID_HEIGHT = GRID_WIDTH;
    private static final int MAX_DIMENSION = 350;
    private static final double HEX_RADIUS = MAX_DIMENSION / (Math.sqrt(3) * GRID_WIDTH);
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = SCREEN_WIDTH;

    private boolean isPlayerOneTurn = true;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        double boardOffset = SCREEN_WIDTH / 2;

        for (int row = -GRID_HEIGHT / 2; row <= GRID_HEIGHT / 2; row++) {
            for (int col = -GRID_WIDTH / 2; col <= GRID_WIDTH / 2; col++) {
                Polygon hexagon = createHexagon(col, row, boardOffset);
                root.getChildren().add(hexagon);
            }
        }

        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        primaryStage.setTitle("Hexagonalt Grid");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private Polygon createHexagon(int col, int row, double boardOffset) {
        Polygon hexagon = new Polygon();
        double offset = row * HEX_RADIUS * Math.sqrt(3) / 2;
        double centerX = boardOffset + offset + col * HEX_RADIUS * Math.sqrt(3);
        double centerY = boardOffset + row * 1.5 * HEX_RADIUS;

        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 6 + (Math.PI * i) / 3;
            double x = centerX + HEX_RADIUS * Math.cos(angle);
            double y = centerY + HEX_RADIUS * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }

        hexagon.setFill(Color.LIGHTGREY);
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(2);

        hexagon.setOnMouseClicked(_ -> {
            if (hexagon.getFill().equals(Color.LIGHTGREY)) {
                if (isPlayerOneTurn) {
                    hexagon.setFill(Color.RED);
                } else {
                    hexagon.setFill(Color.BLUE);
                }
                isPlayerOneTurn = !isPlayerOneTurn;
            }
        });

        return hexagon;
    }

}
