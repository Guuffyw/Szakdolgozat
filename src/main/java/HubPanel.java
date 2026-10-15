import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HubPanel extends JPanel {
    private final GameFrame frame;
    public   JPanel cardGrid;
    private final List<GameCard> cards = new ArrayList<>();
    private static JLabel UserName = new JLabel("Not logged in yet");
    private JButton loginButton;
    private boolean favToggle = true;
    private JTextField searchBar;



    private final Color NAV_CLR = new Color(0x30302e);
    private final Color BG_CLR = new Color(0x141413);
    private static final Color BG_END_CLR = new Color(0x1d1c1a);
    private static final Color CARD_TOP_CLR = new Color(0x383836);
    private static final Color CARD_BOTTOM_CLR = new Color(0x2c2c2a);
    private static final Color BORDER_CLR = new Color(0x3d3d3a);
    private static final Color MUTED_CLR = new Color(0xa8a8a0);
    private static final Color ACCENT_CLR = new Color(0xd97757);
    private static final Color ACCENT_HOVER_CLR = new Color(0xe58a6d);
    private static final Color GOLD_CLR = new Color(0xf5c451);
    private static final Color GHOST_CLR = new Color(255, 255, 255, 0);
    private static final Color GHOST_HOVER_CLR = new Color(255, 255, 255, 28);

    // badge gradients (start, end) picked per game name
    private static final Color[][] BADGES = {
            {new Color(0xe58a6d), new Color(0xc4553a)},
            {new Color(0x7c9cff), new Color(0x4b63d6)},
            {new Color(0x5fd0a0), new Color(0x2f9a72)},
            {new Color(0xb68cf5), new Color(0x7c4fd0)}
    };

    public HubPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());
        buildPanel();
    }

    private void buildPanel() {
        //---------------- NAVIGATION BAR ----------------
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(NAV_CLR);
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(NAV_CLR);
        topRow.setPreferredSize(new Dimension(0, 60));
        JPanel buttomRow = new JPanel(new GridBagLayout());
        buttomRow.setBackground(NAV_CLR);
        buttomRow.setPreferredSize(new Dimension(0, 60));


        JPanel leftSpacer = new JPanel();
        leftSpacer.setBackground(NAV_CLR);
        leftSpacer.setPreferredSize(new Dimension(300, 60));

        JLabel title = new JLabel("Game Hub", new LogoIcon(), SwingConstants.CENTER);
        title.setIconTextGap(10);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 14));
        rightPanel.setBackground(NAV_CLR);
        rightPanel.setPreferredSize(new Dimension(300, 60));

        searchBar = new SearchField("Search games...", BG_CLR);
        searchBar.setForeground(Color.WHITE);
        searchBar.setCaretColor(Color.WHITE);
        searchBar.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        JButton favourites = new RoundedButton("\u2605 Favourites", GHOST_CLR, GHOST_HOVER_CLR, 999)
                .outlined(BORDER_CLR);
        favourites.setFont(new Font("SansSerif", Font.PLAIN, 14));
        GridBagConstraints gBC = new GridBagConstraints();
        gBC.fill = GridBagConstraints.HORIZONTAL;
        gBC.gridy = 0;

        UserName = new JLabel(getUserName());
        UserName.setForeground(MUTED_CLR);
        UserName.setFont(new Font("SansSerif", Font.PLAIN, 13));

        loginButton = new RoundedButton("Login", GHOST_CLR, GHOST_HOVER_CLR, 999).outlined(BORDER_CLR);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 13));


        rightPanel.add(UserName);
        rightPanel.add(loginButton);
        topRow.add(leftSpacer, BorderLayout.WEST);
        topRow.add(title, BorderLayout.CENTER);
        topRow.add(rightPanel, BorderLayout.EAST);
        gBC.gridx = 0;
        gBC.weightx = 0.75;
        gBC.ipady = 4;
        gBC.insets = new Insets(0, 20, 14, 10);
        buttomRow.add(searchBar, gBC);
        gBC.gridx = 1;
        gBC.weightx = 0.25;
        gBC.insets = new Insets(0, 0, 14, 20);
        buttomRow.add(favourites, gBC);
        navBar.add(topRow, BorderLayout.NORTH);
        navBar.add(buttomRow, BorderLayout.SOUTH);
        add(navBar, BorderLayout.NORTH);
        //---------------- ACTIONS ----------------
        loginButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter username");


            if (input != null && !input.trim().isEmpty()) {
                input.toLowerCase().trim();
                input = input.substring(0, 1).toUpperCase() + input.substring(1);

                UserName.setText(input);
                loginButton.setText("Change User");
                try {
                    frame.currentPlayerId = frame.db.getOrCreatePlayer(input);
                    for (GameCard card : cards){
                        int newScore = frame.db.getTotalScore(frame.currentPlayerId,card.gameName);
                        card.updateScore(newScore);
                        Filter();
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        searchBar.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){
                Filter();
            }
            public void removeUpdate(DocumentEvent e){
                Filter();
            }
            public void changedUpdate(DocumentEvent e){
                Filter();
            }
        });

        favourites.addActionListener(e -> {
            favToggle = !favToggle;
            Filter();
        });

        //---------------- CARD BAR ----------------
        cardGrid = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 24)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_CLR, 0, getHeight(), BG_END_CLR));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        cardGrid.setBackground(BG_CLR);

        //-------- GAMES --------
        addGame("Nonogramm",false);
        addGame("Sodoku",false);

        add(cardGrid, BorderLayout.CENTER);
    }


    //---------------- METHODS ----------------
    private void addGame(String name, boolean fav) {
        GameCard card = new GameCard(name,fav);
        cards.add(card);
        cardGrid.add(card);
    }



    //---------------- METHODS(NEW GAMES GO HERE) ----------------
    private void launchGame(String name) {
        if (name.equals("Nonogramm")) {
            frame.startNewNonogram(5);
        } else if (name.equals("Sodoku")) {
            frame.startNewSodoku(1);
        }
    }

    public static String getUserName(){
        return UserName.getText();
    }

    public void Filter(){
        String query = searchBar.getText().toLowerCase().trim();

        for(GameCard card :cards){
            boolean nameMatch = card.gameName.toLowerCase().contains(query);
            boolean favMatch = !favToggle || card.isFavorited;
            card.setVisible(nameMatch && favMatch);

        }
        cardGrid.revalidate();
        cardGrid.repaint();
    }
    public void refreshCards() {
        try {
            for (GameCard card : cards) {
                int score = frame.db.getTotalScore(
                        frame.currentPlayerId,
                        card.gameName);

                card.updateScore(score);
            }

            cardGrid.revalidate();
            cardGrid.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //---------------- STYLED COMPONENTS ----------------
    private static void smooth(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** Small rounded logo shown next to the title. */
    private static class LogoIcon implements Icon {
        public int getIconWidth()  { return 30; }
        public int getIconHeight() { return 30; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setPaint(new GradientPaint(x, y, ACCENT_HOVER_CLR, x + 30, y + 30, ACCENT_CLR));
            g2.fillRoundRect(x, y, 30, 30, 10, 10);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 17));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("G", x + (30 - fm.stringWidth("G")) / 2, y + (30 + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    /** Flat button with rounded corners, hover colour and optional outline. */
    private static class RoundedButton extends JButton {
        private final Color base;
        private final Color hover;
        private final int arc;
        private Color outline;
        private boolean hovered;

        RoundedButton(String text, Color base, Color hover, int arc) {
            super(text);
            this.base = base;
            this.hover = hover;
            this.arc = arc;
            setForeground(Color.WHITE);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
            });
        }

        RoundedButton outlined(Color c) {
            this.outline = c;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(hovered ? hover : base);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            if (outline != null) {
                g2.setColor(outline);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Pill-shaped search field with magnifier icon, placeholder and accent border on focus. */
    private static class SearchField extends JTextField {
        private final String placeholder;
        private final Color fill;

        SearchField(String placeholder, Color fill) {
            this.placeholder = placeholder;
            this.fill = fill;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 42, 8, 18));
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e) { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.setColor(hasFocus() ? ACCENT_CLR : BORDER_CLR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());

            // magnifier icon
            int cy = getHeight() / 2;
            g2.setColor(MUTED_CLR);
            g2.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(18, cy - 7, 10, 10);
            g2.drawLine(26, cy + 1, 30, cy + 5);

            if (getText().isEmpty()) {
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(placeholder, getInsets().left, y);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Round gradient badge showing the first letter of a game. */
    private static class Badge extends JComponent {
        private final String letter;
        private final Color[] colors;

        Badge(String name) {
            this.letter = name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase();
            this.colors = BADGES[Math.abs(name.hashCode()) % BADGES.length];
            setPreferredSize(new Dimension(42, 42));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setPaint(new GradientPaint(0, 0, colors[0], getWidth(), getHeight(), colors[1]));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(letter, (getWidth() - fm.stringWidth(letter)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    /** Label drawn on a soft rounded pill. */
    private static class PillLabel extends JLabel {
        PillLabel(String text) {
            super(text);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }


    //---------------- CARD DESIGN ----------------
    private class GameCard extends JPanel {
        String gameName;
        boolean isFavorited;

        private final JButton starBtn;
        private JLabel score;
        private boolean hovered;

        public GameCard(String name, boolean isFavorited) {
            this.gameName = name;
            this.isFavorited = isFavorited;
            setPreferredSize(new Dimension(220, 200));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
            setLayout(new BorderLayout(0, 8));

            //---------------- TOP ----------------
            JPanel topPart = new JPanel(new BorderLayout());
            topPart.setOpaque(false);

            starBtn = new RoundedButton(isFavorited ? "\u2605" : "\u2606", GHOST_CLR, GHOST_HOVER_CLR, 999);
            starBtn.setFont(new Font("SansSerif", Font.PLAIN, 18));
            starBtn.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            starBtn.setForeground(isFavorited ? GOLD_CLR : MUTED_CLR);

            JPanel starWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            starWrap.setOpaque(false);
            starWrap.add(starBtn);

            topPart.add(new Badge(name), BorderLayout.WEST);
            topPart.add(starWrap, BorderLayout.EAST);

            //---------------- CENTER ----------------
            JPanel middlePart = new JPanel();
            middlePart.setOpaque(false);
            middlePart.setLayout(new BoxLayout(middlePart, BoxLayout.Y_AXIS));

            JLabel gameLabel = new JLabel(name);
            gameLabel.setForeground(Color.WHITE);
            gameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            gameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            score = new PillLabel("Score: 0");
            score.setForeground(MUTED_CLR);
            score.setFont(new Font("SansSerif", Font.PLAIN, 12));
            score.setAlignmentX(Component.LEFT_ALIGNMENT);

            middlePart.add(Box.createVerticalGlue());
            middlePart.add(gameLabel);
            middlePart.add(Box.createVerticalStrut(6));
            middlePart.add(score);
            middlePart.add(Box.createVerticalGlue());

            //---------------- BOTTOM ----------------
            JButton playBtn = new RoundedButton("\u25B6  Play", ACCENT_CLR, ACCENT_HOVER_CLR, 16);
            playBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            playBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            playBtn.addActionListener(e -> launchGame(gameName));

            add(topPart, BorderLayout.NORTH);
            add(middlePart, BorderLayout.CENTER);
            add(playBtn, BorderLayout.SOUTH);

            //---------------- ACTIONS ----------------
            starBtn.addActionListener(e -> {
                toggleFav();
            });

            attachHover(this);
        }

        // highlights the card while the mouse is anywhere over it (including children)
        private void attachHover(Component c) {
            c.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { setHover(true); }
                @Override public void mouseExited(MouseEvent e) {
                    Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), GameCard.this);
                    if (!GameCard.this.contains(p)) setHover(false);
                }
            });
            if (c instanceof Container) {
                for (Component child : ((Container) c).getComponents()) attachHover(child);
            }
        }

        private void setHover(boolean value) {
            if (hovered != value) {
                hovered = value;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            int x = 6, y = 4, w = getWidth() - 12, h = getHeight() - 12, arc = 22;

            // soft shadow
            for (int i = 0; i < 6; i++) {
                g2.setColor(new Color(0, 0, 0, 16));
                g2.fillRoundRect(x - i, y - i + 4, w + 2 * i, h + 2 * i, arc + 2 * i, arc + 2 * i);
            }
            // body
            g2.setPaint(new GradientPaint(0, y, hovered ? CARD_TOP_CLR.brighter() : CARD_TOP_CLR,
                    0, y + h, CARD_BOTTOM_CLR));
            g2.fillRoundRect(x, y, w, h, arc, arc);
            // border
            g2.setColor(hovered ? ACCENT_CLR : BORDER_CLR);
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }

        private void toggleFav(){
            isFavorited = !isFavorited;
            starBtn.setText(isFavorited ? "\u2605" : "\u2606");
            starBtn.setForeground(isFavorited ? GOLD_CLR : MUTED_CLR);
            cardGrid.revalidate();
            cardGrid.repaint();
        }

        private void updateScore(int updatedscore){
            score.setText("Score: " + updatedscore);
        }
    }
}