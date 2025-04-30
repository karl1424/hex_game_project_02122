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
            showLobby();
            client.getStartGame(() -> {
                initGame();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onstartGame() {
        client.sendStartGame(() -> {
            initGame();
        });
    }

    public void initGame(){
        int gridSize = 7;
        /* if (sizeSmallCheckBox.isSelected()) {
            gridSize = 3;
        } else if (sizeMediumCheckBox.isSelected()) {
            gridSize = 7;
        } else if (sizeLargeCheckBox.isSelected()) {
            gridSize = 11;
        } */
        manager.startGame(gridSize, 0);
    }
}
