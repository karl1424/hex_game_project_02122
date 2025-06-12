package dk.dtu.network.tags;

public enum SpaceTag {
    PROTOCOL("tcp://"),
    SERVER_IP("localhost"),
    SERVER_PORT("31145"),
    LOBBY_REQUEST("lobbyRequests"),
    SERVER("server"),
    LOBBY("lobby");

    private final String value;

    SpaceTag(String value) {
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
