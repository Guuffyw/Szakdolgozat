
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.jar.JarEntry;

public class NonogrammPanel extends JPanel {

    private final GameFrame frame;
    private final NonogrammLogic logic;

    private JLabel health;
    private JButton[][] buttons;

    public String selectedDiff = "Easy";

    private Timer gameTimer;
    private int secondsElapsed = 0;
    private JLabel timerLabel;

    private static final Color DARK        = new Color(0x111111);
    private static final Color DARK_PANEL  = new Color(0x1A1A1A);
    private static final Color DARK_ROW    = new Color(0x2A2A2A);
    private static final Color DARK_BORDER = new Color(0x333333);
    private static final Color TEXT_BRIGHT = new Color(0xCCCCCC);
    private static final Color TEXT_DIM    = new Color(0x555555);
    private static final Color PAGE_BG     = new Color(0xF5F5F3);
    private static final Color CELL_BORDER = new Color(0xCCCCCC);
    private static final Color BLUE_BTN    = new Color(0x4A90D9);
    private static final Color WRONG_BG    = new Color(0xFDF0F0);
    private static final Color WRONG_BORDER= new Color(0xE8A8A8);
    private static final Color WRONG_TEXT  = new Color(0xC25050);

    public NonogrammPanel(GameFrame frame,NonogrammLogic logic) {
        this.frame = frame;
        this.logic = logic;
        setLayout(new BorderLayout());
        buildPanel();
    }

    public void buildPanel()
    {
        setBackground(PAGE_BG);


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK);
        topPanel.setPreferredSize(new Dimension(0, 48));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel title = new JLabel("NONOGRAMM", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(Color.WHITE);

        topPanel.add(title, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);



        //---------------- MIDDLE PANEL ----------------
        int N = logic.getSize();
        JPanel gridPanel = new JPanel(new GridLayout(N + 1, N + 1, 3, 3));
        gridPanel.setBackground(PAGE_BG);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buttons = new JButton[N][N];

        for (int i = 0; i < N + 1; i++) {
            for (int j = 0; j < N + 1; j++) {
                if (i == 0 && j == 0) {
                    gridPanel.add(new JLabel(""));
                } else if (i == 0) {
                    JLabel lbl = new JLabel(logic.countColoumns(j - 1), SwingConstants.CENTER);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                    lbl.setForeground(new Color(0x222222));

                    gridPanel.add(lbl);
                } else if (j == 0) {
                    JLabel lbl = new JLabel(logic.countRows(i-1), SwingConstants.CENTER);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                    lbl.setForeground(new Color(0x222222));
                    gridPanel.add(lbl);
                } else {
                    JButton button = new JButton();
                    button.setBackground(Color.WHITE);
                    button.setFocusPainted(false);
                    button.setOpaque(true);
                    button.setBorder(BorderFactory.createLineBorder(CELL_BORDER, 1));
                    button.setFont(new Font("SansSerif", Font.BOLD, 13));
                    button.setForeground(CELL_BORDER);
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
        rightPanel.setBackground(DARK_PANEL);
        rightPanel.setPreferredSize(new Dimension(145, 0));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, DARK),
                BorderFactory.createEmptyBorder(18, 14, 18, 14)
        ));

        timerLabel = new JLabel("Time: 0");

        health = new JLabel("Health: " + logic.getHealth());
        health.setFont(new Font("SansSerif", Font.PLAIN, 12));
        health.setForeground(new Color(0xAAAAAA));

        JLabel username = new JLabel("Logged in as:\n" + HubPanel.getUserName());
        username.setForeground(Color.WHITE);

        JLabel diffLabel = new JLabel("DIFFICULTY");
        diffLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        diffLabel.setForeground(TEXT_DIM);
        diffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> difficulty = new JComboBox<>(new String[]{"Easy", "Normal", "Hard"});
        difficulty.setFont(new Font("SansSerif", Font.PLAIN, 12));
        difficulty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        difficulty.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficulty.setBackground(DARK_ROW);
        difficulty.setForeground(TEXT_BRIGHT);

        JButton startBtn = new JButton("Start");
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        startBtn.setBackground(BLUE_BTN);
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setOpaque(true);
        startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        startBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton backBtn = new JButton("Back to Hub");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setBackground(DARK_PANEL);
        backBtn.setForeground(TEXT_DIM);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(DARK_BORDER, 1));
        backBtn.setOpaque(true);
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        rightPanel.add(health);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(timerLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(username);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(diffLabel);
        rightPanel.add(Box.createVerticalStrut(8));
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
        leftPanel.setBackground(DARK_PANEL);
        leftPanel.setPreferredSize(new Dimension(145, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));

        JLabel scoreTitle = new JLabel("SCOREBOARD");
        scoreTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        scoreTitle.setForeground(TEXT_DIM);
        scoreTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(scoreTitle);
        leftPanel.add(Box.createVerticalStrut(12));

        String[][] scores = {{"1. Alice", "1240"}, {"2. Bob", "980"}, {"3. Carol", "870"}, {"4. Dave", "760"}, {"5. Eve", "640"}};
        for (int k = 0; k < scores.length; k++) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(k == 0 ? DARK_ROW : DARK_PANEL);
            row.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel name = new JLabel(scores[k][0]);
            name.setFont(new Font("SansSerif", Font.PLAIN, 12));
            name.setForeground(TEXT_BRIGHT);

            JLabel pts = new JLabel(scores[k][1]);
            pts.setFont(new Font("SansSerif", Font.PLAIN, 12));
            pts.setForeground(TEXT_DIM);

            row.add(name, BorderLayout.WEST);
            row.add(pts, BorderLayout.EAST);
            leftPanel.add(row);
            leftPanel.add(Box.createVerticalStrut(2));
        }
        leftPanel.add(Box.createVerticalGlue());
        add(leftPanel, BorderLayout.WEST);



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
                button.setBackground(Color.RED);
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
                int result = JOptionPane.showConfirmDialog(this,"You won\nstart new game","Victory",JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION){
                    frame.startNewNonogram(logic.setDiff(selectedDiff));
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

}