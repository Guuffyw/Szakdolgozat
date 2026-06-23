import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HubPanel extends JPanel {

    private final GameFrame frame;
    private JPanel cardGrid;
    private final List<GameCard> cards = new ArrayList<>();

    public HubPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        buildPanel();
    }

    private void buildPanel() {
        //Top Panel
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.BLACK);
        topBar.setPreferredSize(new Dimension(0, 50));

        JLabel title = new JLabel("Game Hub");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        topBar.add(title, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        cardGrid = new JPanel(new FlowLayout());

        addGame("Nonogramm");

        add(cardGrid, BorderLayout.CENTER);
    }

    private void addGame(String name) {
        GameCard card = new GameCard(name);
        cards.add(card);
        cardGrid.add(card);
    }

    private void launchGame(String name) {
        if (name.equals("Nonogramm")) {
            frame.startNewNonogram(5);
        }
    }

    private class GameCard extends JPanel {

        String gameName;

        public GameCard(String name) {
            this.gameName = name;

            setPreferredSize(new Dimension(180, 150));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setLayout(new BorderLayout());

            JLabel label = new JLabel(name, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 18));

            JButton playBtn = new JButton("Play");
            playBtn.addActionListener(e -> launchGame(gameName));

            add(label, BorderLayout.CENTER);
            add(playBtn, BorderLayout.SOUTH);
        }
    }
}