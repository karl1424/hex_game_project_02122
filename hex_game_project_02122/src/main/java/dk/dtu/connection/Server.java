package dk.dtu.connection;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspace.*;

public class Server {
    static Map<Integer, lobbyHandler> lobbyHandlers = new ConcurrentHashMap<>();

    public static SpaceRepository serverSpace;
    public static RemoteSpace lobbyRequests;
    final static String IP = "localhost";
    final static String port = "31145";
    public static int lobbyID;

    public static void main(String[] args) throws IOException {
        setUpServerSapce();
        lobbyRequests = new RemoteSpace(getUri("lobbyRequests"));
        while (true) {
            waitingForHost();
        }
    }

    public static String getUri(String name) {
        String Uri = "tcp://" + IP + ":" + port + "/" + name + "?conn";
        return Uri;
    }

    public static void setUpServerSapce() {
        lobbyID = 0;
        serverSpace = new SpaceRepository();
        serverSpace.addGate(getUri(""));
        serverSpace.add("lobbyRequests", new SequentialSpace());
        System.out.println("server up");
    }

    public static void waitingForHost() {
        try {
            handshake();
            lobbyRequests.get(new ActualField("host"));
            System.out.println("Host detected");
            createLobby();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handshake() {
        try {
            lobbyRequests.get(new ActualField("server"), new ActualField("try to connect"));
            lobbyRequests.put("connection", "Connected");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createLobby() {
        try {
            lobbyHandler tempLobbyHandler = new lobbyHandler(IP, port, lobbyID, serverSpace);
            lobbyHandlers.put(lobbyID, tempLobbyHandler); // Store the lobbys in a list - lobbyHandler handler
                                                          // lobbyHandlers.get(1); //How to acces a specific
            new Thread(tempLobbyHandler).start();
            lobbyRequests.put("lobby", lobbyID);
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
        serverSpace.add(lobbyID + "lobby", new SequentialSpace());
        new Thread(() -> getConnect()).start();
    }

    public void getConnect() {
        try {
            lobbySpace = new RemoteSpace(getUri(lobbyID + "lobby"));
            lobbySpace.get(new ActualField("join/leave"), new ActualField("try to connect"));
            lobbySpace.put("connection", "Connected");
            System.out.println("The host has joined Lobby: " + lobbyID);
            System.out.println("Before while");
            new Thread(() -> checkCloseLobby()).start();
            while (running) {
                Object[] player2Status = lobbySpace.get(new ActualField("join/leave"), new FormalField(String.class));
                if (player2Status[1].equals("try to connect")) {
                    if (!checkOccupied(lobbySpace)) {
                        System.out.println("Not occupied");
                        lobbySpace.put("occupied");
                        lobbySpace.put("connection", "Connected");
                        System.out.println("Player 2 has joined Lobby: " + lobbyID);
                        // lobbySpace.put("Player joined", 0);
                        // Boolean to start
                        lobbySpace.put(true);
                        player2 = true;
                    } else {
                        System.out.println("Occupied");
                        lobbySpace.put("connection", "Not connected");
                    }
                } else {
                    lobbySpace.get(new ActualField("occupied"));
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
            lobbySpace.get(new ActualField("CLOSE LOBBY"));
            lobbySpace.put("lobby has been closed");

            long startTime = System.currentTimeMillis();
            boolean acknowledged = false;

            while (System.currentTimeMillis() - startTime < 2000) {
                Object[] ack = lobbySpace.getp(new ActualField("acknowledge close"));
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

            serverSpace.remove(lobbyID + "lobby");
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
            isOccupied = space.queryp(new ActualField("occupied"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (isOccupied != null);
    }

    public String getUri(String name) {
        String Uri = "tcp://" + IP + ":" + port + "/" + name + "?conn";
        return Uri;
    }

    public int getLobbyId() {
        return lobbyID;
    }
}