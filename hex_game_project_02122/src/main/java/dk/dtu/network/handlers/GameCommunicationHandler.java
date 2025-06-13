package dk.dtu.network.handlers;

import dk.dtu.main.GamePanel;
import dk.dtu.network.tags.TupleTag;

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
            System.out.println("Receiving from " + opponent);
            while (running) {
                try {
                    Object[] spot = lobby.get(new FormalField(Integer.class), new FormalField(Integer.class),
                            new ActualField(opponent));
                    onSpotReceived.accept(spot);
                } catch (InterruptedException e) {
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public void stopReceivingSpots() {
        running = false;
    }

    public void sendStartGame(int boardSize, int playerNumber) throws InterruptedException {
        lobby.put(TupleTag.START_GAME.value(), boardSize, playerNumber);
    }

    @FunctionalInterface
    public interface GameStartHandler {
        void onStart(int boardSize, int playerNumber);
    }

    public void getStartGame(GameStartHandler handler) {
        new Thread(() -> {
            try {
                Object[] gameInfo = lobby.get(new ActualField(TupleTag.START_GAME.value()),
                        new FormalField(Integer.class), new FormalField(Integer.class));
                handler.onStart((int) gameInfo[1], (int) gameInfo[2]);
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    @FunctionalInterface
    public interface GameInfoHandler {
        void onStart(String tag, int value);
    }

    public void getGameSettings(GameInfoHandler handler) {
        new Thread(() -> {
            try {
                while (true) {
                    Object[] info = lobby.get(
                            new FormalField(String.class),
                            new FormalField(Integer.class));

                    String tag = (String) info[0];
                    int value = (int) info[1];

                    switch (tag) {
                        case "board size": // Can we change??+
                            handler.onStart(TupleTag.BOARD_SIZE.value(), value);
                            break;
                        case "playerStart":
                            handler.onStart(TupleTag.PLAYER_START.value(), value);
                            break;
                        default:
                    }
                }

            } catch (InterruptedException e) {

            }
        }).start();
    }

    public void sendGameSettings(GamePanel gamePanel) {
        try {
            int boardSize = gamePanel.getMenuManager().getOnlineGameMenu().getBoardSize();
            int playerStart = gamePanel.getMenuManager().getOnlineGameMenu().getPlayerStart();
            lobby.put(TupleTag.BOARD_SIZE.value(), boardSize);
            lobby.put(TupleTag.PLAYER_START.value(), playerStart);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateBoardSize(String key, int size) throws InterruptedException {
        lobby.put(key, size);
    }

    public void updateStartTurn(String key, int playerStart) throws InterruptedException {
        lobby.put(key, playerStart);
    }
}
