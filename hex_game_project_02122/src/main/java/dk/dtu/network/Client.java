package dk.dtu.network;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;

import dk.dtu.main.GamePanel;
import dk.dtu.network.handlers.ClientState;
import dk.dtu.network.handlers.ConnectionManager;
import dk.dtu.network.handlers.GameCommunicationHandler;
import dk.dtu.network.handlers.LobbyMessageHandler;
import dk.dtu.network.tags.SpaceTag;
import dk.dtu.network.tags.TupleTag;
import javafx.application.Platform;

public class Client {

    private GamePanel gamePanel;
    private ConnectionManager connectionManager;
    private ClientState clientState;
    private LobbyMessageHandler lobbyMessageHandler;
    private GameCommunicationHandler gameCommunicationHandler;

    public Client(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.connectionManager = new ConnectionManager(this);
        this.clientState = new ClientState();
    }

    public void establishConnetionAsHost(boolean isHost, int oldLobbyID)
            throws IllegalStateException, InterruptedException, IOException {
        clientState.setOffline(false);
        clientState.setHost(isHost);
        try {
            connectionManager.connectHost(oldLobbyID);
        } catch (Exception e) {
            clientState.setOffline(true);
            return;
        }
        connectionManager.connectToLobby(connectionManager.getLobbyID());
        new Thread(() -> lookForP2()).start();
    }

    public void lookForP2() {
        try {
            while (true) {
                Object[] start = connectionManager.getLobby().get(new FormalField(Boolean.class));
                if ((boolean) start[0]) {
                    gameCommunicationHandler.sendGameSettings(gamePanel);
                    clientState.setCanStart(true);
                } else {
                    clientState.setCanStart(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLeftPlayer2() {
        try {
            connectionManager.getLobby().put(SpaceTag.LOBBY.value(), TupleTag.LEFT.value());
            connectionManager.getLobby().put(TupleTag.PLAYER_LEFT.value(), clientState.isHost(), true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void checkForLobbyClosed() { // Player 2 needs to stop listening for a lobby closing after exitin the lobby
                                        // himself
        try {
            connectionManager.getLobby().get(new ActualField(TupleTag.LOBBY_CLOSED.value()));
            connectionManager.getLobby().put(TupleTag.ACKNOWLEDGE_CLOSE.value());
            System.out.println("Lobby has been closed");
            Platform.runLater(() -> {
                gamePanel.getMenuManager().getPrimaryStage().getScene().setRoot(gamePanel.getMenuManager());
                lobbyMessageHandler.stopReceivingMessages();
                gamePanel.getMenuManager().getOnlineGameMenu().goToOnlineSetup();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutDownLobby() {
        try {
            connectionManager.getLobby().put(TupleTag.CLOSE_LOBBY.value());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getLobbyID() throws UnknownHostException {
        if (clientState.isOffline()) {
            throw new UnknownHostException();
        }
        return connectionManager.getLobbyID() + "";
    }

    public void sendToLobby() {
        try {
            connectionManager.getLobby().put("to lobby");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void receiveToLobby() {
        new Thread(() -> {
            try {
                connectionManager.getLobby().get(new ActualField("to lobby"));
                Thread.sleep(500);
                System.out.println("I HAVE RECEIVED");
                Platform.runLater(() -> {
                    try {
                        gamePanel.getMenuManager().getOnlineGameMenu().onJoinLobby(getLobbyID());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception ignored) {
            }
        }).start();
    }

    public ClientState getClientState() {
        return clientState;
    }

    public LobbyMessageHandler getLobbyMessageHandler() {
        return lobbyMessageHandler;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public GameCommunicationHandler getGameCommunicationHandler() {
        return gameCommunicationHandler;
    }

    public void createHandlers() {
        this.lobbyMessageHandler = new LobbyMessageHandler(connectionManager.getLobby(), gamePanel,
                clientState.isHost());
        this.gameCommunicationHandler = new GameCommunicationHandler(connectionManager.getLobby());
    }
}
