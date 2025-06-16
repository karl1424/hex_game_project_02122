package dk.dtu.menu.game_over;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.Help;
import dk.dtu.menu.MenuManager;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class GameOverPane extends StackPane {

    private Label resultLabel;
    private Button againBtn;
    private Button mainBtn;
    private MenuManager manager;
    private GamePanel gamePanel;

    public GameOverPane(MenuManager manager, GamePanel gamePanel) {
        this.manager = manager;
        this.gamePanel = gamePanel;

        setPrefSize(600, 600);

        Rectangle overlay = new Rectangle(600, 600);
        overlay.setFill(Color.rgb(0, 0, 0, 0.5));

        VBox popup = new VBox(20);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(25));
        popup.setMaxSize(300, 250);
        popup.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 5);");

        Label title = Help.createLabel("Game Over", 36, true);
        resultLabel = Help.createLabel("", 24, true);

        againBtn = Help.createButton("Play Again", 140, 35, false);
        mainBtn = Help.createButton("Main Menu", 140, 35, false);

        HBox buttons = new HBox(15, againBtn, mainBtn);
        buttons.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(title, resultLabel, buttons);
        getChildren().addAll(overlay, popup);
    }

    public void setWinner(int winner) {
        resultLabel.setText("Player " + winner + " wins!");
        resultLabel.setTextFill(winner == 1 ? Color.RED : Color.BLUE);
    }

    public void setLocal() {
        againBtn.setText("Play Again");
        againBtn.setOnAction(_ -> {
            gamePanel.getChildren().remove(manager.gameOverPanel);
            gamePanel.resetGame();
        });
        mainBtn.setOnAction(_ -> {
            // showOnlineSetup();
            gamePanel.isOnline = false;
            manager.onlinePanel.getChildren().clear();
            manager.getPrimaryStage().getScene().setRoot(manager);
            manager.showMainMenu();

        });
    }

    public void setOnline() {
        againBtn.setText("Go to Lobby");
        if (!manager.getOnlineGameMenu().getClient().getClientState().isHost()) {
            againBtn.setDisable(true);
        } else {
            againBtn.setDisable(false);
        }
        againBtn.setOnAction(_ -> {
            gamePanel.getChildren().remove(manager.gameOverPanel);
            manager.getPrimaryStage().getScene().setRoot(manager);
            manager.getOnlineGameMenu().getClient().sendToLobby();
            manager.getOnlineGameMenu().getClient().getConnectionManager().shutDownLobby(true);
            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(_ -> {
                manager.getOnlineGameMenu().onHost(
                        manager.getOnlineGameMenu().getClient().getConnectionManager().getLobbyID());
            });
            pause.play();
        });

        mainBtn.setOnAction(_ -> {
            if (!manager.getOnlineGameMenu().getClient().getClientState().isHost()) {
                manager.getOnlineGameMenu().getClient().getPlayer2Connection().preventP2ReentryToLobby();
            }
            manager.onlinePanel.getChildren().clear();
            gamePanel.getClient().getGameCommunicationHandler().stopReceivingSpots();
            manager.getPrimaryStage().getScene().setRoot(manager);
            manager.getOnlineGameMenu().showOnlineSetup();
            gamePanel.isOnline = false;
        });
    }
}
