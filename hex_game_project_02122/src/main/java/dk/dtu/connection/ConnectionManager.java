package dk.dtu.connection;

import org.jspace.*;
import java.io.IOException;

public class ConnectionManager {
    private RemoteSpace server;
    private RemoteSpace lobby;
    private int lobbyID;
    private Client client;

    public ConnectionManager(Client client){
        this.client = client;
    }

    public ConnectionManager(){
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

    public boolean performHandshake(String spaceTag, RemoteSpace space) throws InterruptedException {
        space.put(spaceTag, TupleTag.TRYTOCONNECT.value());
        Object[] connection = space.get(new ActualField(TupleTag.CONNECTION.value()), new FormalField(String.class));
        return (((String) connection[1]).equals(TupleTag.CONNECTED.value()));
    }

    public void connectHost() throws InterruptedException, IOException {
        server = establishConnectionToRemoteSpace(SpaceTag.LOBBY_REQUEST.value());
            if (!performHandshake(SpaceTag.SERVER.value(), server)) {
                throw new IllegalStateException();
            }
            server.put(TupleTag.HOST.value());
            Object[] lobby = server.get(new ActualField(SpaceTag.LOBBY.value()), new FormalField(Integer.class));
            lobbyID = (int) lobby[1];
            System.out.println("Lobby ID: " + lobbyID);
        
    }

    public void connectToLobby(int lobbyID) throws InterruptedException, IOException, IllegalStateException {
        lobby = establishConnectionToRemoteSpace(lobbyID + SpaceTag.LOBBY.value());
        if (!performHandshake(SpaceTag.LOBBY.value(), lobby)) {
            throw new IllegalStateException();
        }
        System.out.println("Connection to Lobby: " + lobbyID + " Succesfull!");
        client.creatLobbyMessageHandler();
    }

    public int getLobbyID(){
        return this.lobbyID;
    }
    public void setLobbyID(int lobbyID){
        this.lobbyID = lobbyID;
    }

    public RemoteSpace getLobby() {
        return lobby;
    }
}
