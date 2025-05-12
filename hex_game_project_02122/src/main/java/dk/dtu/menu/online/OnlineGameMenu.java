package dk.dtu.menu.online;

import java.io.IOException;
import java.net.UnknownHostException;

import dk.dtu.connection.Client;
import dk.dtu.main.GamePanel;
import dk.dtu.menu.MenuManager;
import dk.dtu.menu.MenuPanel;
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
        System.out.println("Hey!!!!");
        if (gamePanel.isOnline == true) {
            if (!client.getIsHost()) {
                System.out.println("player 2 left");
                client.sendLeftPlayer2();
            } else {
                client.shutDownLobby();
            }
        }
        goToOnlineSetup();
    }

    public void goToOnlineSetup() {
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

    public void onHost() {
        gamePanel.isOnline = true;
        client.establishConnetion(true);
        try {
            client.getLobbyID();
            showLobby();
            new Thread(() -> client.recieveMessage()).start();
            // new Thread(() -> client.recieveServerMessages()).start();
        } catch (UnknownHostException e) {
            showOnlineSetup();
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
        client.connectToLobby(lobbyID);
        client.setLobbyID(lobbyID);
        gamePanel.isOnline = true;
        showLobby();

        client.getStartGame((sizeBoard, numberPlayer) -> {
            initGame(sizeBoard, numberPlayer);
        });
        client.getGameInfo((tag, value) -> {
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
        client.recieveOldMessages();
        Platform.runLater(() -> {
            client.sendMessage("PLAYER JOINED", true);
            pane.appendMessage("PLAYER JOINED");
        });
        new Thread(() -> client.recieveMessage()).start();
        new Thread(() -> client.checkForLobbyClosed()).start();
    }

    public void onstartGame() {
        if (client.getCanStart()) {
            boardSize = pane.sizeSmallCheckBox.isSelected() ? 3 : pane.sizeLargeCheckBox.isSelected() ? 11 : 7;
            playerNumber = pane.player1CheckBox.isSelected() ? 1 : 2;
            opponentNumber = pane.player1CheckBox.isSelected() ? 2 : 1;
            client.sendStartGame(boardSize, opponentNumber);
            initGame(boardSize, playerNumber);
        } else {
            pane.showLobbyNotFull();
        }

    }

    public void onSend(String message) {
        client.sendMessage(message, false);
    }

    public void initGame(int boardSize, int playerNumber) {
        manager.startGame(boardSize, 0, playerNumber, 0);
        gamePanel.beginGettingCoordinates();
    }

    public void updateBoardSize(String boardSizeString, int boardSize) {
        client.updateBoardSize(boardSizeString, boardSize);
    }

    public void updateStartTurn(String playerStartString, int playerStart) {
        client.updateStartTurn(playerStartString, playerStart);
    }

    public Client getClient() {
        return client;
    }

    public LobbyPane getLobbyPane() {
        return pane;
    }
}
