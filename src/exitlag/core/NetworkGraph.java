package exitlag.core;

import exitlag.model.RelayNode;
import exitlag.model.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A weighted undirected graph of relay nodes. Edge weights are base latencies
 * (in milliseconds) between two relays. At query time the effective weight is
 * adjusted by the relay load so the A* search prefers less congested hops —
 * the same idea ExitLag uses when it switches routes in real time.
 */
public class NetworkGraph {

    public static class Link {
        public final String to;
        public final double baseMs;
        public final double jitterMs;
        public final double lossPct;

        public Link(String to, double baseMs, double jitterMs, double lossPct) {
            this.to = to;
            this.baseMs = baseMs;
            this.jitterMs = jitterMs;
            this.lossPct = lossPct;
        }
    }

    private final Map<String, RelayNode> nodes = new HashMap<>();
    private final Map<String, List<Link>> adj = new HashMap<>();

    public void addNode(RelayNode node) {
        nodes.put(node.getId(), node);
        adj.putIfAbsent(node.getId(), new ArrayList<>());
    }

    public void addLink(String a, String b, double baseMs, double jitterMs, double lossPct) {
        adj.computeIfAbsent(a, k -> new ArrayList<>())
                .add(new Link(b, baseMs, jitterMs, lossPct));
        adj.computeIfAbsent(b, k -> new ArrayList<>())
                .add(new Link(a, baseMs, jitterMs, lossPct));
    }

    public RelayNode getNode(String id) { return nodes.get(id); }
    public Map<String, RelayNode> getNodes() { return nodes; }
    public List<Link> neighbors(String id) {
        return adj.getOrDefault(id, Collections.emptyList());
    }

    /**
     * Cost of traversing a link given the current load on the destination
     * relay. Higher load = higher effective ping, which pushes A* toward less
     * congested paths.
     */
    private double effectiveCost(Link link) {
        RelayNode dest = nodes.get(link.to);
        double loadPenalty = dest == null ? 0 : dest.getLoad() * 0.25;
        return link.baseMs + loadPenalty;
    }

    /**
     * Straight-line geographic distance in the graph's coordinate space,
     * scaled so it is admissible relative to link costs. Used as the A*
     * heuristic — cheap to compute and never overestimates true ping.
     */
    private double heuristic(String from, String to) {
        RelayNode a = nodes.get(from);
        RelayNode b = nodes.get(to);
        if (a == null || b == null) return 0;
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy) * 0.8;
    }

    /**
     * Simulated unoptimized ISP path: distance-based ping inflated by a
     * congestion factor that grows with distance (public-internet peering
     * hops, transoceanic contention, etc.). This is the baseline ExitLag
     * tries to beat.
     */
    public Route directRoute(String origin, String destination) {
        if (!nodes.containsKey(origin) || !nodes.containsKey(destination)) return null;
        RelayNode a = nodes.get(origin);
        RelayNode b = nodes.get(destination);
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double raw = Math.sqrt(dx * dx + dy * dy);
        double ping = raw * 2.6 + 25 + Math.min(50, raw * 0.9);
        double jitter = 10 + raw * 0.18;
        double loss = 2.0 + raw * 0.025;
        List<String> hops = new ArrayList<>();
        hops.add(origin);
        hops.add(destination);
        return new Route(hops, ping, jitter, Math.min(loss, 9.9), 1);
    }

    /**
     * A* search for the route that minimises total effective ping. When a path
     * is found we recompute the aggregated ping, jitter and packet-loss along
     * the chosen hops — ExitLag reports similar metrics after it has picked a
     * route.
     */
    public Route optimalRoute(String origin, String destination) {
        if (!nodes.containsKey(origin) || !nodes.containsKey(destination)) return null;

        PriorityQueue<double[]> open = new PriorityQueue<>((x, y) -> Double.compare(x[0], y[0]));
        Map<String, Double> gCost = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> idx = new HashMap<>();
        Set<String> closed = new HashSet<>();
        List<String> order = new ArrayList<>();

        gCost.put(origin, 0.0);
        idx.put(origin, 0);
        order.add(origin);
        open.add(new double[]{heuristic(origin, destination), 0.0, 0});
        parent.put(origin, null);

        int explored = 0;
        while (!open.isEmpty()) {
            double[] cur = open.poll();
            String city = order.get((int) cur[2]);
            if (closed.contains(city)) continue;
            closed.add(city);
            explored++;

            if (city.equals(destination)) {
                List<String> path = rebuildPath(parent, destination);
                return summarise(path, explored);
            }

            for (Link link : neighbors(city)) {
                if (closed.contains(link.to)) continue;
                double tentative = gCost.get(city) + effectiveCost(link);
                Double known = gCost.get(link.to);
                if (known == null || tentative < known) {
                    gCost.put(link.to, tentative);
                    parent.put(link.to, city);
                    int id = order.size();
                    order.add(link.to);
                    idx.put(link.to, id);
                    double f = tentative + heuristic(link.to, destination);
                    open.add(new double[]{f, tentative, id});
                }
            }
        }
        return null;
    }

    private Route summarise(List<String> path, int explored) {
        double ping = 0, jitter = 0, survival = 1.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Link link = findLink(path.get(i), path.get(i + 1));
            if (link == null) continue;
            RelayNode dest = nodes.get(link.to);
            double loadPenalty = dest == null ? 0 : dest.getLoad() * 0.25;
            ping += link.baseMs + loadPenalty;
            jitter += link.jitterMs;
            survival *= (1.0 - link.lossPct / 100.0);
        }
        double loss = (1.0 - survival) * 100.0;
        return new Route(path, ping, jitter, loss, explored);
    }

    private Link findLink(String a, String b) {
        for (Link l : neighbors(a)) if (l.to.equals(b)) return l;
        return null;
    }

    private List<String> rebuildPath(Map<String, String> parent, String end) {
        List<String> path = new ArrayList<>();
        String cur = end;
        while (cur != null) {
            path.add(cur);
            cur = parent.get(cur);
        }
        Collections.reverse(path);
        return path;
    }
}
