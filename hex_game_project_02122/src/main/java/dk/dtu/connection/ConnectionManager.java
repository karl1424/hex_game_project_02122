package dk.dtu.connection;

import org.jspace.*;
import java.io.IOException;

public class ConnectionManager {
    private RemoteSpace server;
    private RemoteSpace lobby;
    private int lobbyID;

    public String getUri(String name) {
        return SpaceTag.PROTOCOL.value() + SpaceTag.SERVER_IP.value() + ":" + SpaceTag.SERVER_PORT.value() + "/" + name
                + "?conn";
    }

    

    public void connectHost(String uri) throws Exception {
        server = new RemoteSpace(uri);
        if (!performHandshake(SpaceTag.SERVER.value(), server)) {
            throw new IllegalStateException();
        }
        server.put(TupleTag.HOST.value());
        Object[] lobby = server.get(new ActualField(SpaceTag.LOBBY.value()), new FormalField(Integer.class));
        lobbyID = (int) lobby[1];
    }


    public void connectToLobby(int lobbyID) throws Exception {
        this.lobbyID = lobbyID;
        String uriLobby = getUri(lobbyID + SpaceTag.LOBBY.value());
        lobby = new RemoteSpace(uriLobby);

        if (!performHandshake(SpaceTag.LOBBY.value(), lobby)) {
            throw new IllegalStateException();
        }
    }

    public boolean performHandshake(String spaceTag, RemoteSpace space) throws InterruptedException {
        space.put(spaceTag, TupleTag.TRYTOCONNECT.value());
        Object[] connection = space.get(new ActualField(TupleTag.CONNECTION.value()), new FormalField(String.class));
        return connection[1].equals(TupleTag.CONNECTED.value());
    }

    public RemoteSpace getLobby() {
        return lobby;
    }

    public int getLobbyID() {
        return lobbyID;
    }
}
