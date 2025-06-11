package dk.dtu.connection;

public enum TupleTag {
    HOST("host"),
    TRYTOCONNECT("try to connect"),

    CONNECTION("connection"),
    CONNECTED("connected"),
    NOT_CONNECTED("not connected"),
    START_GAME("start game"),

    PLAYER_START("playerStart"),
    BOARD_SIZE("board size"),

    PLAYER_LEFT("PLAYER LEFT"),
    LEFT("Left"),
    
    LOBBY_CLOSED("lobby has been closed"),
    ACKNOWLEDGE_CLOSE("acknowledge close"),
    CLOSE_LOBBY("CLOSE LOBBY"),

    OCCUPIED("occupied");

    private final String value;

    TupleTag(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
