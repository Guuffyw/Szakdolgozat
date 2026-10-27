import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new GameFrame();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Could not open the database (nonogram.db).",
                        "Database error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}