package dk.dtu.menu.game_over;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.Help;
import dk.dtu.menu.MenuManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameOverPane extends StackPane {

    private Label resultLabel;

    public GameOverPane(MenuManager manager, GamePanel gamePanel) {
        setPrefSize(600, 600);

        Rectangle overlay = new Rectangle(600, 600);
        overlay.setFill(Color.rgb(0, 0, 0, 0.5));

        VBox popup = new VBox(20);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(25));
        popup.setMaxSize(300, 250);
        popup.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        Label title = Help.createLabel("Game Over", 36, true);
        resultLabel = Help.createLabel("", 24, true);

        Button againBtn = Help.createButton("Play Again", 140, 35, false);
        Button mainBtn = Help.createButton("Main Menu", 140, 35, false);

        againBtn.setOnAction(_ -> {
            gamePanel.getChildren().remove(manager.gameOverPanel);
            gamePanel.resetGame();
        });

        mainBtn.setOnAction(_ -> {
            gamePanel.isOnline = false;
            manager.onlinePanel.getChildren().clear();
            gamePanel.getClient().stopReceivingSpots();
            manager.getPrimaryStage().getScene().setRoot(manager);
            manager.showMainMenu();
        });

        HBox buttons = new HBox(15, againBtn, mainBtn);
        buttons.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(title, resultLabel, buttons);
        getChildren().addAll(overlay, popup);
    }

    public void setWinner(int winner) {
        resultLabel.setText("Player " + winner + " wins!");
        resultLabel.setTextFill(winner == 1 ? Color.RED : Color.BLUE);
    }
}
