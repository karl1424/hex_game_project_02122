package dk.dtu.network.handlers;

import org.jspace.*;

import dk.dtu.network.Client;
import dk.dtu.network.tags.SpaceTag;
import dk.dtu.network.tags.TupleTag;

import java.io.IOException;

public class ConnectionManager {
    private RemoteSpace server;
    private RemoteSpace lobby;
    private int lobbyID;
    private Client client;

    public ConnectionManager() {
    }

    public ConnectionManager(Client client) {
        this.client = client;
    }

    public String getUri(String name) {
        return SpaceTag.PROTOCOL.value() + SpaceTag.SERVER_IP.value() + ":" + SpaceTag.SERVER_PORT.value() + "/" + name
                + "?conn";
    }

    public RemoteSpace establishConnectionToRemoteSpace(String name) throws InterruptedException, IOException {
        String uri = getUri(name);
        RemoteSpace space = new RemoteSpace(uri);
        return space;
    }

    public void performHandshakeServer(String spaceTag, RemoteSpace space) throws InterruptedException {
        space.get(new ActualField(spaceTag), new ActualField(TupleTag.TRYTOCONNECT.value()));
        space.put(TupleTag.CONNECTION.value(), TupleTag.CONNECTED.value());
    }

    public boolean performHandshakeClients(String spaceTag, RemoteSpace space) throws InterruptedException {
        space.put(spaceTag, TupleTag.TRYTOCONNECT.value());
        Object[] connection = space.get(new ActualField(TupleTag.CONNECTION.value()), new FormalField(String.class));
        return (((String) connection[1]).equals(TupleTag.CONNECTED.value()));
    }

    public void connectHost(int oldLobbyID) throws InterruptedException, IOException {
        server = establishConnectionToRemoteSpace(SpaceTag.LOBBY_REQUEST.value());
        if (!performHandshakeClients(SpaceTag.SERVER.value(), server)) {
            throw new IllegalStateException();
        }
        server.put(TupleTag.HOST.value(), oldLobbyID);
        Object[] lobby = server.get(new ActualField(SpaceTag.LOBBY.value()), new FormalField(Integer.class));
        lobbyID = (int) lobby[1];
    }

    public void connectToLobby(int lobbyID) throws InterruptedException, IOException, IllegalStateException {
        lobby = establishConnectionToRemoteSpace(lobbyID + SpaceTag.LOBBY.value());
        if (!performHandshakeClients(SpaceTag.LOBBY.value(), lobby)) {
            throw new IllegalStateException();
        }
        client.createHandlers();
    }

    public boolean receiveCloseLobby() throws InterruptedException {
        Object[] lobbyClose = lobby.get(new ActualField(TupleTag.LOBBY_CLOSED.value()), new FormalField(Boolean.class));
        lobby.put(TupleTag.ACKNOWLEDGE_CLOSE.value());
        System.out.println((boolean) lobbyClose[1]);
        return (boolean) lobbyClose[1];
    }

    public void notifyHostLeftLobby(ClientState clientState) {
        try {
            lobby.put(SpaceTag.LOBBY.value(), TupleTag.LEFT.value());
            lobby.put(TupleTag.PLAYER_LEFT.value(), clientState.isHost(), true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutDownLobby(boolean toLobby) {
        try {
            lobby.put(TupleTag.CLOSE_LOBBY.value(), toLobby);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getLobbyID() {
        return this.lobbyID;
    }

    public void setLobbyID(int lobbyID) {
        this.lobbyID = lobbyID;
    }

    public RemoteSpace getLobby() {
        return lobby;
    }

    public Client getClient() {
        return client;
    }
}
