package exitlag.ui;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

/**
 * A small round indicator that pulses when connected. Green = connected,
 * gray = idle. The pulse is a soft opacity oscillation on a ring drawn
 * around the LED.
 */
public class PingLed extends JPanel {

    private boolean connected = false;
    private float pulse = 0f;
    private final Timer timer;

    public PingLed() {
        setOpaque(false);
        setPreferredSize(new Dimension(20, 20));
        timer = new Timer(60, e -> { pulse = (pulse + 0.06f) % 1f; repaint(); });
    }

    public void setConnected(boolean v) {
        if (v == connected) return;
        connected = v;
        if (connected) timer.start(); else { timer.stop(); pulse = 0; }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2, cy = getHeight() / 2;
        int r = 5;
        Color base = connected ? new Color(72, 220, 140) : new Color(120, 130, 150);

        if (connected) {
            float alpha = (float) (0.35 * (1 - pulse));
            int ringR = (int) (r + 4 + pulse * 8);
            Color halo = new Color(base.getRed(), base.getGreen(), base.getBlue(),
                    Math.max(0, Math.min(255, (int) (alpha * 255))));
            g.setColor(halo);
            g.fillOval(cx - ringR, cy - ringR, ringR * 2, ringR * 2);
        }

        RadialGradientPaint core = new RadialGradientPaint(
                new Point2D.Float(cx - 1, cy - 1), r + 1,
                new float[]{0f, 1f},
                new Color[]{base.brighter(), base});
        g.setPaint(core);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(0, 0, 0, 80));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.dispose();
    }
}
