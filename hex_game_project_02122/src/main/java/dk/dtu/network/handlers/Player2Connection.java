package dk.dtu.network.handlers;

import org.jspace.FormalField;

import dk.dtu.main.GamePanel;
import dk.dtu.network.tags.SpaceTag;
import dk.dtu.network.tags.TupleTag;

public class Player2Connection {

    private ConnectionManager connectionManager;
    private ClientState clientState;
    private GameCommunicationHandler gameCommunicationHandler;
    private GamePanel gamePanel;
    
    public Player2Connection(ConnectionManager connectionManager, ClientState clientState, GameCommunicationHandler gameCommunicationHandler, GamePanel gamePanel){
        this.connectionManager = connectionManager;
        this.clientState = clientState;
        this.gameCommunicationHandler = gameCommunicationHandler;
        this.gamePanel = gamePanel;
    }

    public void lookForP2() {
        try {
            while (true) {
                Object[] start = connectionManager.getLobby().get(new FormalField(Boolean.class));
                if ((boolean) start[0]) {
                    gameCommunicationHandler.sendGameSettings(gamePanel);
                    clientState.setCanStart(true);
                } else {
                    clientState.setCanStart(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLeftPlayer2() {
        try {
            connectionManager.getLobby().put(SpaceTag.LOBBY.value(), TupleTag.LEFT.value());
            connectionManager.getLobby().put(TupleTag.PLAYER_LEFT.value(), clientState.isHost(), true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
