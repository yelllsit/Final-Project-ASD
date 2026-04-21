package exitlag.ui;

import exitlag.core.NetworkGraph;
import exitlag.core.NetworkSimulator;
import exitlag.data.NetworkFactory;
import exitlag.model.GameServer;
import exitlag.model.RelayNode;
import exitlag.model.Route;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Main ExitLag-like window. Lets the user pick an origin city and a game
 * server, then compares the direct route against the A*-optimized route and
 * shows ping / jitter / packet-loss improvements live.
 */
public class MainWindow extends JFrame {

    private final NetworkGraph graph = NetworkFactory.buildDefault();
    private final NetworkSimulator simulator = new NetworkSimulator(graph);
    private final List<GameServer> servers = NetworkFactory.defaultServers();

    private final JComboBox<String> originBox = new JComboBox<>();
    private final JComboBox<GameServer> serverBox = new JComboBox<>();
    private final JButton connectBtn = new JButton("Connect & Optimize");
    private final JLabel statusLbl = new JLabel("Idle");

    private final RouteCanvas canvas = new RouteCanvas(graph);
    private final DefaultListModel<String> logModel = new DefaultListModel<>();
    private final JList<String> logView = new JList<>(logModel);

    private final MetricBox pingBox = new MetricBox("Ping", "ms");
    private final MetricBox jitterBox = new MetricBox("Jitter", "ms");
    private final MetricBox lossBox = new MetricBox("Loss", "%");
    private final MetricBox hopsBox = new MetricBox("Hops", "");

    private boolean connected = false;
    private Timer ticker;

    public MainWindow() {
        super("FiveM Route Optimizer — ExitLag-style (Final Project ASD)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(22, 26, 38));

        for (String id : NetworkFactory.defaultOrigins()) {
            RelayNode n = graph.getNode(id);
            originBox.addItem(n.getId() + " — " + n.getCity());
        }
        for (GameServer s : servers) serverBox.addItem(s);

        setLayout(new BorderLayout(12, 12));
        add(buildSidebar(), BorderLayout.WEST);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        connectBtn.addActionListener(e -> toggleConnection());

        ticker = new Timer(1500, e -> onTick());
        ticker.start();

        log("FiveM Booster ready. Pick a FiveM server and click Connect.");
    }

    private JPanel buildSidebar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(28, 33, 48));
        p.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        p.setPreferredSize(new Dimension(280, 0));

        JLabel title = header("FiveM Booster", 20f);
        JLabel subtitle = header("ExitLag-style route optimizer", 12f);
        subtitle.setForeground(new Color(150, 160, 180));

        p.add(title);
        p.add(subtitle);
        p.add(Box.createVerticalStrut(18));
        p.add(label("Your Location"));
        stretch(originBox);
        p.add(originBox);
        p.add(Box.createVerticalStrut(12));
        p.add(label("FiveM Server"));
        stretch(serverBox);
        p.add(serverBox);
        p.add(Box.createVerticalStrut(18));
        stretch(connectBtn);
        connectBtn.setBackground(new Color(72, 220, 140));
        connectBtn.setForeground(new Color(10, 20, 30));
        connectBtn.setFocusPainted(false);
        connectBtn.setFont(connectBtn.getFont().deriveFont(Font.BOLD, 14f));
        p.add(connectBtn);
        p.add(Box.createVerticalStrut(10));
        statusLbl.setForeground(new Color(170, 180, 200));
        statusLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(statusLbl);
        p.add(Box.createVerticalGlue());

