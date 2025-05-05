package dk.dtu.menu.online;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

import dk.dtu.menu.Help;

public class JoinPane extends BorderPane {
    public JoinPane(OnlineGameMenu parent) {
        setPrefSize(600, 600);
        setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME Join", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        setTop(titleBox);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter Lobby ID");

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        inputField.setTextFormatter(new TextFormatter<>(integerFilter));

        Button joinButton = Help.createButton("Join", 150, 0, false);
        Button backButton = Help.createButton("Back", 150, 40, false);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, inputField, joinButton);

        setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        backButton.setOnAction(_ -> parent.showOnlineSetup());
        joinButton.setOnAction(_ -> {
            try {
                parent.onJoinLobby(inputField.getText());
            } catch (Exception e) {
                Label label = Help.createLabel("Lobby does not exist", 40, false);
                VBox labelBox = new VBox(label);
                labelBox.setAlignment(Pos.CENTER);
                labelBox.setPadding(new Insets(0, 0, 200, 0));
                setCenter(labelBox);
            }
        });
    }
}
