package dk.dtu.menu;

import dk.dtu.main.GamePanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameOver extends MenuPanel {
    private Label resultLabel;
    private StackPane gameOverPane;
    
    public GameOver(MenuManager manager, GamePanel gamePanel) {
        super(manager, gamePanel);
    }
    
    @Override
    protected void createUI() {
        gameOverPane = new StackPane();
        gameOverPane.setPrefSize(600, 600);

        Rectangle overlay = new Rectangle(600, 600);
        overlay.setFill(Color.rgb(0, 0, 0, 0.5));

        VBox popupContent = new VBox(20);
        popupContent.setAlignment(Pos.CENTER);
        popupContent.setMaxWidth(300);
        popupContent.setMaxHeight(250);
        popupContent.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        popupContent.setPadding(new Insets(25));

        Label titleLabel = Help.createLabel("Game Over", 36, true);
        resultLabel = Help.createLabel("", 24, true);

        Button playAgainBtn = Help.createButton("Play Again", 140, 35, false);
        Button mainMenuBtn = Help.createButton("Main Menu", 140, 35, false);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(playAgainBtn, mainMenuBtn);

        popupContent.getChildren().addAll(titleLabel, resultLabel, buttonBox);
        gameOverPane.getChildren().addAll(overlay, popupContent);
        
        playAgainBtn.setOnAction(e -> {
            gamePanel.getChildren().remove(this);
            gamePanel.resetGame();
        });

        mainMenuBtn.setOnAction(e -> {
            Stage primaryStage = manager.getPrimaryStage();
            primaryStage.getScene().setRoot(manager);
            manager.showMainMenu();
        });

        getChildren().add(gameOverPane);
        
        StackPane.setAlignment(popupContent, Pos.CENTER);
    }
    
    public void setWinner(int winner) {
        if (winner == 1) {
            resultLabel.setText("Player 1 wins!");
            resultLabel.setTextFill(Color.RED);
        } else {
            resultLabel.setText("Player 2 wins!");
            resultLabel.setTextFill(Color.BLUE);
        }
    }
}