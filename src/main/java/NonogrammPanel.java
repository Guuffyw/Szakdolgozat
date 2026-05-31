import javax.swing.*;
import java.awt.*;

public class NonogrammPanel extends JPanel {

    public NonogrammPanel(GameFrame frame,NonogrammLogic logic) {

        setLayout(new BorderLayout());

        //TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLUE);
        topPanel.setPreferredSize(new Dimension(100, 80));

        JLabel welcomeText = new JLabel("Welcome to Nonogramm", SwingConstants.CENTER);

        topPanel.add(welcomeText, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        //MIDDLE PANEL
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.setBackground(Color.GREEN);

        JPanel gridPanel = new JPanel(new GridLayout(logic.N+1, logic.N+1));
        for (int i = 0; i < logic.N+1; i++)
            for (int j = 0; j < logic.N+1; j++)
                if (i== 0 && j == 0){
                    gridPanel.add(new JLabel(""));
                }
                else if(i == 0 || j == 0) {
                    if (j != 0){
                        gridPanel.add(new JLabel(String.valueOf(logic.countColoumns(j-1))));
                    }
                    else {
                        gridPanel.add(new JLabel(String.valueOf(logic.countRows(i-1))));
                    }
                }
                else {
                    JButton button = new JButton();

                    if (logic.isBlack(i,j)==1){
                        button.setBackground(Color.BLACK);
                    }
                    else {
                        button.setBackground(Color.WHITE);
                    }
                    gridPanel.add(button);
                }

        middlePanel.add(gridPanel, BorderLayout.CENTER);

        add(middlePanel, BorderLayout.CENTER);


        //RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.LIGHT_GRAY);

        JButton backButton = new JButton("Back to Hub");
        backButton.setPreferredSize(new Dimension(100,50));
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(50,50));
        JLabel healthCounter = new JLabel("Health: ");
        startButton.setPreferredSize(new Dimension(50,50));
        JLabel timer = new JLabel("Time:");
        startButton.setPreferredSize(new Dimension(50,50));
        JMenuBar menuBar = new JMenuBar();
        JMenu difficulity = new JMenu("Difficulity: ");
        startButton.setPreferredSize(new Dimension(50,50));
        JMenuItem easy = new JMenuItem("Easy");
        startButton.setPreferredSize(new Dimension(50,50));
        JMenuItem normal = new JMenuItem("Normal");
        startButton.setPreferredSize(new Dimension(50,50));
        JMenuItem hard = new JMenuItem("Hard");
        startButton.setPreferredSize(new Dimension(50,50));

        menuBar.add(difficulity);
        difficulity.add(easy);
        difficulity.add(normal);
        difficulity.add(hard);


        rightPanel.add(menuBar);
        rightPanel.add(timer);
        rightPanel.add(backButton);
        rightPanel.add(startButton);
        rightPanel.add(healthCounter);

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