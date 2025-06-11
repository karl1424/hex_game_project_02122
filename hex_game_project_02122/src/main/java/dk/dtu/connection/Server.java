package dk.dtu.connection;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspace.*;

public class Server {
    static Map<Integer, lobbyHandler> lobbyHandlers = new ConcurrentHashMap<>();

    public static SpaceRepository serverSpace;
    public static RemoteSpace lobbyRequests;
    public static int lobbyID;

    public static void main(String[] args) throws IOException {
        setUpServerSapce();
        lobbyRequests = new RemoteSpace(getUri(SpaceTag.LOBBY_REQUEST.value()));
        while (true) {
            waitingForHost();
        }
    }

    public static void setUpServerSapce() {
        lobbyID = 0;
        serverSpace = new SpaceRepository();
        serverSpace.addGate(getUri(""));
        serverSpace.add(SpaceTag.LOBBY_REQUEST.value(), new SequentialSpace());
        System.out.println("server up");
    }

    public static String getUri(String name) {
        String Uri = SpaceTag.PROTOCOL.value() + SpaceTag.SERVER_IP.value() + ":" + SpaceTag.SERVER_PORT.value() + "/" + name + "?conn";
        return Uri;
    }

    public static void waitingForHost() {
        try {
            handShake();
            System.out.println("Handshake completed");
            lobbyRequests.get(new ActualField(TupleTag.HOST.value()));
            System.out.println("Host detected");
            createLobby();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handShake() {
        try {
            lobbyRequests.get(new ActualField(SpaceTag.SERVER.value()), new ActualField(TupleTag.TRYTOCONNECT.value()));
            lobbyRequests.put(TupleTag.CONNECTION.value(), TupleTag.CONNECTED.value());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }

    public static void createLobby() {
        try {
            lobbyHandler tempLobbyHandler = new lobbyHandler(SpaceTag.SERVER_IP.value(), SpaceTag.SERVER_PORT.value(), lobbyID, serverSpace);
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
    private String IP;
    private String port;
    public int lobbyID;
    private SpaceRepository serverSpace;
    private Space lobbySpace;
    private boolean player2; // Used to check if the lobby is full
    private volatile boolean running = true;

    public lobbyHandler(String IP, String port, int lobbyID, SpaceRepository serverSpace) {
        this.IP = IP;
        this.port = port;
        this.lobbyID = lobbyID;
        this.serverSpace = serverSpace;
    }

    public void run() {
        serverSpace.add(lobbyID + SpaceTag.LOBBY.value(), new SequentialSpace());
        new Thread(() -> getConnect()).start();
    }

    public void getConnect() {
        try {
            lobbySpace = new RemoteSpace(getUri(lobbyID + SpaceTag.LOBBY.value()));
            lobbySpace.get(new ActualField(SpaceTag.LOBBY.value()), new ActualField(TupleTag.TRYTOCONNECT.value()));
            lobbySpace.put(TupleTag.CONNECTION.value(), TupleTag.CONNECTED.value());
            System.out.println("The host has joined Lobby: " + lobbyID);
            System.out.println("Before while");
            new Thread(() -> checkCloseLobby()).start();
            while (running) {
                Object[] player2Status = lobbySpace.get(new ActualField(SpaceTag.LOBBY.value()), new FormalField(String.class));
                if (player2Status[1].equals(TupleTag.TRYTOCONNECT.value())) {
                    if (!checkOccupied(lobbySpace)) {
                        System.out.println("Not occupied");
                        lobbySpace.put(TupleTag.OCCUPIED.value());
                        lobbySpace.put(TupleTag.CONNECTION.value(), TupleTag.CONNECTED.value());
                        System.out.println("Player 2 has joined Lobby: " + lobbyID);
                        // lobbySpace.put("Player joined", 0);
                        // Boolean to start
                        lobbySpace.put(true);
                        player2 = true;
                    } else {
                        System.out.println("Occupied");
                        lobbySpace.put(TupleTag.CONNECTION.value(), TupleTag.NOT_CONNECTED.value());
                    }
                } else {
                    lobbySpace.get(new ActualField(TupleTag.OCCUPIED.value()));
                    System.out.println("PLAYER 2 LEFT");
                    // lobbySpace.put("Player left", 0);
                    // boolean not ready to start
                    lobbySpace.put(false);
                    player2 = false;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkCloseLobby() {
        try {
            System.out.println("Started check close lobby");
            lobbySpace.get(new ActualField(TupleTag.CLOSE_LOBBY.value()));
            lobbySpace.put(TupleTag.LOBBY_CLOSED.value());

            long startTime = System.currentTimeMillis();
            boolean acknowledged = false;

            while (System.currentTimeMillis() - startTime < 2000) {
                Object[] ack = lobbySpace.getp(new ActualField(TupleTag.ACKNOWLEDGE_CLOSE.value()));
                if (ack != null) {
                    acknowledged = true;
                    System.out.println("Received acknowledge close");
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

    public static String getUri(String name) {
        String Uri = SpaceTag.PROTOCOL.value() + SpaceTag.SERVER_IP.value() + ":" + SpaceTag.SERVER_PORT.value() + "/" + name + "?conn";
        return Uri;
    }

    public int getLobbyId() {
        return lobbyID;
    }
}