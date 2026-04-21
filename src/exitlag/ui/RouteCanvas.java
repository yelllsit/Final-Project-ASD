package exitlag.ui;

import exitlag.core.NetworkGraph;
import exitlag.model.RelayNode;
import exitlag.model.Route;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.List;

/**
 * Visualises the relay network as a map. Draws every edge faintly, then
 * highlights the currently chosen route so the user can see the hops the
 * optimizer picked — similar to ExitLag's route visualization.
 */
public class RouteCanvas extends JPanel {

    private final NetworkGraph graph;
    private Route route;
    private String origin;
    private String destination;

    public RouteCanvas(NetworkGraph graph) {
        this.graph = graph;
        setBackground(new Color(16, 20, 30));
        setPreferredSize(new Dimension(640, 420));
    }

    public void setRoute(String origin, String destination, Route route) {
        this.origin = origin;
        this.destination = destination;
        this.route = route;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (RelayNode n : graph.getNodes().values()) {
            minX = Math.min(minX, n.getX());
            maxX = Math.max(maxX, n.getX());
            minY = Math.min(minY, n.getY());
            maxY = Math.max(maxY, n.getY());
        }
        double pad = 40;
        double w = getWidth() - pad * 2;
        double h = getHeight() - pad * 2;

        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(40, 52, 74));
        for (RelayNode a : graph.getNodes().values()) {
            for (NetworkGraph.Link link : graph.neighbors(a.getId())) {
                RelayNode b = graph.getNode(link.to);
                if (b == null) continue;
                if (a.getId().compareTo(b.getId()) >= 0) continue;
                int ax = (int) (pad + (a.getX() - minX) / (maxX - minX) * w);
                int ay = (int) (pad + (maxY - a.getY()) / (maxY - minY) * h);
                int bx = (int) (pad + (b.getX() - minX) / (maxX - minX) * w);
                int by = (int) (pad + (maxY - b.getY()) / (maxY - minY) * h);
                g.draw(new Line2D.Float(ax, ay, bx, by));
            }
        }

        if (route != null && route.getHops() != null) {
            List<String> hops = route.getHops();
            Stroke thick = new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g.setStroke(thick);
            g.setColor(new Color(72, 220, 140));
            for (int i = 0; i < hops.size() - 1; i++) {
                RelayNode a = graph.getNode(hops.get(i));
                RelayNode b = graph.getNode(hops.get(i + 1));
                if (a == null || b == null) continue;
                int ax = (int) (pad + (a.getX() - minX) / (maxX - minX) * w);
                int ay = (int) (pad + (maxY - a.getY()) / (maxY - minY) * h);
                int bx = (int) (pad + (b.getX() - minX) / (maxX - minX) * w);
                int by = (int) (pad + (maxY - b.getY()) / (maxY - minY) * h);
                g.draw(new Line2D.Float(ax, ay, bx, by));
            }
        }

        g.setFont(getFont().deriveFont(Font.PLAIN, 11f));
        for (RelayNode n : graph.getNodes().values()) {
            int x = (int) (pad + (n.getX() - minX) / (maxX - minX) * w);
            int y = (int) (pad + (maxY - n.getY()) / (maxY - minY) * h);
            boolean isOrigin = n.getId().equals(origin);
            boolean isDest = n.getId().equals(destination);
            boolean onRoute = route != null && route.getHops() != null
                    && route.getHops().contains(n.getId());

            Color fill;
            if (isOrigin) fill = new Color(90, 170, 255);
            else if (isDest) fill = new Color(255, 120, 120);
            else if (onRoute) fill = new Color(72, 220, 140);
            else fill = new Color(180, 190, 210);

            int r = isOrigin || isDest ? 10 : 7;
            g.setColor(fill);
            g.fillOval(x - r, y - r, r * 2, r * 2);
            g.setColor(new Color(10, 12, 18));
            g.drawOval(x - r, y - r, r * 2, r * 2);

            g.setColor(new Color(220, 225, 235));
            g.drawString(n.getCity() + " (" + n.getLoad() + "%)", x + 10, y + 4);
        }

        g.dispose();
    }
}
