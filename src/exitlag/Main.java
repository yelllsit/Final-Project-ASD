package exitlag;

import exitlag.ui.MainWindow;
import exitlag.ui.SplashScreen;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen(1600, () -> new MainWindow().setVisible(true));
            splash.setVisible(true);
        });
    }
}
