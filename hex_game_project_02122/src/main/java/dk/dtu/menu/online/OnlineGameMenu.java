package dk.dtu.menu.online;

import dk.dtu.connection.Client;
import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;

public class OnlineGameMenu extends MenuPanel {
    private Client client;

    public OnlineGameMenu(MenuManager manager, GamePanel gamePanel, Client client) {
        super(manager, gamePanel);
        this.client = client;
        showOnlineSetup();
    }

    @Override
    protected void createUI() {
        showOnlineSetup();
    }

    public void showOnlineSetup() {
        gamePanel.isOnline = false;
        getChildren().clear();
        getChildren().add(new OnlineSetupPane(this));
    }

    public void showLobby() {
        getChildren().clear();
        getChildren().add(new LobbyPane(this, client.getLobbyID()));
    }

    public void showJoin() {
        getChildren().clear();
        getChildren().add(new JoinPane(this));
    }

    public void onHost() {
        gamePanel.isOnline = true;
        client.establishConnetion(true);
        System.out.println(client.getLobbyID());
        showLobby();
    }

    public void onJoin() {
        showJoin();
    }

    public void onBack() {
        manager.showMainMenu();
    }

    public void onJoinLobby(String lobbyIDText) {
        try {
            int lobbyID = Integer.parseInt(lobbyIDText);
            client.connectToLobby(lobbyID);
            gamePanel.isOnline = true;
            showLobby();
            client.getStartGame(() -> {
                initGame(2);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onstartGame() {
        client.sendStartGame();
        initGame(1);
    }

    public void initGame(int playerNumber){
        int gridSize = 7;
        /* if (sizeSmallCheckBox.isSelected()) {
            gridSize = 3;
        } else if (sizeMediumCheckBox.isSelected()) {
            gridSize = 7;
        } else if (sizeLargeCheckBox.isSelected()) {
            gridSize = 11;
        } */
        manager.startGame(gridSize, 0, playerNumber);
        gamePanel.beginGettingCoordinates();
    }
}
