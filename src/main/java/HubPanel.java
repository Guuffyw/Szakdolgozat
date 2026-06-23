import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HubPanel extends JPanel {

    private final GameFrame frame;
    private JPanel cardGrid;
    private final List<GameCard> cards = new ArrayList<>();


    private final Color NAV_CLR = new Color(0x30302e);
    private final Color BG_CLR = new Color(0x141413);

    public HubPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        buildPanel();
    }

    private void buildPanel() {
        //Top Panel
        JPanel navBar = new JPanel(new BorderLayout());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(NAV_CLR);
        topBar.setPreferredSize(new Dimension(0, 50));
        JPanel top2 = new JPanel(new BorderLayout());
        top2.setBackground(NAV_CLR);
        top2.setPreferredSize(new Dimension(0, 50));

        JLabel title = new JLabel("Game Hub");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        topBar.add(title, BorderLayout.WEST);

        navBar.add(topBar, BorderLayout.NORTH);
        navBar.add(top2, BorderLayout.SOUTH);
        add(navBar, BorderLayout.NORTH);


        cardGrid = new JPanel(new FlowLayout());
        cardGrid.setBackground(BG_CLR);

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











    //Class for the small card designs
    private class GameCard extends JPanel {
        String gameName;

        public GameCard(String name) {
            this.gameName = name;

            setPreferredSize(new Dimension(180, 150));
            setBackground(NAV_CLR);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.BLACK),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            setLayout(new BorderLayout(0, 8));

            //---------------- TOP ----------------
            JPanel topPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            topPart.setBackground(NAV_CLR);

            JButton starBtn = new JButton("★");
            starBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
            starBtn.setForeground(Color.WHITE);
            starBtn.setBorderPainted(false);
            starBtn.setContentAreaFilled(false);
            starBtn.setFocusPainted(false);
            starBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            topPart.add(starBtn);

            //---------------- CENTER ----------------
            JPanel middlePart = new JPanel();
            middlePart.setBackground(NAV_CLR);
            middlePart.setLayout(new BoxLayout(middlePart, BoxLayout.Y_AXIS));

            JLabel gameLabel = new JLabel(name);
            gameLabel.setForeground(Color.WHITE);
            gameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel score = new JLabel("Score: 0");
            score.setForeground(Color.LIGHT_GRAY);
            score.setFont(new Font("SansSerif", Font.PLAIN, 14));
            score.setAlignmentX(Component.CENTER_ALIGNMENT);

            middlePart.add(Box.createVerticalGlue());
            middlePart.add(gameLabel);
            middlePart.add(Box.createVerticalStrut(10));
            middlePart.add(score);
            middlePart.add(Box.createVerticalGlue());

            //---------------- BOTTOM ----------------
            JButton playBtn = new JButton("Play");
            playBtn.setForeground(Color.WHITE);
            playBtn.setBackground(new Color(70, 130, 180));
            playBtn.setFocusPainted(false);
            playBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            playBtn.addActionListener(e -> launchGame(gameName));

            add(topPart, BorderLayout.NORTH);
            add(middlePart, BorderLayout.CENTER);
            add(playBtn, BorderLayout.SOUTH);
        }
    }
}