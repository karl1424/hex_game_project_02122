package dk.dtu.network;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspace.*;

import dk.dtu.network.handlers.ConnectionManager;
import dk.dtu.network.handlers.LobbyHandler;
import dk.dtu.network.tags.SpaceTag;
import dk.dtu.network.tags.TupleTag;

public class Server {
    public static Map<Integer, LobbyHandler> lobbyHandlers = new ConcurrentHashMap<>();
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
        lobbyID = 1;
        serverSpace = new SpaceRepository();
        serverSpace.addGate(connectionManager.getUri(""));
        serverSpace.add(SpaceTag.LOBBY_REQUEST.value(), new SequentialSpace());
        System.out.println("The server is running");
    }

    public static void waitingForHost() {
        try {
            connectionManager.performHandshakeServer(SpaceTag.SERVER.value(), lobbyRequests);
            Object[] request = lobbyRequests.get(new ActualField(TupleTag.HOST.value()), new FormalField(Integer.class));
            if ((int) request[1] == 0) {
                createLobby();
            } else {
                createOldLobby((int) request[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createLobby() {
        try {
            LobbyHandler tempLobbyHandler = new LobbyHandler(lobbyID, serverSpace);
            lobbyHandlers.put(lobbyID, tempLobbyHandler); // Store the lobbys in a list - lobbyHandler handler
            new Thread(tempLobbyHandler).start();
            lobbyRequests.put(SpaceTag.LOBBY.value(), lobbyID);
            System.out.println("Lobby ID: " + lobbyHandlers.get(lobbyID).getLobbyId() + " have been created ");
            lobbyID++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createOldLobby(int oldLobbyID) {
        try {
            LobbyHandler tempLobbyHandler = new LobbyHandler(oldLobbyID, serverSpace);
            lobbyHandlers.put(oldLobbyID, tempLobbyHandler);
            new Thread(tempLobbyHandler).start();
            lobbyRequests.put(SpaceTag.LOBBY.value(), oldLobbyID);
            System.out.println("Old Lobby ID: " + lobbyHandlers.get(oldLobbyID).getLobbyId() + " have been created ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static LobbyHandler getSpecificLobbyHandler(int lobbyID){
        return lobbyHandlers.get(lobbyID);
    }

    public Map<Integer, LobbyHandler> getLobbyHandlers(){
        return lobbyHandlers;
    }
}
