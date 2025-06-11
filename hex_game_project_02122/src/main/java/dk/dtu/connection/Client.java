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

    public Client(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.connectionManager = new ConnectionManager();
    }

    public void establishConnetion(boolean isHost) throws IllegalStateException, InterruptedException, IOException {
        isOffline = false;
        this.isHost = isHost;
        if (isHost) {
            connectHost();
        }
        connectToLobby(lobbyID);
    }

    public void connectHost() {
        try {
            server = establishConnectionToRemoteSpace(SpaceTag.LOBBY_REQUEST.value());
            if (!perfromHandShake(SpaceTag.SERVER.value(), server)) {
                throw new IllegalStateException();
            }
            server.put(TupleTag.HOST.value());
            Object[] lobby = server.get(new ActualField(SpaceTag.LOBBY.value()), new FormalField(Integer.class));
            lobbyID = (int) lobby[1];
            System.out.println("Lobby ID: " + lobbyID);
        } catch (Exception e) {
            isOffline = true;
            return;
        }
    }


    public RemoteSpace establishConnectionToRemoteSpace(String name) throws InterruptedException, IOException {
        String uri = connectionManager.getUri(name);
        RemoteSpace space = new RemoteSpace(uri);
        return space;
    }

    public void connectToLobby(int lobbyID) throws InterruptedException, IOException, IllegalStateException {
        lobby = establishConnectionToRemoteSpace(lobbyID + SpaceTag.LOBBY.value());
        if (!perfromHandShake(SpaceTag.LOBBY.value(), lobby)) {
            throw new IllegalStateException();
        }
        System.out.println("Connection to Lobby: " + lobbyID + " Succesfull!");
        if (isHost) {
            new Thread(() -> lookForP2()).start();
        }
    }

    public boolean perfromHandShake(String spaceTag, RemoteSpace space) throws InterruptedException {
        space.put(spaceTag, TupleTag.TRYTOCONNECT.value());
        Object[] connection = space.get(new ActualField(TupleTag.CONNECTION.value()), new FormalField(String.class));
        return (((String) connection[1]).equals(TupleTag.CONNECTED.value()));
    }

    public String getLobbyID() throws UnknownHostException {
        if (isOffline) {
            throw new UnknownHostException();
        }
        return lobbyID + "";
    }

    public void sendSpot(int x, int y, int player) {
        try {
            lobby.put(x, y, player);
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
                        Object[] spot = lobby.get(
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
            lobby.put(TupleTag.START_GAME.value(), boardSize, playerNumber);
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
                Object[] gameInfo = lobby.get(
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
                    Object[] info = lobby.get(
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

    public void setLobbyID(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    public void updateBoardSize(String boardStringSize, int boardSize) {
        try {
            lobby.put(boardStringSize, boardSize);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateStartTurn(String playerStartString, int playerStart) {
        try {
            lobby.put(playerStartString, playerStart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean getIsHost() {
        System.out.println(isHost);
        return isHost;
    }

    public void lookForP2() {
        try {
            while (true) {
                Object[] start = lobby.get(new FormalField(Boolean.class));
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
            lobby.put(SpaceTag.LOBBY.value(), TupleTag.LEFT.value());
            lobby.put(TupleTag.PLAYER_LEFT.value(), isHost, true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, boolean joinOrLeave) {
        try {
            lobby.put(message, isHost, joinOrLeave);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopReceivingMessages() {
        recieveMessages = false;
    }

    public void recieveMessage() {
        recieveMessages = true;
        while (recieveMessages) {
            try {
                Object[] message = lobby.get(new FormalField(String.class), new ActualField(!isHost),
                        new FormalField(Boolean.class));
                gamePanel.getMenuManager().getOnlineGameMenu().getLobbyPane()
                        .appendMessage(((boolean) message[2] ? "" : "Other: ") + (String) message[0]);
                lobby.put(message[0], message[1], message[2], "old");
            } catch (InterruptedException e) {

            }
        }
    }

    public void recieveOldMessages() {
        try {
            List<Object[]> oldMessages = lobby.queryAll(new FormalField(String.class),
                    new FormalField(Boolean.class), new FormalField(Boolean.class), new ActualField("old"));
            for (Object[] m : oldMessages) {
                gamePanel.getMenuManager().getOnlineGameMenu().getLobbyPane()
                        .appendMessage(((boolean) m[2] ? "" : ((boolean) m[1] ? "Other: " : "You: ")) + (String) m[0]);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void checkForLobbyClosed() {
        try {
            lobby.get(new ActualField(TupleTag.LOBBY_CLOSED.value()));
            lobby.put(TupleTag.ACKNOWLEDGE_CLOSE.value());
            System.out.println("Lobby has been closed");
            Platform.runLater(() -> {
                stopReceivingMessages();
                gamePanel.getMenuManager().getOnlineGameMenu().goToOnlineSetup();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutDownLobby() {
        try {
            lobby.put(TupleTag.CLOSE_LOBBY.value());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
        System.out.println("set:" + this.isHost);
    }

    private void sendGameSettings() {
        try {
            int boardSize = gamePanel.getMenuManager().getOnlineGameMenu().getBoardSize();
            int playerStart = gamePanel.getMenuManager().getOnlineGameMenu().getPlayerStart();
            lobby.put(TupleTag.BOARD_SIZE.value(), boardSize);
            lobby.put(TupleTag.PLAYER_START.value(), playerStart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
