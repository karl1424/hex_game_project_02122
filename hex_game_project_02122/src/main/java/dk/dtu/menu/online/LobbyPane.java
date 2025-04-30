package dk.dtu.menu.online;

import dk.dtu.menu.Help;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class LobbyPane extends BorderPane {
    public LobbyPane(OnlineGameMenu parent, String lobbyID) {
        setPrefSize(600, 600);
        setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        setTop(titleBox);

        Label lobbyIDLabel = Help.createLabel(lobbyID, 40, false);
        VBox lobbyIDBox = new VBox(lobbyIDLabel);
        lobbyIDBox.setAlignment(Pos.CENTER);
        lobbyIDBox.setPadding(new Insets(0, 0, 200, 0));
        setCenter(lobbyIDBox);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button backBtn = Help.createButton("Back", 150, 40, false);
        Button startButton = Help.createButton("Start", 150, 0, false);
        buttonBox.getChildren().addAll(backBtn, startButton);
        setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        

        backBtn.setOnAction(_ -> parent.showOnlineSetup());
        startButton.setOnAction(_ -> parent.onstartGame());
    }
}
