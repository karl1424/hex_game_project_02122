package dk.dtu.main;

import dk.dtu.computer_opponent.ComputerManager;
import dk.dtu.computer_opponent.SmallBoardStrategy;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;

public class Hexagon extends Polygon {
    int col, row, xCor, yCor;
    double boardOffset, HEX_RADIUS;
    String key;

    GamePanel gamePanel;
    GameBoard gameBoard;

    private Group hexGroup;

    public Hexagon(int col, int row, double boardOffset, double HEX_RADIUS, int xCor, int yCor, GamePanel gamePanel,
            GameBoard gameBoard) {
        this.col = col;
        this.row = row;
        this.boardOffset = boardOffset;
        this.HEX_RADIUS = HEX_RADIUS;
        this.xCor = xCor;
        this.yCor = yCor;
        key = xCor + "," + yCor;
        this.gamePanel = gamePanel;
        this.gameBoard = gameBoard;
        createHexagon();
    }

    private void createHexagon() {
        double offset = row * HEX_RADIUS * Math.sqrt(3) / 2;
        double centerX = boardOffset + offset + col * HEX_RADIUS * Math.sqrt(3);
        double centerY = boardOffset + row * 1.5 * HEX_RADIUS;

        double[][] points = new double[6][2];

        for (int i = 0; i < 6; i++) {
            double angle = Math.PI / 6 + (Math.PI * i) / 3;
            double x = centerX + HEX_RADIUS * Math.cos(angle);
            double y = centerY + HEX_RADIUS * Math.sin(angle);
            points[i][0] = x;
            points[i][1] = y;
            this.getPoints().addAll(x, y);
        }

        this.setFill(Color.LIGHTGREY);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(HEX_RADIUS / 20.0);

        // Create a Group to store hexagon + edge lines
        hexGroup = new Group(this);

        boolean isTopEdge = (yCor == gameBoard.boardN - 1);
        boolean isBottomEdge = (yCor == 0);
        boolean isLeftEdge = (xCor == 0);
        boolean isRightEdge = (xCor == gameBoard.boardM - 1);

        // Add color to specific edges only
        if (isTopEdge) {
            drawEdge(points[0], points[1], Color.BLUE);
            drawEdge(points[1], points[2], Color.BLUE);
        }
        if (isBottomEdge) {
            drawEdge(points[3], points[4], Color.BLUE);
            drawEdge(points[4], points[5], Color.BLUE);
        }
        if (isLeftEdge) {
            drawEdge(points[1], points[2], Color.RED);
            drawEdge(points[2], points[3], Color.RED);
        }
        if (isRightEdge) {
            drawEdge(points[4], points[5], Color.RED);
            drawEdge(points[5], points[0], Color.RED);
        }

        this.setOnMouseClicked(_ -> {
            if (gameBoard.getWinner() != 0 ||
                    gameBoard.getBoard()[xCor][yCor].getState() != 0) {
                return;
            }

            boolean isLocalGame = gamePanel.getComputerPlayer() == 0;
            boolean isHumanTurn = (gamePanel.getTurn() && gamePanel.getComputerPlayer() != 1) ||
                    (!gamePanel.getTurn() && gamePanel.getComputerPlayer() != 2);

            if (isLocalGame || isHumanTurn) {

                if(gamePanel.getIsOnline()){
                    if(gamePanel.getTurn()){

                        gameBoard.pickSpot(xCor, yCor, gamePanel.getPlayerNumber());
                        gamePanel.sendCoordinates(xCor, yCor, gamePanel.getPlayerNumber());
                        System.out.println("Player number: " + gamePanel.getPlayerNumber());
                        
                    }
                    else {
                        return;
                    }
                } else {

                     gameBoard.pickSpot(xCor, yCor, gamePanel.getTurn() ? 1 : 2);

                }
                this.setFill(gamePanel.getTurn()  ? Color.RED : Color.BLUE);
                System.out.println("Human move at: " + xCor + ", " + yCor);
               

                ComputerManager comp = gamePanel.getComputerOpponent();
                if (comp != null) {
                    comp.setLastHumanMove(xCor, yCor);
                }

                System.out.println("Board after human move:");
                gameBoard.printBoard();

                gamePanel.changeTurn();

                if (gameBoard.getWinner() == 0 && comp != null) {
                    Timeline delay = new Timeline(new KeyFrame(Duration.seconds(0.5), _2 -> {
                        boolean isComputerTurn = (comp.getPlayerNumber() == 1 && gamePanel.getTurn()) ||
                                (comp.getPlayerNumber() == 2 && !gamePanel.getTurn());

                        if (isComputerTurn && gameBoard.getWinner() == 0) {
                            comp.makeMove();
                            System.out.println("Board after computer move:");
                            gameBoard.printBoard();
                            gamePanel.changeTurn();
                        }
                    }));
                    delay.setCycleCount(1);
                    delay.play();
                } else if (gameBoard.getWinner() != 0) {
                    System.out.println("Game over! Player " + gameBoard.getWinner() + " wins!");
                }
            }
        });

    }

    private void drawEdge(double[] p1, double[] p2, Color color) {
        Line line = new Line(p1[0], p1[1], p2[0], p2[1]);
        line.setStroke(color);
        line.setStrokeWidth(HEX_RADIUS / 15.0);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        hexGroup.getChildren().add(line);
    }

    public Group getHexGroup() {
        return hexGroup;
    }

    public void setFill(Color color) {
        super.setFill(color);
    }
}