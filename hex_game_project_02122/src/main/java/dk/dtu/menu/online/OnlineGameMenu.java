package dk.dtu.menu.online;

import java.io.IOException;
import java.net.UnknownHostException;

import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;
import dk.dtu.network.Client;
import dk.dtu.network.tags.SpaceTag;
import javafx.application.Platform;
import javafx.scene.control.CheckBox;

public class OnlineGameMenu extends MenuPanel {
    private Client client;
    private int playerNumber;
    private int boardSize;
    private int opponentNumber;
    private LobbyPane pane;
    private OnlineSetupPane onlinePane;

    public OnlineGameMenu(MenuManager manager, GamePanel gamePanel, Client client) {
        super(manager, gamePanel);
        this.client = client;
    }

    @Override
    protected void createUI() {
        showOnlineSetup();
    }

    public void showOnlineSetup() {
        System.out.println("HEJ");
        if (gamePanel.isOnline == true) {
            if (!client.getClientState().isHost()) {
                System.out.println("player 2 left");
                client.getPlayer2Connection().sendLeftPlayer2();
                client.sendToMainMenu();
            } else {
                client.getConnectionManager().shutDownLobby(false);
            }
            client.getClientState().setHost(false);
        }
        goToOnlineSetup();
    }

    public void goToOnlineSetup() {
        if (client != null && client.getLobbyMessageHandler() != null) {
            client.getLobbyMessageHandler().stopReceivingMessages();
        }
        gamePanel.isOnline = false;
        getChildren().clear();
        onlinePane = new OnlineSetupPane(this);
        getChildren().add(onlinePane);
    }

    public void showLobby() {
        getChildren().clear();
        try {
            pane = new LobbyPane(this, client.getLobbyID());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        getChildren().add(pane);
    }

    public void showJoin() {
        getChildren().clear();
        getChildren().add(new JoinPane(this));
    }

    public void onHost(int lobbyID) {
        gamePanel.isOnline = true;
        try {
            client.establishConnetionAsHost(true, lobbyID);
            client.getLobbyID();
            showLobby();
            client.getLobbyMessageHandler().receiveMessage();
        } catch (Exception e) {
            goToOnlineSetup();
            onlinePane.serverIsDown();
        }

    }

    public void onJoin() {
        showJoin();
    }

    public void onBack() {
        manager.showMainMenu();
    }

    public void onJoinLobby(String lobbyIDText) throws InterruptedException, IOException {
        int lobbyID = Integer.parseInt(lobbyIDText);
        client.getConnectionManager().connectToLobby(lobbyID);
        client.getConnectionManager().setLobbyID(lobbyID);
        gamePanel.isOnline = true;
        gamePanel.getChildren().remove(manager.gameOverPanel);
        showLobby();
        manager.getPrimaryStage().getScene().setRoot(manager);
    

        client.getGameCommunicationHandler().getStartGame((sizeBoard, numberPlayer) -> {
            initGame(sizeBoard, numberPlayer);
        });
        client.getGameCommunicationHandler().getGameSettings((tag, value) -> {
            System.out.println("tag, value: " + tag + value);
            if (tag.equals("board size")) {
                for (CheckBox cb : pane.checkBoxes) {
                    cb.setSelected(false); // Clear all selections
                }
                if (value == 3) {
                    pane.sizeSmallCheckBox.setSelected(true);
                } else if (value == 11) {
                    pane.sizeLargeCheckBox.setSelected(true);
                } else {
                    pane.sizeMediumCheckBox.setSelected(true);
                }
            }
            if (tag.equals("playerStart")) {
                if (value == 1) {
                    pane.player1CheckBox.setSelected(true);
                    pane.player2CheckBox.setSelected(false);
                } else if (value == 2) {
                    pane.player1CheckBox.setSelected(false);
                    pane.player2CheckBox.setSelected(true);
                }
            }
        });
        client.getLobbyMessageHandler().receiveOldMessages();
        Platform.runLater(() -> {
            try {
                client.getLobbyMessageHandler().sendMessage("PLAYER JOINED", true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pane.appendMessage("PLAYER JOINED");
        });
        new Thread(() -> client.checkForLobbyClosed()).start();
        client.getLobbyMessageHandler().receiveMessage();
        
    }

    public void onstartGame() throws InterruptedException {
        if (client.getClientState().canStart()) {
            boardSize = getBoardSize();
            playerNumber = getPlayerStart();
            opponentNumber = pane.player1CheckBox.isSelected() ? 2 : 1;
            client.getGameCommunicationHandler().sendStartGame(boardSize, opponentNumber);
            initGame(boardSize, playerNumber);
        } else {
            pane.showLobbyNotFull();
        }

    }

    public void onSend(String message) throws InterruptedException {
        client.getLobbyMessageHandler().sendMessage(message, false);
    }

    public void initGame(int boardSize, int playerNumber) {
        manager.startGame(boardSize, 0, playerNumber, 0);
        gamePanel.beginGettingCoordinates();
    }

    public void updateBoardSize(String boardSizeString, int boardSize) throws InterruptedException {
        client.getGameCommunicationHandler().updateBoardSize(boardSizeString, boardSize);
    }

    public void updateStartTurn(String playerStartString, int playerStart) throws InterruptedException {
        client.getGameCommunicationHandler().updateStartTurn(playerStartString, playerStart);
    }

    public Client getClient() {
        return client;
    }

    public LobbyPane getLobbyPane() {
        return pane;
    }

    public int getBoardSize(){
        return pane.sizeSmallCheckBox.isSelected() ? 3 : pane.sizeLargeCheckBox.isSelected() ? 11 : 7;
    }

    public int getPlayerStart(){
        return pane.player1CheckBox.isSelected() ? 1 : 2;
    }
}
