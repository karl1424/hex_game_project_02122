package dk.dtu.network;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspace.*;

import dk.dtu.network.handlers.ConnectionManager;
import dk.dtu.network.tags.SpaceTag;
import dk.dtu.network.tags.TupleTag;

public class Server {
    static Map<Integer, lobbyHandler> lobbyHandlers = new ConcurrentHashMap<>();

    public static SpaceRepository serverSpace;
    public static RemoteSpace lobbyRequests;
    public static int lobbyID;
    private static ConnectionManager connectionManager;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server.connectionManager = new ConnectionManager();
        setUpServerSapce();
        lobbyRequests = connectionManager.establishConnectionToRemoteSpace(SpaceTag.LOBBY_REQUEST.value());
        while (true) {
            waitingForHost();
        }
    }

    public static void setUpServerSapce() {
        lobbyID = 0;
        serverSpace = new SpaceRepository();
        serverSpace.addGate(connectionManager.getUri(""));
        serverSpace.add(SpaceTag.LOBBY_REQUEST.value(), new SequentialSpace());
        System.out.println("server up");
    }

    public static void waitingForHost() {
        try {
            connectionManager.performHandshakeServer(SpaceTag.SERVER.value(), lobbyRequests);
            lobbyRequests.get(new ActualField(TupleTag.HOST.value()));
            createLobby();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createLobby() {
        try {
            lobbyHandler tempLobbyHandler = new lobbyHandler(lobbyID, serverSpace);
            lobbyHandlers.put(lobbyID, tempLobbyHandler); // Store the lobbys in a list - lobbyHandler handler
                                                          // lobbyHandlers.get(1); //How to acces a specific
            new Thread(tempLobbyHandler).start();
            lobbyRequests.put(SpaceTag.LOBBY.value(), lobbyID);
            System.out.println("Lobby ID: " + lobbyHandlers.get(lobbyID).getLobbyId() + " have been created ");
            lobbyID++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class lobbyHandler implements Runnable {
    public int lobbyID;
    private SpaceRepository serverSpace;
    private RemoteSpace lobbySpace;
    private boolean player2; // Used to check if the lobby is full
    private volatile boolean running = true;

    private ConnectionManager connectionManager;

    public lobbyHandler(int lobbyID, SpaceRepository serverSpace) {
        this.connectionManager = new ConnectionManager();
        this.lobbyID = lobbyID;
        this.serverSpace = serverSpace;
    }

    public void run() {
        serverSpace.add(lobbyID + SpaceTag.LOBBY.value(), new SequentialSpace());
        new Thread(() -> getConnect()).start();
    }

    public void getConnect() {
        try {
            lobbySpace = connectionManager.establishConnectionToRemoteSpace(lobbyID + SpaceTag.LOBBY.value());
            connectionManager.performHandshakeServer(SpaceTag.LOBBY.value(), lobbySpace);
            System.out.println("The host has joined Lobby: " + lobbyID);
            new Thread(() -> checkCloseLobby()).start();
            while (running) {
                Object[] player2Status = lobbySpace.get(new ActualField(SpaceTag.LOBBY.value()), new FormalField(String.class));
                if (player2Status[1].equals(TupleTag.TRYTOCONNECT.value())) {
                    if (!checkOccupied(lobbySpace)) {
                        System.out.println("Not occupied");
                        lobbySpace.put(TupleTag.OCCUPIED.value());
                        lobbySpace.put(TupleTag.CONNECTION.value(), TupleTag.CONNECTED.value());
                        System.out.println("Player 2 has joined Lobby: " + lobbyID);                        
                        lobbySpace.put(true); // Boolean to start
                        player2 = true;
                    } else {
                        System.out.println("Occupied");
                        lobbySpace.put(TupleTag.CONNECTION.value(), TupleTag.NOT_CONNECTED.value());
                    }
                } else {
                    lobbySpace.get(new ActualField(TupleTag.OCCUPIED.value()));
                    lobbySpace.put(false); // boolean not ready to start
                    player2 = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkCloseLobby() {
        try {
            lobbySpace.get(new ActualField(TupleTag.CLOSE_LOBBY.value()));
            lobbySpace.put(TupleTag.LOBBY_CLOSED.value());
            long startTime = System.currentTimeMillis();
            boolean acknowledged = false;
            while (System.currentTimeMillis() - startTime < 2000) {
                Object[] ack = lobbySpace.getp(new ActualField(TupleTag.ACKNOWLEDGE_CLOSE.value()));
                if (ack != null) {
                    acknowledged = true;
                    break;
                }
                Thread.sleep(100);
            }

            if (!acknowledged) {
                System.out.println("No acknowledge close received in 2 seconds, continuing anyway.");
            }

            serverSpace.remove(lobbyID + SpaceTag.LOBBY.value());
            System.out.println("Lobby " + lobbyID + " closed and removed from repository");

            Server.lobbyHandlers.remove(lobbyID);
            running = false;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean checkOccupied(Space space) {
        Object[] isOccupied = null;
        try {
            isOccupied = space.queryp(new ActualField(TupleTag.OCCUPIED.value()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (isOccupied != null);
    }

    public int getLobbyId() {
        return lobbyID;
    }
}
