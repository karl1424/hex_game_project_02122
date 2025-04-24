package dk.dtu.Menu;

import dk.dtu.Connection.Client;
import dk.dtu.main.GamePanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OnlineGameMenu extends MenuPanel{
    private Client client;

    private VBox onlineSetupPane;
    private BorderPane lobbySetup;

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
        joinBtn.setOnAction(_ -> System.out.println("not implemented"));
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

        Label lobbyID = Help.createLabel(client.getLobbyID(), 40,false);
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

    private void showLobby() {
        getChildren().clear();
        getChildren().add(lobbySetup);
    }
}