package exitlag.data;

import exitlag.core.NetworkGraph;
import exitlag.model.GameServer;
import exitlag.model.RelayNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the FiveM-focused relay network. Coordinates are rough lat/long for
 * major PoP cities across Asia, North America, Europe and Oceania so the map
 * draws a recognizable shape, and link weights reflect plausible inter-city
 * backbone latencies. Server entries are real well-known FiveM roleplay
 * servers mapped to the PoP closest to their published hosting region.
 */
public final class NetworkFactory {

    private NetworkFactory() {}

    public static NetworkGraph buildDefault() {
        NetworkGraph g = new NetworkGraph();

        g.addNode(new RelayNode("JKT", "Jakarta",      "ID", 106.8, -6.2, 40));
        g.addNode(new RelayNode("SBY", "Surabaya",     "ID", 112.7, -7.3, 30));
        g.addNode(new RelayNode("BDG", "Bandung",      "ID", 107.6, -6.9, 25));
        g.addNode(new RelayNode("SIN", "Singapore",    "SG", 103.8,  1.3, 55));
        g.addNode(new RelayNode("KUL", "Kuala Lumpur", "MY", 101.7,  3.1, 35));
        g.addNode(new RelayNode("BKK", "Bangkok",      "TH", 100.5, 13.7, 45));
        g.addNode(new RelayNode("MNL", "Manila",       "PH", 121.0, 14.6, 40));
        g.addNode(new RelayNode("HKG", "Hong Kong",    "HK", 114.1, 22.3, 50));
        g.addNode(new RelayNode("TPE", "Taipei",       "TW", 121.5, 25.0, 45));
        g.addNode(new RelayNode("TYO", "Tokyo",        "JP", 139.7, 35.7, 60));
        g.addNode(new RelayNode("ICN", "Seoul",        "KR", 126.9, 37.5, 50));
        g.addNode(new RelayNode("SYD", "Sydney",       "AU", 151.2, -33.9, 35));

        g.addNode(new RelayNode("LAX", "Los Angeles",  "US", -118.2, 34.0, 55));
        g.addNode(new RelayNode("SEA", "Seattle",      "US", -122.3, 47.6, 40));
        g.addNode(new RelayNode("SJC", "San Jose",     "US", -121.9, 37.3, 45));
        g.addNode(new RelayNode("DFW", "Dallas",       "US",  -96.8, 32.8, 50));
        g.addNode(new RelayNode("ORD", "Chicago",      "US",  -87.6, 41.9, 50));
        g.addNode(new RelayNode("NYC", "New York",     "US",  -74.0, 40.7, 60));
        g.addNode(new RelayNode("IAD", "Ashburn",      "US",  -77.4, 39.0, 55));
        g.addNode(new RelayNode("MIA", "Miami",        "US",  -80.2, 25.8, 35));

        g.addNode(new RelayNode("LHR", "London",       "UK",   -0.1, 51.5, 50));
        g.addNode(new RelayNode("FRA", "Frankfurt",    "DE",    8.7, 50.1, 55));
        g.addNode(new RelayNode("AMS", "Amsterdam",    "NL",    4.9, 52.4, 45));
        g.addNode(new RelayNode("PAR", "Paris",        "FR",    2.4, 48.9, 40));

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

        link(g, "LAX", "SEA", 22, 1.6, 0.3);
        link(g, "LAX", "SJC", 10, 0.8, 0.2);
        link(g, "SEA", "SJC", 20, 1.4, 0.3);
        link(g, "LAX", "DFW", 32, 2.4, 0.4);
        link(g, "SJC", "DFW", 36, 2.6, 0.4);
        link(g, "DFW", "ORD", 22, 1.8, 0.3);
        link(g, "ORD", "NYC", 18, 1.4, 0.3);
        link(g, "NYC", "IAD", 10, 0.8, 0.2);
        link(g, "IAD", "MIA", 28, 2.2, 0.4);
        link(g, "DFW", "MIA", 34, 2.6, 0.5);
        link(g, "LAX", "NYC", 62, 5.0, 0.7);
        link(g, "SEA", "ORD", 45, 3.6, 0.5);

        link(g, "TYO", "LAX", 95,  8.0, 1.0);
        link(g, "TYO", "SEA", 90,  7.5, 0.9);
        link(g, "ICN", "LAX", 100, 8.2, 1.0);
        link(g, "HKG", "LAX", 115, 9.0, 1.2);
        link(g, "SIN", "LAX", 140, 11.0, 1.4);
        link(g, "SYD", "LAX", 130, 10.0, 1.3);

        link(g, "LHR", "FRA", 12, 1.0, 0.2);
        link(g, "LHR", "AMS", 10, 0.9, 0.2);
        link(g, "LHR", "PAR",  8, 0.7, 0.1);
        link(g, "FRA", "AMS",  8, 0.7, 0.2);
        link(g, "FRA", "PAR", 14, 1.1, 0.2);
        link(g, "LHR", "NYC", 68, 5.4, 0.8);
        link(g, "FRA", "NYC", 78, 6.0, 0.9);
        link(g, "AMS", "NYC", 72, 5.6, 0.8);
        link(g, "LHR", "IAD", 72, 5.8, 0.8);

        link(g, "SIN", "FRA", 155, 12.0, 1.5);
        link(g, "HKG", "FRA", 160, 12.5, 1.6);
        link(g, "TYO", "FRA", 220, 16.0, 1.8);

        return g;
    }

    public static List<GameServer> defaultServers() {
        List<GameServer> s = new ArrayList<>();
        s.add(new GameServer("FiveM — NoPixel 4.0",        "US Central (Dallas)",  "DFW"));
        s.add(new GameServer("FiveM — NoPixel Public",     "US Central (Dallas)",  "DFW"));
        s.add(new GameServer("FiveM — GTA World",          "US Central (Chicago)", "ORD"));
        s.add(new GameServer("FiveM — Eclipse RP",         "US East (New York)",   "NYC"));
        s.add(new GameServer("FiveM — ProdigyRP",          "US East (Ashburn)",    "IAD"));
        s.add(new GameServer("FiveM — New Day RP",         "US West (Los Angeles)", "LAX"));
        s.add(new GameServer("FiveM — DOJ RP",             "US West (Seattle)",    "SEA"));
        s.add(new GameServer("FiveM — LucidRP",            "US West (San Jose)",   "SJC"));
        s.add(new GameServer("FiveM — OCRP",               "Oceania (Sydney)",     "SYD"));
        s.add(new GameServer("FiveM — Indo Roleplay",      "SEA (Jakarta)",        "JKT"));
        s.add(new GameServer("FiveM — Anak Perantauan RP", "SEA (Singapore)",      "SIN"));
        s.add(new GameServer("FiveM — Malaysia RP",        "SEA (Kuala Lumpur)",   "KUL"));
        s.add(new GameServer("FiveM — Tokyo RP",           "Japan (Tokyo)",        "TYO"));
        s.add(new GameServer("FiveM — Korean RP",          "Korea (Seoul)",        "ICN"));
        s.add(new GameServer("FiveM — UK Roleplay",        "Europe (London)",      "LHR"));
        s.add(new GameServer("FiveM — EuropaRP",           "Europe (Frankfurt)",   "FRA"));
        return s;
    }

    public static List<String> defaultOrigins() {
        List<String> o = new ArrayList<>();
        o.add("JKT");
        o.add("SBY");
        o.add("BDG");
        o.add("KUL");
        o.add("SIN");
        return o;
    }

    private static void link(NetworkGraph g, String a, String b,
                             double base, double jitter, double loss) {
        g.addLink(a, b, base, jitter, loss);
    }
}
