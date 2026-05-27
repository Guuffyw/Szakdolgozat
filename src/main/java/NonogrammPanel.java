import javax.swing.*;
import java.awt.*;

public class NonogrammPanel extends JPanel {

    public NonogrammPanel(GameFrame frame) {

        setLayout(new BorderLayout());

        //TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLUE);
        topPanel.setPreferredSize(new Dimension(100, 80));

        JLabel timerText = new JLabel("Timer: 0");
        JLabel welcomeText = new JLabel("Welcome to Nonogramm", SwingConstants.CENTER);

        topPanel.add(timerText, BorderLayout.WEST);
        topPanel.add(welcomeText, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        //MIDDLE PANEL
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Color.GREEN);

        JLabel placeholder = new JLabel("Game grid goes here");
        middlePanel.add(placeholder);

        add(middlePanel, BorderLayout.CENTER);

        //RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.LIGHT_GRAY);

        JButton backButton = new JButton("Back to Hub");
        backButton.setPreferredSize(new Dimension(50,50));
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(50,50));

        rightPanel.add(backButton, BorderLayout.NORTH);
        rightPanel.add(startButton, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        //LEFT PANEL
        JPanel leftPanel = new JPanel(new BorderLayout());
        JLabel ScoreBoard = new JLabel("ScoreBoard:");

        String week[]= { "Monday","Tuesday","Wednesday",
                "Thursday","Friday","Saturday","Sunday"};

        JList ScoreBoardList = new JList(week);
        leftPanel.add(ScoreBoard,BorderLayout.NORTH);
        leftPanel.add(ScoreBoardList,BorderLayout.SOUTH);
        add(leftPanel,BorderLayout.WEST);



        // ================= ACTION =================
        backButton.addActionListener(e -> {
            frame.showHub();
        });
    }
}