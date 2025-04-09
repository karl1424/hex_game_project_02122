package dk.dtu.Connection;
import java.io.IOException;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

public class Client {
    private static Space server, lobbySpace;
    private static int lobbyID;
    private static int port = 31145;
    private static String ServerIP = "localhost";
    private static boolean isHost = true;

    public static void main(String[] args) {
        establishConnetion();
    }

    public static void establishConnetion(){
        try {
            if (isHost) {
                connectHost(getUri("lobbyRequests"));
            }
            connectToLobby(lobbyID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connectHost(String uri) {
        try {
            server = new RemoteSpace(uri);
            server.put("host");
            Object[] lobby = server.get(new ActualField("lobby"), new FormalField(Integer.class));
            lobbyID = (int) lobby[1];
            System.out.println("Lobby ID: " + lobbyID);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getUri(String name) {
        String Uri = "tcp://" + ServerIP + ":" + port + "/" + name + "?conn";
        return Uri;
    }

    public static void connectToLobby(int lobbyID) throws InterruptedException, IOException {
        establishConnectionToLobby(lobbyID);
        if (!lobbyHandShake()) {
            throw new IllegalStateException();
        }
        System.out.println("Connection to Lobby: " + lobbyID + " Succesfull!");
    }

    public static void establishConnectionToLobby(int lobbyID) throws InterruptedException, IOException {
        String uriLobby = getUri(lobbyID + "lobby");
        lobbySpace = new RemoteSpace(uriLobby);
    }

    public static boolean lobbyHandShake() throws InterruptedException {
        lobbySpace.put("join/leave", "try to connect");
        Object[] connection = lobbySpace.get(new ActualField("connection"), new FormalField(String.class));
        return (((String) connection[1]).equals("Connected"));
    }

}
