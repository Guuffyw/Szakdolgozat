import javax.swing.*;
import javax.swing.border.MatteBorder;
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
        topPanel.setBackground(BG_CLR);
        topPanel.setPreferredSize(new Dimension(0, 48));
        topPanel.setBorder(BorderFactory.createMatteBorder(0,0,2,0,Color.BLACK));

        JLabel title = new JLabel("NONOGRAMM", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(Color.WHITE);

        topPanel.add(title, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);



        //---------------- MIDDLE PANEL ----------------
        int N = logic.getSize();
        buttons = new JButton[N][N];

        JPanel gridPanel = new JPanel(new GridLayout(N + 1, N + 1,1,1));
        gridPanel.setBackground(NAV_CLR);
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
                    lbl.setForeground(Color.WHITE);
                    lbl.setPreferredSize(new Dimension(30, 60));
                    lbl.setVerticalAlignment(SwingConstants.BOTTOM);

                    gridPanel.add(lbl);
                } else if (j == 0) {
                    JLabel lbl = new JLabel(logic.countRows(i-1),SwingConstants.RIGHT);
                    lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
                    lbl.setBorder(BorderFactory.createEmptyBorder(0,0,0,2));
                    lbl.setForeground(Color.WHITE);
                    gridPanel.add(lbl);
                    lbl.setAlignmentX(RIGHT_ALIGNMENT);

                } else {
                    JButton button = new JButton();
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
        rightPanel.setPreferredSize(new Dimension(145, 0));


        timerLabel = new JLabel("Time: 0");
        timerLabel.setForeground(Color.YELLOW);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        health = new JLabel("Health: " + logic.getHealth());
        health.setFont(new Font("SansSerif", Font.BOLD, 14));
        health.setForeground(Color.RED);

        JLabel loggedIn = new JLabel("Logged in as:");
        loggedIn.setForeground(Color.WHITE);
        JLabel username = new JLabel(HubPanel.getUserName());
        username.setForeground(Color.WHITE);

        JLabel diffLabel = new JLabel("DIFFICULTY");
        diffLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        diffLabel.setForeground(Color.WHITE);
        diffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> difficulty = new JComboBox<>(new String[]{"Easy", "Normal", "Hard"});
        difficulty.setFont(new Font("SansSerif", Font.BOLD, 12));
        difficulty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        difficulty.setAlignmentX(Component.LEFT_ALIGNMENT);
        difficulty.setBackground(BG_CLR);
        difficulty.setForeground(Color.WHITE);
        difficulty.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));
        difficulty.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        JButton startBtn = new JButton("Start");
        startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        startBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBackground(BG_CLR);
        startBtn.setFocusPainted(false);
        startBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startBtn.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));

        JButton backBtn = new JButton("Back to Hub");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(BG_CLR);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.BLACK));


        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(loggedIn);
        rightPanel.add(username);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(health);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(timerLabel);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(diffLabel);
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
        leftPanel.setPreferredSize(new Dimension(145, 0));
        leftPanel.setBorder(new MatteBorder(0,0,0,2,Color.BLACK));

        JLabel scoreTitle = new JLabel("Leaderboard:");
        scoreTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        scoreTitle.setForeground(Color.WHITE);
        scoreTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        leaderboardRows = new JPanel();
        leaderboardRows.setLayout(new BoxLayout(leaderboardRows, BoxLayout.Y_AXIS));
        leaderboardRows.setBackground(NAV_CLR);
        leaderboardRows.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardRows.setBorder(BorderFactory.createMatteBorder(1,0,0,0,Color.BLACK));


        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(scoreTitle);
        leftPanel.add(Box.createVerticalStrut(10));
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
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel nameLabel  = new JLabel(rank + ". " + name);
                JLabel scoreLabel = new JLabel(String.valueOf(score));
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
                nameLabel.setForeground(Color.WHITE);
                scoreLabel.setForeground(Color.WHITE);

                row.add(nameLabel,  BorderLayout.WEST);
                row.add(scoreLabel, BorderLayout.EAST);

                leaderboardRows.add(row);
                leaderboardRows.add(Box.createVerticalStrut(4));
                rank++;
            }
        } catch (SQLException e) {
            JLabel err = new JLabel("Unavailable");
            err.setFont(new Font("SansSerif", Font.BOLD, 12));
            err.setForeground(Color.WHITE);
            leaderboardRows.add(err);
        }

        leaderboardRows.revalidate();
        leaderboardRows.repaint();
    }
}