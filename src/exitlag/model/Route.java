package exitlag.model;

import java.util.List;

/**
 * The result of a route-optimization run: the ordered hops from the player's
 * origin node to the game server, plus the aggregated quality metrics.
 */
public class Route {
    private final List<String> hops;
    private final double pingMs;
    private final double jitterMs;
    private final double packetLossPct;
    private final int nodesExplored;

    public Route(List<String> hops, double pingMs, double jitterMs,
                 double packetLossPct, int nodesExplored) {
        this.hops = hops;
        this.pingMs = pingMs;
        this.jitterMs = jitterMs;
        this.packetLossPct = packetLossPct;
        this.nodesExplored = nodesExplored;
    }

    public List<String> getHops() { return hops; }
    public double getPingMs() { return pingMs; }
    public double getJitterMs() { return jitterMs; }
    public double getPacketLossPct() { return packetLossPct; }
    public int getNodesExplored() { return nodesExplored; }

    public int hopCount() { return hops == null ? 0 : hops.size(); }

    @Override
    public String toString() {
        return String.format("Ping %.1f ms | Jitter %.1f ms | Loss %.2f%% | Hops %d",
                pingMs, jitterMs, packetLossPct, hopCount());
    }
}
