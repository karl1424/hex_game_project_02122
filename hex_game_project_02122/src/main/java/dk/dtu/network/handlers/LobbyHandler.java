package dk.dtu.network.handlers;

import dk.dtu.network.Server;
import dk.dtu.network.tags.SpaceTag;
import dk.dtu.network.tags.TupleTag;
import org.jspace.*;

public class LobbyHandler implements Runnable {
    public int lobbyID;
    private SpaceRepository serverSpace;
    private RemoteSpace lobbySpace;
    private boolean player2InLobby;
    private volatile boolean running = true;

    private ConnectionManager connectionManager;

    public LobbyHandler(int lobbyID, SpaceRepository serverSpace) {
        this.connectionManager = new ConnectionManager();
        this.lobbyID = lobbyID;
        this.serverSpace = serverSpace;
    }

    public void run() {
        serverSpace.add(lobbyID + SpaceTag.LOBBY.value(), new SequentialSpace());
        new Thread(() -> createLobby()).start();
    }

    public void createLobby() {
        try {
            lobbySpace = connectionManager.establishConnectionToRemoteSpace(lobbyID + SpaceTag.LOBBY.value());
            connectionManager.performHandshakeServer(SpaceTag.LOBBY.value(), lobbySpace);
            System.out.println("The host has joined Lobby: " + lobbyID);
            new Thread(() -> checkCloseLobby()).start();
            while (running) {
                Object[] player2Status = lobbySpace.get(new ActualField(SpaceTag.LOBBY.value()),
                        new FormalField(String.class));
                if (player2Status[1].equals(TupleTag.TRYTOCONNECT.value())) {
                    if (!checkOccupied(lobbySpace)) {
                        lobbySpace.put(TupleTag.OCCUPIED.value());
                        lobbySpace.put(TupleTag.CONNECTION.value(), TupleTag.CONNECTED.value());
                        System.out.println("Player 2 has joined Lobby: " + lobbyID);
                        lobbySpace.put(true); // Boolean to start
                        player2InLobby = true;
                    } else {
                        lobbySpace.put(TupleTag.CONNECTION.value(), TupleTag.NOT_CONNECTED.value());
                    }
                } else {
                    lobbySpace.get(new ActualField(TupleTag.OCCUPIED.value()));
                    lobbySpace.put(false); // boolean not ready to start
                    player2InLobby = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkCloseLobby() {
        try {
            Object[] closeLobby = lobbySpace.get(new ActualField(TupleTag.CLOSE_LOBBY.value()),
                    new FormalField(Boolean.class));
            lobbySpace.put(TupleTag.LOBBY_CLOSED.value(), (boolean) closeLobby[1]);
            long startTime = System.currentTimeMillis();
            boolean acknowledged = false;
            while (System.currentTimeMillis() - startTime < 2000) {
                Object[] ack = lobbySpace.getp(new ActualField(TupleTag.ACKNOWLEDGE_CLOSE.value()));
                if (ack != null) {
                    acknowledged = true;
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

    public int getLobbyId() {
        return lobbyID;
    }

    public boolean isPlayer2InLobby() {
        return player2InLobby;
    }
}
