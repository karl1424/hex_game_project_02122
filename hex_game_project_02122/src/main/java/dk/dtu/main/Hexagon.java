package dk.dtu.main;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Hexagon extends Polygon {
    int col, row, xCor, yCor;
    double boardOffset, HEX_RADIUS;
    
    GamePanel gamePanel;
    GameBoard gameBoard;

    public Hexagon(int col, int row, double boardOffset, double HEX_RADIUS, int xCor, int yCor, GamePanel gamePanel, GameBoard gameBoard){
        this.col = col;
        this.row = row;
        this. boardOffset = boardOffset;
        this.HEX_RADIUS = HEX_RADIUS;
        this.xCor = xCor;
        this.yCor = yCor;
        this.gamePanel = gamePanel;
        this.gameBoard = gameBoard;
        createHexagon();
        
    }
    
    private void createHexagon() {
        double offset = row * HEX_RADIUS * Math.sqrt(3) / 2;
        double centerX = boardOffset + offset + col * HEX_RADIUS * Math.sqrt(3);
        double centerY = boardOffset + row * 1.5 * HEX_RADIUS;

        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 6 + (Math.PI * i) / 3;
            double x = centerX + HEX_RADIUS * Math.cos(angle);
            double y = centerY + HEX_RADIUS * Math.sin(angle);
            this.getPoints().addAll(x, y);
        }

        this.setFill(Color.LIGHTGREY);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(2);

        this.setOnMouseClicked(_ -> {
            if (this.getFill().equals(Color.LIGHTGREY)) {
                if (gamePanel.getTurn()) {
                    this.setFill(Color.RED);
                } else {
                    this.setFill(Color.BLUE);
                }
                gamePanel.changeTurn();
            }
            System.out.println(xCor + ", " + yCor);
            String spot = yCor + "," + xCor;
            gameBoard.pickSpot(spot, yCor, xCor, gamePanel.getTurn() ? 1 : 2);
            gameBoard.printBoard(gameBoard.board);
        });
    }

}
