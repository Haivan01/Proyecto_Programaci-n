import view.Login;

import javax.swing.*;

/**
 * Punto de entrada. Lanza la ventana de Login en el EDT.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* tema por defecto */ }

        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}

