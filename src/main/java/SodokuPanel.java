import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SodokuPanel extends JPanel {

    private final GameFrame frame;
    private final SodokuLogic logic;

    private JLabel health;
    private CellButton[][] buttons;
    private JButton[] padButtons;

    public String selectedDiff = "Easy";
    private JPanel leaderboardRows;

    private Timer gameTimer;
    private int secondsElapsed = 0;
    private JLabel timerLabel;

    // selected cell and the short "wrong number" flash
    private int selR = -1, selC = -1;
    private int wrongR = -1, wrongC = -1, wrongVal = 0;

    private final Color NAV_CLR = new Color(0x30302e);
    private final Color BG_CLR = new Color(0x141413);
    private static final Color BG_END_CLR = new Color(0x1d1c1a);
    private static final Color BORDER_CLR = new Color(0x3d3d3a);
    private static final Color MUTED_CLR = new Color(0xa8a8a0);
    private static final Color ACCENT_CLR = new Color(0xd97757);
    private static final Color ACCENT_HOVER_CLR = new Color(0xe58a6d);
    private static final Color GOLD_CLR = new Color(0xf5c451);
    private static final Color SILVER_CLR = new Color(0xc9c9c2);
    private static final Color BRONZE_CLR = new Color(0xcd8b5a);
    private static final Color WRONG_CLR = new Color(0xe5534b);
    private static final Color GHOST_CLR = new Color(255, 255, 255, 0);
    private static final Color GHOST_HOVER_CLR = new Color(255, 255, 255, 28);

    // cell colours
    private static final Color CELL_TXT_CLR = new Color(0x1f1f1d);
    private static final Color USER_TXT_CLR = new Color(0xb4482a);
    private static final Color SEL_BG_CLR = new Color(0xffd9c6);
    private static final Color SAME_BG_CLR = new Color(0xfbe6da);
    private static final Color PEER_BG_CLR = new Color(0xeeece3);

    public SodokuPanel(GameFrame frame, SodokuLogic logic) {
        this.frame = frame;
        this.logic = logic;
        setLayout(new BorderLayout());
        buildPanel();
    }

    public void buildPanel()
    {
        setFocusable(true);

        //---------------- TOP PANEL ----------------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(NAV_CLR);
        topPanel.setPreferredSize(new Dimension(0, 56));
        topPanel.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER_CLR));

        JLabel title = new JLabel("SODOKU", new LogoIcon(), SwingConstants.CENTER);
        title.setIconTextGap(10);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        topPanel.add(title, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);



        //---------------- MIDDLE PANEL ----------------
        int N = logic.getSize();
        buttons = new CellButton[N][N];
        padButtons = new JButton[N];

        JPanel centerPanel = new JPanel(new BoardLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BG_CLR, 0, getHeight(), BG_END_CLR));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        centerPanel.setBackground(BG_CLR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        // 3x3 boxes, each box holds 3x3 cells
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 8, 8));
        boardPanel.setOpaque(false);
        BoxPanel[] boxes = new BoxPanel[9];
        for (int b = 0; b < 9; b++) {
            boxes[b] = new BoxPanel();
            boardPanel.add(boxes[b]);
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                CellButton button = new CellButton();
                button.setFont(new Font("SansSerif", Font.BOLD, 20));
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.setFocusable(false);

                int finalI = i;
                int finalJ = j;
                button.addActionListener(e -> {
                    selR = finalI;
                    selC = finalJ;
                    refreshCells();
                    requestFocusInWindow();
                });

                buttons[i][j] = button;
                boxes[(i / 3) * 3 + (j / 3)].add(button);
            }
        }

        // number pad under the board
        JPanel padRow = new JPanel(new GridLayout(1, 9, 6, 0));
        padRow.setOpaque(false);
        for (int d = 1; d <= 9; d++) {
            int digit = d;
            JButton pad = new RoundedButton(String.valueOf(d), new Color(255, 255, 255, 18), ACCENT_CLR, 14)
                    .outlined(BORDER_CLR);
            pad.setFont(new Font("SansSerif", Font.BOLD, 16));
            pad.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            pad.setFocusable(false);
            pad.addActionListener(e -> {
                handleEntry(digit);
                requestFocusInWindow();
            });
            padButtons[d - 1] = pad;
            padRow.add(pad);
        }

        centerPanel.add(boardPanel);
        centerPanel.add(padRow);
        add(centerPanel, BorderLayout.CENTER);



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
        refreshCells();
        refreshPad();



        //---------------- ACTIONS ----------------

        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed);
        });
        gameTimer.start();


        backBtn.addActionListener(e -> {
            gameTimer.stop();
            frame.showHub();
        });

        startBtn.addActionListener(e ->{
            gameTimer.stop();
            frame.startNewSodoku(logic.setDiff(selectedDiff));
        });

        difficulty.addActionListener(e -> {
            selectedDiff = (String) difficulty.getSelectedItem();
        });

        bindKeys();
    }



    //---------------- METHODS ----------------
    private void handleEntry(int value){
        if (selR < 0){
            return;
        }
        if (logic.isFilled(selR, selC)){
            return;
        }
        if (logic.isDead() || logic.isWon()){
            return;
        }

        boolean isCorrect = logic.placeNumber(selR, selC, value);

        if (!isCorrect){
            wrongR = selR;
            wrongC = selC;
            wrongVal = value;
            Timer flash = new Timer(600, ev -> {
                wrongR = -1;
                wrongC = -1;
                refreshCells();
            });
            flash.setRepeats(false);
            flash.start();
        }

        health.setText("Health: " + logic.getHealth());
        refreshCells();
        refreshPad();

        if (logic.isWon()){
            gameTimer.stop();
            try{
                if (frame.currentPlayerId != -1){
                    frame.db.saveScore(frame.currentPlayerId,"Sodoku",logic.calculateScore(secondsElapsed));
                }
            }catch (SQLException b){
                b.printStackTrace();
            }
            refreshLeaderboard();
            int result = JOptionPane.showConfirmDialog(this,"You won and scored: "+logic.calculateScore(secondsElapsed)+"\nStart a new one?","Victory",JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION){
                frame.startNewSodoku(logic.setDiff(selectedDiff));
            }else{
                frame.showHub();
            }
        } else if (logic.isDead()){
            gameTimer.stop();
            int result = JOptionPane.showConfirmDialog(this,"You have lost!\nStart new game?","DEFEAT",JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION){
                frame.startNewSodoku(logic.setDiff(selectedDiff));
            }else{
                frame.showHub();
            }
        }
    }

    // paints every cell from the current state of the logic + selection
    private void refreshCells(){
        int selVal = (selR >= 0) ? logic.getValue(selR, selC) : 0;

        for (int i = 0; i < buttons.length; i++){
            for (int j = 0; j < buttons[i].length; j++){
                CellButton b = buttons[i][j];
                int v = logic.getValue(i, j);
                boolean isSel = (i == selR && j == selC);
                boolean isWrong = (i == wrongR && j == wrongC && v == 0);

                String text = (v == 0) ? "" : String.valueOf(v);
                Color bg = Color.WHITE;
                Color fg = CELL_TXT_CLR;

                if (isWrong){
                    text = String.valueOf(wrongVal);
                    bg = WRONG_CLR;
                    fg = Color.WHITE;
                } else {
                    if (v != 0){
                        fg = logic.isGiven(i, j) ? CELL_TXT_CLR : USER_TXT_CLR;
                    }
                    if (isSel){
                        bg = SEL_BG_CLR;
                    } else if (selVal != 0 && v == selVal){
                        bg = SAME_BG_CLR;
                    } else if (selR >= 0 && isPeer(i, j, selR, selC)){
                        bg = PEER_BG_CLR;
                    }
                }

                b.setText(text);
                b.setBackground(bg);
                b.setForeground(fg);
                b.setSelectedCell(isSel);
            }
        }
    }

    // disables a pad button once that digit is on the board nine times
    private void refreshPad(){
        for (int d = 1; d <= 9; d++){
            padButtons[d - 1].setEnabled(logic.countOf(d) < 9);
        }
    }

    private boolean isPeer(int i, int j, int r, int c){
        return i == r || j == c || (i / 3 == r / 3 && j / 3 == c / 3);
    }

    private void moveSelection(int dr, int dc){
        if (selR < 0){
            selR = 0;
            selC = 0;
        } else {
            selR = Math.max(0, Math.min(8, selR + dr));
            selC = Math.max(0, Math.min(8, selC + dc));
        }
        refreshCells();
    }

    // keyboard: 1-9 to enter a number, arrow keys to move
    private void bindKeys(){
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();

        for (int d = 1; d <= 9; d++){
            int digit = d;
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + d, 0), "digit" + d);
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + d, 0), "digit" + d);
            am.put("digit" + d, new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) { handleEntry(digit); }
            });
        }

        int[][] arrows = {{KeyEvent.VK_UP, -1, 0}, {KeyEvent.VK_DOWN, 1, 0},
                {KeyEvent.VK_LEFT, 0, -1}, {KeyEvent.VK_RIGHT, 0, 1}};
        for (int[] a : arrows){
            int dr = a[1];
            int dc = a[2];
            im.put(KeyStroke.getKeyStroke(a[0], 0), "move" + a[0]);
            am.put("move" + a[0], new AbstractAction() {
                @Override public void actionPerformed(ActionEvent e) { moveSelection(dr, dc); }
            });
        }
    }


    private void refreshLeaderboard() {
        leaderboardRows.removeAll();

        try {
            ResultSet rs = frame.db.getLeaderBoard("Sodoku");
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

    /** Lays out a square board with the number pad underneath, both centered. */
    private static class BoardLayout implements LayoutManager {
        private static final int PAD_H = 44;
        private static final int GAP = 16;

        public void addLayoutComponent(String name, Component comp) {}
        public void removeLayoutComponent(Component comp) {}

        public Dimension preferredLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            return new Dimension(420 + in.left + in.right, 420 + GAP + PAD_H + in.top + in.bottom);
        }

        public Dimension minimumLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            return new Dimension(270 + in.left + in.right, 270 + GAP + PAD_H + in.top + in.bottom);
        }

        public void layoutContainer(Container parent) {
            Insets in = parent.getInsets();
            int w = parent.getWidth() - in.left - in.right;
            int h = parent.getHeight() - in.top - in.bottom;
            int s = Math.max(0, Math.min(w, h - GAP - PAD_H));
            int x = in.left + (w - s) / 2;
            int y = in.top + (h - (s + GAP + PAD_H)) / 2;

            parent.getComponent(0).setBounds(x, y, s, s);
            parent.getComponent(1).setBounds(x, y + s + GAP, s, PAD_H);
        }
    }

    /** Faint rounded card behind each 3x3 box. */
    private static class BoxPanel extends JPanel {
        BoxPanel() {
            super(new GridLayout(3, 3, 3, 3));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(new Color(255, 255, 255, 14));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setColor(BORDER_CLR);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Small rounded logo shown next to the title (3x3 motif). */
    private static class LogoIcon implements Icon {
        public int getIconWidth()  { return 28; }
        public int getIconHeight() { return 28; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setPaint(new GradientPaint(x, y, ACCENT_HOVER_CLR, x + 28, y + 28, ACCENT_CLR));
            g2.fillRoundRect(x, y, 28, 28, 10, 10);
            for (int r = 0; r < 3; r++) {
                for (int col = 0; col < 3; col++) {
                    boolean center = (r == 1 && col == 1);
                    g2.setColor(center ? Color.WHITE : new Color(255, 255, 255, 120));
                    g2.fillRoundRect(x + 5 + col * 7, y + 5 + r * 7, 5, 5, 2, 2);
                }
            }
            g2.dispose();
        }
    }

    /** Puzzle cell: rounded, painted with whatever background is set, accent outline when selected. */
    private static class CellButton extends JButton {
        private boolean selectedCell;

        CellButton() {
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder());
            setForeground(CELL_TXT_CLR);
            setBackground(Color.WHITE);
        }

        void setSelectedCell(boolean value) {
            selectedCell = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            smooth(g2);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            if (selectedCell) {
                g2.setColor(ACCENT_CLR);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
            }
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
            g2.setColor(hovered && isEnabled() ? hover : base);
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