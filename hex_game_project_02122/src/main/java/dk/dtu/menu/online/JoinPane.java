package dk.dtu.menu.online;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextFormatter;

import java.io.IOException;
import java.util.function.UnaryOperator;

import dk.dtu.menu.Help;

public class JoinPane extends BorderPane {
    private Label errorLabel;

    public JoinPane(OnlineGameMenu parent) {
        setPrefSize(600, 600);
        setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        setTop(titleBox);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter Lobby ID");
        inputField.setMaxWidth(300);
        inputField.setPrefHeight(40);
        inputField.setStyle("-fx-font-size: 18px;");

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        inputField.setTextFormatter(new TextFormatter<>(integerFilter));

        Label enterLobbyLabel = Help.createLabel("Enter Lobby ID", 30, false);

        errorLabel = Help.createLabel("", 20, false);
        errorLabel.setVisible(false);

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.getChildren().addAll(enterLobbyLabel, inputField, errorLabel);
        setCenter(centerBox);

        Button joinButton = Help.createButton("Join", 150, 40, true);
        Button backButton = Help.createButton("Back", 150, 40, true);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, joinButton);

        setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        backButton.setOnAction(_ -> parent.showOnlineSetup());
        joinButton.setOnAction(_ -> {
            try {
                parent.onJoinLobby(inputField.getText());
            } catch (InterruptedException | IOException e) {
                if (e.getMessage().equals("remote space does not exist!")) {
                    makeLabel("Lobby does not exist");
                } else {
                    makeLabel("Server is down");
                }
            } catch (IllegalStateException e) {
                makeLabel("Lobby is full");
            } catch (NumberFormatException e) {
                makeLabel("Not a valid Lobby ID");
            }
        });

    }

    private void makeLabel(String text) {
        errorLabel.setText(text);
        errorLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(_ -> errorLabel.setVisible(false));
        pause.play();
    }
}
