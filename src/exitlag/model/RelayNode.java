package exitlag.model;

/**
 * A relay node in the ExitLag-like network. Acts as an intermediate hop
 * between the player and the game server, similar to ExitLag's relay servers.
 * Each node has a geographic location (for drawing) and a current load that
 * affects the ping cost of routing through it.
 */
public class RelayNode {
    private final String id;
    private final String city;
    private final String region;
    private final double x;
    private final double y;
    private int load;

    public RelayNode(String id, String city, String region, double x, double y, int load) {
        this.id = id;
        this.city = city;
        this.region = region;
        this.x = x;
        this.y = y;
        this.load = load;
    }

    public String getId() { return id; }
    public String getCity() { return city; }
    public String getRegion() { return region; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getLoad() { return load; }

    public void setLoad(int load) {
        this.load = Math.max(0, Math.min(100, load));
    }

    @Override
    public String toString() {
        return city + " (" + region + ")";
    }
}
