package dk.dtu.Connection;
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
            lobbyRequests.get(new ActualField("host"));
            System.out.println("Host detected");
            createLobby();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createLobby() {
        try {
            lobbyHandler tempLobbyHandler = new lobbyHandler(IP, port, lobbyID, serverSpace);
            lobbyHandlers.put(lobbyID, tempLobbyHandler); // Store the lobbys in a list - lobbyHandler handler = lobbyHandlers.get(1); //How to acces a specific lobbyHanlder
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

    public lobbyHandler(String IP, String port, int lobbyID, SpaceRepository serverSpace) {
        this.IP = IP;
        this.port = port;
        this.lobbyID = lobbyID;
        this.serverSpace = serverSpace;
    }

    public void run() {
        serverSpace.add(lobbyID + "lobby", new SequentialSpace());
        try {
            Space lobbySpace = new RemoteSpace(getUri(lobbyID + "lobby"));
            lobbySpace.get(new ActualField("join/leave"), new ActualField("try to connect"));
            lobbySpace.put("connection", "Connected");
            System.out.println("The host has joined Lobby: " + lobbyID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUri(String name) {
        String Uri = "tcp://" + IP + ":" + port + "/" + name + "?conn";
        return Uri;
    }
    public int getLobbyId(){
        return lobbyID;
    }
}
