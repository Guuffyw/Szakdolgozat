import javax.swing.*;
import java.awt.*;

public class NonogrammPanel extends JPanel {

    public NonogrammPanel(GameFrame frame,NonogrammLogic logic) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        //TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel welcomeText = new JLabel("Welcome to Nonogramm");
        welcomeText.setFont(new Font("SansSherif",Font.BOLD,16));
        topPanel.add(welcomeText, BorderLayout.WEST);

        add(topPanel, BorderLayout.NORTH);

        //MIDDLE PANEL
        int N = logic.N;

        JPanel gridPanel = new JPanel(new GridLayout(N+1,N+1,2,2));
        gridPanel.setBackground(Color.WHITE);

        for (int i = 0; i < N+1; i++) {
            for (int j = 0; j < N + 1; j++) {
                if (i == 0 && j == 0) {
                    gridPanel.add(new JLabel(""));
                } else if (i == 0 || j == 0) {
                    if (j != 0) {
                        gridPanel.add(new JLabel(String.valueOf(logic.countColoumns(j - 1))));
                    } else {
                        gridPanel.add(new JLabel(String.valueOf(logic.countRows(i - 1))));
                    }
                } else {
                    JButton button = new JButton();

                    if (logic.isBlack(i, j) == 1) {
                        button.setBackground(Color.BLACK);
                    } else {
                        button.setBackground(Color.WHITE);
                    }
                    gridPanel.add(button);
                }
            }
        }

        add(gridPanel,BorderLayout.CENTER);

        //RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setPreferredSize(new Dimension(130,0));


        JLabel diffLabel = new JLabel("Difficulty");
        diffLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        diffLabel.setForeground(Color.GRAY);
        diffLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> difficulty = new JComboBox<>(new String[]{"Easy", "Normal", "Hard"});
        difficulty.setFont(new Font("SansSerif", Font.PLAIN, 12));
        difficulty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        difficulty.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton startBtn = new JButton("Start");
        startBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        startBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        startBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backBtn = new JButton("Back to Hub");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(diffLabel);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(difficulty);
        rightPanel.add(Box.createVerticalStrut(16));
        rightPanel.add(startBtn);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(backBtn);

        add(rightPanel, BorderLayout.EAST);

        //LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setPreferredSize(new Dimension(130, 0));

        JLabel scoreTitle = new JLabel("Scoreboard");
        scoreTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        scoreTitle.setForeground(Color.GRAY);
        leftPanel.add(scoreTitle);
        leftPanel.add(Box.createVerticalStrut(8));

        String[][] scores = {{"1. Alice", "1240"}, {"2. Bob", "980"}, {"3. Carol", "870"}, {"4. Dave", "760"}, {"5. Eve", "640"}};
        for (String[] s : scores) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            JLabel name = new JLabel(s[0]);
            name.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JLabel pts = new JLabel(s[1]);
            pts.setFont(new Font("SansSerif", Font.PLAIN, 12));
            pts.setForeground(Color.GRAY);
            row.add(name, BorderLayout.WEST);
            row.add(pts, BorderLayout.EAST);
            leftPanel.add(row);
            leftPanel.add(Box.createVerticalStrut(4));
        }

        add(leftPanel, BorderLayout.WEST);


        // ================= ACTION =================
        backBtn.addActionListener(e -> {
            frame.showHub();
        });
    }
}