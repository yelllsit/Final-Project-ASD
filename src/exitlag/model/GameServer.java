package exitlag.model;

/**
 * A game server endpoint (e.g. Valorant Tokyo, Mobile Legends Singapore).
 * It is modelled as a destination node in the network graph that the player
 * wants to reach with the lowest possible ping.
 */
public class GameServer {
    private final String game;
    private final String region;
    private final String relayId;

    public GameServer(String game, String region, String relayId) {
        this.game = game;
        this.region = region;
        this.relayId = relayId;
    }

    public String getGame() { return game; }
    public String getRegion() { return region; }
    public String getRelayId() { return relayId; }

    @Override
    public String toString() {
        return game + " — " + region;
    }
}
