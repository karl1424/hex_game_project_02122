package dk.dtu.connection;

import java.io.IOException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.Space;

public class ConnectionService {
    private final String serverIP;
    private final int port;

    public ConnectionService(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
    }

    public RemoteSpace connectToServer(String name) throws IOException {
        String uri = getUri(name);
        return new RemoteSpace(uri);
    }

    public RemoteSpace connectToLobby(int lobbyID) throws IOException {
        String uri = getUri(lobbyID + "lobby");
        return new RemoteSpace(uri);
    }

    public boolean performHandshake(Space space, String type) throws InterruptedException {
        space.put(type, "try to connect");
        Object[] response = space.get(new ActualField("connection"), new FormalField(String.class));
        return "connected".equals(response[1]);
    }

    private String getUri(String name) {
        return "tcp://" + serverIP + ":" + port + "/" + name + "?conn";
    }
}
