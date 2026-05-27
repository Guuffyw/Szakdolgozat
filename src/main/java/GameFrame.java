import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private CardLayout layout;
    private JPanel container;

    public GameFrame() {

        // create layout
        layout = new CardLayout();

        // container that holds screens
        container = new JPanel(layout);

        // add screens
        container.add(new HubPanel(this), "hub");
        container.add(new NonogrammPanel(this), "nonogramm");

        // show starting screen
        layout.show(container, "hub");

        // frame setup
        add(container);

        setTitle("APPLICATION WINDOW");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // helper methods for switching screens
    public void showGame() {
        layout.show(container, "nonogramm");
    }

    public void showHub() {
        layout.show(container, "hub");
    }
}