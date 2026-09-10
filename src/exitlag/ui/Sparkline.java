package exitlag.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A small line-chart component that plots the last N values the caller pushes
 * into it. Rendered as a filled area under the curve so it reads at a glance
 * from a metric card.
 */
public class Sparkline extends JPanel {

    private final Deque<Double> samples = new ArrayDeque<>();
    private final int capacity;
    private Color stroke = new Color(72, 220, 140);
    private Color fill = new Color(72, 220, 140, 40);

    public Sparkline(int capacity) {
        this.capacity = capacity;
        setOpaque(false);
        setPreferredSize(new Dimension(120, 30));
    }

    public void push(double v) {
        samples.addLast(v);
        while (samples.size() > capacity) samples.removeFirst();
        repaint();
    }

    public void clear() {
        samples.clear();
        repaint();
    }

    public void setColor(Color c) {
        this.stroke = c;
        this.fill = new Color(c.getRed(), c.getGreen(), c.getBlue(), 40);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (samples.size() < 2) return;

        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        double min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
        for (double v : samples) { min = Math.min(min, v); max = Math.max(max, v); }
        if (max - min < 0.001) { min -= 1; max += 1; }

        double stepX = (double) w / (samples.size() - 1);
        Path2D line = new Path2D.Double();
        Path2D area = new Path2D.Double();
        int i = 0;
        for (double v : samples) {
            double x = i * stepX;
            double y = h - ((v - min) / (max - min)) * (h - 4) - 2;
            if (i == 0) {
                line.moveTo(x, y);
                area.moveTo(x, h);
                area.lineTo(x, y);
            } else {
                line.lineTo(x, y);
                area.lineTo(x, y);
            }
            i++;
        }
        area.lineTo(w, h);
        area.closePath();

        g.setColor(fill);
        g.fill(area);
        g.setColor(stroke);
        g.setStroke(new BasicStroke(1.6f));
        g.draw(line);
        g.dispose();
    }
}
