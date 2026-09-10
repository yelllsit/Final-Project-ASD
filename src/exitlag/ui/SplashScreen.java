package exitlag.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Startup splash: a rounded window with the app name and a progress bar that
 * fills over the given duration, then disposes itself and calls the onDone
 * callback so MainWindow can be shown.
 */
public class SplashScreen extends JWindow {

    private float progress = 0f;
    private final JLabel status;

    public SplashScreen(int durationMs, Runnable onDone) {
        setSize(420, 220);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));

        JPanel content = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D bg = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 22, 22);
                g.setPaint(new GradientPaint(0, 0, new Color(22, 26, 38),
                        0, getHeight(), new Color(30, 40, 60)));
                g.fill(bg);

                int barX = 30, barY = getHeight() - 46, barW = getWidth() - 60, barH = 8;
                g.setColor(new Color(60, 72, 96));
                g.fillRoundRect(barX, barY, barW, barH, 6, 6);
                g.setPaint(new GradientPaint(barX, barY, new Color(72, 220, 140),
                        barX + barW, barY, new Color(90, 170, 255)));
                g.fillRoundRect(barX, barY, (int) (barW * progress), barH, 6, 6);
                g.dispose();
            }
        };
        content.setOpaque(false);
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(38, 30, 30, 30));

        JLabel title = new JLabel("FiveM Booster", SwingConstants.LEFT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setForeground(new Color(240, 245, 255));

        JLabel sub = new JLabel("ExitLag-style route optimizer", SwingConstants.LEFT);
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 13f));
        sub.setForeground(new Color(150, 170, 200));

        status = new JLabel("Warming up relays...", SwingConstants.LEFT);
        status.setFont(status.getFont().deriveFont(Font.PLAIN, 11f));
        status.setForeground(new Color(140, 160, 190));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new javax.swing.BoxLayout(top, javax.swing.BoxLayout.Y_AXIS));
        top.add(title);
        top.add(javax.swing.Box.createVerticalStrut(2));
        top.add(sub);
        content.add(top, BorderLayout.NORTH);
        content.add(status, BorderLayout.SOUTH);
        setContentPane(content);

        String[] steps = {
                "Warming up relays...",
                "Probing SEA backbone...",
                "Measuring baseline ping...",
                "Ready."
        };
        Timer t = new Timer(durationMs / 60, null);
        long start = System.currentTimeMillis();
        t.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - start;
            progress = Math.min(1f, elapsed / (float) durationMs);
            int idx = Math.min(steps.length - 1, (int) (progress * steps.length));
            status.setText(steps[idx]);
            repaint();
            if (progress >= 1f) {
                t.stop();
                dispose();
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }
}
