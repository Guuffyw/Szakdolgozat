import javax.swing.*;

public class HubPanel extends JPanel {

    public HubPanel(GameFrame frame) {
        setLayout(null);
        JButton nonoButton = new JButton("Nonogramm");
        nonoButton.setBounds(0,200,100,100);

        nonoButton.addActionListener(e -> {
            frame.showGame();
        });

        add(nonoButton);
    }
}