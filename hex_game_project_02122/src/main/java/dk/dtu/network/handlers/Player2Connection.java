package dk.dtu.network.handlers;

import org.jspace.FormalField;
import org.jspace.Tuple;

import dk.dtu.main.GamePanel;
import dk.dtu.network.tags.TupleTag;

public class Player2Connection {

    private ConnectionManager connectionManager;
    
    public Player2Connection(ConnectionManager connectionManager){
        this.connectionManager = connectionManager;
    }

    public void lookForP2(GamePanel gamePanel, ClientState clientState, GameCommunicationHandler gameCommunicationHandler) {
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

    public void preventP2ReentryToLobby() {
        try {
            connectionManager.getLobby().put(TupleTag.TO_LOBBY.value(), 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopP2HostExitListener() {
        try {
            connectionManager.getLobby().put(TupleTag.LOBBY_CLOSED.value(), true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopP2SettingListener() {
        try {
            System.out.println("SEND BOARD SIZE AND PLAYER START");
            connectionManager.getClient().getGameCommunicationHandler().setFlag(false);
            connectionManager.getLobby().put(TupleTag.BOARD_SIZE.value(), 7);
            connectionManager.getLobby().put(TupleTag.PLAYER_START.value(), 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