        JLabel note = new JLabel("<html><body style='width:220px'>"
                + "<p style='color:#8892a8;font-size:10px'>Simulation only — does not "
                + "tunnel real FiveM/GTA traffic. It demonstrates the A* routing "
                + "strategy that tools like ExitLag use to pick the fastest relay "
                + "path to a FiveM server.</p>"
                + "</body></html>");
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(note);
        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);

        JPanel metrics = new JPanel(new GridLayout(1, 4, 10, 10));
        metrics.setOpaque(false);
        metrics.add(pingBox);
        metrics.add(jitterBox);
        metrics.add(lossBox);
        metrics.add(hopsBox);

        p.add(metrics, BorderLayout.NORTH);
        p.add(canvas,  BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        logView.setBackground(new Color(14, 18, 28));
        logView.setForeground(new Color(210, 220, 235));
        logView.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(logView);
        sp.setPreferredSize(new Dimension(0, 130));
        sp.setBorder(BorderFactory.createLineBorder(new Color(40, 48, 66)));
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void toggleConnection() {
        connected = !connected;
        if (connected) {
            connectBtn.setText("Disconnect");
            connectBtn.setBackground(new Color(255, 120, 120));
            statusLbl.setText("Connected");
            log("Connecting to " + selectedServer());
            recomputeRoute();
        } else {
            connectBtn.setText("Connect & Optimize");
            connectBtn.setBackground(new Color(72, 220, 140));
            statusLbl.setText("Idle");
            canvas.setRoute(null, null, null);
            pingBox.clear();
            jitterBox.clear();
            lossBox.clear();
            hopsBox.clear();
            log("Disconnected.");
        }
    }

    private void onTick() {
        simulator.tick();
        if (connected) recomputeRoute();
        else canvas.repaint();
    }

    private void recomputeRoute() {
        String originId = selectedOriginId();
        GameServer server = selectedServer();
        if (originId == null || server == null) return;

        Route direct = graph.directRoute(originId, server.getRelayId());
        Route optimal = graph.optimalRoute(originId, server.getRelayId());
        if (optimal == null) {
            log("No route found to " + server);
            return;
        }
        canvas.setRoute(originId, server.getRelayId(), optimal);

        pingBox.update(optimal.getPingMs(), direct.getPingMs());
        jitterBox.update(optimal.getJitterMs(), direct.getJitterMs());
        lossBox.update(optimal.getPacketLossPct(), direct.getPacketLossPct());
        hopsBox.update(optimal.hopCount(), direct.hopCount());

        log(String.format("%s → %s | direct %.0fms, optimized %.0fms (saved %.0fms) | hops: %s",
                originId, server.getRelayId(),
                direct.getPingMs(), optimal.getPingMs(),
                Math.max(0, direct.getPingMs() - optimal.getPingMs()),
                String.join("→", optimal.getHops())));
    }

    private String selectedOriginId() {
        Object v = originBox.getSelectedItem();
        if (v == null) return null;
        String s = v.toString();
        int idx = s.indexOf(" — ");
        return idx > 0 ? s.substring(0, idx) : s;
    }

    private GameServer selectedServer() {
        return (GameServer) serverBox.getSelectedItem();
    }

    private void log(String line) {
        logModel.add(0, line);
        while (logModel.size() > 40) logModel.remove(logModel.size() - 1);
    }

    private static JLabel header(String text, float size) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, size));
        l.setForeground(new Color(240, 245, 255));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(170, 180, 200));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private static void stretch(Component c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /** A small card showing an optimized metric and the improvement over the direct route. */
    private static class MetricBox extends JPanel {
        private final JLabel name = new JLabel("", SwingConstants.LEFT);
        private final JLabel value = new JLabel("--", SwingConstants.LEFT);
        private final JLabel delta = new JLabel(" ", SwingConstants.LEFT);
        private final String unit;

        MetricBox(String label, String unit) {
            this.unit = unit;
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(28, 33, 48));
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
            name.setText(label);
            name.setForeground(new Color(150, 160, 180));
            value.setForeground(new Color(240, 245, 255));
            value.setFont(value.getFont().deriveFont(Font.BOLD, 24f));
            delta.setForeground(new Color(72, 220, 140));
            add(name);
            add(value);
            add(delta);
        }

        void update(double optimal, double baseline) {
            value.setText(format(optimal) + (unit.isEmpty() ? "" : " " + unit));
            double diff = baseline - optimal;
            if (Math.abs(baseline) < 0.001) {
                delta.setText(" ");
            } else {
                double pct = diff / baseline * 100.0;
                String sign = diff >= 0 ? "-" : "+";
                delta.setText(String.format("%s%.1f%s vs direct (%.0f%%)",
                        sign, Math.abs(diff), unit.isEmpty() ? "" : unit, pct));
                delta.setForeground(diff >= 0 ? new Color(72, 220, 140) : new Color(255, 120, 120));
            }
        }

        void clear() {
            value.setText("--");
            delta.setText(" ");
        }

        private String format(double v) {
            if (unit.isEmpty()) return String.valueOf((int) Math.round(v));
            return String.format("%.1f", v);
        }
    }
}
