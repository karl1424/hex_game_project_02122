package dk.dtu.menu.online;

import dk.dtu.menu.Help;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class LobbyPane extends BorderPane {

    public CheckBox player1CheckBox;
    public CheckBox player2CheckBox;
    public CheckBox sizeSmallCheckBox;
    public CheckBox sizeMediumCheckBox;
    public CheckBox sizeLargeCheckBox;

    public CheckBox[] checkBoxes;

    private Label errorLabel;
    private TextArea chatArea;

    public LobbyPane(OnlineGameMenu parent, String lobbyID) {
        setPrefSize(600, 600);
        setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        setTop(titleBox);

        Label lobbyIDLabel = Help.createLabel("Lobby ID: " + lobbyID, 30, false);
        VBox lobbyIDBox = new VBox(lobbyIDLabel);
        lobbyIDBox.setAlignment(Pos.CENTER);
        lobbyIDBox.setPadding(new Insets(0, 0, 200, 0));
        setCenter(lobbyIDBox);

        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setTranslateY(40);

        HBox playerBox = new HBox(50);
        playerBox.setAlignment(Pos.CENTER);
        Label playerLabel = Help.createLabel("Player", 18, true);

        player1CheckBox = Help.creatCheckBox("1", true);
        player2CheckBox = Help.creatCheckBox("2", false);

        player1CheckBox.setOnAction(_ -> {
            player1CheckBox.setSelected(true);
            player2CheckBox.setSelected(false);
            parent.updateStartTurn("playerStart", 1);
        });

        player2CheckBox.setOnAction(_ -> {
            player1CheckBox.setSelected(false);
            player2CheckBox.setSelected(true);
            parent.updateStartTurn("playerStart", 2);
        });

        playerBox.getChildren().addAll(playerLabel, player1CheckBox, player2CheckBox);

        HBox sizeBox = new HBox(25);
        sizeBox.setAlignment(Pos.CENTER);
        Label sizeLabel = Help.createLabel("Board Size", 18, true);

        sizeSmallCheckBox = Help.creatCheckBox("Small", false);
        sizeMediumCheckBox = Help.creatCheckBox("Medium", true);
        sizeLargeCheckBox = Help.creatCheckBox("Large", false);

        checkBoxes = new CheckBox[] { sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox };
        for (CheckBox cb : checkBoxes) {
            cb.setOnAction(_ -> {
                for (CheckBox other : checkBoxes) {
                    if (other != cb)
                        other.setSelected(false);
                }
                if (!cb.isSelected())
                    cb.setSelected(true);
                int boardSize = sizeSmallCheckBox.isSelected() ? 3 : sizeLargeCheckBox.isSelected() ? 11 : 7;
                parent.updateBoardSize("board size", boardSize);
            });
        }

        errorLabel = Help.createLabel("", 20, false);
        errorLabel.setVisible(false);

        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(130);
        chatArea.setMaxWidth(260);
        chatArea.setStyle(
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );

        VBox chatBox = new VBox(0);
        chatBox.setAlignment(Pos.CENTER);
        chatBox.getChildren().add(chatArea);

        TextField chatInput = new TextField();
        chatInput.setPromptText("Write a message...");
        chatInput.setPrefWidth(200);

        Button sendButton = new Button("Send");
        sendButton.setFocusTraversable(false);
        sendButton.setOnAction(_ -> {
            String message = chatInput.getText().trim();
            parent.onSend(message);
            if (!message.isEmpty()) {
                chatArea.appendText("You: " + message + "\n");
                chatInput.clear();
            }
            chatInput.requestFocus();
        });
        chatInput.setOnAction(_ -> sendButton.fire());

        HBox chatInputBox = new HBox(10, chatInput, sendButton);
        chatInputBox.setAlignment(Pos.CENTER);

        sizeBox.getChildren().addAll(sizeLabel, sizeSmallCheckBox, sizeMediumCheckBox, sizeLargeCheckBox);
        centerContent.getChildren().addAll(lobbyIDLabel, playerBox, sizeBox, errorLabel, chatBox, chatInputBox);
        setCenter(centerContent);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button backBtn = Help.createButton("Back", 150, 40, false);
        Button startButton = Help.createButton("Start", 150, 40, false);
        buttonBox.getChildren().addAll(backBtn, startButton);
        setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        backBtn.setOnAction(_ -> parent.showOnlineSetup());
        startButton.setOnAction(_ -> parent.onstartGame());

        boolean isHost = parent.getClient().getIsHost();

        if (!isHost) {
            player1CheckBox.setDisable(true);
            player2CheckBox.setDisable(true);
            for (CheckBox cb : checkBoxes) {
                cb.setDisable(true);
            }
            startButton.setDisable(true);
        }
    }

    public void showLobbyNotFull() {
        errorLabel.setText("Lobby is not full");
        errorLabel.setVisible(true);
    }

    public void appendMessage(String message) {
        chatArea.appendText(message + "\n");
    }
}