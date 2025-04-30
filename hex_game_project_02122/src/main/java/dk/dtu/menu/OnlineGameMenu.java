package dk.dtu.menu;

import java.io.IOException;
import java.util.function.UnaryOperator;

import dk.dtu.connection.Client;
import dk.dtu.main.GamePanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OnlineGameMenu extends MenuPanel {
    private Client client;

    private VBox onlineSetupPane;
    private BorderPane lobbySetup;
    private BorderPane joinSetup;

    public OnlineGameMenu(MenuManager manager, GamePanel gamePanel, Client client) {
        super(manager, gamePanel);
        this.client = client;
    }

    @Override
    protected void createUI() {
        onlineSetupPane = new VBox(20);
        onlineSetupPane.setAlignment(Pos.CENTER);
        onlineSetupPane.setPadding(new Insets(50));
        onlineSetupPane.setPrefSize(600, 600);

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);

        Button hostBtn = Help.createButton("Host", 200, 40, true);
        Button joinBtn = Help.createButton("Join", 200, 40, true);
        Button backBtn = Help.createButton("Back", 200, 40, true);

        onlineSetupPane.getChildren().addAll(titleLabel, hostBtn, joinBtn, backBtn);

        // Center the VBox in the Pane
        onlineSetupPane.layoutXProperty().bind(widthProperty().subtract(onlineSetupPane.widthProperty()).divide(2));
        onlineSetupPane.layoutYProperty().bind(heightProperty().subtract(onlineSetupPane.heightProperty()).divide(2));

        hostBtn.setOnAction(_ -> {
            client.establishConnetion(true);
            System.out.println(client.getLobbyID());
            setupLobby();
            showLobby();
        });
        joinBtn.setOnAction(_ -> {
            setupJoin();
            showJoin();
        });
        backBtn.setOnAction(_ -> manager.showMainMenu());

        getChildren().add(onlineSetupPane);
    }

    private void setupLobby() {
        lobbySetup = new BorderPane();
        lobbySetup.setPrefSize(600, 600);
        lobbySetup.setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        lobbySetup.setTop(titleBox);

        Label lobbyID = Help.createLabel(client.getLobbyID(), 40, false);
        VBox lobbyIDBox = new VBox(lobbyID);
        lobbyIDBox.setAlignment(Pos.CENTER);
        lobbyIDBox.setPadding(new Insets(0, 0, 200, 0));
        lobbySetup.setCenter(lobbyIDBox);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);

        Button backBtn = Help.createButton("Back", 150, 40, false);

        buttonBox.getChildren().add(backBtn);
        lobbySetup.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        backBtn.setOnAction(_ -> {
            lobbySetup.getChildren().clear();
            createUI();
            manager.showOnlineSetup();
        });
        getChildren().add(lobbySetup);

    }

    private void setupJoin() {
        joinSetup = new BorderPane();
        joinSetup.setPrefSize(600, 600);
        joinSetup.setPadding(new Insets(40));

        Label titleLabel = Help.createTitleLabel("HEX GAME Join", 60);
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, -120, 0));
        joinSetup.setTop(titleBox);

        HBox inputFieldBox = new HBox(20);
        inputFieldBox.setAlignment(Pos.CENTER);
        TextField inputField = new TextField();
        inputField.setPromptText("Skriv noget her...");

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> integerFormatter = new TextFormatter<>(integerFilter);
        inputField.setTextFormatter(integerFormatter);

        HBox buttonBoxJoin = new HBox(30);
        buttonBoxJoin.setAlignment(Pos.CENTER);
        Button joinButton = Help.createButton("Join", 150, 0, false);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        Button backBtn = Help.createButton("Back", 150, 40, false);

        buttonBox.getChildren().addAll(backBtn, inputField, joinButton);
        joinSetup.setBottom(buttonBox);
        BorderPane.setAlignment(buttonBox, Pos.CENTER);

        backBtn.setOnAction(_ -> {
            joinSetup.getChildren().clear();
            createUI();
            manager.showOnlineSetup();
        });
        joinButton.setOnAction(_ -> {
            try {
                client.setLobbyID(Integer.parseInt(inputField.getText()));
                client.establishConnetion(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getChildren().add(joinSetup);

    }

    private void showLobby() {
        getChildren().clear();
        getChildren().add(lobbySetup);
    }

    private void showJoin() {
        getChildren().clear();
        getChildren().add(joinSetup);
    }
}