package dk.dtu.menu.online;

import dk.dtu.menu.Help;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class LobbyPane extends BorderPane {

    public CheckBox player1CheckBox;
    public CheckBox player2CheckBox;
    public CheckBox sizeSmallCheckBox;
    public CheckBox sizeMediumCheckBox;
    public CheckBox sizeLargeCheckBox;

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

        VBox centerContent = new VBox(30);
        centerContent.setAlignment(Pos.CENTER);

        HBox playerBox = new HBox(50);
        playerBox.setAlignment(Pos.CENTER);
        Label playerLabel = Help.createLabel("Player", 18, true);

        player1CheckBox = Help.creatCheckBox("1", true);
        player2CheckBox = Help.creatCheckBox("2", false);

        player1CheckBox.setOnAction(_ -> {
            player2CheckBox.setSelected(!player1CheckBox.isSelected());
        });

        player2CheckBox.setOnAction(_ -> {
            player1CheckBox.setSelected(!player2CheckBox.isSelected());
        });

        playerBox.getChildren().addAll(playerLabel, player1CheckBox, player2CheckBox);

        HBox sizeBox = new HBox(25);
        sizeBox.setAlignment(Pos.CENTER);
        Label sizeLabel = Help.createLabel("Board Size", 18, true);

        sizeSmallCheckBox = Help.creatCheckBox("Small", false);
        sizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        sizeLargeCheckBox = Help.creatCheckBox("Large", false);

        CheckBox[] checkBoxes = { sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox };
        for (CheckBox cb : checkBoxes) {
            cb.setOnAction(_ -> {
                for (CheckBox other : checkBoxes) {
                    if (other != cb) other.setSelected(false);
                }
                if (!cb.isSelected()) cb.setSelected(true);
            });
        }

        sizeBox.getChildren().addAll(sizeLabel, sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox);
        centerContent.getChildren().addAll(playerBox, sizeBox);
        setCenter(centerContent);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button backBtn = Help.createButton("Back", 150, 40, false);
        Button startButton = Help.createButton("Start", 150, 40, false);
        buttonBox.getChildren().addAll(lobbyIDLabel, backBtn, startButton, playerLabel, player1CheckBox, player2CheckBox);
        setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);        

        backBtn.setOnAction(_ -> parent.showOnlineSetup());
        startButton.setOnAction(_ -> parent.onstartGame());
    }
}