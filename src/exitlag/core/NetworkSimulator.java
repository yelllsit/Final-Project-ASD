package exitlag.core;

import exitlag.model.RelayNode;

import java.util.Random;

/**
 * Lightweight stand-in for a real network monitor. Every tick it jitters the
 * load on every relay node so the A* search finds slightly different routes
 * over time — this is what ExitLag calls "dynamic re-routing".
 */
public class NetworkSimulator {

    private final NetworkGraph graph;
    private final Random random = new Random();

    public NetworkSimulator(NetworkGraph graph) {
        this.graph = graph;
        for (RelayNode node : graph.getNodes().values()) {
            node.setLoad(25 + random.nextInt(40));
        }
    }

    public void tick() {
        for (RelayNode node : graph.getNodes().values()) {
            int delta = random.nextInt(15) - 7;
            node.setLoad(node.getLoad() + delta);
        }
    }
}
