package dk.dtu.connection;

import dk.dtu.main.GamePanel;
import org.jspace.*;
import java.util.function.Consumer;

public class GameCommunicationHandler {

    private final RemoteSpace lobby;
    private boolean running = false;

    public GameCommunicationHandler(RemoteSpace lobby) {
        this.lobby = lobby;
    }

    public void sendSpot(int x, int y, int player) throws InterruptedException {
        lobby.put(x, y, player);
    }

    public void getSpot(GamePanel gamePanel, Consumer<Object[]> onSpotReceived) {
        running = true;
        new Thread(() -> {
            int opponent = gamePanel.getPlayerNumber() == 1 ? 2 : 1;
            while (running) {
                if (!gamePanel.getTurn()) {
                    try {
                        Object[] spot = lobby.get(new FormalField(Integer.class), new FormalField(Integer.class), new ActualField(opponent));
                        onSpotReceived.accept(spot);
                    } catch (InterruptedException e) { break; }
                }
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    public void stopReceivingSpots() {
        running = false;
    }

    public void sendStartGame(int boardSize, int playerNumber) throws InterruptedException {
        lobby.put(TupleTag.START_GAME.value(), boardSize, playerNumber);
    }

    public void getStartGame(Client.GameStartHandler handler) {
        new Thread(() -> {
            try {
                Object[] gameInfo = lobby.get(new ActualField(TupleTag.START_GAME.value()), new FormalField(Integer.class), new FormalField(Integer.class));
                handler.onStart((int) gameInfo[1], (int) gameInfo[2]);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void updateBoardSize(String key, int size) throws InterruptedException {
        lobby.put(key, size);
    }

    public void updateStartTurn(String key, int playerStart) throws InterruptedException {
        lobby.put(key, playerStart);
    }
    
}
