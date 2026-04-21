package exitlag.data;

import exitlag.core.NetworkGraph;
import exitlag.model.GameServer;
import exitlag.model.RelayNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the demo relay network. The coordinates are rough lat/long positions
 * for cities in South-East and East Asia so the map draws in a familiar shape,
 * and the edge weights are plausible inter-city latencies.
 */
public final class NetworkFactory {

    private NetworkFactory() {}

    public static NetworkGraph buildDefault() {
        NetworkGraph g = new NetworkGraph();

        g.addNode(new RelayNode("JKT", "Jakarta",   "ID", 106.8, -6.2, 40));
        g.addNode(new RelayNode("SBY", "Surabaya",  "ID", 112.7, -7.3, 30));
        g.addNode(new RelayNode("BDG", "Bandung",   "ID", 107.6, -6.9, 25));
        g.addNode(new RelayNode("SIN", "Singapore", "SG", 103.8,  1.3, 55));
        g.addNode(new RelayNode("KUL", "Kuala Lumpur", "MY", 101.7, 3.1, 35));
        g.addNode(new RelayNode("BKK", "Bangkok",   "TH", 100.5, 13.7, 45));
        g.addNode(new RelayNode("MNL", "Manila",    "PH", 121.0, 14.6, 40));
        g.addNode(new RelayNode("HKG", "Hong Kong", "HK", 114.1, 22.3, 50));
        g.addNode(new RelayNode("TPE", "Taipei",    "TW", 121.5, 25.0, 45));
        g.addNode(new RelayNode("TYO", "Tokyo",     "JP", 139.7, 35.7, 60));
        g.addNode(new RelayNode("ICN", "Seoul",     "KR", 126.9, 37.5, 50));
        g.addNode(new RelayNode("SYD", "Sydney",    "AU", 151.2, -33.9, 35));

        link(g, "JKT", "BDG",  4, 0.5, 0.1);
        link(g, "JKT", "SBY", 12, 1.0, 0.2);
        link(g, "BDG", "SBY", 11, 1.0, 0.2);
        link(g, "JKT", "SIN", 14, 1.2, 0.2);
        link(g, "SIN", "KUL",  6, 0.6, 0.1);
        link(g, "KUL", "BKK", 18, 1.5, 0.3);
        link(g, "SIN", "BKK", 20, 1.8, 0.3);
        link(g, "SIN", "HKG", 28, 2.2, 0.4);
        link(g, "BKK", "HKG", 30, 2.4, 0.4);
        link(g, "HKG", "TPE", 11, 1.0, 0.2);
        link(g, "TPE", "TYO", 24, 2.0, 0.3);
        link(g, "TYO", "ICN", 18, 1.4, 0.3);
        link(g, "HKG", "ICN", 30, 2.2, 0.4);
        link(g, "MNL", "HKG", 22, 1.8, 0.3);
        link(g, "MNL", "TPE", 26, 2.0, 0.4);
        link(g, "SIN", "MNL", 32, 2.6, 0.5);
        link(g, "SYD", "SIN", 70, 6.0, 0.8);
        link(g, "SYD", "TYO", 85, 7.0, 1.0);
        link(g, "SBY", "SIN", 18, 1.6, 0.3);
        link(g, "JKT", "KUL", 17, 1.5, 0.3);

        return g;
    }

    public static List<GameServer> defaultServers() {
        List<GameServer> s = new ArrayList<>();
        s.add(new GameServer("Valorant",          "Tokyo",     "TYO"));
        s.add(new GameServer("Valorant",          "Singapore", "SIN"));
        s.add(new GameServer("Mobile Legends",    "Singapore", "SIN"));
        s.add(new GameServer("PUBG",              "Hong Kong", "HKG"));
        s.add(new GameServer("League of Legends", "Seoul",     "ICN"));
        s.add(new GameServer("Dota 2",            "Singapore", "SIN"));
        s.add(new GameServer("CS2",               "Sydney",    "SYD"));
        s.add(new GameServer("Apex Legends",      "Tokyo",     "TYO"));
        s.add(new GameServer("Genshin Impact",    "Taipei",    "TPE"));
        return s;
    }

    public static List<String> defaultOrigins() {
        List<String> o = new ArrayList<>();
        o.add("JKT");
        o.add("SBY");
        o.add("BDG");
        return o;
    }

    private static void link(NetworkGraph g, String a, String b,
                             double base, double jitter, double loss) {
        g.addLink(a, b, base, jitter, loss);
    }
}
