import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NonogrammPanel extends JPanel {

    private final GameFrame frame;
    private final NonogrammLogic logic;

    private JLabel health;
    private JButton[][] buttons;

    public String selectedDiff = "Easy";
    private JPanel leaderboardRows;

    private Timer gameTimer;
    private int secondsElapsed = 0;
    private JLabel timerLabel;

    private final Color NAV_CLR = new Color(0x30302e);
    private final Color BG_CLR = new Color(0x141413);
    private final Color CREAMY = new Color(0xFDFBD4);
    private static final Color BG_END_CLR = new Color(0x1d1c1a);
    private static final Color BORDER_CLR = new Color(0x3d3d3a);
    private static final Color MUTED_CLR = new Color(0xa8a8a0);
    private static final Color CLUE_CLR = new Color(0xe4e4dc);
    private static final Color ACCENT_CLR = new Color(0xd97757);
    private static final Color ACCENT_HOVER_CLR = new Color(0xe58a6d);
    private static final Color GOLD_CLR = new Color(0xf5c451);
    private static final Color SILVER_CLR = new Color(0xc9c9c2);
    private static final Color BRONZE_CLR = new Color(0xcd8b5a);
    private static final Color WRONG_CLR = new Color(0xe5534b);
    private static final Color GHOST_CLR = new Color(255, 255, 255, 0);
    private static final Color GHOST_HOVER_CLR = new Color(255, 255, 255, 28);

    public NonogrammPanel(GameFrame frame,NonogrammLogic logic) {
        this.frame = frame;
        this.logic = logic;
        setLayout(new BorderLayout());
        buildPanel();
    }

    public void buildPanel()
    {
        setBackground(CREAMY);

        //---------------- TOP PANEL ----------------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(NAV_CLR);
        topPanel.setPreferredSize(new Dimension(0, 56));
        topPanel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER_CLR));

        JLabel title = new JLabel("NONOGRAMM", new LogoIcon(), SwingConstants.CENTER);
        title.setIconTextGap(10);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        topPanel.add(title, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);



        //---------------- MIDDLE PANEL ----------------
        int N = logic.getSize();
        buttons = new JButton[N][N];

        JPanel gridPanel = new JPanel(new GridLayout(N + 1, N + 1,4,4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_CLR, 0, getHeight(), BG_END_CLR));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        gridPanel.setBackground(BG_CLR);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));


        for (int i = 0; i < N + 1; i++) {
            for (int j = 0; j < N + 1; j++) {
                if (i == 0 && j == 0) {
                    gridPanel.add(new JLabel(""));
                } else if (i == 0) {
                    String clue = logic.countColoumns(j - 1);
                    clue = "<html><center>" + clue.replace(" ", "<br>") + "</center></html>";

                    JLabel lbl = new JLabel(clue, SwingConstants.CENTER);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                    lbl.setForeground(CLUE_CLR);
                    lbl.setPreferredSize(new Dimension(30, 60));
                    lbl.setVerticalAlignment(SwingConstants.BOTTOM);

                    gridPanel.add(lbl);
                } else if (j == 0) {
                    JLabel lbl = new JLabel(logic.countRows(i-1),SwingConstants.RIGHT);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0,0,0,6));
                    lbl.setForeground(CLUE_CLR);
                    gridPanel.add(lbl);
                    lbl.setAlignmentX(RIGHT_ALIGNMENT);

                } else {
                    JButton button = new CellButton();
                    button.setBackground(Color.WHITE);
                    button.setFont(new Font("SansSerif", Font.BOLD, 13));
                    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    int finalI = i - 1;
                    int finalJ = j - 1;

                    button.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            handleClick(e, button, finalI, finalJ);
                        }
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            if (button.getBackground().equals(Color.WHITE))
                                button.setBackground(new Color(0xEEEEEE));
                        }
                        @Override
                        public void mouseExited(MouseEvent e) {
                            if (button.getBackground().equals(new Color(0xEEEEEE)))
                                button.setBackground(Color.WHITE);
                        }
                    });

                    buttons[finalI][finalJ] = button;
                    gridPanel.add(button);
                }
            }
        }
        add(gridPanel, BorderLayout.CENTER);



        //---------------- RIGHT PANEL ----------------
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(NAV_CLR);
        rightPanel.setPreferredSize(new Dimension(170, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,1,0,0,BORDER_CLR),
                BorderFactory.createEmptyBorder(0,16,0,16)));


        timerLabel = new PillLabel("Time: 0");
        timerLabel.setForeground(GOLD_CLR);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        health = new PillLabel("Health: " + logic.getHealth());
        health.setFont(new Font("SansSerif", Font.BOLD, 14));
        health.setForeground(WRONG_CLR);
        health.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loggedIn = new JLabel("Logged in as:");
        loggedIn.setForeground(MUTED_CLR);
        loggedIn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loggedIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel username = new JLabel(HubPanel.getUserName());
        username.setForeground(Color.WHITE);
        username.setFont(new Font("SansSerif", Font.BOLD, 15));
        username.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel diffLabel = new JLabel("DIFFICULTY");
        diffLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        diffLabel.setForeground(MUTED_CLR);
        diffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> difficulty = new JComboBox<>(new String[]{"Easy", "Normal", "Hard"});
        styleCombo(difficulty);
        difficulty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        difficulty.setAlignmentX(Component.LEFT_ALIGNMENT);


        JButton startBtn = new RoundedButton("\u25B6  Start", ACCENT_CLR, ACCENT_HOVER_CLR, 16);
        startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        startBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = new RoundedButton("Back to Hub", GHOST_CLR, GHOST_HOVER_CLR, 16).outlined(BORDER_CLR);
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(loggedIn);
        rightPanel.add(Box.createVerticalStrut(2));
        rightPanel.add(username);
        rightPanel.add(Box.createVerticalStrut(18));
        rightPanel.add(health);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(timerLabel);
        rightPanel.add(Box.createVerticalStrut(22));
        rightPanel.add(diffLabel);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(difficulty);
        rightPanel.add(Box.createVerticalStrut(20));
        rightPanel.add(startBtn);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(backBtn);
        rightPanel.add(Box.createVerticalGlue());
        add(rightPanel, BorderLayout.EAST);



        //---------------- LEFT PANEL ----------------
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(NAV_CLR);
        leftPanel.setPreferredSize(new Dimension(170, 0));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,0,1,BORDER_CLR),
                BorderFactory.createEmptyBorder(0,16,0,16)));

        JLabel scoreTitle = new JLabel("Leaderboard:");
        scoreTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        scoreTitle.setForeground(Color.WHITE);
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        leaderboardRows = new JPanel();
        leaderboardRows.setLayout(new BoxLayout(leaderboardRows, BoxLayout.Y_AXIS));
        leaderboardRows.setBackground(NAV_CLR);
        leaderboardRows.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardRows.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1,0,0,0,BORDER_CLR),
                BorderFactory.createEmptyBorder(12,0,0,0)));


        leftPanel.add(Box.createVerticalStrut(18));
        leftPanel.add(scoreTitle);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(leaderboardRows);
        leftPanel.add(Box.createVerticalGlue());
        add(leftPanel, BorderLayout.WEST);
        refreshLeaderboard();



        //---------------- ACTIONS ----------------

        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed);
        });
        gameTimer.start();


        backBtn.addActionListener(e -> {
            frame.showHub();
        });

        startBtn.addActionListener(e ->{
            frame.startNewNonogram(logic.setDiff(selectedDiff));
        });

        difficulty.addActionListener(e -> {
            selectedDiff = (String) difficulty.getSelectedItem();
        });



    }



    //---------------- METHODS ----------------
    private void handleClick(MouseEvent e,JButton button,int i ,int j){
        if (e.getButton() == MouseEvent.BUTTON1){
            if (logic.isReavealed(i,j)){
                return;
            } else if (button.getText().equals("X")) {
                return;
            }

            boolean isBlack = logic.revealCell(i,j);

            if (isBlack){
                button.setBackground(Color.BLACK);
            }else {
                button.setBackground(WRONG_CLR);
            }

            health.setText("Health: " + logic.getHealth());

            if (logic.isWon()){
                gameTimer.stop();
                try{
                    if (frame.currentPlayerId != -1){
                        frame.db.saveScore(frame.currentPlayerId,"Nonogramm",logic.calculateScore(secondsElapsed));
                    }
                }catch (SQLException b){
                    b.printStackTrace();
                };
                int result = JOptionPane.showConfirmDialog(this,"You won and scored: "+logic.calculateScore(secondsElapsed)+"Start a new one?","Victory",JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION){
                    frame.startNewNonogram(logic.setDiff(selectedDiff));
                    refreshLeaderboard();
                }else{
                    frame.showHub();

                }
            }

            if (logic.isDead()){
                gameTimer.stop();
                int result = JOptionPane.showConfirmDialog(this,"You have lost!\n start new game","DEFEAT",JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION){
                    frame.startNewNonogram(logic.setDiff(selectedDiff));
                }else{
                    frame.showHub();
                }
            }

        }
        else if(e.getButton() == MouseEvent.BUTTON3){
            if (logic.isReavealed(i,j)){
                return;
            }
            if (button.getText().equals("X")){
                button.setText("");
            }else {
                button.setText("X");
            }
        }
    }


    private void refreshLeaderboard() {
        leaderboardRows.removeAll();

        try {
            ResultSet rs = frame.db.getLeaderBoard("Nonogramm");
            int rank = 1;
            while (rs.next() && rank <= 10) {
                String name  = rs.getString("username");
                int    score = rs.getInt("totalScore");

                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(NAV_CLR);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel nameLabel  = new JLabel(rank + ". " + name);
                JLabel scoreLabel = new JLabel(String.valueOf(score));
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                scoreLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                nameLabel.setForeground(rank == 1 ? GOLD_CLR : rank == 2 ? SILVER_CLR : rank == 3 ? BRONZE_CLR : Color.WHITE);
                scoreLabel.setForeground(MUTED_CLR);

                row.add(nameLabel,  BorderLayout.WEST);
                row.add(scoreLabel, BorderLayout.EAST);

                leaderboardRows.add(row);
                leaderboardRows.add(Box.createVerticalStrut(6));
                rank++;
            }
        } catch (SQLException e) {
            JLabel err = new JLabel("Unavailable");
            err.setFont(new Font("SansSerif", Font.BOLD, 12));
            err.setForeground(MUTED_CLR);
            leaderboardRows.add(err);
        }

        leaderboardRows.revalidate();
        leaderboardRows.repaint();
    }


    //---------------- STYLING HELPERS ----------------
    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.BOLD, 12));
        combo.setBackground(BG_CLR);
        combo.setForeground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new BasicArrowButton(SwingConstants.SOUTH, BG_CLR, BG_CLR, MUTED_CLR, BG_CLR);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(comboBox.getBackground());
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                l.setBackground(index >= 0 && isSelected ? ACCENT_CLR : BG_CLR);
                l.setForeground(Color.WHITE);
                return l;
            }
        });
    }

    private static void smooth(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** Small rounded logo shown next to the title. */
    private static class LogoIcon implements Icon {
        public int getIconWidth()  { return 28; }
        public int getIconHeight() { return 28; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setPaint(new GradientPaint(x, y, ACCENT_HOVER_CLR, x + 28, y + 28, ACCENT_CLR));
            g2.fillRoundRect(x, y, 28, 28, 10, 10);
            // tiny 2x2 nonogram motif
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x + 6, y + 6, 7, 7, 3, 3);
            g2.fillRoundRect(x + 15, y + 15, 7, 7, 3, 3);
            g2.setColor(new Color(255, 255, 255, 110));
            g2.fillRoundRect(x + 15, y + 6, 7, 7, 3, 3);
            g2.fillRoundRect(x + 6, y + 15, 7, 7, 3, 3);
            g2.dispose();
        }
    }

    /** Puzzle cell: rounded, painted with whatever background the logic sets. */
    private static class CellButton extends JButton {
        CellButton() {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
            setForeground(ACCENT_CLR);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
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

    /** Label drawn on a soft rounded pill. */
    private static class PillLabel extends JLabel {
        PillLabel(String text) {
            super(text);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        }

        @Override
        public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(new Color(255, 255, 255, 20));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}