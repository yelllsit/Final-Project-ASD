package exitlag.data;

import exitlag.core.NetworkGraph;
import exitlag.model.GameServer;
import exitlag.model.RelayNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds the SEA-only relay network for FiveM. Only covers Indonesia, Singapore,
 * Thailand and the Philippines; Malaysia + Hong Kong are kept as transit hops
 * so A* has meaningful alternative paths to choose between.
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
        g.addNode(new RelayNode("CNX", "Chiang Mai",   "TH",  98.9, 18.8, 25));
        g.addNode(new RelayNode("MNL", "Manila",       "PH", 121.0, 14.6, 40));
        g.addNode(new RelayNode("CEB", "Cebu",         "PH", 123.9, 10.3, 30));
        g.addNode(new RelayNode("HKG", "Hong Kong",    "HK", 114.1, 22.3, 50));

        link(g, "JKT", "BDG",  4, 0.5, 0.1);
        link(g, "JKT", "SBY", 12, 1.0, 0.2);
        link(g, "BDG", "SBY", 11, 1.0, 0.2);
        link(g, "JKT", "SIN", 14, 1.2, 0.2);
        link(g, "SBY", "SIN", 18, 1.6, 0.3);
        link(g, "JKT", "KUL", 17, 1.5, 0.3);
        link(g, "SIN", "KUL",  6, 0.6, 0.1);
        link(g, "KUL", "BKK", 18, 1.5, 0.3);
        link(g, "SIN", "BKK", 20, 1.8, 0.3);
        link(g, "BKK", "CNX", 10, 0.9, 0.2);
        link(g, "SIN", "MNL", 32, 2.6, 0.5);
        link(g, "MNL", "CEB",  8, 0.7, 0.2);
        link(g, "MNL", "HKG", 22, 1.8, 0.3);
        link(g, "SIN", "HKG", 28, 2.2, 0.4);
        link(g, "BKK", "HKG", 30, 2.4, 0.4);

        return g;
    }

    public static List<GameServer> defaultServers() {
        List<GameServer> s = new ArrayList<>();
        s.add(new GameServer("FiveM — Indo Roleplay",      "Jakarta",   "JKT"));
        s.add(new GameServer("FiveM — Nusantara RP",       "Jakarta",   "JKT"));
        s.add(new GameServer("FiveM — Jakarta City RP",    "Jakarta",   "JKT"));
        s.add(new GameServer("FiveM — Anak Perantauan RP", "Singapore", "SIN"));
        s.add(new GameServer("FiveM — SG Asia RP",         "Singapore", "SIN"));
        s.add(new GameServer("FiveM — Singa RP",           "Singapore", "SIN"));
        s.add(new GameServer("FiveM — Thai Roleplay",      "Bangkok",   "BKK"));
        s.add(new GameServer("FiveM — Siam RP",            "Bangkok",   "BKK"));
        s.add(new GameServer("FiveM — Lanna RP",           "Chiang Mai", "CNX"));
        s.add(new GameServer("FiveM — Pinoy RP",           "Manila",    "MNL"));
        s.add(new GameServer("FiveM — Manila City RP",     "Manila",    "MNL"));
        s.add(new GameServer("FiveM — Cebu RP",            "Cebu",      "CEB"));
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
