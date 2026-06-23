import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HubPanel extends JPanel {
    private final GameFrame frame;
    private JPanel cardGrid;
    private final List<GameCard> cards = new ArrayList<>();
    private static JLabel UserName = new JLabel("Not logged in yet");
    private JButton loginButton;


    private final Color NAV_CLR = new Color(0x30302e);
    private final Color BG_CLR = new Color(0x141413);

    public HubPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        buildPanel();
    }

    private void buildPanel() {
        //---------------- NAVIGATION BAR ----------------
        JPanel navBar = new JPanel(new BorderLayout());

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(NAV_CLR);
        topRow.setPreferredSize(new Dimension(0, 50));
        topRow.setBorder(BorderFactory.createMatteBorder(0,0,2,0,BG_CLR));
        JPanel buttomRow = new JPanel(new BorderLayout());
        buttomRow.setBackground(NAV_CLR);
        buttomRow.setPreferredSize(new Dimension(0, 50));

        JPanel leftSpacer = new JPanel();
        leftSpacer.setBackground(NAV_CLR);
        leftSpacer.setPreferredSize(new Dimension(200, 50));

        JLabel title = new JLabel("Game Hub", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightPanel.setBackground(NAV_CLR);
        rightPanel.setPreferredSize(new Dimension(200, 50));


        UserName = new JLabel(getUserName());
        UserName.setForeground(Color.WHITE);

        loginButton = new JButton( "Login");

        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        rightPanel.add(UserName);
        rightPanel.add(loginButton);
        topRow.add(leftSpacer, BorderLayout.WEST);
        topRow.add(title, BorderLayout.CENTER);
        topRow.add(rightPanel, BorderLayout.EAST);
        navBar.add(topRow, BorderLayout.NORTH);
        navBar.add(buttomRow, BorderLayout.SOUTH);
        add(navBar, BorderLayout.NORTH);
        //---------------- ACTIONS ----------------
        loginButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter username");

            if (input != null && !input.trim().isEmpty()) {
                UserName.setText(input);
                loginButton.setText("Change User");
                try {
                    frame.currentPlayerId = frame.db.getOrCreatePlayer(input);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        //---------------- CARD BAR ----------------
        cardGrid = new JPanel(new FlowLayout());
        cardGrid.setBackground(BG_CLR);

        //-------- GAMES --------
        addGame("Nonogramm");
        addGame("Nonogramm");
        addGame("Nonogramm");
        addGame("Nonogramm");
        addGame("Nonogramm");

        add(cardGrid, BorderLayout.CENTER);
    }


    //---------------- METHODS ----------------
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

    public static String getUserName(){
        return UserName.getText();
    }






    //---------------- CARD DESIGN ----------------
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
            playBtn.setBackground(BG_CLR);
            playBtn.setFocusPainted(false);
            playBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            playBtn.addActionListener(e -> launchGame(gameName));

            add(topPart, BorderLayout.NORTH);
            add(middlePart, BorderLayout.CENTER);
            add(playBtn, BorderLayout.SOUTH);
        }
    }
}