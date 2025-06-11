package dk.dtu.connection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.function.Consumer;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import dk.dtu.main.GamePanel;
import javafx.application.Platform;

public class Client {
    private int lobbyID;
    private boolean isHost;
    private boolean isOffline = false;
    private boolean running = false;
    private boolean canStart;
    private boolean recieveMessages = false;

    private GamePanel gamePanel;
    private RemoteSpace server;
    private RemoteSpace lobby;

    private ConnectionManager connectionManager;
    private LobbyMessageHandler lobbyMessageHandler;

    public Client(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.connectionManager = new ConnectionManager(this);
    }

    public void establishConnetion(boolean isHost) throws IllegalStateException, InterruptedException, IOException {
        isOffline = false;
        this.isHost = isHost;
        //setIsHost(isHost);
        try {
            connectionManager.connectHost();
        } catch (Exception e) {
            isOffline = true;
            return;
        }
        connectionManager.connectToLobby(connectionManager.getLobbyID());
        new Thread(() -> lookForP2()).start();
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void creatLobbyMessageHandler(){
        this.lobbyMessageHandler = new LobbyMessageHandler(connectionManager.getLobby(), gamePanel, isHost);
    }

    public String getLobbyID() throws UnknownHostException {
        if (isOffline) {
            throw new UnknownHostException();
        }
        return connectionManager.getLobbyID() + "";
    }

    public void setLobbyID(int lobbyID) {
        this.lobbyID = lobbyID;
        connectionManager.setLobbyID(lobbyID);
    }

    // ------------

    public void sendSpot(int x, int y, int player) {
        try {
            connectionManager.getLobby().put(x, y, player);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getSpot(int player, GamePanel gamePanel, Consumer<Object[]> onSpotReceived) {
        running = true;
        new Thread(() -> {
            int opponent = gamePanel.getPlayerNumber() == 1 ? 2 : 1;
            while (running) {
                boolean currentTurn = gamePanel.getTurn();
                if (!currentTurn) {
                    try {
                        Object[] spot = connectionManager.getLobby().get(
                                new FormalField(Integer.class),
                                new FormalField(Integer.class),
                                new ActualField(opponent));
                        System.out.println("Received: " + spot[0] + ", " + spot[1]);
                        onSpotReceived.accept(spot);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public void stopReceivingSpots() {
        running = false;
    }

    public void sendStartGame(int boardSize, int playerNumber) {
        try {
            connectionManager.getLobby().put(TupleTag.START_GAME.value(), boardSize, playerNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface GameStartHandler {
        void onStart(int boardSize, int playerNumber);
    }

    public void getStartGame(GameStartHandler handler) {
        new Thread(() -> {
            try {
                Object[] gameInfo = connectionManager.getLobby().get(
                        new ActualField(TupleTag.START_GAME.value()),
                        new FormalField(Integer.class),
                        new FormalField(Integer.class));
                System.out.println(gameInfo[0]);

                int boardSize = (int) gameInfo[1];
                int playerNumber = (int) gameInfo[2];

                handler.onStart(boardSize, playerNumber);
            } catch (InterruptedException e) {

            }
        }).start();
    }

    @FunctionalInterface
    public interface GameInfoHandler {
        void onStart(String tag, int value);
    }

    public void getGameInfo(GameInfoHandler handler) {
        new Thread(() -> {
            try {
                while (true) {
                    Object[] info = connectionManager.getLobby().get(
                            new FormalField(String.class),
                            new FormalField(Integer.class));

                    String tag = (String) info[0];
                    int value = (int) info[1];

                    switch (tag) {
                        case "board size": // Can we change??+
                            handler.onStart(TupleTag.BOARD_SIZE.value(), value);
                            break;
                        case "playerStart":
                            handler.onStart(TupleTag.PLAYER_START.value(), value);
                            break;
                        default:
                    }
                }

            } catch (InterruptedException e) {

            }
        }).start();
    }

    public void updateBoardSize(String boardStringSize, int boardSize) {
        try {
            connectionManager.getLobby().put(boardStringSize, boardSize);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateStartTurn(String playerStartString, int playerStart) {
        try {
            connectionManager.getLobby().put(playerStartString, playerStart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean getIsHost() {
        System.out.println(isHost);
        return isHost;
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
        System.out.println("set:" + this.isHost);
    }

    public void lookForP2() {
        try {
            while (true) {
                Object[] start = connectionManager.getLobby().get(new FormalField(Boolean.class));
                if ((boolean) start[0]) {
                    sendGameSettings();
                    canStart = true;
                } else {
                    canStart = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getCanStart() {
        return canStart;
    }

    public void sendLeftPlayer2() {
        try {
            connectionManager.getLobby().put(SpaceTag.LOBBY.value(), TupleTag.LEFT.value());
            connectionManager.getLobby().put(TupleTag.PLAYER_LEFT.value(), isHost, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // --------------

    public LobbyMessageHandler getLobbyMessageHandler() {
        return lobbyMessageHandler;
    }


    public void checkForLobbyClosed() { // Player 2 needs to stop listening for a lobby closing after exitin the lobby
                                        // himself
        try {
            connectionManager.getLobby().get(new ActualField(TupleTag.LOBBY_CLOSED.value()));
            connectionManager.getLobby().put(TupleTag.ACKNOWLEDGE_CLOSE.value());
            System.out.println("Lobby has been closed");
            Platform.runLater(() -> {
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

    private void sendGameSettings() {
        try {
            int boardSize = gamePanel.getMenuManager().getOnlineGameMenu().getBoardSize();
            int playerStart = gamePanel.getMenuManager().getOnlineGameMenu().getPlayerStart();
            connectionManager.getLobby().put(TupleTag.BOARD_SIZE.value(), boardSize);
            connectionManager.getLobby().put(TupleTag.PLAYER_START.value(), playerStart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
