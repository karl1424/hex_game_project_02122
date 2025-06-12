package dk.dtu.network.handlers;

import dk.dtu.main.GamePanel;
import org.jspace.*;
import java.util.List;

public class LobbyMessageHandler {
    private final RemoteSpace lobby;
    private final GamePanel gamePanel;
    private boolean receiving = false;
    private final boolean isHost;

    public LobbyMessageHandler(RemoteSpace lobby, GamePanel gamePanel, boolean isHost) {
        this.lobby = lobby;
        this.gamePanel = gamePanel;
        this.isHost = isHost;
    }

    public void sendMessage(String message, boolean joinOrLeave) throws InterruptedException {
        lobby.put(message, isHost, joinOrLeave);
    }

    public void receiveMessage() {
        receiving = true;
        System.out.println("Start to receive messages");
        new Thread(() -> {
            while (receiving) {
                try {
                    Object[] msg = lobby.get(new FormalField(String.class), new ActualField(!isHost), new FormalField(Boolean.class));
                    System.out.println("Got something");
                    gamePanel.getMenuManager().getOnlineGameMenu().getLobbyPane().appendMessage( ((boolean) msg[2] ? "" : "Other: ") + (String) msg[0]);
                    System.out.println("Should print: " + msg[2] + msg[0]);
                    lobby.put(msg[0], msg[1], msg[2], "old");
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    public void stopReceivingMessages() {
        receiving = false;
    }

    public void receiveOldMessages() throws InterruptedException {
        List<Object[]> oldMessages = lobby.queryAll(new FormalField(String.class), new FormalField(Boolean.class), new FormalField(Boolean.class), new ActualField("old"));
        for (Object[] msg : oldMessages) {
            String prefix = (boolean) msg[2] ? "" : ((boolean) msg[1] ? "Other: " : "You: ");
            gamePanel.getMenuManager().getOnlineGameMenu().getLobbyPane().appendMessage(prefix + (String) msg[0]);
        }
    }
} 
