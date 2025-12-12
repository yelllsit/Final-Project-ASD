package graphlinked;

import java.util.*;

public class GraphIUP {
    private Map<String, List<Edge>> adj = new HashMap<>();
    private Map<String, Integer> trafficLevel = new HashMap<>();

    public static class Edge {
        String destination;
        double weight;

        public Edge(String destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    private static class Node implements Comparable<Node> {
        String city;
        double gCost;
        double hCost;
        double fCost;

        public Node(String city, double gCost, double hCost) {
            this.city = city;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost, other.fCost);
        }
    }

    public void addCity(String city) {
        adj.putIfAbsent(city, new ArrayList<>());
        trafficLevel.put(city, 0);
    }

    public void addCity(String city, int traffic) {
        adj.putIfAbsent(city, new ArrayList<>());
        trafficLevel.put(city, traffic);
    }

    public void addConnection(String from, String to, double weight) {
        adj.putIfAbsent(from, new ArrayList<>());
        adj.putIfAbsent(to, new ArrayList<>());
        adj.get(from).add(new Edge(to, weight));
        adj.get(to).add(new Edge(from, weight));
    }

    private double heuristic(String current, String destination, int hopCount) {
        double estimatedDistance = hopCount;
        int destTraffic = trafficLevel.getOrDefault(destination, 0);
        return estimatedDistance + (destTraffic * 0.5);
    }

    public PathResult findOptimalPath(String origin, String destination) {
        if (!adj.containsKey(origin) || !adj.containsKey(destination)) {
            System.out.println("Invalid city name.");
            return null;
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Map<String, Double> gCosts = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Map<String, Integer> hopCounts = new HashMap<>();
        Set<String> closedSet = new HashSet<>();
        int nodesExplored = 0;

        gCosts.put(origin, 0.0);
        hopCounts.put(origin, 0);
        double initialH = heuristic(origin, destination, estimateHops(origin, destination));
        openSet.add(new Node(origin, 0, initialH));
        parent.put(origin, null);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (closedSet.contains(current.city)) continue;
            closedSet.add(current.city);
            nodesExplored++;

            if (current.city.equals(destination)) {
                List<String> path = reconstructPath(parent, destination);
                return new PathResult(path, current.gCost, nodesExplored);
            }

            for (Edge edge : adj.get(current.city)) {
                if (closedSet.contains(edge.destination)) continue;

                double tentativeG = gCosts.get(current.city) + edge.weight;
                int newHops = hopCounts.get(current.city) + 1;

                if (!gCosts.containsKey(edge.destination) || tentativeG < gCosts.get(edge.destination)) {
                    gCosts.put(edge.destination, tentativeG);
                    hopCounts.put(edge.destination, newHops);
                    parent.put(edge.destination, current.city);

                    double h = heuristic(edge.destination, destination, estimateHops(edge.destination, destination));
                    openSet.add(new Node(edge.destination, tentativeG, h));
                }
            }
        }

        System.out.println("No path found.");
        return null;
    }

    private int estimateHops(String from, String to) {
        return from.equals(to) ? 0 : 2;
    }

    public static class PathResult {
        public List<String> path;
        public double totalDistance;
        public int nodesExplored;

        public PathResult(List<String> path, double totalDistance, int nodesExplored) {
            this.path = path;
            this.totalDistance = totalDistance;
            this.nodesExplored = nodesExplored;
        }

        @Override
        public String toString() {
            return "Path: " + path +
                    "\nTotal Distance: " + String.format("%.2f km", totalDistance) +
                    "\nNodes Explored: " + nodesExplored;
        }
    }

    private List<String> reconstructPath(Map<String, String> parent, String destination) {
        List<String> path = new ArrayList<>();
        String current = destination;

        while (current != null) {
            path.add(current);
            current = parent.get(current);
        }

        Collections.reverse(path);
        return path;
    }
}
