package net.evlikat.siberian;

import net.evlikat.siberian.swing.FieldVisualizationPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainWindow extends JFrame {

    private JPanel rootPanel;
    private JPanel managementPanel;
    private JButton stopStartButton;
    private FieldVisualizationPanel fieldPanel;

    public MainWindow() throws HeadlessException {
        super("Siberian Forest");
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        rootPanel = new JPanel(new FlowLayout());
        rootPanel.setBackground(Color.WHITE);

        managementPanel = new JPanel();
        BoxLayout managementLayout = new BoxLayout(managementPanel, BoxLayout.Y_AXIS);
        managementPanel.setLayout(managementLayout);

        stopStartButton = styled(new JButton("Stop/Start"));
        stopStartButton.addActionListener(e -> fieldPanel.stopOrResume());

        managementPanel.add(stopStartButton);

        fieldPanel = new FieldVisualizationPanel();
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.init();

        rootPanel.add(managementPanel);
        rootPanel.add(fieldPanel);

        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton styled(JButton button) {
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        button.setBorder(compound);
        return button;
    }
}
