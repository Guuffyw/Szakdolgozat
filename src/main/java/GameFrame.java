import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    public final Database db = new Database();
    public int currentPlayerId = -1;

    private CardLayout layout;
    private JPanel container;

    private HubPanel hubPanel;
    private NonogrammPanel nonogrammPanel;

    public GameFrame() {

        layout = new CardLayout();
        container = new JPanel(layout);

        hubPanel = new HubPanel(this);

        NonogrammLogic logic = new NonogrammLogic(5,3);
        nonogrammPanel = new NonogrammPanel(this, logic);

        container.add(hubPanel, "hub");
        container.add(nonogrammPanel, "nonogramm");

        layout.show(container, "hub");

        add(container);

        setTitle("APPLICATION WINDOW");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public void startNewNonogram(int size) {

        switch (size) {
            case 5:setSize(800, 600);break;
            case 10:setSize(1000, 700);break;
            case 15: setSize(1300, 1300);break;
        }

        setLocationRelativeTo(null);
        container.removeAll();

        HubPanel hubPanel = new HubPanel(this);
        container.add(hubPanel, "hub");

        NonogrammPanel nonogrammPanel =
                new NonogrammPanel(this, new NonogrammLogic(size, 3));
        container.add(nonogrammPanel, "nonogramm");

        layout.show(container, "nonogramm");
        revalidate();
        repaint();
    }

    @Override
    public void dispose() {
        db.close();
        super.dispose();
    }


    public void showHub() {
        hubPanel.refreshCards();
        layout.show(container, "hub");
        setSize(800, 600);
        setLocationRelativeTo(null);

    }
}