package dk.dtu.connection;
import java.io.IOException;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;


public class Client {
    private Space server, lobbySpace;
    private int lobbyID;
    private int port = 31145;
    private String ServerIP = "localhost";
    private boolean isHost;
    private boolean isOffline = false;

    public Client() {}

    public void establishConnetion(boolean isHost){
        this.isHost = isHost;
        try {
            if (isHost) {
                connectHost(getUri("lobbyRequests"));
            }
            connectToLobby(lobbyID);
        } catch (Exception e) {
            return;
            //e.printStackTrace();
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
            //e.printStackTrace();
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

    public String getLobbyID() {
        if (isOffline) {
            return "Server is down!";
        }
        return lobbyID + "";
    }

    public void setLobbyID(int lobbyID) {
        this.lobbyID = lobbyID;
    }

}
