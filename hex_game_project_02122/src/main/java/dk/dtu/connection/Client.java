package dk.dtu.connection;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

import dk.dtu.main.GamePanel;

public class Client {
    private Space server, lobbySpace;
    private int lobbyID;
    private int port = 31145;
    private String ServerIP = "localhost";
    private boolean isHost;
    private boolean isOffline = false;
    private boolean running = false;

    public Client() {
    }

    public void establishConnetion(boolean isHost) {
        this.isHost = isHost;
        try {
            if (isHost) {
                connectHost(getUri("lobbyRequests"));
            }
            connectToLobby(lobbyID);
        } catch (Exception e) {
            return;
            // e.printStackTrace();
        }
    }

    public void connectHost(String uri) {
        try {
            server = new RemoteSpace(uri);
            server.put("host");
            Object[] lobby = server.get(new ActualField("lobby"), new FormalField(Integer.class));
            lobbyID = (int) lobby[1];
            System.out.println("Lobby ID: " + lobbyID);
        } catch (Exception e) {
            isOffline = true;
            return;
            // e.printStackTrace();
        }

    }

    public String getUri(String name) {
        String Uri = "tcp://" + ServerIP + ":" + port + "/" + name + "?conn";
        return Uri;
    }

    public void connectToLobby(int lobbyID) throws InterruptedException, IOException {
        establishConnectionToLobby(lobbyID);
        if (!lobbyHandShake()) {
            throw new IllegalStateException();
        }
        System.out.println("Connection to Lobby: " + lobbyID + " Succesfull!");
    }

    public void establishConnectionToLobby(int lobbyID) throws InterruptedException, IOException {
        String uriLobby = getUri(lobbyID + "lobby");
        lobbySpace = new RemoteSpace(uriLobby);
    }

    public boolean lobbyHandShake() throws InterruptedException {
        lobbySpace.put("join/leave", "try to connect");
        Object[] connection = lobbySpace.get(new ActualField("connection"), new FormalField(String.class));
        return (((String) connection[1]).equals("Connected"));
    }

    public String getLobbyID() throws UnknownHostException {
        if (isOffline) {
            throw new UnknownHostException();
        }
        return lobbyID + "";
    }

    public void sendSpot(int x, int y, int player) {
        try {
            lobbySpace.put(x, y, player);
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
                        Object[] spot = lobbySpace.get(
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
            lobbySpace.put("start game", boardSize, playerNumber);
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
                Object[] gameInfo = lobbySpace.get(
                        new ActualField("start game"),
                        new FormalField(Integer.class),
                        new FormalField(Integer.class));
                System.out.println(gameInfo[0]);

                int boardSize = (int) gameInfo[1];
                int playerNumber = (int) gameInfo[2];

                handler.onStart(boardSize, playerNumber);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FunctionalInterface
    public interface GameInfoHandler {
        void onStart(String tag, int value);
    }
    public void getGameInfo(GameInfoHandler handler){
        new Thread(() -> {
            try {
                while (true) {
                    Object[] info = lobbySpace.get(
                        new FormalField(String.class),
                        new FormalField(Integer.class)
                    );
        
                    String tag = (String) info[0];
                    int value = (int) info[1];
        
                    switch (tag) {
                        case "board size":
                            System.out.println("Board size received: " + value);
                            handler.onStart("board size", value);
                            break;
                        case "playerStart":
                            System.out.println("Player start number: " + value);
                            handler.onStart("playerStart", value);
                            break;
                        default:
                            System.out.println("Unknown tuple: " + tag);
                    }
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setLobbyID(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    public void updateBoardSize(String boardStringSize, int boardSize) {
        try {
            lobbySpace.put(boardStringSize, boardSize);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }   
    }

    public void updateStartTurn(String playerStartString, int playerStart) {
        try {
            lobbySpace.put(playerStartString, playerStart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }   
    }

    public boolean getIsHost(){
        return isHost;
    }

}